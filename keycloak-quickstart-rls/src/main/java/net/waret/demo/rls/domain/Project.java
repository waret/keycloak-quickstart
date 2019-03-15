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
@Table(name = "rls_projects")
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Project {

    public static final String ADMIN_ROLE   = "admin_role";
    public static final String USE_ROLE     = "use_role";
    public static final String UPDATE_ROLE  = "update_role";
    public static final String READ_ROLE    = "read_role";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    @EqualsAndHashCode.Include
    private Long projectId;

    @Column(name = "display_name")
    @EqualsAndHashCode.Include
    private String displayName;

    @Column(name = "admin_role_id")
    private Long adminRoleId;

    @Column(name = "use_role_id")
    private Long useRoleId;

    @Column(name = "update_role_id")
    private Long updateRoleId;

    @Column(name = "read_role_id")
    private Long readRoleId;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    public Project setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

}
