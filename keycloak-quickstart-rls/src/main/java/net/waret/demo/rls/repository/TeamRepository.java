package net.waret.demo.rls.repository;

import net.waret.demo.rls.domain.Team;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TeamRepository extends PagingAndSortingRepository<Team, Long>, JpaSpecificationExecutor<Team> {
}
