package com.dev.sweproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Driver for the application.
 */

@SpringBootApplication
@RestController
public class SweProjectApplication {

	/**
	 * Driver method.
	 *
	 * @param args arguments for main
	 */
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(
				            SweProjectApplication.class, args);

		FirebaseService firebaseDatabaseService = context.getBean(FirebaseService.class);

		//Testing DB methods
		// firebaseDatabaseService.createCollection("test-network");

		//firebaseDatabaseService.addEntry("test-network", "name", "Griffin");
		//firebaseDatabaseService.updateEntry("test-network", "name", "Jeannie");
		//firebaseDatabaseService.removeEntry("test-network", "name");
	}

	/**
	 * Tester for apis.
	 *
	 * @param name to display to the console
	 * @return the formatted string
	 */
	@GetMapping("/hello")
	public String sayHello(@RequestParam(value = "myName", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}
}