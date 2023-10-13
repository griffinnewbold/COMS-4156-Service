package com.dev.sweproject;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SweProjectApplication.class)
public class FirebaseServiceTest {

  @Autowired
  private FirebaseService firebaseService;



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
