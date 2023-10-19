package com.dev.sweproject;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Tests for the construcotrs, getters, setters, and methods within the Document class.
 */

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

    /**
     * Tests that a document without previous versions returns null when getPreviousVersions() is called.
     */
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

    /**
     * Tests that a document with itself uploaded as a "previous version" returns itself when getPreviousVersions() is called.
     */
    @Test
    public void testGetPreviousVersions() throws IOException {
        Document document = new Document("userId", "clientId", null, "docId",
            "Title", 100, new ArrayList<Document>());
        assertEquals(new ArrayList<Document>(), document.getPreviousVersions());
    }

    /**
     * Tests that setPreviousVersions() will return null when set to null
     */
    @Test
    public void testSetPreviousVersions() throws IOException {
        Document document = new Document("userId", "clientId", null, "docId",
            "Title", 100, new ArrayList<Document>());
        document.setPreviousVersions(null);
        assertNull(document.getPreviousVersions());
    }

    /**
     * Tests getUserId().
     */
    @Test
    public void testGetUserId() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
        assertEquals("userId", doc.getUserId());
    }

    /**
     * Tests setUserId().
     */
    @Test
    public void testSetUserId() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
                "Title", 100);
        doc.setUserId("newUserId");
        assertEquals("newUserId", doc.getUserId());
    }

    /**
     * Tests getClientId().
     */
    @Test
    public void testGetClientId() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
        assertEquals("clientId", doc.getClientId());
    }

    /**
     * Tests setClientId().
     */
    @Test
    public void testSetClientId() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
                "Title", 100);
        doc.setClientId("newClientId");
        assertEquals("newClientId", doc.getClientId());
    }

    /**
     * Tests getDocId().
     */
    @Test
    public void testGetDocId() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
        assertEquals("docId", doc.getDocId());
    }

    /**
     * Tests setDocId().
     */
    @Test
    public void testSetDocId() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
                "Title", 100);
        doc.setDocId("newDocId");
        assertEquals("newDocId", doc.getDocId());
    }

    /**
     * Tests getTitle().
     */
    @Test
    public void testGetTitle() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
        assertEquals("Title", doc.getTitle());
    }

    /**
     * Tests setTitle().
     */
    @Test
    public void testSetTitle() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
                "Title", 100);
        doc.setTitle("newTitle");
        assertEquals("newTitle", doc.getTitle());
    }

    /**
     * Tests getWordCount().
     */
    @Test
    public void testGetWordCount() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
        assertEquals(100, doc.getWordCount());
    }

    /**
     * Tests setWordCount().
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

    /**
     * Tests getFileString().
     */
    @Test
    public void testGetFileString() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
        assertNull(doc.getFileString());
    }

    /**
     * Tests setFileString().
     */
    @Test
    public void testSetFileString() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
        doc.setFileString("#d");
        assertEquals("#d", doc.getFileString());
    }

    /**
     * Tests equals().
     */
    @Test
    public void testEquals() throws IOException {
        Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
        Document doc2 = doc;
        boolean isEqual = doc.equals(doc2);
        assertTrue(isEqual);
    }

    /**
     * Tests compareTo().
     */
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

    /**
     * Tests generateDocumentId().
     */
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
