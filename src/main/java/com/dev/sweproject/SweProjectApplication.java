package com.dev.sweproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;


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
		firebaseDatabaseService.createCollection("test-network/users");

		firebaseDatabaseService.addEntry("test-network/users", "testDoc",
				new Document("1", null, "012", "the first doc", "txt", 0));
		firebaseDatabaseService.updateEntry("null", "test", null);
		/*
		try {
			CompletableFuture<Object> resultFuture = firebaseDatabaseService.getEntry("test-network", "testDoc");
			HashMap<String, Object> map = (HashMap<String, Object>) resultFuture.get();
			Document value = Document.convertToDocument(map);
			System.out.println("Retrieved value: " + value);
		} catch (InterruptedException | ExecutionException e) {
			System.out.println("Error while retrieving data: " + e.getMessage());
		}
		//firebaseDatabaseService.updateEntry("test-network", "name", "Jeannie");
		//firebaseDatabaseService.removeEntry("test-network", "name");

		 */
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

	@PostMapping(value = "/register-client", produces = MediaType.APPLICATION_JSON_VALUE)
	public String registerClient() throws com.fasterxml.jackson.core.JsonProcessingException {
		// Generate an 8-character network ID (plus a null terminator)
		int len = 8;
		StringBuilder builder = new StringBuilder(len);
		String characters = "0123456789abcdef";
		for (int i = 0; i < len; i++) {
			int index = (int)(Math.random() * characters.length());
			builder.append(characters.charAt(index));
		}
		String network_id = builder.toString();

		// TODO: register the network_id in the database

		ObjectMapper om = new ObjectMapper();
		return om.writeValueAsString(new RegisterClientResponse(network_id));
	}

	@PostMapping(value = "/upload-doc")
	public void uploadDoc(@RequestParam(value = "network-id", required = true) String networkId,
						  @RequestParam(value = "document-name", required = true) String documentName,
						  @RequestBody MultipartFile contents) {
		// TODO: upload the document to the database
	}

	@GetMapping(value = "/download-doc", produces = MediaType.APPLICATION_JSON_VALUE)
	public String downloadDoc(@RequestParam(value = "network-id", required = true) String networkId,
							  @RequestParam(value = "document-name", required = true) String documentName)
			throws com.fasterxml.jackson.core.JsonProcessingException {
		DownloadDocResponse docsResponse = new DownloadDocResponse();

		// TODO: get the document version(s) from the DB, and use it to populate docsResponse

		ObjectMapper om = new ObjectMapper();
		return om.writeValueAsString(docsResponse);
	}

	@PostMapping(value = "/delete-doc")
	public void deleteDoc(@RequestParam(value = "network-id", required = true) String networkId,
						  @RequestParam(value = "document-name", required = true) String documentName) {
		// TODO: delete the document
	}

	@GetMapping(value = "/check-for-doc", produces = MediaType.APPLICATION_JSON_VALUE)
	public String checkForDoc(@RequestParam(value = "network-id", required = true) String networkId,
							  @RequestParam(value = "document-name", required = true) String documentName)
			throws com.fasterxml.jackson.core.JsonProcessingException {
		CheckForDocResponse response = new CheckForDocResponse(false, 0);

		// TODO: query the DB to see if the document exists, and, if so, how many versions there are.
		//       then, use those fields to populate the response

		ObjectMapper om = new ObjectMapper();
		return om.writeValueAsString(response);
	}
}