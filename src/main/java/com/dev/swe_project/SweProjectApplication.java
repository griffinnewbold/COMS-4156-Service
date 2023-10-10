package com.dev.swe_project;

import com.google.firebase.FirebaseApp;
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


		// Initialize the Spring application context
		ApplicationContext context = SpringApplication.run(SweProjectApplication.class, args);

		// Get an instance of the FirebaseDatabaseService from the context
		FirebaseDBService firebaseDatabaseService = context.getBean(FirebaseDBService.class);

		// Execute the methods to add and remove data
		firebaseDatabaseService.addData("exampleKey", "exampleValue");

	}

	@GetMapping("/hello")
	public String sayHello(@RequestParam(value = "myName", defaultValue = "World") String name){
		return String.format("Hello %s!", name);
	}

}
