package net.waret.demo.authz.repository;

import net.waret.demo.authz.domain.Resource;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

}
