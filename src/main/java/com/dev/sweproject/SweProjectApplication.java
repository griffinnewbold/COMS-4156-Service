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
	private static FirebaseService firebaseDataService;


	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(
				            SweProjectApplication.class, args);

		FirebaseService firebaseDatabaseService = context.getBean(FirebaseService.class);
		firebaseDataService = firebaseDatabaseService;
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

	@PostMapping(value="/register-client", produces = MediaType.APPLICATION_JSON_VALUE)
	public String registerClient() throws com.fasterxml.jackson.core.JsonProcessingException {
		String network_id = firebaseDataService.generateNetworkId();
		firebaseDataService.createCollection(network_id);
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