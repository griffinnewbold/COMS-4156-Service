package com.dev.sweproject;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * The `FirebaseService` class provides service methods related to the database (DB).
 * It utilizes `CompletableFuture<Object>` for handling asynchronous operations and representing
 * future results. This class is part of the `java.util.concurrent` package and is used when you
 * want to retrieve a result once it's available or handle an error if the operation fails.
 *
 * Note: The Firebase API for Java used to primarily rely on Tasks, which have since been deprecated,
 * and `CompletableFuture` is one of the recommended alternatives.
 *
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFuture Documentation</a>
 */
@Service
public class FirebaseService {

  private final FirebaseApp firebaseApp;
  public static final int NETWORK_ID_LENGTH = 3;

  /**
   * Creates an instance of the Firebase Service.
   *
   * @param firebaseApp A `FirebaseApp` object representing the Firebase configuration.
   */
  @Autowired
  public FirebaseService(FirebaseApp firebaseApp) {
    this.firebaseApp = firebaseApp;
  }

  /**
   * Returns a reference to the Firebase Realtime Database. If multiple clients are using this service,
   * it will provide the same database reference instance.
   *
   * @return A reference to the Firebase Realtime Database.
   */
  public DatabaseReference getDatabaseReference() {
    FirebaseDatabase database = FirebaseDatabase.getInstance(firebaseApp);
    return database.getReference();
  }

