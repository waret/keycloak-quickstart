package net.waret.demo.authz.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "authz_resource")
@NoArgsConstructor
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id")
    private Long resourceId;

    @OneToOne
    @JoinColumn(name = "parent_resource_id")
    private Resource parentResource;

    private String resourceName;

    private String resourceType;

    @OneToMany(mappedBy = "resource")
    private Set<Role> roles = new HashSet<>();

    public Resource setResourceType(String resourceType) {
        this.resourceType = resourceType;
        return this;
    }

    public Resource setResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }

    public Resource setParentResource(Resource parentResource) {
        this.parentResource = parentResource;
        return this;
    }

    public Resource setResourceId(Long resourceId) {
        this.resourceId = resourceId;
        return this;
    }

}
