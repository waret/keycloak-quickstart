package net.waret.demo.authz.service;

import net.waret.demo.authz.domain.ResourceType;
import net.waret.demo.authz.repository.ResourceRepository;
import net.waret.demo.authz.repository.ResourceTypeRepository;
import net.waret.demo.authz.repository.RoleRepository;
import net.waret.demo.authz.service.dto.RegistorResourceRequest;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

@Service
public class PreloadDatabaseService {

    private final UserRoleService userRoleService;
    private final ResourceTypeRepository resourceTypeRepository;
    private final ResourceRepository resourceRepository;
    private final RoleRepository roleRepository;

    public PreloadDatabaseService(
            UserRoleService userRoleService,
            ResourceTypeRepository resourceTypeRepository,
            ResourceRepository resourceRepository,
            RoleRepository roleRepository) {
        this.userRoleService = userRoleService;
        this.resourceTypeRepository = resourceTypeRepository;
        this.resourceRepository = resourceRepository;
        this.roleRepository = roleRepository;
    }

    public void preloadData() {
        addResourceType();
        addDefaultDomain();
    }

    private void addResourceType() {
        resourceTypeRepository.saveAll(Arrays.asList(
                new ResourceType("Domain"),
                new ResourceType("Group"),
                new ResourceType("Project"),
                new ResourceType("Repository"),
                new ResourceType("RepositoryNamespace"),
                new ResourceType("KubernetsCluster"),
                new ResourceType("KubernetsClusterNamespace")
        ));
    }

    private void addDefaultDomain() {
        RegistorResourceRequest registorResourceRequest = new RegistorResourceRequest();
        registorResourceRequest.setResourceName("Default").setResourceType("Domain")
                .addRoleParent("system_administrator", new LinkedList<>())
                .addRoleParent("system_auditor", new LinkedList<>())
                .addRoleParent("repo_admin_role", Collections.singletonList(
                        new RegistorResourceRequest.RoleField().setResourceType("Domain").setRoleField("system_administrator")
                ))
                .addRoleParent("k8s_cluster_admin_role", Collections.singletonList(
                        new RegistorResourceRequest.RoleField().setResourceType("Domain").setRoleField("system_administrator")
                ))
                .addRoleParent("project_admin_role", Collections.singletonList(
                        new RegistorResourceRequest.RoleField().setResourceType("Domain").setRoleField("system_administrator")
                ));
        userRoleService.registerResource(registorResourceRequest);
    }

}
