package net.waret.demo.photoz.web.assembler;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;

public interface ResourcesAssembler<T, D extends ResourceSupport> {

    /**
     * Converts all given entities into resources and wraps the collection as a resource as well.
     *
     * @see ResourceAssembler#toResource(Object)
     * @param entities must not be {@literal null}.
     * @return {@link Resources} containing {@link Resource} of {@code T}.
     */
    Resources<D> toResources(Iterable<? extends T> entities);

}
