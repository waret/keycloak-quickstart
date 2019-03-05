package net.waret.demo.photoz.web.assembler;

import org.springframework.core.GenericTypeResolver;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.LinkBuilder;
import org.springframework.hateoas.RelProvider;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.EvoInflectorRelProvider;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

public class SimpleIdentifiableResourceAssembler<T extends Identifiable<?>> extends SimpleResourceAssembler<T> {

    /**
     * The Spring MVC class for the {@link Identifiable} from which links will be built.
     */
    private final Class<?> controllerClass;

    /**
     * A {@link RelProvider} to look up names of links as options for resource paths.
     */
    private final RelProvider relProvider;

    /**
     * A {@link Class} depicting the {@link Identifiable}'s type.
     */
    private final Class<?> resourceType;

    /**
     * Default base path as empty.
     */
    private String basePath = "";

    /**
     * Default a assembler based on Spring MVC controller, resource type, and {@link RelProvider}. With this combination
     * of information, resources can be defined.
     *
     * @see #setBasePath(String) to adjust base path to something like "/api"/
     *
     * @param controllerClass - Spring MVC controller to base links off of
     * @param relProvider
     */
    public SimpleIdentifiableResourceAssembler(Class<?> controllerClass, RelProvider relProvider) {

        this.controllerClass = controllerClass;
        this.relProvider = relProvider;

        // Find the "T" type contained in "T extends Identifiable<?>", e.g. SimpleIdentifiableResourceAssembler<User> -> User
        this.resourceType = GenericTypeResolver.resolveTypeArgument(this.getClass(), SimpleIdentifiableResourceAssembler.class);
    }

    /**
     * Alternate constructor that falls back to {@link EvoInflectorRelProvider}.
     *
     * @param controllerClass
     */
    public SimpleIdentifiableResourceAssembler(Class<?> controllerClass) {
        this(controllerClass, new EvoInflectorRelProvider());
    }

    /**
     * Define links to add to every {@link Resource}.
     *
     * @param resource
     */
    @Override
    protected void addLinks(Resource<T> resource) {

        resource.add(getCollectionLinkBuilder().slash(resource.getContent()).withSelfRel());
        resource.add(getCollectionLinkBuilder().withRel(this.relProvider.getCollectionResourceRelFor(this.resourceType)));
    }

    /**
     * Define links to add to {@link Resources} collection.
     *
     * @param resources
     */
    @Override
    protected void addLinks(Resources<Resource<T>> resources) {
        resources.add(getCollectionLinkBuilder().withSelfRel());
    }

    /**
     * Build up a URI for the collection using the Spring MVC controller followed by the resource type transformed
     * by the {@link RelProvider}.
     *
     * Assumption is that an {@link net.waret.demo.photoz.web.rest.employee.EmployeeResource} serving up {@link net.waret.demo.photoz.web.rest.employee.EmployeeResource}
     * objects will be serving resources at {@code /employees} and {@code /employees/1}.
     *
     * If this is not the case, simply override this method in your concrete instance, or simply resort to
     * overriding {@link #addLinks(Resource)} and {@link #addLinks(Resources)} where you have full control over exactly
     * what links are put in the individual and collection resources.
     *
     * @return
     */
    protected LinkBuilder getCollectionLinkBuilder() {

        ControllerLinkBuilder linkBuilder = linkTo(this.controllerClass);

        for (String pathComponent : (getPrefix() + this.relProvider.getCollectionResourceRelFor(this.resourceType)).split("/")) {
            if (!pathComponent.isEmpty()) {
                linkBuilder = linkBuilder.slash(pathComponent);
            }
        }

        return linkBuilder;
    }

    private String getPrefix() {
        return getBasePath().isEmpty() ? "" : getBasePath() + "/";
    }

    public String getBasePath() {
        return this.basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }


}
