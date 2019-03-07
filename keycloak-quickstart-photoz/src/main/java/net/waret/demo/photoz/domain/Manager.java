package net.waret.demo.photoz.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.hateoas.Identifiable;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "t_manager")
@NoArgsConstructor
public class Manager implements Identifiable<Long> {

    @Id
    @GeneratedValue
    private Long id;
    private String name;

    /**
     * To break the recursive, bi-directional interface, don't serialize {@literal employees}.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "manager")
    private List<Employee> employees = new ArrayList<>();

    public Manager(String name) {
        this.name = name;
    }
}
