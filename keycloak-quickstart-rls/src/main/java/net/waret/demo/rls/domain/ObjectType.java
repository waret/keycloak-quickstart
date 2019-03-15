package net.waret.demo.rls.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "rls_object_type")
@NoArgsConstructor
public class ObjectType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "object_type_id")
    private Long objectTypeId;

    private String appLabel;

    private String model;

    @EqualsAndHashCode.Exclude
    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "objectType")
    private Set<Permission> permissions = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "objectType")
    private Set<Role> roles = new HashSet<>();

    public ObjectType(String appLabel, String model) {
        this.appLabel = appLabel;
        this.model = model;
    }

    public ObjectType addPermission(Permission permission) {
        this.permissions.add(permission);
        return this;
    }

}
