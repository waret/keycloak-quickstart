package net.waret.demo.authz.repository;

import net.waret.demo.authz.domain.Resource;
import net.waret.demo.authz.domain.Role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleFieldAndResource(String roleField, Resource resource);

}
