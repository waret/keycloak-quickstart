package net.waret.demo.rls.web.rest;

import net.waret.demo.rls.domain.Team;
import net.waret.demo.rls.domain.User;
import net.waret.demo.rls.service.UserRoleService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class HelloResource {

    private final UserRoleService userRoleService;

    public HelloResource(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @GetMapping(value = "/test")
    public ResponseEntity test() {
        User user = userRoleService.addUser("waret", userRoleService.getDefaultOrganization().getOrganizationId());
        log.debug("add user: {}", user);
        userRoleService.grantRole(user.getUserId(), userRoleService.getDefaultOrganization().getAdminRole().getRoleId());
        Team team = userRoleService.addTeam("TeamA", userRoleService.getDefaultOrganization().getOrganizationId(), user.getUserId());
        log.debug("add team: {}", team);
        return ResponseEntity.status(HttpStatus.OK).body("test");
    }

    @GetMapping(value = "/delete")
    public ResponseEntity delete() {
        return ResponseEntity.status(HttpStatus.OK).body("delete");
    }

}
