package com.dev.sweproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.dev.sweproject.Document.convertToDocument;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;

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
    void testShareDocument() throws Exception {
        String networkId = "networkId";
        String documentName = "documentName";
        String userId1 = "userId1";
        String userId2 = "userId2";

        Mockito.reset(fbService);
        Mockito.when(fbService.searchForDocument(networkId, documentName))
                .thenReturn(CompletableFuture.completedFuture(Mockito.mock(DataSnapshot.class)));

        Mockito.when(fbService.updateEntry(networkId, "userId", "userId1/userId2"))
                .thenReturn(CompletableFuture.completedFuture("userId1/userId2"));

        ResponseEntity<?> responseEntity = myService.shareDocument(networkId, documentName, userId1, userId2);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Service executed", responseEntity.getBody());
    }

    @Test
    @Order(6)
    void testShareDocumentNotFound() throws Exception {
        String networkId = "networkId";
        String documentName = "documentName";
        String userId1 = "userId1";
        String userId2 = "userId2";

        Mockito.reset(fbService);
        Mockito.when(fbService.searchForDocument(networkId, documentName))
                .thenReturn(CompletableFuture.completedFuture(null));

        ResponseEntity<?> responseEntity = myService.shareDocument(networkId, documentName, userId1, userId2);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("No such document exists.", responseEntity.getBody());
    }

    @Test
    @Order(7)
    void testSeePreviousVersion() throws Exception {
        String networkId = "networkId";
        String documentName = "documentName";
        String userId = "userId";
        DataSnapshot mockDS = Mockito.mock(DataSnapshot.class);
        int revisionNumber = 1;

        Mockito.reset(fbService);
        Mockito.when(fbService.searchForDocument(networkId, documentName))
                .thenReturn(CompletableFuture.completedFuture(null));

        ResponseEntity<?> responseEntity = myService.seePreviousVersion(networkId, documentName, userId, revisionNumber);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    @Order(8)
    void testGenerateDiffSummary() throws Exception {
        String networkId = "networkId";
        String documentName1 = "documentName1";
        String documentName2 = "documentName2";
        String userId = "userId";

        Mockito.reset(fbService);
        Mockito.when(fbService.searchForDocument(networkId, documentName1))
                .thenReturn(CompletableFuture.completedFuture(null));
        Mockito.when(fbService.searchForDocument(networkId, documentName2))
                .thenReturn(CompletableFuture.completedFuture(null));

        ResponseEntity<?> responseEntity = myService.generateDiffSummary(networkId, documentName1, documentName2, userId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    @Order(8)
    void testRetrieveDocs() throws Exception {
        String networkId = "networkId";
        String userId = "userId1";

        Mockito.reset(fbService);
        Mockito.when(fbService.collectEntries(networkId, userId))
                .thenReturn(CompletableFuture.completedFuture(null));

        ResponseEntity<?> responseEntity = myService.retrieveDocs(networkId, userId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    @Order(9)
    void testRetrieveDocumentNames() throws Exception {
        String networkId = "networkId";
        String userId = "userId1";
        List<String> titles = Arrays.asList("title1", "title2");

        Mockito.reset(fbService);
        Mockito.when(fbService.getDocumentTitles(networkId, userId))
                .thenReturn(CompletableFuture.completedFuture(titles));

        ResponseEntity<?> responseEntity = myService.retrieveDocument(networkId, userId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}
