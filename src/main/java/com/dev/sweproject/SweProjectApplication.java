package com.dev.sweproject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * The main driver class for the application.
 */
@SpringBootApplication
@RestController
public class SweProjectApplication {

  private static FirebaseService firebaseDataService;

  /**
   * The main entry point for the application.
   *
   * @param args The command-line arguments
   */
  public static void main(String[] args) {
    ApplicationContext context = SpringApplication.run(
        SweProjectApplication.class, args);

    firebaseDataService = context.getBean(FirebaseService.class);
  }

  /**
   * Registers the client in the database to enable various operations. This needs to run
   * once in the client's lifetime.
   *
   * @return A JSON response indicating the successful registration with a network Id.
   * @throws JsonProcessingException If there's an issue processing JSON data
   */
  @PostMapping(value = "/register-client", produces = MediaType.APPLICATION_JSON_VALUE)
  public String registerClient() throws JsonProcessingException {
    try {
      String networkId = firebaseDataService.generateNetworkId();
      firebaseDataService.createCollection(networkId);
      ObjectMapper om = new ObjectMapper();
      return om.writeValueAsString(new RegisterClientResponse(networkId));
    } catch (JsonProcessingException e) {
      System.out.println(e.getMessage());
      return new ObjectMapper().writeValueAsString("An unexpected error has occurred");
    }
  }

  /**
   * Uploads documents to the database. Documents are wrapped in a Document
   * object before being stored.
   *
   * @param networkId     The network to which the client belongs.
   * @param documentName  The name of the document to upload.
   * @param userId        The user Id of the uploader.
   * @param contents      The file to upload.
   *
   * @return A JSON response indicating whether the file was successfully uploaded.
   * @throws JsonProcessingException If there's an issue processing JSON data
   */
  @PostMapping(value = "/upload-doc")
  public String uploadDoc(@RequestParam(value = "network-id") String networkId,
                          @RequestParam(value = "document-name") String documentName,
                          @RequestParam(value = "user-id") String userId,
                          @RequestBody MultipartFile contents) throws JsonProcessingException {
    ObjectMapper om = new ObjectMapper();
    try {
      CompletableFuture<Object> uploadResult = firebaseDataService.uploadFile(
            contents, networkId, documentName, userId);
      uploadResult.get();
      return om.writeValueAsString("File uploaded successfully");
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return om.writeValueAsString("File didn't upload");
    }
  }

