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
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "authz_user")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @EqualsAndHashCode.Include
    private String username;

    private Resource domain;

    @ManyToMany
    @JoinTable(name = "authz_role_grants",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> grantedRoles = new HashSet<>();

    @ManyToMany(mappedBy = "joinedUser")
    private Set<Group> joiningGroups = new HashSet<>();

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public User setDomain(Resource domain) {
        this.domain = domain;
        return this;
    }

}
