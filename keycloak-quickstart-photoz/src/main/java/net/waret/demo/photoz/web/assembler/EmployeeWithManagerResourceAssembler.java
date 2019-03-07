package net.waret.demo.photoz.web.assembler;

import net.waret.demo.photoz.service.dto.EmployeeWithManager;
import net.waret.demo.photoz.web.rest.RootResource;
import net.waret.demo.photoz.web.rest.employee.EmployeeResource;
import net.waret.demo.photoz.web.rest.employee.ManagerResource;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class EmployeeWithManagerResourceAssembler extends SimpleResourceAssembler<EmployeeWithManager> {

    /**
     * Define links to add to every individual {@link Resource}.
     *
     * @param resource resource
     */
    @Override
    protected void addLinks(Resource<EmployeeWithManager> resource) {

        resource.add(linkTo(methodOn(EmployeeResource.class).findDetailedEmployee(resource.getContent().getId())).withSelfRel());
        resource.add(linkTo(methodOn(EmployeeResource.class).one(resource.getContent().getId())).withRel("summary"));
        resource.add(linkTo(methodOn(EmployeeResource.class).findAllDetailedEmployees()).withRel("detailedEmployees"));
    }

    /**
     * Define links to add to the {@link Resources} collection.
     *
     * @param resources resources
     */
    @Override
    protected void addLinks(Resources<Resource<EmployeeWithManager>> resources) {
        resources.add(linkTo(methodOn(EmployeeResource.class).findAllDetailedEmployees()).withSelfRel());
        resources.add(linkTo(methodOn(EmployeeResource.class).all()).withRel("employees"));
        resources.add(linkTo(methodOn(ManagerResource.class).findAll()).withRel("managers"));
        resources.add(linkTo(methodOn(RootResource.class).root()).withRel("root"));
    }

}
