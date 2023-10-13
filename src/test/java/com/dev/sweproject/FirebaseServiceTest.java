package com.dev.sweproject;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SweProjectApplication.class)
public class FirebaseServiceTest {

  @Autowired
  private FirebaseService firebaseService;

  /**
   * Tests the successful creation of a network
   */
  @Test
  public void testCreateCollectionSuccessfully() {
    String collectionName = "testCollection";

    assertDoesNotThrow(() -> {
      String result = firebaseService.createCollection(collectionName).get();
      assertEquals(collectionName, result);
    });
  }

  /**
   * Tests for successful adding to the database
   */
  @Test
  public void testAddEntrySuccess() {
    String collection = "testCollection";
    String key = "testKey";
    String value = "testValue";

    CompletableFuture<Object> result = firebaseService.addEntry(collection, key, value);

    try {
      String resultValue = (String)result.get();
      assertNotNull(resultValue);
      assertEquals("testValue", resultValue);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Tests for successful adding to the database
   */
  @Test
  public void testAddEntrySuccess2() {
    String collection = "testCollection";
    String key = "testKey2";
    String value = "testValue";

    CompletableFuture<Object> result = firebaseService.addEntry(collection, key, value);

    try {
      String resultValue = (String)result.get();
      assertNotNull(resultValue);
      assertEquals("testValue", resultValue);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Tests for successful removal from DB
   */
  @Test
  public void testRemoveEntrySuccess() {
    String collection = "testCollection";
    String key = "testKey2";

    CompletableFuture<String> result = firebaseService.removeEntry(collection, key);

    try {
      String resultValue = result.get();
      assertNotNull(resultValue);
      assertEquals("Data removed successfully.", resultValue);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Tests for successful update
   */
  @Test
  public void testUpdateEntrySuccess() {
    String collection = "testCollection";
    String key = "testKey";
    String newValue = "newTestValue";

    CompletableFuture<Object> result = firebaseService.updateEntry(collection, key, newValue);

    try {
      Object resultValue = result.get();
      assertNotNull(resultValue);
      assertEquals(newValue, resultValue);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Tests for successful get
   */
  @Test
  public void testGetEntrySuccess() {
    String collection = "testCollection";
    String key = "testKey";

    CompletableFuture<Object> result = firebaseService.getEntry(collection, key);
    try {
      Object resultValue = result.get();
      assertNotNull(resultValue);
      assertEquals("newTestValue", resultValue);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
