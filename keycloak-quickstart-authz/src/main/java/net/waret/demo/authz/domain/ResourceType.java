package net.waret.demo.authz.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "authz_resource_type")
public class ResourceType {

    @Id
    private String resourceType;

    public ResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

}
