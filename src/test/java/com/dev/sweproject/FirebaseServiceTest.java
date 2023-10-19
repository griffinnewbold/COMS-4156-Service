package com.dev.sweproject;

import com.google.firebase.database.DataSnapshot;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Firebase methods.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SweProjectApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FirebaseServiceTest {

  @Autowired
  private FirebaseService firebaseService;

  private final String collectionName = "testCollection";

  /**
   * Tests the successful creation of a network.
   */
  @Test
  @Order(1)
  public void testCreateCollectionSuccessfully() {
    assertDoesNotThrow(() -> {
      String result = firebaseService.createCollection(collectionName).get();
      assertEquals(collectionName, result);
    });
  }

  /**
   * Tests for successful adding to the database.
   */
  @Test
  @Order(2)
  public void testAddEntrySuccess() {
    String key = "testKey";
    String value = "testValue";
    CompletableFuture<Object> result = firebaseService.addEntry(collectionName, key, value);

    try {
      String resultValue = (String)result.get();
      assertNotNull(resultValue);
      assertEquals("testValue", resultValue);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Tests for successful adding to the database.
   */
  @Test
  @Order(3)
  public void testAddEntrySuccess2() {
    String key = "testKey2";
    String value = "testValue2";
    CompletableFuture<Object> result = firebaseService.addEntry(collectionName, key, value);

    try {
      String resultValue = (String)result.get();
      assertNotNull(resultValue);
      assertEquals("testValue2", resultValue);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Tests for successful removal from DB.
   */
  @Test
  @Order(4)
  public void testRemoveEntrySuccess() {
    String key = "testKey2";
    CompletableFuture<String> result = firebaseService.removeEntry(collectionName, key);

    try {
      String resultValue = result.get();
      assertNotNull(resultValue);
      assertEquals("Data removed successfully.", resultValue);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Tests for successful update.
   */
  @Test
  @Order(5)
  public void testUpdateEntrySuccess() {
    String key = "testKey";
    String newValue = "newTestValue";
    CompletableFuture<Object> result = firebaseService.updateEntry(collectionName, key, newValue);

    try {
      Object resultValue = result.get();
      assertNotNull(resultValue);
      assertEquals(newValue, resultValue);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Tests for successful get.
   */
  @Test
  @Order(6)
  public void testGetEntrySuccess() {
    String key = "testKey";
    CompletableFuture<Object> result = firebaseService.getEntry(collectionName, key);
    try {
      Object resultValue = result.get();
      assertNotNull(resultValue);
      assertEquals("newTestValue", resultValue);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Tests for unsuccessful get.
   */
  @Test
  @Order(7)
  public void testGetEntryFailure() {
    String key = "testKey3";
    CompletableFuture<Object> result = firebaseService.getEntry(collectionName, key);
    try {
      Object resultValue = result.get();
      assertNotNull(resultValue);
      assertEquals(new RuntimeException("Value not found."), resultValue);
    } catch (ExecutionException e) {
      System.out.println("Successfully caught: " + e);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Tests for successful search.
   */
  @Test
  @Order(8)
  public void testSearchSuccess() {
    CompletableFuture<DataSnapshot> future = firebaseService.searchForDocument("testCollectionDocs",
        "my second doc");
    try {
      DataSnapshot dataSnapshot = future.get();
      assertNotNull(dataSnapshot);
      assertTrue(dataSnapshot.exists());
      assertEquals("my second doc", Document.convertToDocument(
          (HashMap<String, Object>) dataSnapshot.getValue()).getTitle());
      System.out.println("element found: " + dataSnapshot.getValue());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Tests for unsuccessful search.
   */
  @Test
  @Order(9)
  public void testSearchFailure() {
    CompletableFuture<DataSnapshot> future = firebaseService.searchForDocument("testCollectionDocs",
        "i don't exist");
    try {
      DataSnapshot dataSnapshot = future.get();
      assertNull(dataSnapshot);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Tests for successful upload.
   */
  @Test
  @Order(10)
  public void testUploadSuccess(){
    String collectionName = "testCollectionDocs";
    String fileName = "yourFileName";
    String userId = "yourUserId";

    byte[] fileContent = "Sample file content".getBytes();
    MockMultipartFile mockFile = new MockMultipartFile("file", fileName, "text/plain", fileContent);
    try {
      CompletableFuture<Object> result = firebaseService.uploadFile(mockFile, collectionName, fileName, userId);
      assertNotNull(result);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}