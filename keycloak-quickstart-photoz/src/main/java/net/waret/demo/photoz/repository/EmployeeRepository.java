package net.waret.demo.photoz.repository;

import net.waret.demo.photoz.domain.Employee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByManagerId(Long id);
}