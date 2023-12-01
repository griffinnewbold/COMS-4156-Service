package com.dev.sweproject;

import java.util.*;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import java.util.concurrent.CompletableFuture;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.core.io.ByteArrayResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.google.firebase.database.DataSnapshot;



@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = SweProjectApplication.class)
class ApiTest {

    private static FirebaseService fbService;
    private static SweProjectApplication myService;

    @BeforeAll
    static void setup() {
        myService = new SweProjectApplication();
        fbService = Mockito.mock(FirebaseService.class);
        myService.setFirebaseDataService(fbService);
    }

    @Test
    @Order(1)
    void testRegisterClientSuccess() {
        ResponseEntity<?> responseEntity = myService.registerClient();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @Order(2)
    void testRegisterClientFailure() {
        Mockito.when(fbService.generateNetworkId()).thenThrow(new RuntimeException("Simulated error"));

        ResponseEntity<?> responseEntity = new SweProjectApplication().registerClient();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("An unexpected error has occurred", responseEntity.getBody());
    }

    @Test
    @Order(3)
    void testUploadDocNewEntry() throws Exception {
        String networkId = "networkId";
        String documentName = "documentName";
        String userId = "userId";
        MockMultipartFile contents = new MockMultipartFile("contents", "file.txt",
                "text/plain", "File Contents".getBytes());


        Mockito.when(fbService.searchForDocument(networkId, documentName))
                .thenReturn(CompletableFuture.completedFuture(null));

        Mockito.when(fbService.uploadFile(contents, networkId, documentName, userId))
                .thenReturn(CompletableFuture.completedFuture("Upload Success"));

        ResponseEntity<?> responseEntity = myService.uploadDoc(networkId, documentName, userId, contents);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("File Uploaded Successfully!", responseEntity.getBody());
    }

    @Test
    @Order(4)
    void testUploadDocInternalServerError() throws Exception {
        String networkId = "networkId";
        String documentName = "documentName";
        String userId = "userId";
        MockMultipartFile contents = new MockMultipartFile("contents", "file.txt", "text/plain", "File Contents".getBytes());

        Mockito.when(fbService.searchForDocument(networkId, documentName))
                .thenThrow(new RuntimeException("Simulated error"));

        ResponseEntity<?> responseEntity = myService.uploadDoc(networkId, documentName, userId, contents);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("File didn't upload", responseEntity.getBody());
    }

    @Test
    @Order(5)
    void testDownloadDoc() throws Exception { //status code part works
        String networkId = "HKB1700107179552";
        String documentName = "my%20first%20doc";
        String userId = "user01";
        String jsonObject = "{\"clientId\":\"HKB1700107179552\",\"wordCount\":17,\"docId\":\"docDOD1700107250857\",\"previousVersions\":[{\"clientId\":\"\",\"wordCount\":0,\"docId\":\"\",\"title\":\"\",\"userId\":\"\",\"fileString\":\"#\"},{\"clientId\":\"HKB1700107179552\",\"wordCount\":16,\"docId\":\"docDOD1700107250857\",\"title\":\"my first doc\",\"userId\":\"user01/user02/user03\",\"fileString\":\"#VGhpcyBpcyBhIGRlbW8gZG9jdW1lbnQgZm9yIHRoZSBkZW1vIGluIGNsYXNzLg0KDQpDaGVmIE1pa2UncyBpcyBhIDEwLzEwLg==\"}],\"title\":\"my first doc\",\"userId\":\"user01/user02/user03\",\"fileString\":\"#VGhpcyBpcyBhIGRlbW8gZG9jdW1lbnQgZm9yIHRoZSBkZW1vIGluIGNsYXNzLg0KDQpDaGVmIE1pa2UncyBpcyBhIDEwLzEwLiA6RA==\"}";

        CompletableFuture<DataSnapshot> result = new CompletableFuture<>();
        DataSnapshot dataSnapshot = Mockito.mock(DataSnapshot.class);
        Mockito.when(dataSnapshot.exists()).thenReturn(true);
        HashMap<String, Object> documentData = new HashMap<>();
        documentData.put("fileString", "VGhpcyBpcyBhIGRlbW8gZG9jdW1lbnQgZm9yIHRoZSBkZW1vIGluIGNsYXNzLg0KDQpDaGVmIE1pa2UncyBpcyBhIDEwLzEwLg==");
        documentData.put("title", "my first doc");
        Mockito.when(dataSnapshot.getValue()).thenReturn(documentData);
        result.complete(dataSnapshot);
        Mockito.when(fbService.searchForDocument(networkId, documentName)).thenReturn(result);

        ResponseEntity<?> responseEntity = myService.downloadDoc(networkId, documentName, userId, jsonObject);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    //Document Statistics (seeDocumentStats method):
    /*@Test
    @Order(6)
    void testDocumentStats() throws Exception {
        String networkId = "networkId";
        String documentName = "documentName";
        String userId = "userId";

        ResponseEntity<?> responseEntity = myService.seeDocumentStats(networkId, documentName, userId);
        System.out.println("docstat"+responseEntity.getBody().toString());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
         String documentStats = "This document belongs to the following network: networkID The creator of the document is: userId\n" +
                "The word count is: 2\n" +
                "There are 1 able to see the document\n" +
                "The following users are able to see the document: userId";

        assertEquals(documentStats, responseEntity.getBody().toString().trim());
    }*/

    //Document Existence Check (checkForDoc method):
   /* @Test
    @Order(7)
    void testCheckForDoc() throws Exception {
        String networkId = "HKB1700107179552";
        String documentName = "my first doc";
        String userId = "user01";
        //String jsonObject = "{\"clientId\":\"HKB1700107179552\",\"wordCount\":17,\"docId\":\"docDOD1700107250857\",\"previousVersions\":[{\"clientId\":\"\",\"wordCount\":0,\"docId\":\"\",\"title\":\"\",\"userId\":\"\",\"fileString\":\"#\"},{\"clientId\":\"HKB1700107179552\",\"wordCount\":16,\"docId\":\"docDOD1700107250857\",\"title\":\"my first doc\",\"userId\":\"user01/user02/user03\",\"fileString\":\"#VGhpcyBpcyBhIGRlbW8gZG9jdW1lbnQgZm9yIHRoZSBkZW1vIGluIGNsYXNzLg0KDQpDaGVmIE1pa2UncyBpcyBhIDEwLzEwLg==\"}],\"title\":\"my first doc\",\"userId\":\"user01/user02/user03\",\"fileString\":\"#VGhpcyBpcyBhIGRlbW8gZG9jdW1lbnQgZm9yIHRoZSBkZW1vIGluIGNsYXNzLg0KDQpDaGVmIE1pa2UncyBpcyBhIDEwLzEwLiA6RA==\"}";

        CompletableFuture<DataSnapshot> result = new CompletableFuture<>();
        DataSnapshot dataSnapshot = Mockito.mock(DataSnapshot.class);
        Mockito.when(dataSnapshot.exists()).thenReturn(true);
        HashMap<String, Object> documentData = new HashMap<>();
        documentData.put("title", "my first doc");
        Mockito.when(dataSnapshot.getValue()).thenReturn(documentData);
        result.complete(dataSnapshot);
        Mockito.when(fbService.searchForDocument(networkId, documentName)).thenReturn(result);
        System.out.println("prnt1:" +result);
        ResponseEntity<?> responseEntity = myService.checkForDoc(networkId, documentName, userId);
        System.out.println("prnt2:" +responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        //Prev version of the code:
       /* String networkId = "HKB1700107179552";
        String documentName = "my%20first%20doc";
        String userId = "user01";

        String expectedFileContent = "{\"clientId\":\"HKB1700107179552\",\"wordCount\":17,\"docId\":\"docDOD1700107250857\",\"previousVersions\":[{\"clientId\":\"\",\"wordCount\":0,\"docId\":\"\",\"title\":\"\",\"userId\":\"\",\"fileString\":\"#\"},{\"clientId\":\"HKB1700107179552\",\"wordCount\":16,\"docId\":\"docDOD1700107250857\",\"title\":\"my first doc\",\"userId\":\"user01/user02/user03\",\"fileString\":\"#VGhpcyBpcyBhIGRlbW8gZG9jdW1lbnQgZm9yIHRoZSBkZW1vIGluIGNsYXNzLg0KDQpDaGVmIE1pa2UncyBpcyBhIDEwLzEwLg==\"}],\"title\":\"my first doc\",\"userId\":\"user01/user02/user03\",\"fileString\":\"#VGhpcyBpcyBhIGRlbW8gZG9jdW1lbnQgZm9yIHRoZSBkZW1vIGluIGNsYXNzLg0KDQpDaGVmIE1pa2UncyBpcyBhIDEwLzEwLiA6RA==\"}";
        ResponseEntity<?> responseEntity = myService.checkForDoc(networkId, documentName, userId);

        byte[] actualBytes = ((ByteArrayResource) responseEntity.getBody()).getByteArray();
        String actualString = new String(actualBytes, StandardCharsets.UTF_8);
        actualString = actualString.trim();
        String trimmedDocumentContent = expectedFileContent.trim();

        System.out.println("firCFD: " + trimmedDocumentContent);
        System.out.println("secCFD: " + actualString);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(trimmedDocumentContent.equals(actualString));
     }*/


    //Document Deletion (deleteDoc method): works
   /* @Test
    @Order(8)
    void testDeleteDocError() throws Exception {
        String networkId = "networkId";
        String documentName = "documentName";
        String userId = "userId";
        MockMultipartFile contents = new MockMultipartFile("contents", "file.txt",
                "text/plain", "File Contents".getBytes());

        Mockito.reset(fbService);
        Mockito.when(fbService.searchForDocument(networkId, documentName))
                .thenReturn(CompletableFuture.completedFuture(null));

        Mockito.when(fbService.uploadFile(contents, networkId, documentName, userId))
                .thenReturn(CompletableFuture.completedFuture("Upload Success"));

        ResponseEntity<?> responseEntity = myService.deleteDoc(networkId, documentName, userId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("No such document exists.", responseEntity.getBody());
    } */
}
