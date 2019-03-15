package net.waret.demo.rls.repository.test;

import net.waret.demo.rls.domain.test.User2;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface User2Repository extends PagingAndSortingRepository<User2, Long>, JpaSpecificationExecutor<User2> {

}
