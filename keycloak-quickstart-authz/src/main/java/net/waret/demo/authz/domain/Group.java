package net.waret.demo.authz.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "authz_group")
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    @EqualsAndHashCode.Include
    private Long groupId;

    private String groupName;

    @OneToOne
    private Resource resource;

    @ManyToMany
    @JoinTable(name = "authz_user_joins_team",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> joinedUser;

    public Group setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public Group setResource(Resource resource) {
        this.resource = resource;
        return this;
    }

}
