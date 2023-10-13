package com.dev.sweproject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.dev.sweproject.FirebaseService;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FirebaseServiceTest {

  private FirebaseService firebaseService;

  @BeforeEach
  public void setup() {
    ApplicationContext context = SpringApplication.run(
        SweProjectApplication.class, "");

    this.firebaseService = context.getBean(FirebaseService.class);
  }


  @Test
  public void sampleTest() {
    assertEquals(2 + 2, 4);
  }

  @Test
  public void testCreateCollectionSuccessfully() {
    String collectionName = firebaseService.generateNetworkId();

    assertDoesNotThrow(() -> {
      String result = firebaseService.createCollection(collectionName).get();
      assertEquals(collectionName, result);
    });
  }






}
