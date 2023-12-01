package com.dev.sweproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.firebase.database.DataSnapshot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

/**
 * This is a test class designed to perform internal integration between
 * different components. Our API makes extensive use of Document objects
 * and our persistent storage solution, this allows us to ensure that they
 * interact properly with each other prior to deployment.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = SweProjectApplication.class)
class InternalIntegrationTest {

  private static FirebaseService fbService;
  private static SweProjectApplication myService;

  @BeforeAll
  static void setup() {
    myService = new SweProjectApplication();
    fbService = Mockito.mock(FirebaseService.class);
    myService.setFirebaseDataService(fbService);
  }

  @Test
  @Order(1)
  void testRegisterClientSuccess() {
    ResponseEntity<?> responseEntity = myService.registerClient();

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
  }

  @Test
  @Order(2)
  void testRegisterClientFailure() {
    Mockito.when(fbService.generateNetworkId()).thenThrow(new RuntimeException("Simulated error"));

    ResponseEntity<?> responseEntity = new SweProjectApplication().registerClient();

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    assertEquals("An unexpected error has occurred", responseEntity.getBody());
  }

  @Test
  @Order(3)
  void testUploadDocNewEntry() throws Exception {
    String networkId = "networkId";
    String documentName = "documentName";
    String userId = "userId";
    MockMultipartFile contents = new MockMultipartFile("contents", "file.txt",
        "text/plain", "File Contents".getBytes());


    Mockito.when(fbService.searchForDocument(networkId, documentName))
        .thenReturn(CompletableFuture.completedFuture(null));

    Mockito.when(fbService.uploadFile(contents, networkId, documentName, userId))
        .thenReturn(CompletableFuture.completedFuture("Upload Success"));

    ResponseEntity<?> responseEntity = myService.uploadDoc(networkId, documentName,
        userId, contents);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals("File Uploaded Successfully!", responseEntity.getBody());
  }

  @Test
  @Order(4)
  void testUploadDocInternalServerError() throws Exception {
    String networkId = "networkId";
    String documentName = "documentName";
    String userId = "userId";
    MockMultipartFile contents = new MockMultipartFile("contents",
        "file.txt", "text/plain", "File Contents".getBytes());

    Mockito.when(fbService.searchForDocument(networkId, documentName))
        .thenThrow(new RuntimeException("Simulated error"));

    ResponseEntity<?> responseEntity = myService.uploadDoc(networkId, documentName,
        userId, contents);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    assertEquals("File didn't upload", responseEntity.getBody());
  }

  @Test
  @Order(5)
  void testShareDocument() throws Exception {
    String networkId = "networkId";
    String documentName = "documentName";

    Mockito.reset(fbService);
    Mockito.when(fbService.searchForDocument(networkId, documentName))
        .thenReturn(CompletableFuture.completedFuture(Mockito.mock(DataSnapshot.class)));

    Mockito.when(fbService.updateEntry(networkId, "userId", "userId1/userId2"))
        .thenReturn(CompletableFuture.completedFuture("userId1/userId2"));

    String userId1 = "userId1";
    String userId2 = "userId2";
    ResponseEntity<?> responseEntity = myService.shareDocument(networkId,
        documentName, userId1, userId2);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals("Service executed", responseEntity.getBody());
  }

  @Test
  @Order(6)
  void testShareDocumentNotFound() throws Exception {
    String networkId = "networkId";
    String documentName = "documentName";
    String userId1 = "userId1";
    String userId2 = "userId2";

    Mockito.reset(fbService);
    Mockito.when(fbService.searchForDocument(networkId, documentName))
        .thenReturn(CompletableFuture.completedFuture(null));

    ResponseEntity<?> responseEntity = myService.shareDocument(networkId,
        documentName, userId1, userId2);

    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertEquals("No such document exists.", responseEntity.getBody());
  }

  @Test
  @Order(7)
  void testSeePreviousVersion() throws Exception {
    String networkId = "networkId";
    String documentName = "documentName";
    String userId = "userId";
    int revisionNumber = 1;

    Mockito.reset(fbService);
    Mockito.when(fbService.searchForDocument(networkId, documentName))
        .thenReturn(CompletableFuture.completedFuture(null));

    ResponseEntity<?> responseEntity = myService.seePreviousVersion(networkId,
        documentName, userId, revisionNumber);

    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
  }

  @Test
  @Order(8)
  void testGenerateDiffSummary() throws Exception {
    String networkId = "networkId";
    String documentName1 = "documentName1";
    String documentName2 = "documentName2";

    Mockito.reset(fbService);
    Mockito.when(fbService.searchForDocument(networkId, documentName1))
        .thenReturn(CompletableFuture.completedFuture(null));
    Mockito.when(fbService.searchForDocument(networkId, documentName2))
        .thenReturn(CompletableFuture.completedFuture(null));

    String userId = "userId";
    ResponseEntity<?> responseEntity = myService.generateDiffSummary(networkId,
        documentName1, documentName2, userId);

    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
  }

  @Test
  @Order(9)
  void testRetrieveDocs() throws Exception {
    String networkId = "networkId";
    String userId = "userId1";

    Mockito.reset(fbService);
    Mockito.when(fbService.collectEntries(networkId, userId))
        .thenReturn(CompletableFuture.completedFuture(null));

    ResponseEntity<?> responseEntity = myService.retrieveDocs(networkId, userId);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
  }

  @Test
  @Order(10)
  void testRetrieveDocumentNames() throws Exception {
    String networkId = "networkId";
    String userId = "userId1";
    List<String> titles = Arrays.asList("title1", "title2");

    Mockito.reset(fbService);
    Mockito.when(fbService.getDocumentTitles(networkId, userId))
        .thenReturn(CompletableFuture.completedFuture(titles));

    ResponseEntity<?> responseEntity = myService.retrieveDocument(networkId, userId);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  @Order(11)
  void testDownloadDoc() throws Exception { //status code part works
    Mockito.reset(fbService);

    DataSnapshot dataSnapshot = Mockito.mock(DataSnapshot.class);
    Mockito.when(dataSnapshot.exists()).thenReturn(true);
    HashMap<String, Object> documentData = new HashMap<>();
    documentData.put("fileString", "VGhpcyBpcyBhIGRlbW8gZG9jdW1lbnQgZm9yIHRoZSBkZW1vIGluI"
        + "GNsYXNzLg0KDQpDaGVmIE1pa2UncyBpcyBhIDEwLzEwLg==");
    documentData.put("title", "my first doc");
    Mockito.when(dataSnapshot.getValue()).thenReturn(documentData);
    CompletableFuture<DataSnapshot> result = new CompletableFuture<>();
    result.complete(dataSnapshot);
    String networkId = "HKB1700107179552";
    String documentName = "my%20first%20doc";
    String userId = "user01";
    Mockito.when(fbService.searchForDocument(networkId, documentName)).thenReturn(result);

    String jsonObject = "{\"clientId\":\"HKB1700107179552\",\"wordCount\":17,\"docId\":\"docDO"
        + "D1700107250857\",\"previousVersions\":[{\"clientId\":\"\",\"wordCount\":0,"
        + "\"docId\":\"\",\"title\":\"\",\"userId\":\"\",\"fileString\":\"#\"},{\"clientId\""
        + ":\"HKB1700107179552\",\"wordCount\":16,\"docId\":\"docDOD1700107250857\",\"title\""
        + ":\"my first doc\",\"userId\":\"user01/user02/user03\",\"fileString\":\"#VGhpcyBpcyBhI"
        + "GRlbW8gZG9jdW1lbnQgZm9yIHRoZSBkZW1vIGluIGNsYXNzLg0KDQpDaGVmIE1pa2UncyBpcyBhIDEwLzEw"
        + "Lg==\"}],\"title\":\"my first doc\",\"userId\":\"user01/user02/user03\",\"fileSt"
        + "ring\":\"#VGhpcyBpcyBhIGRlbW8gZG9jdW1lbnQgZm9yIHRoZSBkZW1vIGluIGNsYXNzLg0KDQpDa"
        + "GVmIE1pa2UncyBpcyBhIDEwLzEwLiA6RA==\"}";
    ResponseEntity<?> responseEntity = myService.downloadDoc(networkId, documentName, userId,
        jsonObject);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  //Document Statistics (seeDocumentStats method):
  @Test
  @Order(12)
  void testDocumentStats() throws Exception {
    Mockito.reset(fbService);

    DataSnapshot dataSnapshot = Mockito.mock(DataSnapshot.class);
    Mockito.when(dataSnapshot.exists()).thenReturn(true);
    HashMap<String, Object> documentData = new HashMap<>();
    String documentName = "documentName";
    String networkId = "networkId";
    documentData.put("title", documentName);
    documentData.put("userId", "userId");
    documentData.put("clientId", networkId);
    documentData.put("docId", "testDocId");
    documentData.put("fileString", "#VGhpcyBpcyBhIGRlbW8gZG9jdW1lbnQgZm9yIHRoZSBkZW1vIGluI"
        + "GNsYXNzLg0KDQpDaGVmIE1pa2UncyBpcyBhIDEwLzEwLiA6RA==");
    documentData.put("wordCount", 1L);
    ArrayList<Document> docs = new ArrayList<>();
    documentData.put("previousVersions", docs);
    Mockito.when(dataSnapshot.getValue()).thenReturn(documentData);

    CompletableFuture<DataSnapshot> result = new CompletableFuture<>();
    result.complete(dataSnapshot);

    Mockito.when(fbService.searchForDocument(networkId, documentName))
        .thenReturn(CompletableFuture.completedFuture(dataSnapshot));
    String userId = "userId";
    ResponseEntity<?> responseEntity = myService.seeDocumentStats(networkId, documentName, userId);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  //Document Existence Check (checkForDoc method):
  @Test
  @Order(13)
  void testCheckForDoc() throws Exception {
    Mockito.reset(fbService);

    DataSnapshot dataSnapshot = Mockito.mock(DataSnapshot.class);
    Mockito.when(dataSnapshot.exists()).thenReturn(true);
    HashMap<String, Object> documentData = new HashMap<>();
    documentData.put("title", "documentName");
    documentData.put("userId", "userId");
    documentData.put("clientId", "networkId");
    documentData.put("docId", "testDocId");
    documentData.put("fileString", "#VGhpcyBpcyBhIGRlbW8gZG9jdW1lbnQgZm9yIHRoZSBkZW1vIGluIGNsY"
        + "XNzLg0KDQpDaGVmIE1pa2UncyBpcyBhIDEwLzEwLiA6RA==");
    documentData.put("wordCount", 1L);
    ArrayList<Document> docs = new ArrayList<>();
    documentData.put("previousVersions", docs);
    Mockito.when(dataSnapshot.getValue()).thenReturn(documentData);
    CompletableFuture<DataSnapshot> result = new CompletableFuture<>();
    result.complete(dataSnapshot);

    String networkId = "networkId";
    String documentName = "documentName";
    String userId = "userId";
    Mockito.when(fbService.searchForDocument(networkId, documentName))
        .thenReturn(CompletableFuture.completedFuture(dataSnapshot));
    ResponseEntity<?> responseEntity = myService.checkForDoc(networkId, documentName, userId);
    System.out.println(responseEntity.getBody());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  @Order(14)
  void testSeePreviousVersion2() throws Exception {
    //Testing with doc that has no previous versions
    Mockito.reset(fbService);

    DataSnapshot dataSnapshot = Mockito.mock(DataSnapshot.class);
    Mockito.when(dataSnapshot.exists()).thenReturn(true);
    HashMap<String, Object> documentData = new HashMap<>();
    documentData.put("title", "documentName");
    documentData.put("userId", "userId");
    documentData.put("clientId", "networkId");
    documentData.put("docId", "testDocId");
    documentData.put("fileString", "#VGhpcyBpcyBhIGRlbW8gZG9jdW1lbnQgZm9yIHRoZSBkZW1vIGluI"
        + "GNsYXNzLg0KDQpDaGVmIE1pa2UncyBpcyBhIDEwLzEwLiA6RA==");
    documentData.put("wordCount", 1L);
    ArrayList<Document> docs = new ArrayList<>();
    documentData.put("previousVersions", docs);
    Mockito.when(dataSnapshot.getValue()).thenReturn(documentData);
    CompletableFuture<DataSnapshot> result = new CompletableFuture<>();
    result.complete(dataSnapshot);

    String networkId = "networkId";
    String documentName = "documentName";
    String userId = "userId";
    Mockito.when(fbService.searchForDocument(networkId, documentName))
        .thenReturn(CompletableFuture.completedFuture(dataSnapshot));
    ResponseEntity<?> responseEntity = myService.seePreviousVersion(networkId, documentName,
        userId, 0);
    System.out.println(responseEntity.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }

  /* @Test
    @Order(9)
    void testRetrieveDocs() throws Exception {
        Mockito.reset(fbService);
        String networkId = "networkId";
        String documentName = "documentName";
        String userId = "userId";
        HashMap<String, Object> documentData = new HashMap<>();
        documentData.put("title", "documentName");
        documentData.put("userId", "userId");
        documentData.put("clientId", "networkId");
        documentData.put("docId", "testDocId");
        documentData.put("fileString", "#VGhpcyBpcyBhIGRlbW8gZG9jdW1lbnQgZm9yIHRoZSBkZW1vIGluIGNsYXN
        zLg0KDQpDaGVmIE1pa2UncyBpcyBhIDEwLzEwLiA6RA==");
        documentData.put("wordCount", 1L);

        CompletableFuture<Object> result = new CompletableFuture<>();
        DataSnapshot dataSnapshot1 = Mockito.mock(DataSnapshot.class);
        Mockito.when(dataSnapshot1.getValue()).thenReturn(documentData);

        DataSnapshot dataSnapshot2 = Mockito.mock(DataSnapshot.class);
        Mockito.when(dataSnapshot2.getValue()).thenReturn(documentData);

        List<DataSnapshot> documentSnapList = new ArrayList<>();
            documentSnapList.add(dataSnapshot1);
            documentSnapList.add(dataSnapshot2);

        Mockito.when(result.get()).thenReturn(documentSnapList);

        Mockito.when(fbService.collectEntries(networkId, documentName))
                .thenReturn(CompletableFuture.completedFuture(result.get()));

        // Test case: Successful retrieval of documents
        ResponseEntity<?> responseEntity = myService.retrieveDocs(networkId, userId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
  */

  //Document Deletion (deleteDoc method)
  @Test
  @Order(15)
  void testDeleteDoc() throws Exception {
    DataSnapshot dataSnapshot = Mockito.mock(DataSnapshot.class);
    Mockito.when(dataSnapshot.exists()).thenReturn(true);
    HashMap<String, Object> documentData = new HashMap<>();
    documentData.put("title", "documentName");
    documentData.put("userId", "userId");
    documentData.put("clientId", "networkId");
    documentData.put("docId", "testDocId");
    documentData.put("fileString", "#VGhpcyBpcyBhIGRlbW8gZG9jdW1lbnQgZm9yIHRoZSBkZW1vIGluIGNsYXNz"
        + "Lg0KDQpDaGVmIE1pa2UncyBpcyBhIDEwLzEwLiA6RA==");
    documentData.put("wordCount", 1L);
    ArrayList<Document> docs = new ArrayList<>();
    documentData.put("previousVersions", docs);
    Mockito.when(dataSnapshot.getValue()).thenReturn(documentData);
    CompletableFuture<DataSnapshot> result = new CompletableFuture<>();
    result.complete(dataSnapshot);
    String networkId = "networkId";
    String documentName = "documentName";
    String userId = "userId";
    Mockito.when(fbService.searchForDocument(networkId, documentName))
        .thenReturn(CompletableFuture.completedFuture(dataSnapshot));
    ResponseEntity<?> responseEntity = myService.deleteDoc(networkId, documentName, userId);
    System.out.println(responseEntity.getBody());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }
}