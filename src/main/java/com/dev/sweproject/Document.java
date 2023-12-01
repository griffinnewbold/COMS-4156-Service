package com.dev.sweproject;

import com.google.firebase.database.annotations.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Random;
import org.springframework.web.multipart.MultipartFile;

/**
 *  The document class provides information about the
 *  uploaded file.
 */
public class Document {

  private String userId;
  private String clientId;
  private byte[] fileContents;
  private String thisfileContents;
  private String docId;
  private String title;
  private int wordCount;
  private ArrayList<Document> previousVersions;
  private static final Random RANDOM = new Random();

  /**
   * A constant used for the prefix of the document id upon generation.
   */
  public static final int DOC_ID_LENGTH = 3;

  /**
   * Constructs a Document object with the specified attributes.
   *
   * @param userId      The unique user ID associated with this document.
   * @param clientId    The client ID or identifier for the document.
   * @param file        The MultipartFile object representing the document's content.
   * @param docId       The unique ID of the document.
   * @param title       The title or name of the document.
   * @param wordCount   The word count of the document content.
   * @throws IOException If an IO error occurs while processing the MultipartFile content.
   */
  public Document(String userId, String clientId, MultipartFile file, String docId,
                  String title, int wordCount) throws IOException {
    this.userId = userId;
    this.clientId = clientId;
    this.docId = docId;
    this.title = title;
    this.wordCount = wordCount;
    this.previousVersions = new ArrayList<>(DOC_ID_LENGTH);
    this.previousVersions.add(new Document());
    this.fileContents = new byte[0];
    this.thisfileContents = "#";
    if (file != null) {
      this.fileContents = file.getBytes();
      this.thisfileContents = "#" + Base64.getEncoder().encodeToString(fileContents);
    }
  }

  /**
   * Constructs a Default Document object with the specified attributes.
   *
   * @param userId      The unique user ID associated with this document.
   * @param clientId    The client ID or identifier for the document.
   * @param file        The MultipartFile object representing the document's content.
   * @param docId       The unique ID of the document.
   * @throws IOException If an IO error occurs while processing the MultipartFile content.
   */
  public Document(String userId, String clientId,
                  MultipartFile file, String docId) throws IOException {
    this.userId = userId;
    this.clientId = clientId;
    this.docId = docId;
    this.title = "Untitled Document";
    this.wordCount = 0;
    this.previousVersions = new ArrayList<>(DOC_ID_LENGTH);
    this.previousVersions.add(new Document());
    this.fileContents = new byte[0];
    this.thisfileContents = "#";
    if (file != null) {
      this.fileContents = file.getBytes();
      this.thisfileContents = "#" + Base64.getEncoder().encodeToString(fileContents);
    }
  }

  /**
   * Constructs a Document object with the specified attributes,
   * including previous versions of the document.
   *
   * @param userId            The unique user ID associated with this document.
   * @param clientId          The client ID or identifier for the document.
   * @param file              The MultipartFile object representing the document's content.
   * @param docId             The unique ID of the document.
   * @param title             The title or name of the document.
   * @param wordCount         The word count of the document content.
   * @param previousVersions  An ArrayList of previous versions of the document.
   * @throws IOException      If an IO error occurs while processing the MultipartFile content.
   */
  public Document(String userId, String clientId, MultipartFile file, String docId,
                  String title, int wordCount, ArrayList<Document> previousVersions)
                  throws IOException {
    this.userId = userId;
    this.clientId = clientId;
    this.docId = docId;
    this.title = title;
    this.wordCount = wordCount;
    this.previousVersions = previousVersions;
    this.fileContents = new byte[0];
    this.thisfileContents = "#";
    if (file != null) {
      this.fileContents = file.getBytes();
      this.thisfileContents = "#" + Base64.getEncoder().encodeToString(fileContents);
    }
  }

  //A Private constructor meant only for internal use: factory pattern
  private Document() {
    this.userId = "";
    this.clientId = "";
    this.docId = "";
    this.title = "";
    this.wordCount = 0;
    this.fileContents = new byte[0];
    this.thisfileContents = "#";
  }

  /**
   * Returns the previous versions of the Document
   * object.
   *
   * @return An ArrayList<\Document> containing the
   *     previous versions of the document.
   */
  public ArrayList<Document> getPreviousVersions() {
    return this.previousVersions;
  }

