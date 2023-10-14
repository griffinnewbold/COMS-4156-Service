package com.dev.sweproject;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SweProjectApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FirebaseServiceTest {

  @Autowired
  private FirebaseService firebaseService;

  private final String collectionName = "testCollection";

  /**
   * Tests the successful creation of a network
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
   * Tests for successful adding to the database
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
   * Tests for successful adding to the database
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
   * Tests for successful removal from DB
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
   * Tests for successful update
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
   * Tests for successful get
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

}