package net.waret.demo.photoz.config.init;

import net.waret.demo.photoz.domain.Employee;
import net.waret.demo.photoz.domain.Manager;
import net.waret.demo.photoz.domain.Order;
import net.waret.demo.photoz.domain.Status;
import net.waret.demo.photoz.repository.EmployeeRepository;
import net.waret.demo.photoz.repository.ManagerRepository;
import net.waret.demo.photoz.repository.OrderRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class LoadDatabase {

    @Bean
    public CommandLineRunner initEmployeeDatabase(EmployeeRepository employeeRepository, ManagerRepository managerRepository) {
        return args -> {
            /*
             * Gather Gandalf's team
             */
            Manager gandalf = managerRepository.save(new Manager("Gandalf"));

            Employee frodo = employeeRepository.save(new Employee("Frodo", "Baggins", "ring bearer", gandalf));
            Employee bilbo = employeeRepository.save(new Employee("Bilbo", "Baggins", "burglar", gandalf));

            gandalf.setEmployees(Arrays.asList(frodo, bilbo));
            managerRepository.save(gandalf);

            /*
             * Put together Saruman's team
             */
            Manager saruman = managerRepository.save(new Manager("Saruman"));

            Employee sam = employeeRepository.save(new Employee("Sam", "Baggins", "gardener", saruman));

            saruman.setEmployees(Collections.singletonList(sam));

            managerRepository.save(saruman);
        };
    }

    @Bean
    public CommandLineRunner initOrderDatabase(OrderRepository orderRepository) {
        return args -> {
            orderRepository.save(new Order("MacBook Pro", Status.COMPLETED));
            orderRepository.save(new Order("iPhone", Status.IN_PROGRESS));

            orderRepository.findAll().forEach(order -> log.info("Preloaded " + order));
        };
    }
}