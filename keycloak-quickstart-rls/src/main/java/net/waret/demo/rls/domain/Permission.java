package net.waret.demo.rls.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "rls_permissions")
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    @EqualsAndHashCode.Include
    private Long permissionId;

    @EqualsAndHashCode.Include
    private String name;

    @EqualsAndHashCode.Include
    private String codename;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "object_type_id")
    private ObjectType objectType;

    public Permission(String name, String codename) {
        this.name = name;
        this.codename = codename;
    }

    public Permission(String name, String codename, ObjectType objectType) {
        this.name = name;
        this.codename = codename;
        this.objectType = objectType;
    }

}
