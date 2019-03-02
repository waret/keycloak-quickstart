package net.waret.demo.charlie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class CharlieApplication {

	public static void main(String[] args) {
        SpringApplication.run(CharlieApplication.class, args);
	}

}
