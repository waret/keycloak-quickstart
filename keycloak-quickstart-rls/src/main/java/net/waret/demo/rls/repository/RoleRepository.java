package net.waret.demo.rls.repository;

import net.waret.demo.rls.domain.Role;
import net.waret.demo.rls.domain.User;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends PagingAndSortingRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    List<Role> findByGrantsUser(User user);

    Optional<Role> findByRoleField(String roleField);

}
