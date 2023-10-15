package com.dev.sweproject;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SweProjectApplication.class)
public class DocumentTests {
    /**
     * Tests the successful creation of a Document
     */
    @Test
    public void testInitializationDoc(){
        Document document = new Document("userId", "clientId", null, "docId",
                "Title", "txt", 100);
        assertEquals("userId", document.getUserId());
        assertEquals("clientId", document.getClientId());
        assertNull(null, document.getFile());
        assertEquals("docId", document.getDocId());
        assertEquals("Title", document.getTitle());
        assertEquals("txt", document.getType());
        assertEquals(100, document.getWordCount());
    }
    /**
     * Tests the successful creation of a Blank/Default Document
     */
    @Test
    public void testInitializationDefaultDoc(){
        Document document = new Document("userId", "clientId", null, "docId");
        assertEquals("userId", document.getUserId());
        assertEquals("clientId", document.getClientId());
        assertNull(null, document.getFile());
        assertEquals("docId", document.getDocId());
    }
    /**
     * Tests the successful setting of a new userId and getting that userId
     */
    @Test
    public void test_setUserId(){
        Document doc = new Document("userId", "clientId", null, "docId",
                "Title", "txt", 100);
        doc.setUserId("newUserId");
        assertEquals("newUserId", doc.getUserId());
    }
    /**
     * Tests the successful setting of a new clientId and getting that new clientId
     */
    @Test
    public void test_setClientId(){
        Document doc = new Document("userId", "clientId", null, "docId",
                "Title", "txt", 100);
        doc.setClientId("newClientId");
        assertEquals("newClientId", doc.getClientId());
    }
    /**
     * Tests the successful setting of a new file and getting that new file
     */
    @Test
    public void test_setFile(){
        Document doc = new Document("userId", "clientId", null, "docId",
                "Title", "txt", 100);
        doc.setFile(null);
        assertNull(null, doc.getFile());
    }
    /**
     * Tests the successful setting of a new docId and getting that new docId
     */
    @Test
    public void test_setDocId(){
        Document doc = new Document("userId", "clientId", null, "docId",
                "Title", "txt", 100);
        doc.setDocId("newDocId");
        assertEquals("newDocId", doc.getDocId());
    }
    /**
     * Tests the successful setting of a new title and getting that new title
     */
    @Test
    public void test_setTitle(){
        Document doc = new Document("userId", "clientId", null, "docId",
                "Title", "txt", 100);
        doc.setTitle("newTitle");
        assertEquals("newTitle", doc.getTitle());
    }
    /**
     * Tests the successful setting of a new type of document and getting that new type
     */
    @Test
    public void test_setType(){
        Document doc = new Document("userId", "clientId", null, "docId",
                "Title", "txt", 100);
        doc.setType("other");
        assertEquals("other", doc.getType());
    }
    /**
     * Tests the successful setting of a new wordCount and getting that wordCount number
     */
    @Test
    public void test_setWordCount(){
        Document doc = new Document("userId", "clientId", null, "docId",
                "Title", "txt", 100);
        doc.setWordCount(50);
        assertEquals(50, doc.getWordCount());
    }
    /**
     * Tests the toString() method on a Doc, should give a String representation of the object
     */
    @Test
    public void test_toString(){
        Document doc = new Document("userId", "clientId", null, "docId",
                "Title", "txt", 100);
        assertEquals("Title: 100", doc.toString());
    }
}
