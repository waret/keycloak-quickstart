package net.waret.demo.photoz.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import net.waret.demo.photoz.domain.Employee;

import lombok.Value;

@Value
@JsonPropertyOrder({"id", "name", "role", "manager"})
public class EmployeeWithManager {

    @JsonIgnore
    private final Employee employee;

    public Long getId() {
        return this.employee.getId();
    }

    public String getName() {
        return this.employee.getName();
    }

    public String getRole() {
        return this.employee.getRole();
    }

    public String getManager() {
        return this.employee.getManager().getName();
    }

}
