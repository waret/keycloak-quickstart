package net.waret.demo.rls.repository;

import net.waret.demo.rls.domain.Permission;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PermissionRepository extends PagingAndSortingRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
}
