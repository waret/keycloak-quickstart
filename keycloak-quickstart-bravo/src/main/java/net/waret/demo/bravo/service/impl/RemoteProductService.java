package net.waret.demo.bravo.service.impl;

import net.waret.demo.bravo.service.ProductService;

import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

//@Service
public class RemoteProductService implements ProductService {

    private final KeycloakRestTemplate template;

    private String endpoint;

    @Autowired
    public RemoteProductService(KeycloakRestTemplate template) {
        this.template = template;
    }

    @Override
    public List<String> getProducts() {
        ResponseEntity<String[]> response = template.getForEntity(endpoint, String[].class);
        return Arrays.asList(response.getBody());
    }
}