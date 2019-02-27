package net.waret.demo.bravo.web.rest;

import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@Validated
@RestController
public class GreetingController {

    @GetMapping("/greetings")
    public String greeting(@NotNull Model model, @NotNull Authentication auth) {
        String email = ((SimpleKeycloakAccount) auth.getDetails())
                .getKeycloakSecurityContext()
                .getToken()
                .getEmail();

        model.addAttribute("name", email);
        return "greetings";
    }

}