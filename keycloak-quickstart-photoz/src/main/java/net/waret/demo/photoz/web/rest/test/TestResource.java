package net.waret.demo.photoz.web.rest.test;

import net.waret.demo.photoz.domain.Order;
import net.waret.demo.photoz.domain.Status;
import net.waret.demo.photoz.web.error.RunnableExc;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.ClientAuthorizationContext;
import org.keycloak.authorization.client.representation.TokenIntrospectionResponse;
import org.keycloak.authorization.client.resource.PolicyResource;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.Permission;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.keycloak.representations.idm.authorization.UmaPermissionRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
public class TestResource {

    private final HttpServletRequest request;

    public TestResource(HttpServletRequest request) {
        this.request = request;
    }

    private void test() {

        log.debug("query permissions");
        for (Permission permission : getAuthorizationContext().getPermissions()) {
            log.debug("    " + permission);
        }

        AuthzClient authzClient = getAuthzClient();
        String tokenString = getKeycloakSecurityContext().getTokenString();
        AuthorizationRequest ar = new AuthorizationRequest();
        ar.setScope("album:view");
        String rpt = authzClient.authorization(tokenString).authorize(ar).getToken();
        log.debug("rpt: " + rpt);

        TokenIntrospectionResponse requestingPartyToken = authzClient.protection().introspectRequestingPartyToken(rpt);
        if (requestingPartyToken.getActive()) {
            log.debug("query permissions");
            for (Permission granted : requestingPartyToken.getPermissions()) {
                log.debug("    " + granted);
            }
        }
    }

    private void test2() {
        HashSet<ScopeRepresentation> scopes = new HashSet<>();
        scopes.add(new ScopeRepresentation("vm:view"));
        scopes.add(new ScopeRepresentation("vm:control"));
        ResourceRepresentation albumResource = new ResourceRepresentation("vm03", scopes, "", "vms");
        albumResource.setOwnerManagedAccess(true);
        ResourceRepresentation response = getAuthzClient().protection().resource().create(albumResource);

        String tokenString = getKeycloakSecurityContext().getTokenString();
        log.debug("access token: " + tokenString);
        String rpt = getAuthzClient().authorization(tokenString).authorize().getToken();
        log.debug("rpt: " + rpt);

        PolicyResource policy = getAuthzClient().protection("resource-photoz", "secret")
                .policy(response.getId());
        UmaPermissionRepresentation permission = new UmaPermissionRepresentation();
        permission.addRole("admin");
        permission.setName("test");
        permission.setDescription("Give access to ADMIN ROLE");
        permission = policy.create(permission);
        log.debug("aaaa: " + permission);
        for (UmaPermissionRepresentation urp : policy.find(null, null, null, null)) {
            log.debug("aaaaaaa: " + urp);
        }
    }

    @GetMapping(value = "/test-client-ip", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity testClientIp() {
        try {
            test2();
        } catch (Exception e) {
            log.error("xxxxxxxxxxxxxxx", e);
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Order("test-client-id", Status.COMPLETED));
    }

    @GetMapping(value = "/hello", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity testClientIp2() {
        return ResponseEntity.status(HttpStatus.OK).body(new Order("test-client-id", Status.COMPLETED));
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
