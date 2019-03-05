package net.waret.demo.photoz.web.assembler;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.Resources;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class SimpleResourceAssembler<T> implements ResourceAssembler<T, Resource<T>>, ResourcesAssembler<T, Resource<T>> {

    /**
     * Converts the given entity into a {@link Resource}.
     *
     * @param entity
     * @return
     */
    @Override
    public Resource<T> toResource(T entity) {

        Resource<T> resource = new Resource<T>(entity);

        addLinks(resource);

        return resource;
    }

    /**
     * Converts all given entities into resources and wraps the collection as a resource as well.
     *
     * @see #toResource(Object)
     * @param entities must not be {@literal null}.
     * @return {@link Resources} containing {@link Resource} of {@code T}.
     */
    public Resources<Resource<T>> toResources(Iterable<? extends T> entities) {

        Assert.notNull(entities, "Entities must not be null!");
        List<Resource<T>> result = new ArrayList<Resource<T>>();

        for (T entity : entities) {
            result.add(toResource(entity));
        }

        Resources<Resource<T>> resources = new Resources<>(result);

        addLinks(resources);

        return resources;
    }

    /**
     * Define links to add to every individual {@link Resource}.
     *
     * @param resource
     */
    protected void addLinks(Resource<T> resource) {
        // Default adds no links
    }

    /**
     * Define links to add to the {@link Resources} collection.
     *
     * @param resources
     */
    protected void addLinks(Resources<Resource<T>> resources) {
        // Default adds no links.
    }
}

