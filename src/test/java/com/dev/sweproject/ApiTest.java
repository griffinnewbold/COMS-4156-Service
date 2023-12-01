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
        Mockito.reset(fbService);
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
    @Test
    @Order(6)
    void testDocumentStats() throws Exception {
        Mockito.reset(fbService);
        String networkId = "networkId";
        String documentName = "documentName";
        String userId = "userId";

        CompletableFuture<DataSnapshot> result = new CompletableFuture<>();

        ArrayList<Document> docs = new ArrayList<>();
        DataSnapshot dataSnapshot = Mockito.mock(DataSnapshot.class);
        Mockito.when(dataSnapshot.exists()).thenReturn(true);
        HashMap<String, Object> documentData = new HashMap<>();
        documentData.put("title", documentName);
        documentData.put("userId", "userId");
        documentData.put("clientId", networkId);
        documentData.put("docId", "testDocId");
        documentData.put("fileString", "#VGhpcyBpcyBhIGRlbW8gZG9jdW1lbnQgZm9yIHRoZSBkZW1vIGluIGNsYXNzLg0KDQpDaGVmIE1pa2UncyBpcyBhIDEwLzEwLiA6RA==");
        documentData.put("wordCount", 1L);
        documentData.put("previousVersions", docs);
        Mockito.when(dataSnapshot.getValue()).thenReturn(documentData);

        result.complete(dataSnapshot);

        Mockito.when(fbService.searchForDocument(networkId, documentName))
                .thenReturn(CompletableFuture.completedFuture(dataSnapshot));

        ResponseEntity<?> responseEntity = myService.seeDocumentStats(networkId, documentName, userId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    //Document Existence Check (checkForDoc method):
    @Test
    @Order(7)
    void testCheckForDoc() throws Exception {
        Mockito.reset(fbService);
        String networkId = "networkId";
        String documentName = "documentName";
        String userId = "userId";

        CompletableFuture<DataSnapshot> result = new CompletableFuture<>();

        ArrayList<Document> docs = new ArrayList<>();
        DataSnapshot dataSnapshot = Mockito.mock(DataSnapshot.class);
        Mockito.when(dataSnapshot.exists()).thenReturn(true);
        HashMap<String, Object> documentData = new HashMap<>();
        documentData.put("title", "documentName");
        documentData.put("userId", "userId");
        documentData.put("clientId", "networkId");
        documentData.put("docId", "testDocId");
        documentData.put("fileString", "#VGhpcyBpcyBhIGRlbW8gZG9jdW1lbnQgZm9yIHRoZSBkZW1vIGluIGNsYXNzLg0KDQpDaGVmIE1pa2UncyBpcyBhIDEwLzEwLiA6RA==");
        documentData.put("wordCount", 1L);
        documentData.put("previousVersions", docs);
        Mockito.when(dataSnapshot.getValue()).thenReturn(documentData);

        result.complete(dataSnapshot);

        Mockito.when(fbService.searchForDocument(networkId, documentName))
                .thenReturn(CompletableFuture.completedFuture(dataSnapshot));
        ResponseEntity<?> responseEntity = myService.checkForDoc(networkId, documentName, userId);
        System.out.println(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    @Order(8)
    void testSeePreviousVersion() throws Exception {
        //Testing with doc that has no previous versions
        Mockito.reset(fbService);
        String networkId = "networkId";
        String documentName = "documentName";
        String userId = "userId";

        CompletableFuture<DataSnapshot> result = new CompletableFuture<>();

        ArrayList<Document> docs = new ArrayList<>();
        DataSnapshot dataSnapshot = Mockito.mock(DataSnapshot.class);
        Mockito.when(dataSnapshot.exists()).thenReturn(true);
        HashMap<String, Object> documentData = new HashMap<>();
        documentData.put("title", "documentName");
        documentData.put("userId", "userId");
        documentData.put("clientId", "networkId");
        documentData.put("docId", "testDocId");
        documentData.put("fileString", "#VGhpcyBpcyBhIGRlbW8gZG9jdW1lbnQgZm9yIHRoZSBkZW1vIGluIGNsYXNzLg0KDQpDaGVmIE1pa2UncyBpcyBhIDEwLzEwLiA6RA==");
        documentData.put("wordCount", 1L);
        documentData.put("previousVersions", docs);
        Mockito.when(dataSnapshot.getValue()).thenReturn(documentData);

        result.complete(dataSnapshot);

        Mockito.when(fbService.searchForDocument(networkId, documentName))
                .thenReturn(CompletableFuture.completedFuture(dataSnapshot));
        ResponseEntity<?> responseEntity = myService.seePreviousVersion(networkId, documentName, userId,0);
        System.out.println(responseEntity.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

   /* @Test
    @Order(9)
    void testRetrieveDocs() throws Exception {
        Mockito.reset(fbService);
        String networkId = "networkId";
        String documentName = "documentName";
        String userId = "userId";
        HashMap<String, Object> documentData = new HashMap<>();
        documentData.put("title", "documentName");
        documentData.put("userId", "userId");
        documentData.put("clientId", "networkId");
        documentData.put("docId", "testDocId");
        documentData.put("fileString", "#VGhpcyBpcyBhIGRlbW8gZG9jdW1lbnQgZm9yIHRoZSBkZW1vIGluIGNsYXNzLg0KDQpDaGVmIE1pa2UncyBpcyBhIDEwLzEwLiA6RA==");
        documentData.put("wordCount", 1L);

        CompletableFuture<Object> result = new CompletableFuture<>();
        DataSnapshot dataSnapshot1 = Mockito.mock(DataSnapshot.class);
        Mockito.when(dataSnapshot1.getValue()).thenReturn(documentData);

        DataSnapshot dataSnapshot2 = Mockito.mock(DataSnapshot.class);
        Mockito.when(dataSnapshot2.getValue()).thenReturn(documentData);

        List<DataSnapshot> documentSnapList = new ArrayList<>();
            documentSnapList.add(dataSnapshot1);
            documentSnapList.add(dataSnapshot2);

        Mockito.when(result.get()).thenReturn(documentSnapList);

        Mockito.when(fbService.collectEntries(networkId, documentName))
                .thenReturn(CompletableFuture.completedFuture(result.get()));

        // Test case: Successful retrieval of documents
        ResponseEntity<?> responseEntity = myService.retrieveDocs(networkId, userId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }*/

    //Document Deletion (deleteDoc method)
    @Test
    @Order(8)
    void testDeleteDoc() throws Exception {
        String networkId = "networkId";
        String documentName = "documentName";
        String userId = "userId";

        CompletableFuture<DataSnapshot> result = new CompletableFuture<>();

        ArrayList<Document> docs = new ArrayList<>();
        DataSnapshot dataSnapshot = Mockito.mock(DataSnapshot.class);
        Mockito.when(dataSnapshot.exists()).thenReturn(true);
        HashMap<String, Object> documentData = new HashMap<>();
        documentData.put("title", "documentName");
        documentData.put("userId", "userId");
        documentData.put("clientId", "networkId");
        documentData.put("docId", "testDocId");
        documentData.put("fileString", "#VGhpcyBpcyBhIGRlbW8gZG9jdW1lbnQgZm9yIHRoZSBkZW1vIGluIGNsYXNzLg0KDQpDaGVmIE1pa2UncyBpcyBhIDEwLzEwLiA6RA==");
        documentData.put("wordCount", 1L);
        documentData.put("previousVersions", docs);
        Mockito.when(dataSnapshot.getValue()).thenReturn(documentData);

        result.complete(dataSnapshot);

        Mockito.when(fbService.searchForDocument(networkId, documentName))
                .thenReturn(CompletableFuture.completedFuture(dataSnapshot));
        ResponseEntity<?> responseEntity = myService.deleteDoc(networkId, documentName, userId);
        System.out.println(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}
