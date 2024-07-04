package com.epam.jmp.spring.task3;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Task3Application implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Task3Application.class, args);
	}

	@Override
	public void run(String... args) {
		System.out.println("Hello World's from Task3 Application!");
	}

}
