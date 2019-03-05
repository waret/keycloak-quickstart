package net.waret.demo.photoz.config.init;

import net.waret.demo.photoz.domain.Employee;
import net.waret.demo.photoz.repository.EmployeeRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(EmployeeRepository repository) {
        return args -> {
            log.info("Preloading " + repository.save(new Employee("Bilbo Baggins", "burglar", "ADMIN")));
            log.info("Preloading " + repository.save(new Employee("Frodo Baggins", "thief", "USER")));
        };
    }
}