package net.waret.demo.rls.repository;

import net.waret.demo.rls.domain.ObjectType;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ObjectTypeRepository extends PagingAndSortingRepository<ObjectType, Long>, JpaSpecificationExecutor<ObjectType> {
}
