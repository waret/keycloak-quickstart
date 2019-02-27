package net.waret.demo.photoz.web.rest.album;

import net.waret.demo.photoz.domain.Album;
import net.waret.demo.photoz.repository.AlbumRepository;
import net.waret.demo.photoz.web.dto.SharedAlbum;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.ClientAuthorizationContext;
import org.keycloak.authorization.client.resource.ProtectionResource;
import org.keycloak.representations.idm.authorization.PermissionTicketRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/album")
//@Transactional
public class AlbumService {

    private static final String SCOPE_ALBUM_VIEW = "album:view";
    private static final String SCOPE_ALBUM_DELETE = "album:delete";

    private final HttpServletRequest request;

    private final AlbumRepository albumRepository;

    @Autowired
    public AlbumService(HttpServletRequest request,
            AlbumRepository albumRepository) {
        this.request = request;
        this.albumRepository = albumRepository;
    }

    @PostMapping()
    public ResponseEntity create(Album newAlbum) {
        Principal userPrincipal = request.getUserPrincipal();
        newAlbum.setUserId(userPrincipal.getName());

        if (!CollectionUtils.isEmpty(albumRepository.findByUserIdAndName(newAlbum.getUserId(), newAlbum.getName()))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Name [" + newAlbum.getName() + "] already taken. Choose another one.");
        }

        try {
            newAlbum = albumRepository.save(newAlbum);
            log.debug("New Album: " + newAlbum);
            createProtectedResource(newAlbum);
        } catch (Exception e) {
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
    public ResponseEntity<List<Album>> findAll() {
        log.debug("request: " + request);
        log.debug("albumRepository: " + albumRepository);
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        return ResponseEntity.ok(albumRepository.findByUserId(name));
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
        KeycloakSecurityContext keycloakSecurityContext = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        ignoringExc(() -> {
            log.debug("keycloakSecurityContext.getToken(): {}", keycloakSecurityContext.getToken());
            log.debug("keycloakSecurityContext.getTokenString(): {}", keycloakSecurityContext.getTokenString());
            log.debug("keycloakSecurityContext.getIdToken(): {}", keycloakSecurityContext.getIdToken());
            log.debug("keycloakSecurityContext.getIdTokenString(): {}", keycloakSecurityContext.getIdTokenString());
            log.debug("keycloakSecurityContext.getAuthorizationContext().getPermissions(): {}", keycloakSecurityContext.getAuthorizationContext().getPermissions());
            log.debug("keycloakSecurityContext.getRealm(): {}", keycloakSecurityContext.getRealm());
            log.debug("keycloakSecurityContext.getToken().getPreferredUsername(): {}", keycloakSecurityContext.getToken().getPreferredUsername());
        });
        return keycloakSecurityContext;
//        return (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
    }

    private static void ignoringExc(RunnableExc r) {
        try {
            r.run();
        } catch (Exception ignored) {
        }
    }

    @FunctionalInterface
    public interface RunnableExc {
        void run() throws Exception;
    }
}
