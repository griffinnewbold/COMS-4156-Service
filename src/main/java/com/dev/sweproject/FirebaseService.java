package com.dev.sweproject;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
CompletableFuture<Object> is a class in Java that represents a future result of
an asynchronous computation. Part of the java.util.concurrent package and
Used when you want to retrieve a result once it's available or handle an error if the operation
fails. https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html

The Firebase API for Java used to mainly run off of Tasks which have since been deprecated
CompletableFuture was one of the recommended alternatives.
 */

/**
 * Has all service methods related to the DB.
 */
@Service
public class FirebaseService {

  private final FirebaseApp firebaseApp;
  public static final int NETWORK_ID_LENGTH = 3;

  /**
   * Creates an instance of the Firebase Databases.
   *
   *
   * @param firebaseApp FirebaseApp object
   */
  @Autowired
  public FirebaseService(FirebaseApp firebaseApp) {
    this.firebaseApp = firebaseApp;
  }

  /**
   * Returns a reference to the database, if multiple clients are using this
   * should give back the same instance.
   *
   * @return A reference to the Firebase realtime Database.
   */
  public DatabaseReference getDatabaseReference() {
    FirebaseDatabase database = FirebaseDatabase.getInstance(firebaseApp);
    return database.getReference();
  }

  /**
   * Add an entry to the specified collection with the key : value association provided.
   *
   * @param collection A String denoting which collection this belongs to
   * @param key A String denoting the key for the association
   * @param value An object representing the value
   * @return A CompletableFuture that completes with the value upon successful addition
   * or an error message
   */
  public CompletableFuture<Object> addEntry(String collection, String key, Object value) {
    DatabaseReference databaseReference = getDatabaseReference();
    DatabaseReference collectionReference = databaseReference.child(collection);

    CompletableFuture<Object> resultFuture = new CompletableFuture<>();

    collectionReference.child(key).setValue(value, (error, ref) -> {
      if (error != null) {
        String errorMessage = "Data could not be added: " + error.getMessage();
        System.out.println(errorMessage);
        resultFuture.completeExceptionally(new RuntimeException(errorMessage));
      } else {
        System.out.println("Data added successfully.");
        resultFuture.complete(value);
      }
    });

    return resultFuture;
  }

  /**
   * Remove an entry from the specified collection with the given key.
   *
   * @param collection A String denoting which collection this entry belongs to
   * @param key A String denoting the key for the entry to be removed
   * @return A CompletableFuture that completes with a success message upon successful removal
   * or an error message
   */
  public CompletableFuture<String> removeEntry(String collection, String key) {
    DatabaseReference databaseReference = getDatabaseReference();
    DatabaseReference collectionReference = databaseReference.child(collection);

    CompletableFuture<String> resultFuture = new CompletableFuture<>();

    collectionReference.child(key).removeValue((error, ref) -> {
      if (error != null) {
        String errorMessage = "Data could not be removed: " + error.getMessage();
        System.out.println(errorMessage);
        resultFuture.completeExceptionally(new RuntimeException(errorMessage));
      } else {
        String successMessage = "Data removed successfully.";
        System.out.println(successMessage);
        resultFuture.complete(successMessage);
      }
    });

    return resultFuture;
  }

  /**
   * Create a collection in the database.
   *
   * @param collection A String representing the name of the collection
   * @return A CompletableFuture that completes with the collection name upon successful
   * creation or an error message
   */
  public CompletableFuture<String> createCollection(String collection) {
    DatabaseReference databaseReference = getDatabaseReference();
    Map<String, Object> collectionData = new HashMap<>();
    collectionData.put(collection, "");

    CompletableFuture<String> resultFuture = new CompletableFuture<>();

    databaseReference.updateChildren(collectionData, (error, ref) -> {
      if (error != null) {
        String errorMessage = "Collection could not be created: " + error.getMessage();
        System.out.println(errorMessage);
        resultFuture.completeExceptionally(new RuntimeException(errorMessage));
      } else {
        System.out.println("Collection created successfully: " + collection);
        resultFuture.complete(collection);
      }
    });

    return resultFuture;
  }

  /**
   * Delete a collection from the database.
   *
   * @param collection A String representing the name of the collection
   * @return A CompletableFuture that completes with the collection name upon successful
   * deletion or an error message
   */
  public CompletableFuture<String> deleteCollection(String collection) {
    DatabaseReference databaseReference = getDatabaseReference();
    DatabaseReference collectionReference = databaseReference.child(collection);

    CompletableFuture<String> resultFuture = new CompletableFuture<>();

    collectionReference.removeValue((error, ref) -> {
      if (error != null) {
        String errorMessage = "Error deleting documents in collection: " + error.getMessage();
        System.out.println(errorMessage);
        resultFuture.completeExceptionally(new RuntimeException(errorMessage));
      } else {
        System.out.println("Collection deleted successfully: " + collection);
        resultFuture.complete(collection);
      }
    });

    return resultFuture;
  }

  /**
   * Update an entry in the specified collection with the key : value association provided.
   *
   * @param collection A String denoting which collection this entry belongs to
   * @param key A String denoting the key for the association
   * @param newValue An object representing the new value to be assigned
   * @return A CompletableFuture that completes with the new value upon successful update
   * or an error message
   */
  public CompletableFuture<Object> updateEntry(String collection, String key, Object newValue) {
    DatabaseReference databaseReference = getDatabaseReference();
    DatabaseReference collectionReference = databaseReference.child(collection);
    DatabaseReference entryReference = collectionReference.child(key);

    CompletableFuture<Object> resultFuture = new CompletableFuture<>();

    entryReference.setValue(newValue, (error, ref) -> {
      if (error != null) {
        String errorMessage = "Value could not be changed: " + error.getMessage();
        System.out.println(errorMessage);
        resultFuture.completeExceptionally(new RuntimeException(errorMessage));
      } else {
        System.out.println("Value was changed successfully: " + newValue);
        resultFuture.complete(newValue);
      }
    });

    return resultFuture;
  }


  /**
   * Retrieves the value associated with the specified collection and key.
   *
   * @param collection A String denoting which collection this belongs to
   * @param key A String denoting the key for the association
   * @return A CompletableFuture that will be completed with the retrieved value
   */
  public CompletableFuture<Object> getEntry(String collection, String key) {
    CompletableFuture<Object> future = new CompletableFuture<>();

    DatabaseReference databaseReference = getDatabaseReference();
    DatabaseReference collectionReference = databaseReference.child(collection);
    DatabaseReference entryReference = collectionReference.child(key);

    entryReference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        Object value = dataSnapshot.getValue();
        if (value != null) {
          future.complete(value);
        } else {
          future.completeExceptionally(new RuntimeException("Value not found."));
        }
      }
      @Override
      public void onCancelled(DatabaseError databaseError) {
        future.completeExceptionally(new RuntimeException(databaseError.getMessage()));
      }
    });

    return future;
  }

  /**
   * Generates a unique id for a network to use.
   *
   * @return A String containing the unique network id
   */
  public String generateNetworkId() {
    String timestamp = String.valueOf(System.currentTimeMillis());
    StringBuilder netId = new StringBuilder();
    Random random = new Random();

    for (int i = 0; i < NETWORK_ID_LENGTH; i++) {
      netId.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(random.nextInt(26)));
    }
    return netId + timestamp;
  }
}