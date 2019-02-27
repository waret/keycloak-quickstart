package net.waret.demo.alice.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.keycloak.KeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
@Slf4j
@RestController
public class ApplicationResource {

    private final HttpServletRequest request;

    private final ObjectMapper objectMapper;

    @Autowired
    public ApplicationResource(HttpServletRequest request,
            ObjectMapper objectMapper) {
        this.request = request;
        this.objectMapper = objectMapper;
    }

    @RequestMapping(value = "/api/resourcea", method = RequestMethod.GET)
    public String handleResourceA() {
        log.debug("request: {}, objectMapper: {}", request, objectMapper);
        return "ResourceA: " + createResponse();
    }

    @RequestMapping(value = "/api/resourceb", method = RequestMethod.GET)
    public String handleResourceB() {
        return "ResourceB: " + createResponse();
    }

    @RequestMapping(value = "/api/premium", method = RequestMethod.GET)
    public String handlePremiumResource() {
        return "Premium: " + createResponse();
    }

    @RequestMapping(value = "/api/admin", method = RequestMethod.GET)
    public String handleAdminResource() {
        return "Admin: " + createResponse();
    }

    private String createResponse() {
        try {
            return getKeycloakSecurityContext().getToken().getPreferredUsername();
        } catch (Exception ignored) {
        }
        return "aa";
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
    }

    private static void ignoringExc(RunnableExc r) {
        try { r.run(); } catch (Exception ignored) { }
    }

    @FunctionalInterface public interface RunnableExc {
        void run() throws Exception;
    }

}
