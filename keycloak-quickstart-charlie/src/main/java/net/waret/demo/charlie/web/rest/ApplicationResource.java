package net.waret.demo.charlie.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.keycloak.KeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

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
        return (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
    }

}
