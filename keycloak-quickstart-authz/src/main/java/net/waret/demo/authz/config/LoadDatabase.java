package net.waret.demo.authz.config;

import net.waret.demo.authz.service.PreloadDatabaseService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
class LoadDatabase {

    @Bean
    public CommandLineRunner initEmployeeDatabase(PreloadDatabaseService preloadDatabaseService) {
        return args -> preloadDatabaseService.preloadData();
    }

}