package com.dev.sweproject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;



/**
 * Driver for the application.
 */

@SpringBootApplication
@RestController
public class SweProjectApplication {


	private static FirebaseService firebaseDataService;

	/**
	 * Driver method.
	 *
	 * @param args arguments for main
	 */
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(
				            SweProjectApplication.class, args);

		firebaseDataService = context.getBean(FirebaseService.class);
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

	/**
	 * Post Mapping that registers the client in the database so they may
	 * perform different operations, only needs to run once in the lifetime
	 * of the client.
	 *
	 * @return TODO
	 * @throws JsonProcessingException
	 */
	@PostMapping(value="/register-client", produces = MediaType.APPLICATION_JSON_VALUE)
	public String registerClient() throws JsonProcessingException {
		String network_id = firebaseDataService.generateNetworkId();
		firebaseDataService.createCollection(network_id);
		ObjectMapper om = new ObjectMapper();
		return om.writeValueAsString(new RegisterClientResponse(network_id));
	}

	/**
	 * Post Mapping that uploads documents to the database
	 * documents are wrapped in the Document object prior
	 * to being placed in the database.
	 *
	 * @param networkId A String representing the network the client
	 *                  belongs to.
	 *
	 * @param documentName A String representing the name of the
	 *                     document they plan to upload.
	 *
	 * @param contents A MultipartFile representing the file they
	 *                 plan to upload.
	 * @return A String detailing whether the file was successfully added or not
	 *
	 * @throws JsonProcessingException
	 */
	@PostMapping(value = "/upload-doc")
	public String uploadDoc(@RequestParam(value = "network-id") String networkId,
						  @RequestParam(value = "document-name") String documentName,
							@RequestParam(value = "user-id") String userId,
						  @RequestBody MultipartFile contents) throws JsonProcessingException {
		ObjectMapper om = new ObjectMapper();
		try {
			CompletableFuture<Object> uploadResult = firebaseDataService.uploadFile(contents, networkId, documentName, userId);
			uploadResult.get();
			return om.writeValueAsString("File uploaded successfully");
		} catch (Exception e) {
			e.printStackTrace();
			return om.writeValueAsString("File didn't upload");
		}
	}

	@GetMapping(value = "/download-doc", produces = MediaType.APPLICATION_JSON_VALUE)
	public String downloadDoc(@RequestParam(value = "network-id", required = true) String networkId,
							  @RequestParam(value = "document-name", required = true) String documentName)
			throws JsonProcessingException {
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

	/**
	 * Searches the client's network for a specific document
	 * if the document exists it is returned to the client
	 * otherwise a simple message stating otherwise is
	 * provided.
	 *
	 * @param networkId A String representing the client's network
	 * Id.
	 *
	 * @param documentName A String representing the name of the
	 * document that the client is looking for.
	 *
	 * @return A JSON object serialized as a String
	 * @throws com.fasterxml.jackson.core.JsonProcessingException
	 */
	@GetMapping(value = "/check-for-doc", produces = MediaType.APPLICATION_JSON_VALUE)
	public String checkForDoc(@RequestParam(value = "network-id", required = true) String networkId,
							  @RequestParam(value = "document-name", required = true) String documentName)
			throws JsonProcessingException {

		CompletableFuture<DataSnapshot> result = firebaseDataService.searchForDocument(networkId, documentName);
		Object response = new Object();
		try {
			DataSnapshot dataSnapshot = result.get();
			if(dataSnapshot.exists()) {
				response = dataSnapshot.getValue();
			}
		} catch (Exception e) {
			response = "no such document exists";
		}

		ObjectMapper om = new ObjectMapper();
		return om.writeValueAsString(response);
	}
}