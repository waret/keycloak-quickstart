package net.waret.demo.photoz.repository;

import net.waret.demo.photoz.domain.Employee;
import net.waret.demo.photoz.domain.Manager;

import org.springframework.data.repository.CrudRepository;

public interface ManagerRepository extends CrudRepository<Manager, Long> {

    /**
     * Navigate through the JPA relationship to find a {@link Manager} based on an {@link Employee}'s {@literal id}.
     *
     * @param id
     * @return
     */
    Manager findByEmployeesId(Long id);
}
