package com.miempresa.erpmw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootApplication
public class ErpmwApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErpmwApplication.class, args);
	}

	@Bean
    public CommandLineRunner getMappings(ApplicationContext applicationContext) {
        return args -> {
            System.out.println("---- MAPPINGS REGISTRADOS POR SPRING MVC ----");
            RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
            mapping.getHandlerMethods().forEach((key, value) -> System.out.println(key + " " + value));
            System.out.println("-------------------------------------------");
        };
    }
}