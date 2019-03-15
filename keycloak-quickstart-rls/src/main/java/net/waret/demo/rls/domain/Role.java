package net.waret.demo.rls.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "rls_roles")
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Role {

    public static final String SYSTEM_ADMINISTRATOR = "system_administrator";
    public static final String SYSTEM_AUDITOR       = "system_auditor";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    @EqualsAndHashCode.Include
    private Long roleId;

    @EqualsAndHashCode.Include
    private String roleField;

    @EqualsAndHashCode.Include
    private String singletonName;

    private String implicitParents;

    @ManyToOne
    @JoinColumn(name = "object_type_id")
    @EqualsAndHashCode.Include
    private ObjectType objectType;

    @EqualsAndHashCode.Include
    private Long objectId;

    @ToString.Exclude
    @ManyToMany(mappedBy = "grantedRoles")
    private Set<User> grants = new HashSet<>();

    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "rls_role_parents",
            joinColumns = @JoinColumn(name = "child_role_id"),
            inverseJoinColumns = @JoinColumn(name = "parent_role_id"))
    private Set<Role> parents = new HashSet<>();

    @ToString.Exclude
    @ManyToMany(mappedBy = "parents",
            cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE})
    private Set<Role> children = new HashSet<>();

    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "rls_role_ancestors",
            joinColumns = @JoinColumn(name = "descendent_role_id"),
            inverseJoinColumns = @JoinColumn(name = "ancestor_role_id"))
    private Set<Role> ancestors = new HashSet<>();

    @ToString.Exclude
    @ManyToMany(mappedBy = "ancestors",
            cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE})
    private Set<Role> descentdents = new HashSet<>();

    public Role setRoleField(String roleField) {
        this.roleField = roleField;
        return this;
    }

    public Role setSingletonName(String singletonName) {
        this.singletonName = singletonName;
        return this;
    }

    public Role updateImplicitParents() {
        implicitParents = "["
                + Optional.of(parents).orElse(new HashSet<>())
                .stream().map(role -> String.valueOf(role.getRoleId()))
                .sorted().collect(Collectors.joining(",")) +
                ']';
        return this;
    }

    public Role updateAncestors() {
        this.ancestors.clear();
        this.parents.forEach(parent -> this.ancestors.addAll(parent.ancestors));
        this.ancestors.add(this);
        return this;
    }

    public Role setObjectType(ObjectType objectType) {
        this.objectType = objectType;
        return this;
    }

    public Role setObjectId(Long objectId) {
        this.objectId = objectId;
        return this;
    }

    public Role addParent(Role parent) {
        this.parents.add(parent);
        return this;
    }

    public Role addParents(Role... parents) {
        this.parents.addAll(Arrays.asList(parents));
        return this;
    }

    public Role clear() {
//        for (Role parent : parents) {
//            parent.getChildren().remove(this);
//        }
        for (Role child : children) {
            child.getParents().remove(this);
            child.updateImplicitParents();
        }
//        for (Role ancestor : ancestors) {
//            ancestor.getDescentdents().remove(this);
//        }
        for (Role descentdent : descentdents) {
            descentdent.updateAncestors();
        }
//        for (User user : grants) {
//            user.getGrantedRoles().remove(this);
//        }
//        this.parents.clear();
//        this.children.clear();
//        this.ancestors.clear();
//        this.descentdents.clear();
//        this.grants.clear();
        return this;
    }

}
