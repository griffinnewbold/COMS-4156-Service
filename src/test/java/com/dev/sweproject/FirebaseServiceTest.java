package com.dev.sweproject;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.firebase.database.DataSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

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
      String resultValue = (String) result.get();
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
      String resultValue = (String) result.get();
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
    CompletableFuture<DataSnapshot> future = firebaseService.searchForDocument(
        "testCollectionDocs", "yourFileName");
    try {
      DataSnapshot dataSnapshot = future.get();
      assertNotNull(dataSnapshot);
      assertTrue(dataSnapshot.exists());
      assertEquals("yourFileName", Document.convertToDocument(
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
    CompletableFuture<DataSnapshot> future = firebaseService.searchForDocument(
        "testCollectionDocs", "i don't exist");
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
  public void testUploadSuccess() {
    String collectionName = "testCollectionDocs";
    String fileName = "yourFileName";
    String userId = "yourUserId";

    byte[] fileContent = "Sample file content".getBytes();
    MockMultipartFile mockFile = new MockMultipartFile("file", fileName,
        "text/plain", fileContent);
    try {
      CompletableFuture<Object> result =
              firebaseService.uploadFile(mockFile, collectionName, fileName, userId);
      assertNotNull(result);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Tests for successful upload of file with same name.
   */
  @Test
  @Order(11)
  public void testUploadRepeatedFileSuccess() {
    String collectionName = "testCollectionDocs";
    String fileName = "yourFileName";
    String userId = "yourUserId";

    byte[] fileContent = "Sample repeated file content".getBytes();
    MockMultipartFile mockFile = new MockMultipartFile("file", fileName,
        "text/plain", fileContent);
    try {
      CompletableFuture<Object> result =
              firebaseService.uploadFile(mockFile, collectionName, fileName, userId);
      assertNotNull(result);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Tests for successful upload of file with new name.
   */
  @Test
  @Order(12)
  public void testUploadNewFileSuccess() {
    String fileName = "newFileName";
    String userId = "newUserId";

    byte[] fileContent = "Sample new file content".getBytes();
    MockMultipartFile mockFile = new MockMultipartFile("file", fileName,
        "text/plain", fileContent);
    try {
      CompletableFuture<Object> result =
              firebaseService.uploadFile(mockFile, collectionName, fileName, userId);
      assertNotNull(result);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Tests for correct entry count for non-empty collection.
   */
  @Test
  @Order(13)
  public void testEntryCollectionNonEmpty() {
    String collectionName = "testCollectionDocs";
    String userId = "yourUserId";

    try {
      CompletableFuture<Object> result = firebaseService.collectEntries(collectionName, userId);
      assertNotNull(result);

      ArrayList<DataSnapshot> myObject = (ArrayList<DataSnapshot>) result.get();

      assertEquals(1, myObject.size());

      for (DataSnapshot o : myObject) {
        Document myDoc = Document.convertToDocument((HashMap<String, Object>) o.getValue());
        assertNotNull(myDoc);
        assertEquals("yourFileName: 4", myDoc.toString());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Tests for correct entry count for empty collection.
   */
  @Test
  @Order(14)
  public void testEntryCollectionEmpty() {
    String collectionName = "testCollectionDocs";
    String userId = "yourUserId2";

    try {
      CompletableFuture<Object> result = firebaseService.collectEntries(collectionName, userId);
      assertNotNull(result);

      ArrayList<DataSnapshot> myObject = (ArrayList<DataSnapshot>) result.get();
      assertEquals(0, myObject.size());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Tests for correct entry count of collection with empty user.
   */
  @Test
  @Order(15)
  public void testEntryCollectionEmptyUser() {
    String collectionName = "testCollectionDocs";
    String userId = "";

    try {
      CompletableFuture<Object> result = firebaseService.collectEntries(collectionName, userId);
      assertNotNull(result);

      ArrayList<DataSnapshot> myObject = (ArrayList<DataSnapshot>) result.get();
      assertEquals(0, myObject.size());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Tests for correct retrieval of document titles.
   */
  @Test
  @Order(15)
  public void testGetDocumentTitles() {
    String userId = "newUserId";

    try {
      CompletableFuture<List<String>> result = firebaseService.getDocumentTitles(collectionName,
          userId);
      assertNotNull(result);

      ArrayList<String> myObject = (ArrayList<String>) result.get();
      assertEquals(1, myObject.size());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Tests for successful removal of collection.
   */
  @Test
  @Order(17)
  public void testRemoveCollectionSuccessfully() {
    CompletableFuture<String> result = firebaseService.deleteCollection(collectionName);

    try {
      String resultValue = result.get();
      assertNotNull(resultValue);
      assertEquals(collectionName, resultValue);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Tests for successful and random generation of network ID.
   */
  @Test
  @Order(18)
  public void testGenerateNetworkId() {
    String result1 = firebaseService.generateNetworkId();
    String result2 = firebaseService.generateNetworkId();
    assertNotNull(result1);
    assertNotNull(result2);
    assertNotEquals(result1, result2);
  }

}