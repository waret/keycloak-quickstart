package net.waret.demo.rls.repository.test;

import net.waret.demo.rls.domain.test.Role2;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface Role2Repository extends PagingAndSortingRepository<Role2, Long>, JpaSpecificationExecutor<Role2> {

}
