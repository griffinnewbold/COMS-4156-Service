package com.dev.sweproject;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Handles the configuration details of the
 * database.
 */
@Configuration
public class FirebaseConfig {

  /**
   * Configures the firebase DB.
   *
   * @return A FirebaseApp object
   * @throws IOException when the config file does not exist.
   */
  @Bean
  public FirebaseApp firebaseApp() throws IOException {
    FileInputStream serviceAccount =
        new FileInputStream("./firebase_config.json");

    FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .setDatabaseUrl("https://swe-4156-service-default-rtdb.firebaseio.com")
        .build();

    return FirebaseApp.initializeApp(options);
  }
}
