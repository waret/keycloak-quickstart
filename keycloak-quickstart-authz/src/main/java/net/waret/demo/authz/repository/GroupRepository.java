package net.waret.demo.authz.repository;

import net.waret.demo.authz.domain.Group;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findByGroupName(String groupName);

}
