package net.waret.demo.photoz.web.rest.employee;

import net.waret.demo.photoz.domain.Employee;
import net.waret.demo.photoz.domain.Manager;
import net.waret.demo.photoz.repository.ManagerRepository;
import net.waret.demo.photoz.web.assembler.ManagerResourceAssembler;

import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ManagerResource {

    private final ManagerRepository repository;
    private final ManagerResourceAssembler assembler;

    public ManagerResource(ManagerRepository repository, ManagerResourceAssembler assembler) {

        this.repository = repository;
        this.assembler = assembler;
    }

    /**
     * Look up all managers, and transform them into a REST collection resource using
     * {@link ManagerResourceAssembler#toResources(Iterable)}. Then return them through
     * Spring Web's {@link ResponseEntity} fluent API.
     *
     * NOTE: cURL will fetch things as HAL JSON directly, but browsers issue a different
     * default accept header, which allows XML to get requested first, so "produces"
     * forces it to HAL JSON for all clients.
     */
    @GetMapping(value = "/managers", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<Resources<Resource<Manager>>> findAll() {
        return ResponseEntity.ok(
                assembler.toResources(repository.findAll()));

    }
    /**
     * Look up a single {@link Manager} and transform it into a REST resource using
     * {@link ManagerResourceAssembler#toResource(Object)}. Then return it through
     * Spring Web's {@link ResponseEntity} fluent API.
     *
     * See {@link #findAll()} to explain {@link GetMapping}'s "produces" argument.
     *
     * @param id id
     */
    @GetMapping(value = "/managers/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<Resource<Manager>> findOne(@PathVariable long id) {
        return repository.findById(id).map(manager -> ResponseEntity.ok(assembler.toResource(manager)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Find an {@link Employee}'s {@link Manager} based upon employee id. Turn it into a context-based link.
     *
     * @param id id
     * @return ResponseEntity
     */
    @GetMapping(value = "/employees/{id}/manager", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<Resource<Manager>> findManager(@PathVariable long id) {
        return ResponseEntity.ok(
                assembler.toResource(repository.findByEmployeesId(id)));
    }


}
