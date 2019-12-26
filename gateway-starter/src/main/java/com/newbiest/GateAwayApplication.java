package com.newbiest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = LiquibaseAutoConfiguration.class)
@ComponentScan(basePackages = "com.newbiest")
public class GateAwayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GateAwayApplication.class, args);
	}

}

