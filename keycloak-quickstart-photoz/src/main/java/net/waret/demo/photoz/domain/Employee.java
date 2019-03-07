package net.waret.demo.photoz.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.hateoas.Identifiable;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "t_employee")
@NoArgsConstructor
public class Employee implements Identifiable<Serializable> {

    @Id
    @GeneratedValue
    private Long id;

    private String firstName;
    private String lastName;
    private String role;

    /**
     * To break the recursive, bi-directional relationship, don't serialize {@literal manager}.
     */
    @JsonIgnore
    @OneToOne
    private Manager manager;

    public Employee(String firstName, String lastName, String role, Manager manager) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.manager = manager;
    }

    public String getName() {
        return this.firstName + " " + this.lastName;
    }

    public void setName(String name) {
        String[] parts = name.split(" ");
        this.firstName = parts[0];
        this.lastName = parts[1];
    }
}