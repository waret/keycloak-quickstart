package net.waret.demo.rls.repository;

import net.waret.demo.rls.domain.Project;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProjectRepository extends PagingAndSortingRepository<Project, Long>, JpaSpecificationExecutor<Project> {
}
