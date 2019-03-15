package net.waret.demo.rls.config;

import net.waret.demo.rls.service.UserRoleService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
class LoadDatabase {

    @Bean
    public CommandLineRunner initEmployeeDatabase(UserRoleService userRoleService) {
        return args -> userRoleService.preloadData();
    }

}