  /**
   * Set previous versions of the document to be the newPreviousVersions passed in.
   *
   * @param newPreviousVersions ArrayList containing the previous versions of the document
   *                            you want to record
   */
  public void setPreviousVersions(ArrayList<Document> newPreviousVersions) {
    this.previousVersions = newPreviousVersions;
  }

  /**
   * Add a new version of the document to the previousVersions ArrayList.
   *
   * @param d a version of a document
   */
  public void addPreviousVersion(Document d) {
    this.previousVersions.add(d);
  }

  /**
   * Retrieves the user's id.
   *
   * @return a String with the user id
   */
  public String getUserId() {
    return this.userId;
  }

  /**
   * Sets the user's id on the document.
   *
   * @param userId the new user id
   */
  public void setUserId(String userId) {
    this.userId = userId;
  }

  /**
   * Retrieves the client's id.
   *
   * @return a String with the client id
   */
  public String getClientId() {
    return this.clientId;
  }

  /**
   * Sets the client's id on the document.
   *
   * @param clientId the new client id
   */
  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  /**
   * Retrieves the document's id.
   *
   * @return a string with the document's id
   */
  public String getDocId() {
    return this.docId;
  }

  /**
   * Reassign the docs id.
   *
   * @param docId new id
   */
  public void setDocId(String docId) {
    this.docId = docId;
  }

  /**
   * Retrieves the title.
   *
   * @return A string with the title
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Sets the title of the document.
   *
   * @param title A string for the title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Retrieves the document's word count.
   *
   * @return an int representing how many words exist
   */
  public int getWordCount() {
    return this.wordCount;
  }

  /**
   * Retrieves the document's word count.
   *
   * @return an int representing how many words exist
   */
  public String getFileString() {
    return this.thisfileContents;
  }

  /**
   * Reassigns the file's contents.
   *
   * @param newString A String representing the file's contents.
   */
  public void setFileString(String newString) {
    this.thisfileContents = newString;
  }

  /**
   * Updates the word count of the document.
   *
   * @param wordCount count of words
   */
  public void setWordCount(int wordCount) {
    this.wordCount = wordCount;
  }

  /**
   * Gives a String representation of the object.
   *
   * @return a string
   */
  @Override
  public String toString() {
    return title + ": " + wordCount;
  }

  /**
   * Converts a HashMap in JSON format to a Document object.
   *
   * @param map A HashMap containing key-value pairs representing document attributes.
   *            Required keys: "userId," "clientId," "docId," "title," "previousVersions,"
   *            "fileString," and "wordCount."
   * @return A Document wrapper type created from the provided HashMap.
   * @throws IOException If an IO error occurs during document creation.
   */
  public static Document convertToDocument(HashMap<String, Object> map) throws IOException {
    String userId = (String) map.get("userId");
    String clientId = (String) map.get("clientId");
    String docId = (String) map.get("docId");
    String title = (String) map.get("title");
    ArrayList<Document> previous = (ArrayList<Document>) map.get("previousVersions");
    String fileString = (String) map.get("fileString");
    byte[] fileContents;
    if (fileString != null) {
      fileContents = Base64.getDecoder().decode(fileString.substring(1));
    } else {
      fileContents = new byte[0];
    }
    int wordCount = ((Long) map.get("wordCount")).intValue();

    return new Document(userId, clientId, createFile(fileContents, title), docId,
        title, wordCount, previous);
  }

