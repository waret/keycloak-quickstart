package net.waret.demo.rls.repository;

import net.waret.demo.rls.domain.Organization;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrganizationRepository extends PagingAndSortingRepository<Organization, Long>, JpaSpecificationExecutor<Organization> {
}
