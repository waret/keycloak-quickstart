package net.waret.demo.photoz.web.rest.employee;

import net.waret.demo.photoz.domain.Employee;
import net.waret.demo.photoz.domain.Manager;
import net.waret.demo.photoz.repository.EmployeeRepository;
import net.waret.demo.photoz.service.dto.EmployeeWithManager;
import net.waret.demo.photoz.web.assembler.EmployeeResourceAssembler;
import net.waret.demo.photoz.web.assembler.EmployeeWithManagerResourceAssembler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1")
public class EmployeeResource {

    private final EmployeeRepository repository;

    private final EmployeeResourceAssembler assembler;

    private final EmployeeWithManagerResourceAssembler employeeWithManagerResourceAssembler;

    @Autowired
    public EmployeeResource(EmployeeRepository repository,
            EmployeeResourceAssembler assembler,
            EmployeeWithManagerResourceAssembler employeeWithManagerResourceAssembler) {
        this.repository = repository;
        this.assembler = assembler;
        this.employeeWithManagerResourceAssembler = employeeWithManagerResourceAssembler;
    }

    // Aggregate root

    @GetMapping(value = "/employees", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<Resources<Resource<Employee>>> all() {
        return ResponseEntity.ok(
                assembler.toResources(repository.findAll()));
    }

    @PostMapping("/employees")
    public ResponseEntity<Resource<Employee>> newEmployee(@RequestBody Employee employee) {

        Employee savedEmployee = repository.save(employee);

        return ResponseEntity
                .created(linkTo(methodOn(EmployeeResource.class).one(savedEmployee.getId())).toUri())
                .body(assembler.toResource(savedEmployee));
    }

    // Single item

    @GetMapping(value = "/employees/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<Resource<Employee>> one(@PathVariable long id) {
        return repository.findById(id).map(employee -> ResponseEntity.ok(assembler.toResource(employee)))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/employees/{id}")
    public Employee replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {

        return repository.findById(id)
                .map(employee -> {
                    employee.setName(newEmployee.getName());
                    employee.setRole(newEmployee.getRole());
                    return repository.save(employee);
                })
                .orElseGet(() -> {
                    newEmployee.setId(id);
                    return repository.save(newEmployee);
                });
    }

    @DeleteMapping("/employees/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        repository.deleteById(id);
    }

    /**
     * Find an {@link Employee}'s {@link Manager} based upon employee id. Turn it into a context-based link.
     */
    @GetMapping(value = "/managers/{id}/employees", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<Resources<Resource<Employee>>> findEmployees(@PathVariable long id) {
        return ResponseEntity.ok(
                assembler.toResources(repository.findByManagerId(id)));
    }

    @GetMapping(value = "/employees/detailed", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<Resources<Resource<EmployeeWithManager>>> findAllDetailedEmployees() {

        return ResponseEntity.ok(
                employeeWithManagerResourceAssembler.toResources(
                        repository.findAll().stream().map(EmployeeWithManager::new).collect(Collectors.toList())));
    }

    @GetMapping(value = "/employees/{id}/detailed", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<Resource<EmployeeWithManager>> findDetailedEmployee(
            @PathVariable Long id) {
        return repository.findById(id).map(employee -> ResponseEntity.ok(
                employeeWithManagerResourceAssembler.toResource(new EmployeeWithManager(employee))))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


}