  /**
   * Indicates whether some other object is "equal to" this Document.
   * Two Document objects are considered equal if they have the same word count, title,
   * document ID, client ID, and user ID.
   *
   * @param o The object to compare for equality.
   * @return {@code true} if the objects are equal; {@code false} otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Document other = (Document) o;

    return this.getWordCount() == other.getWordCount()
        && this.getTitle().equals(other.getTitle())
        && this.getDocId().equals(other.getDocId())
        && this.getClientId().equals(other.getClientId())
        && this.getUserId().equals(other.getUserId())
        && this.getFileString().equals(other.getFileString());
  }

  @Override
  public int hashCode() {
    return super.hashCode() + 1;
  }

  /**
   * Counts the words in a file.
   *
   * @param contents The contents in a file
   * @return The word count
   */
  public static int countWords(byte[] contents) {
    int wordCount = 0;

    try {
      try (StringReader stringReader = new StringReader(
          new String(contents, StandardCharsets.UTF_8));
           BufferedReader reader = new BufferedReader(stringReader)) {

        String line = reader.readLine();
        while (line != null) {
          String[] words = line.split("\\s+"); // splits by spaces
          wordCount += words.length;
          line = reader.readLine();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return wordCount;
  }


  /**
   * Generates a document id.
   *
   * @return A string representing the document id
   */
  public static String generateDocumentId() {
    String timestamp = String.valueOf(System.currentTimeMillis());
    StringBuilder netId = new StringBuilder();

    for (int i = 0; i < DOC_ID_LENGTH; i++) {
      netId.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(RANDOM.nextInt(26)));
    }
    return "doc" + netId + timestamp;
  }

  /**
   * Generates a multipart file.
   *
   * @param  contents a byte[] used for making the file.
   * @param  name a String denoting the file name.
   * @return A MultipartFile containing the contents of the file
   */
  public static MultipartFile createFile(byte[] contents, String name) {
    return new ByteArrayMultipart(contents, name, name, "text/plain");
  }

  /**
   * Generates statistics on a document.
   *
   * @return String containing the statistics (client id, word count, users able to see the file,
   *     amount of previous versions) on a document
   */
  public String generateUsageStatistics() {
    StringBuilder result = new StringBuilder();

    result.append("This document belongs to the following network: ").append(clientId).append("\n");
    result.append("The creator of the document is: ").append(getOriginalUser()).append("\n");
    result.append("The word count is: ").append(wordCount).append("\n");
    result.append("There are ").append(countUsers()).append(" able to see the document.\n");
    result.append("The following users are able to see the document:\n");

    int slashLocation = userId.indexOf("/");
    int start = 0;
    for (int i = 0; i < countUsers(); i++) {
      if (slashLocation != -1) {
        result.append(userId.substring(start, slashLocation));
      } else {
        result.append(userId.substring(start));
      }
      result.append("\n");
      start = slashLocation + 1;
      slashLocation = userId.indexOf("/", start);
    }

    result.append("There is/are ").append(previousVersions.size() - 1)
        .append(" previous versions on record");

    return result.toString();
  }


  //private helper method for counting users expects '/' as delimiter.
  private int countUsers() {
    int users = 1;
    char delimiter = '/';
    for (int i = 0; i < userId.length(); i++) {
      if (userId.charAt(i) == delimiter) {
        users += 1;
      }
    }
    return users;
  }

  /**
   * Compare two documents based on word count, users, and previous versions.
   *
   * @param other A Document object used for comparison
   * @return String containing the difference of word count, users, and previous versions between
   *         two documents
   */
  public String compareTo(@NotNull Document other) {
    String result = "";
    int wordCountDiff = this.wordCount - other.wordCount;
    int userCountDiff = this.countUsers() - other.countUsers();
    String hasString = " has ";

    if (wordCountDiff > 0) {
      result += this.getTitle() + hasString + wordCountDiff + " more words than " + other.getTitle();
    } else if (wordCountDiff < 0) {
      result += this.getTitle() + hasString + (-1 * wordCountDiff)
         + " less words than " + other.getTitle();
    } else {
      result += this.getTitle() + " has the same word count " + other.getTitle();
    }
    result += "\n";

    //logic dealing with user counts
    if (userCountDiff > 0) {
      result += this.getTitle() + hasString + userCountDiff + " more users than " + other.getTitle();
    } else if (userCountDiff < 0) {
      result += this.getTitle() + hasString + (-1 * userCountDiff) + " less users than "
          + other.getTitle();
    } else {
      result += this.getTitle() + " has the same user count " + other.getTitle();
    }
    result += "\n";

    //logic dealing with version counts
    int versionCountDiff = (this.previousVersions.size() - 1) - (other.previousVersions.size() - 1);
    if (versionCountDiff > 0) {
      result += this.getTitle() + hasString + versionCountDiff + " more versions than "
          + other.getTitle();
    } else if (versionCountDiff < 0) {
      result += this.getTitle() + hasString + (-1 * versionCountDiff) + " less versions than "
          + other.getTitle();
    } else {
      result += this.getTitle() + " has the same version count " + other.getTitle();
    }
    return result;
  }

  private String getOriginalUser() {
    int idx = userId.indexOf("/");
    if (idx == -1) {
      return userId;
    }
    return userId.substring(0, idx);
  }

}