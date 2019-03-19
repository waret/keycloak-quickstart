package net.waret.demo.authz.service.dto;

import net.waret.demo.authz.repository.RoleRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class RegistorResourceRequest {

    private Long parentResourceId;

    private String resourceType;

    private String resourceName;

    private Map<String, List<RoleField>> roleParents = new HashMap<>();

    @Data
    public static class RoleField {

        private String roleField;

        private String resourceType;

        // 如果resourceId为空，则说明是当前资源
        private Long resourceId;

        public RoleField setRoleField(String roleField) {
            this.roleField = roleField;
            return this;
        }

        public RoleField setResourceType(String resourceType) {
            this.resourceType = resourceType;
            return this;
        }

        public RoleField setResourceId(Long resourceId) {
            this.resourceId = resourceId;
            return this;
        }

    }

    public RegistorResourceRequest setResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }

    public RegistorResourceRequest setParentResourceId(Long parentResourceId) {
        this.parentResourceId = parentResourceId;
        return this;
    }

    public RegistorResourceRequest setResourceType(String resourceType) {
        this.resourceType = resourceType;
        return this;
    }

    public RegistorResourceRequest addRoleParent(String roleField, List<RoleField> roles) {
        this.roleParents.put(roleField, roles);
        return this;
    }

}
