package com.dev.sweproject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
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

	@GetMapping(value = "/download-doc", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<?> downloadDoc(@RequestParam(value = "network-id") String networkId,
																			 @RequestParam(value = "document-name") String documentName,
																			 @RequestParam(value = "your-user-id") String yourUserId,
																			 @RequestBody(required = false) String jsonObject) {
		if (jsonObject != null) {
			try {
				ObjectMapper om = new ObjectMapper();
				JsonNode myNode = om.readTree(jsonObject);

				String fileString = myNode.get("fileString").asText().substring(1);
				String fileName = myNode.get("title").asText();
				byte[] fileBytes = Base64.getDecoder().decode(fileString);

				ByteArrayResource resource = new ByteArrayResource(fileBytes);

				HttpHeaders responseHeaders = new HttpHeaders();
				responseHeaders.setContentDispositionFormData("attachment", fileName);
				responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);

				return ResponseEntity.ok().headers(responseHeaders).body(resource);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		CompletableFuture<DataSnapshot> result = firebaseDataService.searchForDocument(networkId, documentName);
		HttpHeaders responseHeaders = null;
		ByteArrayResource resource = null;

		try {
			DataSnapshot dataSnapshot = result.get();
			if (dataSnapshot.exists()) {
				Document myDocument = Document.convertToDocument((HashMap<String, Object>) dataSnapshot.getValue());
				if (!myDocument.getUserId().contains(yourUserId)) {
					return new ResponseEntity<>("You do not have ownership of this document", HttpStatus.FORBIDDEN);
				} else {
					byte[] fileBytes = Base64.getDecoder().decode(myDocument.getFileString().substring(1));
					resource = new ByteArrayResource(fileBytes);
					responseHeaders = new HttpHeaders();
					responseHeaders.setContentDispositionFormData("attachment", documentName);
					responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				}
			}
		} catch (IOException e) {
			return new ResponseEntity<>("An unexpected error has occurred", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			return new ResponseEntity<>("No such document exists", HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok().headers(responseHeaders).body(resource);
	}

	@DeleteMapping(value = "/delete-doc")
	public String deleteDoc(@RequestParam(value = "network-id") String networkId,
													@RequestParam(value = "document-name") String documentName,
													@RequestParam(value = "your-user-id") String yourUserId)
												throws JsonProcessingException {

		CompletableFuture<DataSnapshot> result = firebaseDataService.searchForDocument(networkId, documentName);
		Object response = new Object();
		try {
			DataSnapshot dataSnapshot = result.get();
			if (dataSnapshot.exists()) {
				response = dataSnapshot.getValue();
				Document myDocument = Document.convertToDocument((HashMap<String, Object>) response);
				if (!myDocument.getUserId().contains(yourUserId)) {
					response = "Your user does not have ownership of this document";
				} else {
					String documentId = myDocument.getDocId();
					String databaseLocation = networkId + "/" + documentId;
					firebaseDataService.deleteCollection(databaseLocation);
					response = "Your document was successfully deleted";
				}
			}
		} catch (IOException e) {
			response = "An unexpected error has occurred.";
		} catch (Exception e) {
			response = "no such document exists";
		}
		ObjectMapper om = new ObjectMapper();
		return om.writeValueAsString(response);
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
	public String checkForDoc(@RequestParam(value = "network-id")    String networkId,
							  						@RequestParam(value = "document-name") String documentName,
														@RequestParam(value = "your-user-id")  String yourUserId)
														throws JsonProcessingException {

		CompletableFuture<DataSnapshot> result = firebaseDataService.searchForDocument(networkId, documentName);
		Object response = new Object();
		try {
			DataSnapshot dataSnapshot = result.get();
			if(dataSnapshot.exists()) {
				response = dataSnapshot.getValue();
				Document myDocument = Document.convertToDocument((HashMap<String, Object>) response);
				if (!myDocument.getUserId().contains(yourUserId)) {
					response = "Your user does not have access to this document";
				}
			}
		} catch (Exception e) {
			response = "No such document exists";
		}

		ObjectMapper om = new ObjectMapper();
		return om.writeValueAsString(response);
	}

	@GetMapping(value = "/see-previous-version", produces = MediaType.APPLICATION_JSON_VALUE)
	public String seePreviousVersion(@RequestParam(value = "network-id")      String networkId,
																	 @RequestParam(value = "document-name")   String documentName,
																	 @RequestParam(value = "your-user-id")    String yourUserId,
																	 @RequestParam(value = "revision-number") int revisionNumber)
			                             throws JsonProcessingException {

		CompletableFuture<DataSnapshot> result = firebaseDataService.searchForDocument(networkId, documentName);
		Object response = new Object();
		try {
			DataSnapshot dataSnapshot = result.get();
			if(dataSnapshot.exists()) {
				response = dataSnapshot.getValue();
				Document myDocument = Document.convertToDocument((HashMap<String, Object>) response);
				if (!myDocument.getUserId().contains(yourUserId)) {
					return "Your user does not have access to this document";
				}
				if (revisionNumber <= 0 || revisionNumber >= myDocument.getPreviousVersions().size()) {
					return "This is not a valid revision number";
				}
				response = myDocument.getPreviousVersions().get(revisionNumber);
			}
		} catch (Exception e) {
			return "No such document exists";
		}
		ObjectMapper om = new ObjectMapper();
		return om.writeValueAsString(response);
	}

	@PatchMapping(value = "/share-document", produces = MediaType.APPLICATION_JSON_VALUE)
	public String shareDocument(@RequestParam(value = "network-id") String networkId,
															@RequestParam(value = "document-name") String documentName,
															@RequestParam(value = "your-user-id") String yourUserId,
															@RequestParam(value = "their-user-id") String theirUserId)
															throws JsonProcessingException {

		CompletableFuture<DataSnapshot> result = firebaseDataService.searchForDocument(networkId, documentName);
		Object response = new Object();
		try {
			DataSnapshot dataSnapshot = result.get();
			if (dataSnapshot.exists()) {
				response = dataSnapshot.getValue();
				Document myDocument = Document.convertToDocument((HashMap<String, Object>) response);
				if (!myDocument.getUserId().contains(yourUserId)) {
					response = "Your user does not have access to this document";
				} else if (myDocument.getUserId().contains(yourUserId) && myDocument.getUserId().contains(theirUserId)) {
					response = "This document has already been shared with the desired user";
				} else {
					String newIds = myDocument.getUserId() + "/" + theirUserId;
					String collectionToUpdate = myDocument.getClientId() + "/" + myDocument.getDocId();
					firebaseDataService.updateEntry(collectionToUpdate, "userId", newIds);
					response = "The document has been shared with the desired user";
				}
			}
		} catch (IOException e) {
			response = "An unexpected error has occurred.";
		} catch (Exception e) {
			response = "no such document exists";
		}
		ObjectMapper om = new ObjectMapper();
		return om.writeValueAsString(response);
	}

	@GetMapping(value = "/see-document-stats", produces = MediaType.APPLICATION_JSON_VALUE)
	public String seeDocumentStats(@RequestParam(value = "network-id") String networkId,
																 @RequestParam(value = "document-name") String documentName,
																 @RequestParam(value = "your-user-id") String yourUserId)
		                             throws JsonProcessingException {

		CompletableFuture<DataSnapshot> result = firebaseDataService.searchForDocument(networkId, documentName);
		Object response = new Object();
		try {
			DataSnapshot dataSnapshot = result.get();
			if (dataSnapshot.exists()) {
				response = dataSnapshot.getValue();
				Document myDocument = Document.convertToDocument((HashMap<String, Object>) response);
				if (!myDocument.getUserId().contains(yourUserId)) {
					response = "Your user does not have access to this document";
				} else {
					response = myDocument.generateUsageStatistics();
				}
			}
		} catch (IOException e) {
			response = "An unexpected error has occurred.";
		} catch (Exception e) {
			response = "no such document exists";
		}
		ObjectMapper om = new ObjectMapper();
		return om.writeValueAsString(response);
	}

	@GetMapping(value = "/generate-difference-summary", produces = MediaType.APPLICATION_JSON_VALUE)
	public String generateDifferenceSummary(@RequestParam(value = "network-id") String networkId,
																					@RequestParam(value = "first-document-name") String fstDocumentName,
																					@RequestParam(value = "second-document-name") String sndDocumentName,
																					@RequestParam(value = "your-user-id") String yourUserId)
																					throws JsonProcessingException {

		CompletableFuture<DataSnapshot> resultOne = firebaseDataService.searchForDocument(networkId, fstDocumentName);
		CompletableFuture<DataSnapshot> resultTwo = firebaseDataService.searchForDocument(networkId, sndDocumentName);

		DataSnapshot firstSnapshot = null;
		DataSnapshot secondSnapshot = null;
		Object response = new Object();
		boolean isError = false;

		try {
			firstSnapshot = resultOne.get();
			secondSnapshot = resultTwo.get();

			if (!isError && firstSnapshot.exists() && secondSnapshot.exists()) {

				try {
					Document fstDocument = Document.convertToDocument((HashMap<String, Object>) firstSnapshot.getValue());
					Document sndDocument = Document.convertToDocument((HashMap<String, Object>) secondSnapshot.getValue());

					if (!fstDocument.getUserId().contains(yourUserId) || !sndDocument.getUserId().contains(yourUserId)) {
						response = "Your user does not have access to one of the documents";
					} else {
						response = fstDocument.compareTo(sndDocument);
					}
				} catch (IOException e) {
					response = "An unexpected error has occurred.";
				}

			}
		} catch (Exception e) {
			response = "One or more of the documents does not exist";
		}

		ObjectMapper om = new ObjectMapper();
		return om.writeValueAsString(response);
	}

}