package com.dev.sweproject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
   * Tests the successful addition of a new document to a document's version history.
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  public void testAddPreviousVersion() throws IOException {
    Document document = new Document("userId", "clientId", null, "docId", "Title", 10);
    Document newVersion = new Document("newUserId", "newClientId", null, "newDocId",
        "New Title", 20);

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
    assertEquals(doc.getFileString(), "#");
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
    Document doc2 = new Document("userId", "clientId", null, "docId",
        "Title", 100);
    boolean isEqual = doc.equals(doc2);
    boolean isEqualSame = doc.equals(doc);
    assertTrue(isEqual);
    assertTrue(isEqualSame);
  }

  /**
   * Tests equals() between two documents that do not have the same information.
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  void testNotEquals() throws IOException {
    Document document1 = new Document("user1", "client1", null, "doc1", "Title", 100);
    Document document2 = new Document("user1", "client1", null, "doc1", "Title", 200);
    assertFalse(document1.equals(document2));
    Document document3 = new Document("user1", "client1", null, "doc1", "Different Title", 100);
    assertFalse(document1.equals(document3));
    Document document4 = new Document("user1", "client1", null, "doc2", "Title", 100);
    assertFalse(document1.equals(document4));
    Document document5 = new Document("user1", "client2", null, "doc1", "Title", 100);
    assertFalse(document1.equals(document5));
    Document document6 = new Document("user2", "client1", null, "doc1", "Title", 100);
    assertFalse(document1.equals(document6));
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
    Document doc = new Document("userId", "clientId", null, "docId", "Title", 100);
    StringBuilder expectedBuilder = new StringBuilder();
    expectedBuilder.append("The following statistics are available for the first document: \n\n");
    expectedBuilder.append(doc.generateUsageStatistics()).append("\n\n");
    expectedBuilder.append("The following statistics are available for the second document: \n\n");
    Document doc2 = new Document("userId/userId", "clientId", null, "docId", "Title", 100);
    expectedBuilder.append(doc2.generateUsageStatistics()).append("\n\n");
    expectedBuilder.append(doc.getTitle()).append(" has the same word count ")
        .append(doc2.getTitle()).append("\n");
    expectedBuilder.append(doc.getTitle()).append(" has 1 less users than ")
        .append(doc2.getTitle()).append("\n");
    expectedBuilder.append(doc.getTitle()).append(" has the same version count ")
        .append(doc2.getTitle());
    assertEquals(expectedBuilder.toString(), doc.compareTo(doc2));
  }

  /**
   * Tests compareTo() with Documents with different amounts of users.
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  public void testCompareToUserDiff() throws IOException {
    Document doc = new Document("userId/userId", "clientId", null, "docId", "Title", 100);
    StringBuilder expectedBuilder = new StringBuilder();
    expectedBuilder.append("The following statistics are available for the first document: \n\n");
    expectedBuilder.append(doc.generateUsageStatistics()).append("\n\n");
    expectedBuilder.append("The following statistics are available for the second document: \n\n");
    Document doc2 = new Document("userId/userId", "clientId", null, "docId", "Title", 100);
    expectedBuilder.append(doc2.generateUsageStatistics()).append("\n\n");
    expectedBuilder.append(doc.getTitle()).append(" has the same word count ")
        .append(doc2.getTitle()).append("\n");
    expectedBuilder.append(doc.getTitle()).append(" has the same user count ")
        .append(doc2.getTitle()).append("\n");
    expectedBuilder.append(doc.getTitle()).append(" has the same version count ")
        .append(doc2.getTitle());
    assertEquals(expectedBuilder.toString(), doc.compareTo(doc2));
  }

  /**
   * Tests compareTo() with a difference in word count.
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  public void testCompareToWordDiff() throws IOException {
    Document doc = new Document("userId", "clientId", null, "docId", "Title", 80);
    StringBuilder expectedOneBuilder = new StringBuilder();
    expectedOneBuilder.append("The following statistics are available for the first document: "
        + "\n\n");
    expectedOneBuilder.append(doc.generateUsageStatistics()).append("\n\n");
    expectedOneBuilder.append("The following statistics are available for the second document: "
        + "\n\n");
    Document doc2 = new Document("userId/userId", "clientId", null, "docId", "Title", 100);
    expectedOneBuilder.append(doc2.generateUsageStatistics()).append("\n\n");
    StringBuilder expectedTwoBuilder = new StringBuilder();
    expectedTwoBuilder.append("The following statistics are available for the first document: "
        + "\n\n");
    expectedTwoBuilder.append(doc2.generateUsageStatistics()).append("\n\n");
    expectedTwoBuilder.append("The following statistics are available for the second document: "
        + "\n\n");
    expectedTwoBuilder.append(doc.generateUsageStatistics()).append("\n\n");
    int diff = doc2.getWordCount() - doc.getWordCount();
    expectedOneBuilder.append(doc.getTitle()).append(" has ").append(diff)
        .append(" less words than ")
            .append(doc2.getTitle()).append("\n");
    expectedTwoBuilder.append(doc2.getTitle()).append(" has ").append(diff)
        .append(" more words than ")
            .append(doc.getTitle()).append("\n");

    expectedOneBuilder.append(doc.getTitle()).append(" has 1 less users than ")
        .append(doc2.getTitle()).append("\n").append(doc.getTitle())
        .append(" has the same version count ").append(doc2.getTitle());

    expectedTwoBuilder.append(doc.getTitle()).append(" has 1 more users than ")
        .append(doc2.getTitle()).append("\n").append(doc.getTitle())
        .append(" has the same version count ").append(doc2.getTitle());

    assertEquals(expectedOneBuilder.toString(), doc.compareTo(doc2));
    assertEquals(expectedTwoBuilder.toString(), doc2.compareTo(doc));
  }

  /**
   * Tests compareTo() with a difference in version count.
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  public void testCompareToVersionCount() throws IOException {
    Document doc = new Document("userId", "clientId", null, "docId", "Title", 80);
    Document docPrev = new Document("userId", "clientId", null, "docId", "Title", 80);
    doc.addPreviousVersion(docPrev);

    StringBuilder expectedOneBuilder = new StringBuilder();
    expectedOneBuilder.append("The following statistics are available for the first document: "
        + "\n\n");
    expectedOneBuilder.append(doc.generateUsageStatistics()).append("\n\n");
    expectedOneBuilder.append("The following statistics are available for the second document: "
        + "\n\n");
    Document doc2 = new Document("userId/userId", "clientId", null, "docId", "Title", 80);
    expectedOneBuilder.append(doc2.generateUsageStatistics()).append("\n\n");
    StringBuilder expectedTwoBuilder = new StringBuilder();
    expectedTwoBuilder.append("The following statistics are available for the first document: "
        + "\n\n");
    expectedTwoBuilder.append(doc2.generateUsageStatistics()).append("\n\n");
    expectedTwoBuilder.append("The following statistics are available for the second document: "
        + "\n\n");
    expectedTwoBuilder.append(doc.generateUsageStatistics()).append("\n\n");
    expectedOneBuilder.append(doc.getTitle()).append(" has the same word count ")
        .append(doc2.getTitle()).append("\n");
    expectedOneBuilder.append(doc.getTitle()).append(" has 1 less users than ")
        .append(doc2.getTitle()).append("\n");
    expectedOneBuilder.append(doc.getTitle()).append(" has 1 more versions than ")
        .append(doc2.getTitle());
    expectedTwoBuilder.append(doc2.getTitle()).append(" has the same word count ")
        .append(doc.getTitle()).append("\n");
    expectedTwoBuilder.append(doc2.getTitle()).append(" has 1 more users than ")
        .append(doc.getTitle()).append("\n");
    expectedTwoBuilder.append(doc2.getTitle()).append(" has 1 less versions than ")
        .append(doc.getTitle());
    assertEquals(expectedOneBuilder.toString(), doc.compareTo(doc2));
    assertEquals(expectedTwoBuilder.toString(), doc2.compareTo(doc));
  }

  /**
   * Tests generateDocumentId().
   *
   * @throws IOException if an I/O exception occurs during the test.
   */
  @Test
  public void testGenerateDocumentId() throws IOException {
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
    Document doc = new Document("userId", "clientId", null, "docId", "Title", 100);

    StringBuilder result = new StringBuilder();
    result.append("This document belongs to the following network: ").append(doc.getClientId())
        .append("\n");
    result.append("The creator of the document is: userId").append("\n");
    result.append("The word count is: ").append(doc.getWordCount()).append("\n");
    result.append("There are 1 able to see the document.\n");
    result.append("The following users are able to see the document:\n");
    result.append("userId\n");
    result.append("There is/are 0 previous versions on record");

    StringBuilder result2 = new StringBuilder();
    result2.append("This document belongs to the following network: ").append(doc.getClientId())
        .append("\n");
    result2.append("The creator of the document is: userId").append("\n");
    result2.append("The word count is: ").append(doc.getWordCount()).append("\n");
    result2.append("There are 2 able to see the document.\n");
    result2.append("The following users are able to see the document:\n");
    result2.append("userId\notherUserId\n");
    result2.append("There is/are 0 previous versions on record");

    Document doc2 = new Document("userId/otherUserId", "clientId", null, "docId", "Title", 100);

    assertEquals(result.toString(), doc.generateUsageStatistics());
    assertEquals(result2.toString(), doc2.generateUsageStatistics());
  }
}
