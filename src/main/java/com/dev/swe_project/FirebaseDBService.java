package com.dev.swe_project;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FirebaseDBService {

    private final FirebaseApp firebaseApp;

    @Autowired
    public FirebaseDBService(FirebaseApp firebaseApp) {
        this.firebaseApp = firebaseApp;
    }

    public DatabaseReference getDatabaseReference() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(firebaseApp);
        return database.getReference();
    }

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

    public void updateEntry(String collection, String key, Object newValue){
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

}
