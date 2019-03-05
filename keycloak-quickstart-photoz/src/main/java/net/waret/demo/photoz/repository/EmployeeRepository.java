package net.waret.demo.photoz.repository;

import net.waret.demo.photoz.domain.Employee;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}