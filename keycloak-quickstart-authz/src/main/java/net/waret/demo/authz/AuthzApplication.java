package net.waret.demo.authz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class AuthzApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthzApplication.class, args);
    }

}
