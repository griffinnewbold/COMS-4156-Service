package com.dev.sweproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
class ApiTest {

  private static FirebaseService fbService;
  private static SweProjectApplication myService;

  /**
   * Generic setup that allows for tests to be conducted.
   */
  @BeforeAll
  static void setup() {
    myService = new SweProjectApplication();
    fbService = Mockito.mock(FirebaseService.class);
    myService.setFirebaseDataService(fbService);
  }

  /**
   * Tests for successful creation of a network within our persistent storage.
   * Ensures that CREATION is properly dealt with in persistent storage.
   */
  @Test
  @Order(1)
  void testRegisterClientSuccess() {
    ResponseEntity<?> responseEntity = myService.registerClient();
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
  }

  /**
   * Tests for unsuccessful creation of a network within our persistent storage.
   * Ensures that CREATION is properly dealt with in persistent storage in case of failure.
   */
  @Test
  @Order(2)
  void testRegisterClientFailure() {
    Mockito.when(fbService.generateNetworkId()).thenThrow(new RuntimeException("Simulated error"));

    ResponseEntity<?> responseEntity = new SweProjectApplication().registerClient();

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    assertEquals("An unexpected error has occurred", responseEntity.getBody());
  }

  /**
   * Tests for successful addition of a new entry within our persistent storage.
   * Ensures that CREATION is properly dealt with in persistent storage.
   */
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

  /**
   * Tests for unsuccessful addition of a new entry within our persistent storage.
   * Ensures that CREATION is properly dealt with in persistent storage in case of failure.
   */
  @Test
  @Order(4)
  void testUploadDocInternalServerError() throws Exception {
    String networkId = "networkId";
    String documentName = "documentName";
    String userId = "userId";
    MockMultipartFile contents = new MockMultipartFile("contents", "file.txt",
        "text/plain", "File Contents".getBytes());

    Mockito.when(fbService.searchForDocument(networkId, documentName))
        .thenThrow(new RuntimeException("Simulated error"));

    ResponseEntity<?> responseEntity = myService.uploadDoc(networkId, documentName,
        userId, contents);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    assertEquals("File didn't upload", responseEntity.getBody());
  }
}
