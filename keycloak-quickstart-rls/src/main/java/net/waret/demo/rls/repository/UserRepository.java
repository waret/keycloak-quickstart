package net.waret.demo.rls.repository;

import net.waret.demo.rls.domain.User;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);
}
