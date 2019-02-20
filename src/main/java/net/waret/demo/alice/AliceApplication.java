package net.waret.demo.alice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class AliceApplication {

	public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(AliceApplication.class, args);

		log.debug("Let's inspect the beans provided by Spring Boot:");

		String[] beanNames = ctx.getBeanDefinitionNames();
		Arrays.sort(beanNames);
		for (String beanName : beanNames) {
			log.debug(beanName);
		}
	}

}
