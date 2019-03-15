package net.waret.demo.rls.web.rest;

import net.waret.demo.rls.domain.test.Post;
import net.waret.demo.rls.domain.test.Role2;
import net.waret.demo.rls.domain.test.Tag;
import net.waret.demo.rls.domain.test.User2;
import net.waret.demo.rls.repository.PostRepository;
import net.waret.demo.rls.repository.test.Role2Repository;
import net.waret.demo.rls.repository.TagRepository;
import net.waret.demo.rls.repository.test.User2Repository;
import net.waret.demo.rls.service.UserRoleService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;

import lombok.extern.slf4j.Slf4j;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
public class TestResource {

    private final User2Repository user2Repository;

    private final Role2Repository role2Repository;

    private final TagRepository tagRepository;

    private final PostRepository postRepository;

    public TestResource(User2Repository user2Repository,
            Role2Repository role2Repository,
            TagRepository tagRepository,
            PostRepository postRepository) {
        this.user2Repository = user2Repository;
        this.role2Repository = role2Repository;
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
    }

    @GetMapping(value = "/test-client-ip", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity testClientIp() {
        return ResponseEntity.status(HttpStatus.OK).body("test-client-ip");
    }

    @GetMapping(value = "/test2", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity test2() {

//        User2 user2 = new User2("waret");
//        Role2 role2 = new Role2("admin");
//        user2.getGrantedRoles().add(role2);
//        role2.getGrants().add(user2);
//        user2Repository.save(user2);

        user2Repository.save(new User2("waret", new HashSet<Role2>() {{
            add(new Role2("ADMIN"));
        }}));

        user2Repository.save(new User2("waret", new HashSet<Role2>() {{
            add(new Role2("ADMIN"));
        }}));

        return ResponseEntity.status(HttpStatus.OK).body("test2");
    }

    @GetMapping(value = "/test4", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity test4() {
        user2Repository.deleteById(1L);
        return ResponseEntity.status(HttpStatus.OK).body("test4");
    }

    @GetMapping(value = "/test5", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity test5() {
        role2Repository.deleteById(2L);
        return ResponseEntity.status(HttpStatus.OK).body("test4");
    }

    @GetMapping(value = "/test3", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity test3() {

        {
            postRepository.deleteAllInBatch();
            tagRepository.deleteAllInBatch();

            Post post = new Post("Hibernate Many to Many Example with Spring Boot",
                    "Learn how to map a many to many relationship using hibernate",
                    "Entire Post content with Sample code");

            Tag tag1 = new Tag("Spring Boot");
            Tag tag2 = new Tag("Hibernate");

            post.getTags().add(tag1);
            post.getTags().add(tag2);

            tag1.getPosts().add(post);
            tag2.getPosts().add(post);

            postRepository.save(post);
        }

        return ResponseEntity.status(HttpStatus.OK).body("test3");
    }

}
