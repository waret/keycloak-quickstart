package net.waret.demo.photoz.web.assembler;

import net.waret.demo.photoz.domain.Manager;
import net.waret.demo.photoz.web.rest.employee.RootResource;
import net.waret.demo.photoz.web.rest.employee.EmployeeResource;
import net.waret.demo.photoz.web.rest.employee.ManagerResource;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ManagerResourceAssembler extends SimpleIdentifiableResourceAssembler<Manager> {

    public ManagerResourceAssembler() {
        super(ManagerResource.class);
    }

    /**
     * Retain default links provided by {@link SimpleIdentifiableResourceAssembler}, but add extra ones to each {@link Manager}.
     *
     * @param resource resource
     */
    @Override
    protected void addLinks(Resource<Manager> resource) {
        // Retain default links.
        super.addLinks(resource);

        // Add custom link to find all managed employees
        resource.add(linkTo(methodOn(EmployeeResource.class).findEmployees(resource.getContent().getId())).withRel("employees"));
    }

    /**
     * Retain default links for the entire collection, but add extra custom links for the {@link Manager} collection.
     *
     * @param resources resource
     */
    @Override
    protected void addLinks(Resources<Resource<Manager>> resources) {

        super.addLinks(resources);

        resources.add(linkTo(methodOn(EmployeeResource.class).all()).withRel("employees"));
        resources.add(linkTo(methodOn(EmployeeResource.class).findAllDetailedEmployees()).withRel("detailedEmployees"));
        resources.add(linkTo(methodOn(RootResource.class).root()).withRel("root"));
    }

}
