package com.dev.sweproject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
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
  private static final String ERROR_MSG = "An unexpected error has occurred";
  private static final String NETWORK_ID = "network-id";
  private static final String DOCUMENT_NAME = "document-name";
  private static final String YOUR_USER_ID = "your-user-id";
  private static final String NO_DOCUMENT = "No such document exists.";

  /**
   * The main entry point for the application.
   *
   * @param args The command-line arguments
   */
  public static void main(String[] args) {
    try {
      setup();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Establishes the SpringApplication and assigns the database.
   */
  public static void setup() {
    ApplicationContext context = SpringApplication.run(
            SweProjectApplication.class);

    firebaseDataService = context.getBean(FirebaseService.class);
  }

  /**
   * Assigns the database reference, used solely for internal
   * integration testing.
   *
   * @param fb A FirebaseService reference.
   */
  public void setFirebaseDataService(FirebaseService fb) {
    firebaseDataService = fb;
  }

  /**
   * Registers the client in the database to enable various operations. This needs to run
   * once in the client's lifetime.
   *
   * @return A JSON response indicating the successful registration with a network Id.
   */
  @PostMapping(value = "/register-client", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> registerClient() {
    try {
      String networkId = firebaseDataService.generateNetworkId();
      firebaseDataService.createCollection(networkId);
      return new ResponseEntity<>(new RegisterClientResponse(networkId), HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
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
   */
  @PostMapping(value = "/upload-doc")
  public ResponseEntity<?> uploadDoc(@RequestParam(value = NETWORK_ID) String networkId,
                                     @RequestParam(value = DOCUMENT_NAME) String documentName,
                                     @RequestParam(value = "user-id") String userId,
                                     @RequestBody MultipartFile contents) {
    try {
      CompletableFuture<DataSnapshot> doesExist = firebaseDataService.searchForDocument(
          networkId, documentName);
      DataSnapshot dataSnapshot = doesExist.get();
      if (dataSnapshot != null) {
        String fileString = dataSnapshot.child("fileString").getValue(String.class);
        String currFileString = "#" + Base64.getEncoder().encodeToString(contents.getBytes());
        if (currFileString.equals(fileString)) {
          return new ResponseEntity<>("File already exists!", HttpStatus.OK);
        }
      }
      CompletableFuture<Object> uploadResult = firebaseDataService.uploadFile(
          contents, networkId, documentName, userId);
      uploadResult.get();
      return new ResponseEntity<>("File Uploaded Successfully!", HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>("File didn't upload",
          HttpStatus.INTERNAL_SERVER_ERROR);
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
   */
  @PatchMapping(value = "/share-document", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> shareDocument(@RequestParam(value = NETWORK_ID) String networkId,
                              @RequestParam(value = DOCUMENT_NAME) String documentName,
                              @RequestParam(value = YOUR_USER_ID) String yourUserId,
                              @RequestParam(value = "their-user-id") String theirUserId) {
    CompletableFuture<DataSnapshot> result = firebaseDataService.searchForDocument(
        networkId, documentName);
    try {
      DataSnapshot dataSnapshot = result.get();
      if (dataSnapshot.exists()) {
        Object response = dataSnapshot.getValue();
        Document myDocument = Document.convertToDocument((HashMap<String, Object>) response);

        if (!myDocument.getUserId().contains(yourUserId)) {
          return new ResponseEntity<>("User does not have access to this document!",
              HttpStatus.FORBIDDEN);
        } else if (myDocument.getUserId().contains(yourUserId)
            && myDocument.getUserId().contains(theirUserId)) {
          return new ResponseEntity<>("This document has already been shared with "
              + "the desired user", HttpStatus.OK);
        } else {
          String newIds = myDocument.getUserId() + "/" + theirUserId;
          String collectionToUpdate = myDocument.getClientId() + "/" + myDocument.getDocId();
          firebaseDataService.updateEntry(collectionToUpdate, "userId", newIds);
          return new ResponseEntity<>("The document has been shared with the desired user",
              HttpStatus.OK);
        }
      }
    } catch (IOException e) {
      return new ResponseEntity<>(ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      return new ResponseEntity<>(NO_DOCUMENT, HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>("Service executed", HttpStatus.OK);
  }

  /**
   * Delete Mapping that deletes documents from the database.
   *
   * @param networkId      A String representing the network the client belongs to.
   * @param documentName   A String representing the name of the document to delete.
   * @param yourUserId     A String representing your user Id.
   * @return A JSON response verifying whether the document was successfully deleted
   *         and providing a reason if it could not be deleted.
   */
  @DeleteMapping(value = "/delete-doc")
  public ResponseEntity<?> deleteDoc(@RequestParam(value = NETWORK_ID) String networkId,
                          @RequestParam(value = DOCUMENT_NAME) String documentName,
                          @RequestParam(value = YOUR_USER_ID)  String yourUserId) {
    CompletableFuture<DataSnapshot> result = firebaseDataService.searchForDocument(
        networkId, documentName);
    try {
      DataSnapshot dataSnapshot = result.get();
      if (dataSnapshot.exists()) {
        Object response = dataSnapshot.getValue();
        Document myDocument = Document.convertToDocument((HashMap<String, Object>) response);

        if (!myDocument.getUserId().contains(yourUserId)) {
          return new ResponseEntity<>("Your user does not have ownership of this document",
              HttpStatus.FORBIDDEN);
        } else {
          String documentId = myDocument.getDocId();
          String databaseLocation = networkId + "/" + documentId;
          firebaseDataService.deleteCollection(databaseLocation);
          return new ResponseEntity<>("Your document was successfully deleted",
              HttpStatus.OK);
        }
      }
    } catch (IOException e) {
      return new ResponseEntity<>(ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      return new ResponseEntity<>(NO_DOCUMENT, HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>("Service executed", HttpStatus.OK);
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
  public ResponseEntity<?> checkForDoc(@RequestParam(value = NETWORK_ID) String networkId,
                            @RequestParam(value = DOCUMENT_NAME) String documentName,
                            @RequestParam(value = YOUR_USER_ID)  String yourUserId)
                            throws JsonProcessingException {
    CompletableFuture<DataSnapshot> result = firebaseDataService.searchForDocument(
        networkId, documentName);
    Object response = null;

    try {
      DataSnapshot dataSnapshot = result.get();
      if (dataSnapshot.exists()) {
        response = dataSnapshot.getValue();
        Document myDocument = Document.convertToDocument((HashMap<String, Object>) response);
        if (!myDocument.getUserId().contains(yourUserId)) {
          return new ResponseEntity<>("Your user does not have ownership of this document",
              HttpStatus.FORBIDDEN);
        }
      }
    } catch (Exception e) {
      return new ResponseEntity<>(NO_DOCUMENT, HttpStatus.NOT_FOUND);
    }
    ObjectMapper om = new ObjectMapper();
    return new ResponseEntity<>(om.writeValueAsString(response), HttpStatus.OK);
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
  public ResponseEntity<?> seePreviousVersion(@RequestParam(value = NETWORK_ID) String networkId,
                                   @RequestParam(value = DOCUMENT_NAME) String documentName,
                                   @RequestParam(value = YOUR_USER_ID) String yourUserId,
                                   @RequestParam(value = "revision-number") int revisionNumber)
                                   throws JsonProcessingException {
    CompletableFuture<DataSnapshot> result = firebaseDataService.searchForDocument(
        networkId, documentName);
    Object response = null;

    try {
      DataSnapshot dataSnapshot = result.get();
      if (dataSnapshot.exists()) {
        response = dataSnapshot.getValue();
        Document myDocument = Document.convertToDocument((HashMap<String, Object>) response);
        if (!myDocument.getUserId().contains(yourUserId)) {
          return new ResponseEntity<>("Your user does not have access to this document",
              HttpStatus.FORBIDDEN);
        }
        if (revisionNumber <= 0 || revisionNumber >= myDocument.getPreviousVersions().size()) {
          return new ResponseEntity<>("This is not a valid revision number",
              HttpStatus.BAD_REQUEST);
        }
        response = myDocument.getPreviousVersions().get(revisionNumber);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(NO_DOCUMENT, HttpStatus.NOT_FOUND);
    }
    ObjectMapper om = new ObjectMapper();
    return new ResponseEntity<>(om.writeValueAsString(response), HttpStatus.OK);
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
  public ResponseEntity<?> seeDocumentStats(@RequestParam(value = NETWORK_ID) String networkId,
                                 @RequestParam(value = DOCUMENT_NAME) String documentName,
                                 @RequestParam(value = YOUR_USER_ID) String yourUserId)
                                 throws JsonProcessingException {
    CompletableFuture<DataSnapshot> result = firebaseDataService.searchForDocument(
        networkId, documentName);
    Object response = null;
    try {
      DataSnapshot dataSnapshot = result.get();
      if (dataSnapshot.exists()) {
        response = dataSnapshot.getValue();
        Document myDocument = Document.convertToDocument((HashMap<String, Object>) response);
        if (!myDocument.getUserId().contains(yourUserId)) {
          response = "Your user does not have access to this document";
          return new ResponseEntity<>(new ObjectMapper().writeValueAsString(response),
              HttpStatus.FORBIDDEN);
        } else {
          response = myDocument.generateUsageStatistics();
        }
      }
    } catch (IOException e) {
      return new ResponseEntity<>(ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      return new ResponseEntity<>(new ObjectMapper().writeValueAsString(NO_DOCUMENT),
          HttpStatus.NOT_FOUND);
    }
    ObjectMapper om = new ObjectMapper();
    return new ResponseEntity<>(om.writeValueAsString(response), HttpStatus.OK);
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
  public ResponseEntity<?> generateDiffSummary(@RequestParam(value = NETWORK_ID) String networkId,
                                          @RequestParam(value = "fst-doc-name") String fstDocName,
                                          @RequestParam(value = "snd-doc-name") String sndDocName,
                                          @RequestParam(value = YOUR_USER_ID) String yourUserId)
                                          throws JsonProcessingException {
    CompletableFuture<DataSnapshot> resultOne = firebaseDataService.searchForDocument(
        networkId, fstDocName);
    CompletableFuture<DataSnapshot> resultTwo = firebaseDataService.searchForDocument(
        networkId, sndDocName);

    DataSnapshot firstSnapshot = null;
    DataSnapshot secondSnapshot = null;
    Object response = null;
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
            return new ResponseEntity<>(new ObjectMapper().writeValueAsString(response),
                HttpStatus.FORBIDDEN);
          } else {
            response = fstDocument.compareTo(sndDocument);
          }
        } catch (IOException e) {
          return new ResponseEntity<>(ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
    } catch (Exception e) {
      response = "One or more of the documents does not exist";
      return new ResponseEntity<>(new ObjectMapper().writeValueAsString(response),
          HttpStatus.NOT_FOUND);
    }

    ObjectMapper om = new ObjectMapper();
    return new ResponseEntity<>(om.writeValueAsString(response), HttpStatus.OK);
  }

  /**
   * Downloads documents from the database.
   *
   * @param networkId     A String representing the network to which the client belongs.
   * @param documentName  A String representing the name of the document to download.
   * @param yourUserId    A String representing your user ID.
   * @param jsonObject    An optional JSON Object String.
   * @return A ResponseEntity with the appropriate status code and document
   *         as the response body if available.
   */
  @GetMapping(value = "/download-doc", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<?> downloadDoc(@RequestParam(value = NETWORK_ID) String networkId,
                                       @RequestParam(value = DOCUMENT_NAME) String documentName,
                                       @RequestParam(value = YOUR_USER_ID)  String yourUserId,
                                       @RequestBody(required = false) String jsonObject) {
    //Caller provided a JSON body
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
      return new ResponseEntity<>(ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      return new ResponseEntity<>(NO_DOCUMENT, HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok().headers(responseHeaders).body(resource);
  }

  /**
   * Retrieves a list of documents for a given network and user.
   *
   * @param networkId The ID of the network.
   * @param userId    The ID of the user.
   * @return A ResponseEntity containing a JSON array of documents as a list of HashMaps.
   *         Returns HttpStatus.OK if successful with the list of documents,
   *         or HttpStatus.INTERNAL_SERVER_ERROR with an error message if an error occurs.
   */
  @GetMapping(value = "/retrieve-docs", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<?> retrieveDocs(@RequestParam(value = NETWORK_ID) String networkId,
                                       @RequestParam(value = "user-id")    String userId) {
    try {
      CompletableFuture<Object> result = firebaseDataService.collectEntries(networkId, userId);
      ArrayList<DataSnapshot> documentSnapList = (ArrayList<DataSnapshot>) result.get();

      List<HashMap<String, Object>> documents = new ArrayList<>();

      for (DataSnapshot dataSnapshot : documentSnapList) {
        HashMap<String, Object> documentData = (HashMap<String, Object>) dataSnapshot.getValue();
        documents.add(documentData);
      }

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      return new ResponseEntity<>(documents, headers, HttpStatus.OK);

    } catch (Exception e) {
      return new ResponseEntity<>(ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Retrieves a list of document titles for a given network and user.
   *
   * @param networkId The ID of the network.
   * @param userId    The ID of the user.
   * @return A ResponseEntity containing a JSON array of document titles as a string.
   *         Returns HttpStatus.OK if successful with the list of document titles,
   *         or HttpStatus.INTERNAL_SERVER_ERROR with an error message if an error occurs.
   */
  @GetMapping(value = "/retrieve-doc-names", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<?> retrieveDocument(@RequestParam(value = NETWORK_ID) String networkId,
                                            @RequestParam(value = "user-id")    String userId) {
    try {
      CompletableFuture<List<String>> result = firebaseDataService.getDocumentTitles(networkId,
          userId);
      List<String> documentTitles = result.get();

      StringBuilder listStr = new StringBuilder("[");
      for (int i = 0; i < documentTitles.size(); i++) {
        String newEntry = "\"" + documentTitles.get(i) + "\"";
        listStr.append(newEntry);
        if (i != documentTitles.size() - 1) {
          listStr.append(",");
        }
      }
      listStr.append("]");

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      return new ResponseEntity<>(listStr.toString(), headers, HttpStatus.OK);

    } catch (Exception e) {
      return new ResponseEntity<>(ERROR_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}