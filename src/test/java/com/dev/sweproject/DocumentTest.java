package com.dev.sweproject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests for the constructors, getters, setters, and methods within the Document class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SweProjectApplication.class)
class DocumentTest {

  /**
   * Tests the successful creation of a Document.
   *
   * @throws IOException if an I/O exception occurs during the test.
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
   * Tests the successful creation of a Blank/Default Document.
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  public void testInitializationDefaultDoc() throws IOException {
    Document document = new Document("userId", "clientId", null, "docId");
    assertEquals("userId", document.getUserId());
    assertEquals("clientId", document.getClientId());
    assertEquals("docId", document.getDocId());
  }

  /**
   * Tests that a document without previous versions returns null when getPreviousVersions()
   * is called.
   *
   * @throws IOException if an I/O exception occurs during the test.
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
   * Tests that a document with itself uploaded as a "previous version" returns itself when
   * getPreviousVersions() is called.
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  public void testGetPreviousVersions() throws IOException {
    Document document = new Document("userId", "clientId", null, "docId",
            "Title", 100, new ArrayList<Document>());
    assertEquals(new ArrayList<Document>(), document.getPreviousVersions());
  }

  /**
   * Tests that setPreviousVersions() will return null when set to null.
   *
   * @throws IOException if an I/O exception occurs during the test.
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
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  public void testGetUserId() throws IOException {
    Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
    assertEquals("userId", doc.getUserId());
  }

  /**
   * Tests setUserId().
   *
   * @throws IOException if an I/O exception occurs during the test.
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
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  public void testGetClientId() throws IOException {
    Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
    assertEquals("clientId", doc.getClientId());
  }

  /**
   * Tests setClientId().
   *
   * @throws IOException if an I/O exception occurs during the test.
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
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  public void testGetDocId() throws IOException {
    Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
    assertEquals("docId", doc.getDocId());
  }

  /**
   * Tests setDocId().
   *
   * @throws IOException if an I/O exception occurs during the test.
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
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  public void testGetTitle() throws IOException {
    Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
    assertEquals("Title", doc.getTitle());
  }

  /**
   * Tests setTitle().
   *
   * @throws IOException if an I/O exception occurs during the test.
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
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  public void testGetWordCount() throws IOException {
    Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
    assertEquals(100, doc.getWordCount());
  }

  /**
   * Tests setWordCount().
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  public void testSetWordCount() throws IOException {
    Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
    doc.setWordCount(50);
    assertEquals(50, doc.getWordCount());
  }

  /**
   * Tests the toString() method on a Doc, should give a String representation of the object.
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  public void testToString() throws IOException {
    Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
    assertEquals("Title: 100", doc.toString());
  }

  /**
   * Tests getFileString().
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  public void testGetFileString() throws IOException {
    Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
    assertNull(doc.getFileString());
  }

  /**
   * Tests setFileString().
   *
   * @throws IOException if an I/O exception occurs during the test.
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
   *
   * @throws IOException if an I/O exception occurs during the test.
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
   *
   * @throws IOException if an I/O exception occurs during the test.
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
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  public void testGenerateUsageStatistics() throws IOException {
    Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
    String result = "";
    result += "This document belongs to the following network: " + doc.getClientId() + "\n";
    result += "The word count is: " + doc.getWordCount() + "\n";
    result += "There are 1 able to see the document.\n";
    result += "The following users are able to see the document:\n";
    result += "userId\n";
    result += "There is/are 0 previous versions on record";

    assertEquals(result, doc.generateUsageStatistics());
  }
}
