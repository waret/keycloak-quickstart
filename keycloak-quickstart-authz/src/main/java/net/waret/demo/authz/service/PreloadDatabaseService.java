package net.waret.demo.authz.service;

import net.waret.demo.authz.domain.Resource;
import net.waret.demo.authz.domain.ResourceType;
import net.waret.demo.authz.repository.ResourceRepository;
import net.waret.demo.authz.repository.ResourceTypeRepository;
import net.waret.demo.authz.repository.RoleRepository;

import org.springframework.stereotype.Service;

@Service
public class PreloadDatabaseService {


    private final ResourceTypeRepository resourceTypeRepository;
    private final ResourceRepository resourceRepository;
    private final RoleRepository roleRepository;

    public PreloadDatabaseService(
            ResourceTypeRepository resourceTypeRepository,
            ResourceRepository resourceRepository,
            RoleRepository roleRepository) {
        this.resourceTypeRepository = resourceTypeRepository;
        this.resourceRepository = resourceRepository;
        this.roleRepository = roleRepository;
    }

    public void preloadData() {
        ResourceType domainType = resourceTypeRepository.save(new ResourceType("Domain"));
        ResourceType teamType = resourceTypeRepository.save(new ResourceType("Group"));
        ResourceType projectType = resourceTypeRepository.save(new ResourceType("Project"));
        ResourceType repositoryType = resourceTypeRepository.save(new ResourceType("Repository"));
        ResourceType repositoryNamespaceType = resourceTypeRepository.save(new ResourceType("RepositoryNamespace"));
        ResourceType kubernetsClusterType = resourceTypeRepository.save(new ResourceType("KubernetsCluster"));
        ResourceType kubernetsClusterNamespaceType = resourceTypeRepository.save(new ResourceType("KubernetsClusterNamespace"));

        Resource domain = resourceRepository.save(new Resource().setResourceName("Default").setResourceType("Domain"));
    }

}
