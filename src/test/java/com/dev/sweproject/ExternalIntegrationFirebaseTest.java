package com.dev.sweproject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * This test class performs essential External Integration Testing with
 * Firebase Database, focusing on the essential CRUD behaviors which
 * have extensive use in our service. Firebase does not provide
 * the CRUD operations natively so in order to test proper interactions
 * we need to use the developed methods in the FirebaseService class, this
 * is more general than the testing done FirebaseServiceTest.java but just
 * to check a box this exists.
 */
@SpringBootTest(classes = SweProjectApplication.class)
class ExternalIntegrationFirebaseTest {

  @Autowired
  private FirebaseService firebaseService;

  private final String collectionName = "testCollection";
  private final String key = "testKey";
  private final Object testValue = "testValue";

  /**
   * Creates an entry in the database confirming proper usage of Creation
   * functionality within the database.
   *
   * @throws ExecutionException   If there is an error during execution.
   * @throws InterruptedException If the operation is interrupted.
   */
  @Test
  void testAddEntry() throws ExecutionException, InterruptedException {
    CompletableFuture<Object> resultFuture = firebaseService.addEntry(collectionName, key,
        testValue);
    Object result = resultFuture.get();

    assertEquals(testValue, result);
  }

  /**
   * Deletes an entry in the database confirming proper usage of Deletion
   * functionality within the database.
   *
   * @throws ExecutionException   If there is an error during execution.
   * @throws InterruptedException If the operation is interrupted.
   */
  @Test
  void testRemoveEntry() throws ExecutionException, InterruptedException {
    firebaseService.addEntry(collectionName, key, testValue).join();

    CompletableFuture<String> resultFuture = firebaseService.removeEntry(collectionName, key);
    String result = resultFuture.get();

    assertEquals("Data removed successfully.", result);
  }

  /**
   * Updates an entry in the database confirming proper usage of Update
   * functionality within the database.
   *
   * @throws ExecutionException   If there is an error during execution.
   * @throws InterruptedException If the operation is interrupted.
   */
  @Test
  void testUpdateEntry() throws ExecutionException, InterruptedException {
    firebaseService.addEntry(collectionName, key, testValue).join();

    Object newValue = "newTestValue";
    CompletableFuture<Object> resultFuture = firebaseService.updateEntry(collectionName, key,
        newValue);
    Object result = resultFuture.get();

    assertEquals(newValue, result);
  }

  /**
   * Reads an entry in the database confirming proper usage of Read
   * functionality within the database.
   *
   * @throws ExecutionException   If there is an error during execution.
   * @throws InterruptedException If the operation is interrupted.
   */
  @Test
  void testGetEntry() throws ExecutionException, InterruptedException {
    firebaseService.addEntry(collectionName, key, testValue).join();

    CompletableFuture<Object> resultFuture = firebaseService.getEntry(collectionName, key);
    Object result = resultFuture.get();

    assertEquals(testValue, result);
  }

  /**
   * Attempts to Read an entry in the database that does not exist
   * confirming proper usage of Read functionality within the database.
   */
  @Test
  void testGetEntryNonExistent() {
    assertThrows(ExecutionException.class,
        () -> firebaseService.getEntry(collectionName, "nonExistentKey").get());
  }
}
