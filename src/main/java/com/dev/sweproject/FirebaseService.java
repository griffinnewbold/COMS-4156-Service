package com.dev.sweproject;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
   * Adds an entry to the specified collection with
   * the key : value association provided.
   *
   * @param collection A String denoting which collection this belongs to
   * @param key A String denoting the key for the association
   * @param value An object representing the value
   */
  public void addEntry(String collection, String key, Object value) {
    DatabaseReference databaseReference = getDatabaseReference();
    DatabaseReference collectionReference = databaseReference.child(collection);
    collectionReference.child(key).setValue(value, (error, ref) -> {
      if (error != null) {
        System.out.println("Data could not be saved: " + error.getMessage());
      } else {
        System.out.println("Data saved successfully.");
      }
    });
  }

  /**
   * Removes an entry to the specified collection with
   * the key : value association provided.
   *
   * @param collection A String denoting which collection this belongs to
   * @param key A String denoting the key for the association
   */
  public void removeEntry(String collection, String key) {
    DatabaseReference databaseReference = getDatabaseReference();
    DatabaseReference collectionReference = databaseReference.child(collection);
    collectionReference.child(key).removeValue((error, ref) -> {
      if (error != null) {
        System.out.println("Data could not be removed: " + error.getMessage());
      } else {
        System.out.println("Data removed successfully.");
      }
    });
  }

  /**
   * Creates a collection for the Database.
   *
   * @param collection A String representing the name
   *     of the collection
   */
  public void createCollection(String collection) {
    DatabaseReference databaseReference = getDatabaseReference();
    Map<String, Object> collectionData = new HashMap<>();
    collectionData.put(collection, ""); // Create an empty entry for the collection
    databaseReference.updateChildren(collectionData, (error, ref) -> {
      if (error != null) {
        System.out.println("Collection could not be created: " + error.getMessage());
      } else {
        System.out.println("Collection created successfully: " + collection);
      }
    });
  }

  /**
   * Deletes a collection from the Database.
   *
   * @param collection A String representing the name
   *     of the collection
   */
  public void deleteCollection(String collection) {
    DatabaseReference databaseReference = getDatabaseReference();
    DatabaseReference collectionReference = databaseReference.child(collection);
    collectionReference.removeValue((error, ref) -> {
      if (error != null) {
        System.out.println("Error deleting documents in collection: " + error.getMessage());
      } else {
        collectionReference.removeValue((error1, ref1) -> {
          if (error1 != null) {
            System.out.println("Error deleting collection: " + error1.getMessage());
          } else {
            System.out.println("Collection deleted successfully: " + collection);
          }
        });
      }
    });
  }

  /**
   * Updates an entry to the specified collection with
   * the key : value association provided.
   *
   * @param collection A String denoting which collection this belongs to
   * @param key A String denoting the key for the association
   * @param newValue An object representing the newValue to be assigned
   */
  public void updateEntry(String collection, String key, Object newValue) {
    DatabaseReference databaseReference = getDatabaseReference();
    DatabaseReference collectionReference = databaseReference.child(collection);
    DatabaseReference entryReference = collectionReference.child(key);

    entryReference.setValue(newValue, (error, ref) -> {
      if (error != null) {
        System.out.println("Value could not be changed: " + error.getMessage());
      } else {
        System.out.println("Value was changed successfully: " + newValue);
      }
    });
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
 /*
  public static String generateNetworkId() {
    Random random = new Random();
    boolean isCollision = true;
    String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String networkId = "";

    while(!isCollision) {

      while(networkId.length() != NETWORK_ID_LENGTH) {
        networkId += validChars.charAt(random.nextInt(validChars.length()));
      }

      DatabaseReference collectionReference = getDatabaseReference().child(collectionName);

      DataSnapshot dataSnapshot = collectionReference.get().getResult(); // Synchronous fetch

      if (dataSnapshot == null || !dataSnapshot.exists()) {

      }

    }

    return networkId;
  }
  */
}