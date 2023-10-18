package com.dev.sweproject;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SweProjectApplication.class)
class DocumentTest {
    /**
     * Tests the successful creation of a Document
     */
    @Test
    public void testInitializationDoc() throws IOException {
        Document document = new Document("userId", "clientId", null, "docId",
                "Title", 100);
        assertEquals("userId", document.getUserId());
        assertEquals("clientId", document.getClientId());
        assertEquals("docId", document.getDocId());
        assertEquals("Title", document.getTitle());
        assertEquals(100, document.getWordCount());
    }
    /**
     * Tests the successful creation of a Blank/Default Document
     */
    @Test
    public void testInitializationDefaultDoc() throws IOException {
        Document document = new Document("userId", "clientId", null, "docId");
        assertEquals("userId", document.getUserId());
        assertEquals("clientId", document.getClientId());
        assertEquals("docId", document.getDocId());
    }

    @Test
    public void testInitializationPreviousVersions() throws IOException {
        Document document = new Document("userId", "clientId", null, "docId",
            "Title", 100, null);
        assertEquals("userId", document.getUserId());
        assertEquals("clientId", document.getClientId());
        assertEquals("docId", document.getDocId());
        assertEquals("Title", document.getTitle());
        assertEquals(100, document.getWordCount());
        assertNull(document.getPreviousVersions());
    }

    @Test
    public void testGetPreviousVersions() throws IOException {
        Document document = new Document("userId", "clientId", null, "docId",
            "Title", 100, new ArrayList<Document>());
        assertEquals(new ArrayList<Document>(), document.getPreviousVersions());
    }

    @Test
    public void testSetPreviousVersions() throws IOException {
        Document document = new Document("userId", "clientId", null, "docId",
            "Title", 100, new ArrayList<Document>());
        document.setPreviousVersions(null);
        assertNull(document.getPreviousVersions());
    }

    @Test
    public void testGetUserId() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
        assertEquals("userId", doc.getUserId());
    }

    /**
     * Tests the successful setting of a new userId and getting that userId
     */
    @Test
    public void testSetUserId() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
                "Title", 100);
        doc.setUserId("newUserId");
        assertEquals("newUserId", doc.getUserId());
    }

    @Test
    public void testGetClientId() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
        assertEquals("clientId", doc.getClientId());
    }



    /**
     * Tests the successful setting of a new clientId and getting that new clientId
     */
    @Test
    public void testSetClientId() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
                "Title", 100);
        doc.setClientId("newClientId");
        assertEquals("newClientId", doc.getClientId());
    }

    @Test
    public void testGetDocId() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
        assertEquals("docId", doc.getDocId());
    }

    /**
     * Tests the successful setting of a new docId and getting that new docId
     */
    @Test
    public void testSetDocId() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
                "Title", 100);
        doc.setDocId("newDocId");
        assertEquals("newDocId", doc.getDocId());
    }

    @Test
    public void testGetTitle() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
        assertEquals("Title", doc.getTitle());
    }

    /**
     * Tests the successful setting of a new title and getting that new title
     */
    @Test
    public void testSetTitle() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
                "Title", 100);
        doc.setTitle("newTitle");
        assertEquals("newTitle", doc.getTitle());
    }

    @Test
    public void testGetWordCount() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
        assertEquals(100, doc.getWordCount());
    }

    /**
     * Tests the successful setting of a new wordCount and getting that wordCount number
     */
    @Test
    public void testSetWordCount() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
                "Title", 100);
        doc.setWordCount(50);
        assertEquals(50, doc.getWordCount());
    }

    /**
     * Tests the toString() method on a Doc, should give a String representation of the object
     */
    @Test
    public void testToString() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
                "Title", 100);
        assertEquals("Title: 100", doc.toString());
    }

    @Test
    public void testGetFileString() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
        assertNull(doc.getFileString());
    }

    @Test
    public void testSetFileString() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
        doc.setFileString("#d");
        assertEquals("#d", doc.getFileString());
    }

    @Test
    public void testEquals() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
        Document doc2 = doc;
        boolean isEqual = doc.equals(doc2);
        assertTrue(isEqual);
    }

    @Test
    public void testCompareTo() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
        Document doc2 = new Document("userId/userId", "clientId", null, "docId",
            "Title", 100);
        String expected = doc.getTitle() + " has the same word count " + doc2.getTitle();
        expected += "\n";
        expected += doc.getTitle() + " has 1 less users than " + doc2.getTitle();
        expected += "\n";
        expected += doc.getTitle() + " has the same version count " + doc2.getTitle();
        assertEquals(expected, doc.compareTo(doc2));
    }

    @Test
    public void testGenerateUsageStatistics() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
        String result = "";
        result += "This document belongs to the following network: " + doc.getClientId() + "\n";
        result += "The word count is: " + doc.getWordCount() + "\n";
        result += "There are 1 able to see the document.\n";
        result += "There is/are 0previous versions on record";

        assertEquals(result, doc.generateUsageStatistics());
    }


}
