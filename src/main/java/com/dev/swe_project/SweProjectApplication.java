package com.dev.swe_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SweProjectApplication {

	public static void main(String[] args) {

		ApplicationContext context = SpringApplication.run(SweProjectApplication.class, args);


		FirebaseDBService firebaseDatabaseService = context.getBean(FirebaseDBService.class);

		//Testing database methods
		//firebaseDatabaseService.createCollection("test-network");

		//firebaseDatabaseService.addEntry("test-network", "name", "Griffin");
		//firebaseDatabaseService.updateEntry("test-network", "name", "Jeannie");
		//firebaseDatabaseService.removeEntry("test-network", "name");


	}

	@GetMapping("/hello")
	public String sayHello(@RequestParam(value = "myName", defaultValue = "World") String name){
		return String.format("Hello %s!", name);
	}

}