  /**
   * Add an entry to the specified collection with the key-value association provided.
   *
   * @param collectionName A String denoting which collection this entry belongs to.
   * @param key A String denoting the key for the association.
   * @param value An object representing the value to be added.
   * @return A CompletableFuture that completes with the value upon successful addition
   *         or completes exceptionally with an error message.
   */
  public CompletableFuture<Object> addEntry(String collectionName, String key, Object value) {
    DatabaseReference databaseReference = getDatabaseReference();
    DatabaseReference collectionReference = databaseReference.child(collectionName);

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
   * @param collectionName A String denoting which collection this entry belongs to.
   * @param key A String denoting the key for the entry to be removed.
   * @return A CompletableFuture that completes with a success message upon successful removal
   *         or completes exceptionally with an error message.
   */
  public CompletableFuture<String> removeEntry(String collectionName, String key) {
    DatabaseReference databaseReference = getDatabaseReference();
    DatabaseReference collectionReference = databaseReference.child(collectionName);

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
   * Create a collection in the database with the specified name.
   *
   * @param collectionName A String representing the name of the collection to be created.
   * @return A CompletableFuture that completes with the collection name upon successful
   *         creation or completes exceptionally with an error message.
   */
  public CompletableFuture<String> createCollection(String collectionName) {
    DatabaseReference databaseReference = getDatabaseReference();
    Map<String, Object> collectionData = new HashMap<>();
    collectionData.put(collectionName, "");

    CompletableFuture<String> resultFuture = new CompletableFuture<>();

    databaseReference.updateChildren(collectionData, (error, ref) -> {
      if (error != null) {
        String errorMessage = "Collection could not be created: " + error.getMessage();
        System.out.println(errorMessage);
        resultFuture.completeExceptionally(new RuntimeException(errorMessage));
      } else {
        System.out.println("Collection created successfully: " + collectionName);
        resultFuture.complete(collectionName);
      }
    });

    return resultFuture;
  }

  /**
   * Delete a collection from the database with the specified name.
   *
   * @param collectionName A String representing the name of the collection to be deleted.
   * @return A CompletableFuture that completes with the collection name upon successful
   *         deletion or completes exceptionally with an error message.
   */
  public CompletableFuture<String> deleteCollection(String collectionName) {
    DatabaseReference databaseReference = getDatabaseReference();
    DatabaseReference collectionReference = databaseReference.child(collectionName);

    CompletableFuture<String> resultFuture = new CompletableFuture<>();

    collectionReference.removeValue((error, ref) -> {
      if (error != null) {
        String errorMessage = "Error deleting documents in collection: " + error.getMessage();
        System.out.println(errorMessage);
        resultFuture.completeExceptionally(new RuntimeException(errorMessage));
      } else {
        System.out.println("Collection deleted successfully: " + collectionName);
        resultFuture.complete(collectionName);
      }
    });

    return resultFuture;
  }

  /**
   * Update an entry in the specified collection with the provided key : value association.
   *
   * @param collectionName A String denoting which collection this entry belongs to.
   * @param key A String denoting the key for the association to be updated.
   * @param newValue An object representing the new value to be assigned.
   * @return A CompletableFuture that completes with the new value upon successful update
   *         or completes exceptionally with an error message.
   */
  public CompletableFuture<Object> updateEntry(String collectionName, String key, Object newValue) {
    DatabaseReference databaseReference = getDatabaseReference();
    DatabaseReference collectionReference = databaseReference.child(collectionName);
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
   * @param collectionName A String denoting which collection this entry belongs to.
   * @param key A String denoting the key for the association to be retrieved.
   * @return A CompletableFuture that will be completed with the retrieved value or
   *         completes exceptionally with an error message if the value is not found.
   */
  public CompletableFuture<Object> getEntry(String collectionName, String key) {
    CompletableFuture<Object> future = new CompletableFuture<>();

    DatabaseReference databaseReference = getDatabaseReference();
    DatabaseReference collectionReference = databaseReference.child(collectionName);
    DatabaseReference entryReference = collectionReference.child(key);

    entryReference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        Object value = dataSnapshot.getValue();
        if (value != null) {
          System.out.println("The value has been successfully retrieved");
          System.out.println(value.toString());
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
   * Generates a unique network ID to use.
   *
   * @return A String containing the unique network ID.
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

  /**
   * Searches for a specific document within Firebase.
   *
   * @param collectionName A String representing the name of the collection to search in.
   * @param title A String representing the title of the document to search for.
   * @return A CompletableFuture object that may contain a DataSnapshot if the document is found, or null.
   */
  public CompletableFuture<DataSnapshot> searchForDocument(String collectionName, String title) {
    CompletableFuture<DataSnapshot> future = new CompletableFuture<>();
    DatabaseReference databaseReference = getDatabaseReference();
    DatabaseReference collectionReference = databaseReference.child(collectionName);

    ValueEventListener titleListener = new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot collectionSnapshot) {
        for (DataSnapshot documentSnapshot : collectionSnapshot.getChildren()) {
          String documentTitle = documentSnapshot.child("title").getValue(String.class);

          if (documentTitle != null && documentTitle.equals(title)) {
            future.complete(documentSnapshot);
            return;
          }
        }
        future.complete(null);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        future.completeExceptionally(new RuntimeException(databaseError.getMessage()));
      }
    };

    collectionReference.addListenerForSingleValueEvent(titleListener);

    return future;
  }

  /**
   * Uploads a file to Firebase and stores it as a document in the specified collection.
   *
   * @param file The MultipartFile representing the file to be uploaded.
   * @param collectionName A String representing the name of the collection to upload the file to.
   * @param fileName A String representing the name of the file.
   * @param userId A String representing the user ID.
   * @return A CompletableFuture object that may complete with a result upon successful upload or an error message.
   * @throws IOException If an IO error occurs during the upload process.
   */
  public CompletableFuture<Object> uploadFile(MultipartFile file, String collectionName,
                                              String fileName, String userId) throws IOException {

    CompletableFuture<DataSnapshot> doesExist = searchForDocument(collectionName, fileName);
    DataSnapshot dataSnapshot;
    Document previousDoc = null;

    try {
      dataSnapshot = doesExist.get();
      if(dataSnapshot != null) {
        previousDoc = Document.convertToDocument((HashMap<String, Object>) dataSnapshot.getValue());
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    String documentId = previousDoc == null ? Document.generateDocumentId()  : previousDoc.getDocId();

    Document documentToUpload;

    if (previousDoc != null){
      documentToUpload = new Document(userId, collectionName, file, documentId,
          fileName, Document.countWords(file.getBytes()), previousDoc.getPreviousVersions());
      previousDoc.setPreviousVersions(null);
      if (!documentToUpload.equals(previousDoc)) {
        documentToUpload.addPreviousVersion(previousDoc);
      }
    } else {
      documentToUpload = new Document(userId, collectionName, file, documentId,
          fileName, Document.countWords(file.getBytes()));
    }

    return addEntry(collectionName, documentId, documentToUpload);
  }

}
