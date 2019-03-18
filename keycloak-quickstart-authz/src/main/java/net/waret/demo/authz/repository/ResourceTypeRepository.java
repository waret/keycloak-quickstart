package net.waret.demo.authz.repository;

import net.waret.demo.authz.domain.ResourceType;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceTypeRepository extends JpaRepository<ResourceType, Long> {

}
