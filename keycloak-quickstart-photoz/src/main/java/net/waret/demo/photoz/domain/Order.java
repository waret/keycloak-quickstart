package net.waret.demo.photoz.domain;

import org.springframework.hateoas.Identifiable;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "t_order")
@NoArgsConstructor
public class Order implements Identifiable<Serializable> {

    @Id
    @GeneratedValue
    private Long id;

    private String description;
    private Status status;

    public Order(String description, Status status) {
        this.description = description;
        this.status = status;
    }
}