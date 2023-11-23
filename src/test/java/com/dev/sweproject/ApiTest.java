package com.dev.sweproject;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
