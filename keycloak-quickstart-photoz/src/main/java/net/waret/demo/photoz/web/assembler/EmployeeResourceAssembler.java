package net.waret.demo.photoz.web.assembler;

import net.waret.demo.photoz.domain.Employee;
import net.waret.demo.photoz.web.rest.employee.EmployeeResource;

import org.springframework.stereotype.Component;

@Component
public class EmployeeResourceAssembler extends SimpleIdentifiableResourceAssembler<Employee> {

    /**
     * Link the {@link Employee} domain type to the {@link EmployeeResource} using this
     * {@link SimpleIdentifiableResourceAssembler} in order to generate both {@link org.springframework.hateoas.Resource}
     * and {@link org.springframework.hateoas.Resources}.
     */
    EmployeeResourceAssembler() {
        super(EmployeeResource.class);
    }

}
