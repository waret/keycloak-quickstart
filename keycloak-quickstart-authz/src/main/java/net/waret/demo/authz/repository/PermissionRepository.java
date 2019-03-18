package net.waret.demo.authz.repository;

import net.waret.demo.authz.domain.Permission;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByMethodAndResourceType(String method, String resourceType);

}
