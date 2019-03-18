package net.waret.demo.rls.domain;

import java.util.Date;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "rls_team")
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Team {

    public static final String ADMIN_ROLE   = "admin_role";
    public static final String MEMBER_ROLE  = "member_role";
    public static final String READ_ROLE    = "read_role";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    @EqualsAndHashCode.Include
    private Long teamId;

    @EqualsAndHashCode.Include
    private String name;

    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
//    @Column(name="created", columnDefinition="TIMESTAMP DEFAULT CURRENT_DATE")
//    @Column(name="created", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIME")
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="modified", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date modified;

    @OneToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @OneToOne
    @JoinColumn(name = "modified_by_id")
    private User modifiedBy;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @OneToOne
    @JoinColumn(name = "admin_role_id")
    private Role adminRole;

    @OneToOne
    @JoinColumn(name = "member_role_id")
    private Role memberRole;

    @OneToOne
    @JoinColumn(name = "read_role_id")
    private Role readRole;

    @ManyToMany
    @JoinTable(name = "rls_user_joins_team",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> joinedUser;

    public Team setName(String name) {
        this.name = name;
        return this;
    }

    public Team setOrganization(Organization organization) {
        this.organization = organization;
        return this;
    }

    public Team setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public Team setModifiedBy(User modifiedBy) {
        this.modifiedBy = modifiedBy;
        return this;
    }

    public Team clear() {
        for (User user : joinedUser) {
            user.getJoiningTeams().remove(this);
        }
        this.joinedUser.clear();
        return this;
    }

}
