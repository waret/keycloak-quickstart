package net.waret.demo.rls.domain;

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
import lombok.ToString;

@Data
@Entity
@Table(name = "rls_user")
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @EqualsAndHashCode.Include
    private Long userId;

    @EqualsAndHashCode.Include
    private String username;

    @ManyToMany
    @JoinTable(name = "rls_role_grants",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> grantedRoles = new HashSet<>();

    @ToString.Exclude
    @ManyToMany(mappedBy = "joinedUser")
    private Set<Organization> joiningOrganizations;

    @ToString.Exclude
    @ManyToMany(mappedBy = "joinedUser")
    private Set<Team> joiningTeams = new HashSet<>();

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public User addOrganization(Organization organization) {
        this.joiningOrganizations.add(organization);
        return this;
    }

    public User addRole(Role role) {
        this.grantedRoles.add(role);
        return this;
    }

    public User clear() {
        for (Role role : this.grantedRoles) {
            role.getGrants().remove(this);
        }
        this.grantedRoles.clear();
        for (Team team : this.joiningTeams) {
            team.getJoinedUser().remove(this);
        }
        this.joiningTeams.clear();
        return this;
    }

}
