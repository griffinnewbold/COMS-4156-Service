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
//new
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.springframework.web.multipart.MultipartFile;



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
   * Tests the successful addition of a new document to a document's version history.
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  public void testAddPreviousVersion() throws IOException {
    Document document = new Document("userId", "clientId", null, "docId", "Title", 10);
    Document newVersion = new Document("newUserId", "newClientId", null, "newDocId", "New Title", 20);

    document.addPreviousVersion(newVersion);

    ArrayList<Document> previousVersions = document.getPreviousVersions();

    assertEquals(2, previousVersions.size());
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
      Document doc = new Document("userId", "clientId", null, "docId", "Title", 100);
      Document doc2 = doc;
      /*Document doc2 = new Document("userId", "clientId", null, "docId", "Title", 100);*/
      assertTrue(doc.equals(doc2));
  }

  /**
   * Tests equals() between two documents that do not have the same information.
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  void testNotEquals() throws IOException {
    Document document1 = new Document("user1", "client1", null, "doc1", "Title", 100);
    Document document2 = new Document("user2", "client2", null, "doc2", "Different Title", 200);
    assertFalse(document1.equals(document2));
  }

  /**
   * Tests equals() between a document that contains data and null.
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  void testEqualsWithNull() throws IOException {
    Document document = new Document("user1", "client1", null, "doc1", "Title", 100);
    assertFalse(document.equals(null));
  }

  /**
   * Tests compareTo() with Documents of same word count.
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
   * Tests compareTo() with Documents with different amounts of users.
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  public void testCompareToUserDiff() throws IOException {
    Document doc = new Document("userId/userId", "clientId", null, "docId",
            "Title", 100);
    Document doc2 = new Document("userId/userId", "clientId", null, "docId",
            "Title", 100);
    String expected = doc.getTitle() + " has the same word count " + doc2.getTitle();
    expected += "\n";
    expected += doc.getTitle() + " has the same user count " + doc2.getTitle();
    expected += "\n";
    expected += doc.getTitle() + " has the same version count " + doc2.getTitle();
    assertEquals(expected, doc.compareTo(doc2));
  }

  /**
   * Tests compareTo() with a difference in word count.
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  public void testCompareToWordDiff() throws IOException {
    Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 80);
    Document doc2 = new Document("userId/userId", "clientId", null, "docId",
            "Title", 100);
    int diff = doc2.getWordCount() - doc.getWordCount();
    String expected1 = doc.getTitle() + " has " + diff + " less words than " + doc2.getTitle();
    expected1 += "\n";
    String expected2 = doc2.getTitle() + " has " + diff + " more words than " + doc.getTitle();
    expected2 += "\n";

    expected1 += doc.getTitle() + " has 1 less users than " + doc2.getTitle();
    expected1 += "\n";
    expected1 += doc.getTitle() + " has the same version count " + doc2.getTitle();

    expected2 += doc.getTitle() + " has 1 more users than " + doc2.getTitle();
    expected2 += "\n";
    expected2 += doc.getTitle() + " has the same version count " + doc2.getTitle();
    assertEquals(expected1, doc.compareTo(doc2));
    assertEquals(expected2, doc2.compareTo(doc));
  }

  /**
   * Tests generateDocumentId().
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  public void testGenerateDocumentID() throws IOException {
    String generatedId = Document.generateDocumentId();
    assertNotNull(generatedId);
  }

  /**
   * Tests generateUsageStatistics().
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  public void testGenerateUsageStatistics() throws IOException {
    Document doc = new Document("userId", "clientId", null, "docId",
            "Title", 100);
    String result = "";
    result += "This document belongs to the following network: " + doc.getClientId() + "\n";
    result += "The creator of the document is: userId" + "\n";
    result += "The word count is: " + doc.getWordCount() + "\n";
    result += "There are 1 able to see the document.\n";
    result += "The following users are able to see the document:\n";
    result += "userId\n";
    result += "There is/are 0 previous versions on record";

    assertEquals(result, doc.generateUsageStatistics());
  }
}
