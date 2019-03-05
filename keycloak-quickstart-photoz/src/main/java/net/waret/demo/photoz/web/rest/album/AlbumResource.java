package net.waret.demo.photoz.web.rest.album;

import net.waret.demo.photoz.domain.Album;
import net.waret.demo.photoz.repository.AlbumRepository;
import net.waret.demo.photoz.service.dto.SharedAlbum;
import net.waret.demo.photoz.utils.OffsetBasedPageRequest;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.ClientAuthorizationContext;
import org.keycloak.authorization.client.resource.ProtectionResource;
import org.keycloak.representations.idm.authorization.PermissionTicketRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/album")
//@Transactional
public class AlbumResource {

    private static final String SCOPE_ALBUM_VIEW = "album:view";
    private static final String SCOPE_ALBUM_DELETE = "album:delete";

    private final HttpServletRequest request;

    private final AlbumRepository albumRepository;

    @Autowired
    public AlbumResource(HttpServletRequest request,
            AlbumRepository albumRepository) {
        this.request = request;
        this.albumRepository = albumRepository;
    }

    @PostMapping()
    public ResponseEntity create(@RequestBody Album newAlbum) {
        Principal userPrincipal = request.getUserPrincipal();
        newAlbum.setUserId(userPrincipal.getName());

        if (!CollectionUtils.isEmpty(albumRepository.findByUserIdAndName(newAlbum.getUserId(), newAlbum.getName()))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Name [" + newAlbum.getName() + "] already taken. Choose another one.");
        }

        try {
            newAlbum = albumRepository.save(newAlbum);
            log.debug("New Album: " + newAlbum);
            createProtectedResource(newAlbum);
            albumRepository.save(newAlbum);
        } catch (Exception e) {
            log.error("Create Album Exception", e);
            getAuthzClient().protection().resource().delete(newAlbum.getExternalId());
        }

        return ResponseEntity.ok(newAlbum);
    }

    @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable("id") String id) {
        return albumRepository.findById(id).map(album -> {
            deleteProtectedResource(album);
            albumRepository.delete(album);
            return ResponseEntity.ok(album);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<Album>> findAll(
            @RequestParam(value = "offset", required = false) Long offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        log.debug("request: " + request);
        log.debug("albumRepository: " + albumRepository);
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        Pageable pr;
        if (offset != null && limit != null) {
            pr = new OffsetBasedPageRequest(offset, limit, Sort.Direction.ASC, "name");
        } else {
            pr = PageRequest.of(page, size, Sort.Direction.ASC, "name");
        }
        return ResponseEntity.ok(albumRepository.findByUserId(name, pr).get().collect(Collectors.toList()));
    }

    @GetMapping("/shares")
    public ResponseEntity findShares() {
        List<PermissionTicketRepresentation> permissions = getAuthzClient().protection().permission().find(null, null, null, getKeycloakSecurityContext().getToken().getSubject(), true, true, null, null);
        Map<String, SharedAlbum> shares = new HashMap<>();

        for (PermissionTicketRepresentation permission : permissions) {
            SharedAlbum share = shares.get(permission.getResource());

            if (share == null) {
                share = new SharedAlbum(albumRepository.findByExternalId(permission.getResource()));
                shares.put(permission.getResource(), share);
            }

            if (permission.getScope() != null) {
                share.addScope(permission.getScopeName());
            }
        }

        return ResponseEntity.ok(shares.values());
    }

    @GetMapping("{id}")
    public ResponseEntity findById(@PathVariable("id") String id) {
        return albumRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    private void createProtectedResource(Album album) {
        try {
            HashSet<ScopeRepresentation> scopes = new HashSet<>();

            scopes.add(new ScopeRepresentation(SCOPE_ALBUM_VIEW));
            scopes.add(new ScopeRepresentation(SCOPE_ALBUM_DELETE));

            ResourceRepresentation albumResource = new ResourceRepresentation(album.getName(), scopes, "/album/" + album.getId(), "http://photoz.com/album");

            albumResource.setOwner(album.getUserId());
            albumResource.setOwnerManagedAccess(true);

            ResourceRepresentation response = getAuthzClient().protection().resource().create(albumResource);

            album.setExternalId(response.getId());
        } catch (Exception e) {
            throw new RuntimeException("Could not register protected resource.", e);
        }
    }

    private void deleteProtectedResource(Album album) {
        String uri = "/album/" + album.getId();

        try {
            ProtectionResource protection = getAuthzClient().protection();
            List<ResourceRepresentation> search = protection.resource().findByUri(uri);

            if (search.isEmpty()) {
                throw new RuntimeException("Could not find protected resource with URI [" + uri + "]");
            }

            protection.resource().delete(search.get(0).getId());
        } catch (Exception e) {
            throw new RuntimeException("Could not search protected resource.", e);
        }
    }

    private AuthzClient getAuthzClient() {
        return getAuthorizationContext().getClient();
    }

    private ClientAuthorizationContext getAuthorizationContext() {
        return (ClientAuthorizationContext) getKeycloakSecurityContext().getAuthorizationContext();
    }

    private KeycloakSecurityContext getKeycloakSecurityContext() {
        return (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
    }
}
