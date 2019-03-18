package net.waret.demo.authz.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "authz_role")
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "role_field")
    private String roleField;

    @ManyToMany
    @JoinTable(name = "authz_role_parents",
            joinColumns = @JoinColumn(name = "child_role_id"),
            inverseJoinColumns = @JoinColumn(name = "parent_role_id"))
    private Set<Role> parents = new HashSet<>();

    @ManyToMany(mappedBy = "parents")
    private Set<Role> children = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "authz_role_ancestors",
            joinColumns = @JoinColumn(name = "descendent_role_id"),
            inverseJoinColumns = @JoinColumn(name = "ancestor_role_id"))
    private Set<Role> ancestors = new HashSet<>();

    @ManyToMany(mappedBy = "ancestors")
    private Set<Role> descentdents = new HashSet<>();

    @ManyToMany(mappedBy = "grantedRoles")
    private Set<User> grants = new HashSet<>();

    @ManyToOne
    private Resource resource;

    public Role updateAncestors() {
        this.ancestors.clear();
        this.parents.forEach(parent -> this.ancestors.addAll(parent.ancestors));
        this.ancestors.add(this);
        return this;
    }

    public Role setRoleField(String roleField) {
        this.roleField = roleField;
        return this;
    }

    public Role addParent(Role role) {
        this.parents.add(role);
        return this;
    }

    public Role addAncestor(Role role) {
        this.ancestors.add(role);
        return this;
    }

    public Role setResource(Resource resource) {
        this.resource = resource;
        return this;
    }
}
