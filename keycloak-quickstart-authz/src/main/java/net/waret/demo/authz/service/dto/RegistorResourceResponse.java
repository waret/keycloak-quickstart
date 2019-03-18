package net.waret.demo.authz.service.dto;

import net.waret.demo.authz.domain.Resource;
import net.waret.demo.authz.domain.Role;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class RegistorResourceResponse {

    private String resourceType;

    private Resource resource;

    private Set<Role> roles = new HashSet<>();

    public RegistorResourceResponse setResourceType(String resourceType) {
        this.resourceType = resourceType;
        return this;
    }

    public RegistorResourceResponse setResource(Resource resource) {
        this.resource = resource;
        return this;
    }

    public RegistorResourceResponse addRole(Role role) {
        this.roles.add(role);
        return this;
    }

}
