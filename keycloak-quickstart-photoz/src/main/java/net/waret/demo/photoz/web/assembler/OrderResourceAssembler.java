package net.waret.demo.photoz.web.assembler;

import net.waret.demo.photoz.domain.Order;
import net.waret.demo.photoz.domain.Status;
import net.waret.demo.photoz.web.rest.employee.OrderResource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

@Component
public class OrderResourceAssembler implements ResourceAssembler<Order, Resource<Order>> {

    @Override
    public Resource<Order> toResource(Order order) {

        // Unconditional links to single-item resource and aggregate root

        Resource<Order> orderResource = new Resource<>(order,
                linkTo(methodOn(OrderResource.class).one(order.getId())).withSelfRel(),
                linkTo(methodOn(OrderResource.class).all()).withRel("orders")
        );

        // Conditional links based on state of the order

        if (order.getStatus() == Status.IN_PROGRESS) {
            orderResource.add(
                    linkTo(methodOn(OrderResource.class)
                            .cancel(order.getId())).withRel("cancel"));
            orderResource.add(
                    linkTo(methodOn(OrderResource.class)
                            .complete(order.getId())).withRel("complete"));
        }

        return orderResource;
    }
}