  /**
   * Shares a document with a specified user ('theirUserId').
   *
   * @param networkId     A String representing the client's network ID.
   * @param documentName  A String representing the name of the document to share.
   * @param yourUserId    A String representing your user ID.
   * @param theirUserId   A String representing the user ID of the person you want
   *                      to share the document with.
   * @return A String describing whether the document was successfully shared or not
   *         and the reason why.
   * @throws JsonProcessingException If there's an issue processing JSON data.
   */
  @PatchMapping(value = "/share-document", produces = MediaType.APPLICATION_JSON_VALUE)
  public String shareDocument(@RequestParam(value = "network-id") String networkId,
                              @RequestParam(value = "document-name") String documentName,
                              @RequestParam(value = "your-user-id") String yourUserId,
                              @RequestParam(value = "their-user-id") String theirUserId)
                              throws JsonProcessingException {
    CompletableFuture<DataSnapshot> result = firebaseDataService.searchForDocument(
        networkId, documentName);
    Object response = new Object();
    try {
      DataSnapshot dataSnapshot = result.get();
      if (dataSnapshot.exists()) {
        response = dataSnapshot.getValue();
        Document myDocument = Document.convertToDocument((HashMap<String, Object>) response);

        if (!myDocument.getUserId().contains(yourUserId)) {
          response = "Your user does not have access to this document";
        } else if (myDocument.getUserId().contains(yourUserId)
            && myDocument.getUserId().contains(theirUserId)) {
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

  /**
   * Delete Mapping that deletes documents from the database.
   *
   * @param networkId      A String representing the network the client belongs to.
   * @param documentName   A String representing the name of the document to delete.
   * @param yourUserId     A String representing your user Id.
   * @return A JSON response verifying whether the document was successfully deleted
   *         and providing a reason if it could not be deleted.
   * @throws JsonProcessingException If there's an issue processing JSON data.
   */
  @DeleteMapping(value = "/delete-doc")
  public String deleteDoc(@RequestParam(value = "network-id") String networkId,
                          @RequestParam(value = "document-name") String documentName,
                          @RequestParam(value = "your-user-id")  String yourUserId)
                          throws JsonProcessingException {
    CompletableFuture<DataSnapshot> result = firebaseDataService.searchForDocument(
        networkId, documentName);
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
   * Searches the client's network for a specified document. If the document
   * exists, it is returned to the client. Otherwise a message is provided.
   *
   * @param networkId       A String representing the client's network Id.
   * @param documentName    A String representing the name of the document that the
   *                        client is looking for.
   * @param yourUserId      A String representing your user Id.
   * @return A JSON object serialized as a String.
   * @throws JsonProcessingException If there's an issues processing JSON data.
   */
  @GetMapping(value = "/check-for-doc", produces = MediaType.APPLICATION_JSON_VALUE)
  public String checkForDoc(@RequestParam(value = "network-id") String networkId,
                            @RequestParam(value = "document-name") String documentName,
                            @RequestParam(value = "your-user-id")  String yourUserId)
                            throws JsonProcessingException {
    CompletableFuture<DataSnapshot> result = firebaseDataService.searchForDocument(
        networkId, documentName);
    Object response = new Object();

    try {
      DataSnapshot dataSnapshot = result.get();
      if (dataSnapshot.exists()) {
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

  /**
   * Returns serialized JSON representation of a document's previous versions if it exists
   * and as long as a user has permission to view it.
   *
   * @param networkId      A String representing the client's network Id.
   * @param documentName   A String representing the name of the document that the client
   *                       is looking for.
   * @param yourUserId     A String representing your user ID.
   * @param revisionNumber An int representing the version of the document to retrieve.
   * @return A JSON object serialized as a String.
   * @throws JsonProcessingException If there's an issue processing JSON data.
   */
  @GetMapping(value = "/see-previous-version", produces = MediaType.APPLICATION_JSON_VALUE)
  public String seePreviousVersion(@RequestParam(value = "network-id") String networkId,
                                   @RequestParam(value = "document-name") String documentName,
                                   @RequestParam(value = "your-user-id") String yourUserId,
                                   @RequestParam(value = "revision-number") int revisionNumber)
                                   throws JsonProcessingException {
    CompletableFuture<DataSnapshot> result = firebaseDataService.searchForDocument(
        networkId, documentName);
    Object response = new Object();

    try {
      DataSnapshot dataSnapshot = result.get();
      if (dataSnapshot.exists()) {
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

  /**
   * Generates document statistics in the form of a String.
   *
   * @param networkId      A String representing the client's network ID.
   * @param documentName   A String representing the name of the document.
   * @param yourUserId     A String representing your user ID.
   * @return A String with the client ID, word count, users, and the number of
   *         previous versions saved.
   * @throws JsonProcessingException If there's an issue processing JSON data.
   */
  @GetMapping(value = "/see-document-stats", produces = MediaType.APPLICATION_JSON_VALUE)
  public String seeDocumentStats(@RequestParam(value = "network-id") String networkId,
                                 @RequestParam(value = "document-name") String documentName,
                                 @RequestParam(value = "your-user-id") String yourUserId)
                                 throws JsonProcessingException {
    CompletableFuture<DataSnapshot> result = firebaseDataService.searchForDocument(
        networkId, documentName);
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

  /**
   * Generates a report on the difference between two documents.
   *
   * @param networkId         A String representing the client's network ID.
   * @param fstDocName   A String representing the name of the first document.
   * @param sndDocName   A String representing the name of the second document.
   * @param yourUserId        A String representing your user ID.
   * @return String representation of the comparison made between two documents.
   * @throws JsonProcessingException If there's an issue processing JSON data.
   */
  @GetMapping(value = "/generate-difference-summary", produces = MediaType.APPLICATION_JSON_VALUE)
  public String generateDifferenceSummary(@RequestParam(value = "network-id") String networkId,
                                          @RequestParam(value = "fst-doc-name") String fstDocName,
                                          @RequestParam(value = "snd-doc-name") String sndDocName,
                                          @RequestParam(value = "your-user-id") String yourUserId)
                                          throws JsonProcessingException {
    CompletableFuture<DataSnapshot> resultOne = firebaseDataService.searchForDocument(
        networkId, fstDocName);
    CompletableFuture<DataSnapshot> resultTwo = firebaseDataService.searchForDocument(
        networkId, sndDocName);

    DataSnapshot firstSnapshot = null;
    DataSnapshot secondSnapshot = null;
    Object response = new Object();
    boolean isError = false;

    try {
      firstSnapshot = resultOne.get();
      secondSnapshot = resultTwo.get();

      if (!isError && firstSnapshot.exists() && secondSnapshot.exists()) {

        try {
          Document fstDocument = Document.convertToDocument(
              (HashMap<String, Object>) firstSnapshot.getValue());
          Document sndDocument = Document.convertToDocument(
              (HashMap<String, Object>) secondSnapshot.getValue());

          if (!fstDocument.getUserId().contains(yourUserId)
                || !sndDocument.getUserId().contains(yourUserId)) {
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

  /**
   * Downloads documents from the database.
   *
   * @param networkId     A String representing the network to which the client belongs.
   * @param documentName  A String representing the name of the document to download.
   * @param yourUserId    A String representing your user ID.
   * @param jsonObject    An optional JSON Object String.
   * @returns A ResponseEntity with the appropriate status code and document
   *          as the response body if available.
   */
  @GetMapping(value = "/download-doc", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<?> downloadDoc(@RequestParam(value = "network-id") String networkId,
                                       @RequestParam(value = "document-name") String documentName,
                                       @RequestParam(value = "your-user-id")  String yourUserId,
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
        return new ResponseEntity<>("The request body is malformed", HttpStatus.BAD_REQUEST);
      }
    }
    CompletableFuture<DataSnapshot> result = firebaseDataService.searchForDocument(
        networkId, documentName);
    HttpHeaders responseHeaders = null;
    ByteArrayResource resource = null;
    try {
      DataSnapshot dataSnapshot = result.get();
      if (dataSnapshot.exists()) {
        Document myDocument = Document.convertToDocument(
            (HashMap<String, Object>) dataSnapshot.getValue());
        if (!myDocument.getUserId().contains(yourUserId)) {
          return new ResponseEntity<>(
        "You do not have ownership of this document", HttpStatus.FORBIDDEN);
        } else {
          byte[] fileBytes = Base64.getDecoder().decode(
              myDocument.getFileString().substring(1));
          resource = new ByteArrayResource(fileBytes);
          responseHeaders = new HttpHeaders();
          responseHeaders.setContentDispositionFormData("attachment", documentName);
          responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }
      }
    } catch (IOException e) {
      return new ResponseEntity<>("An unexpected error has occurred",
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      return new ResponseEntity<>("No such document exists", HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok().headers(responseHeaders).body(resource);
  }
}