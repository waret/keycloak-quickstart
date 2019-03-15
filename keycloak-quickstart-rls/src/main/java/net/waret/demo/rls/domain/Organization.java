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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "rls_organization")
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Organization {

    public static final String ADMIN_ROLE           = "admin_role";
    public static final String AUDITOR_ROLE         = "auditor_role";
    public static final String MEMBER_ROLE          = "member_role";
    public static final String READ_ROLE            = "read_role";
    public static final String PROJECT_ADMIN_ROLE   = "project_admin_role";
    public static final String REPO_ADMIN_ROLE      = "repo_admin_role";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_id")
    @EqualsAndHashCode.Include
    private Long organizationId;

    @EqualsAndHashCode.Include
    private String displayName;

    private String description;

    @OneToOne
    @JoinColumn(name = "created_by_id")
    private User createdByUser;

    @OneToOne
    @JoinColumn(name = "modified_by_id")
    private User modifiedByUser;

    @OneToOne
    @JoinColumn(name = "admin_role_id")
    private Role adminRole;

    @OneToOne
    @JoinColumn(name = "auditor_role_id")
    private Role auditorRole;

    @OneToOne
    @JoinColumn(name = "member_role_id")
    private Role memberRole;

    @OneToOne
    @JoinColumn(name = "read_role_id")
    private Role readRole;

    @OneToOne
    @JoinColumn(name = "project_admin_role_id")
    private Role projectAdminRole;

    @OneToOne
    @JoinColumn(name = "repo_admin_role_id")
    private Role repoAdminRole;

    @ManyToMany
    @ToString.Exclude
    @JoinTable(name = "rls_user_joins_organization",
            joinColumns = @JoinColumn(name = "organization_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> joinedUser;

    @ToString.Exclude
    @OneToMany(mappedBy = "organization")
    private Set<Team> teams = new HashSet<>();

    public Organization setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public Organization setDescription(String description) {
        this.description = description;
        return this;
    }

    public Organization setCreatedByUser(User user) {
        this.createdByUser = user;
        return this;
    }

    public Organization setModifiedByUser(User user) {
        this.modifiedByUser = user;
        return this;
    }

}
