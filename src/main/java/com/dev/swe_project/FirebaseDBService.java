package com.dev.swe_project;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FirebaseDBService {

    private final FirebaseApp firebaseApp;

    @Autowired
    public FirebaseDBService(FirebaseApp firebaseApp) {
        this.firebaseApp = firebaseApp;
    }

    public DatabaseReference getDatabaseReference() {
        // Initialize the Firebase database reference using the FirebaseApp instance
        FirebaseDatabase database = FirebaseDatabase.getInstance(firebaseApp);
        return database.getReference(); // You can specify a path if needed
    }

    public void addData(String key, Object value) {
        DatabaseReference databaseReference = getDatabaseReference();
        databaseReference.child(key).setValue(value, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if (error != null) {
                    System.out.println("Data could not be saved: " + error.getMessage());
                } else {
                    System.out.println("Data saved successfully.");
                }
            }
        });
    }

    public void removeData(String key) {
        DatabaseReference databaseReference = getDatabaseReference();
        databaseReference.child(key).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if (error != null) {
                    System.out.println("Data could not be removed: " + error.getMessage());
                } else {
                    System.out.println("Data removed successfully.");
                }
            }
        });
    }

    // You can add other methods as needed to interact with the database
}
