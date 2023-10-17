package com.dev.sweproject;


import com.google.firebase.database.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Random;

/**
 * Document class contains information about a document.
 */
public class Document {
  private String userId;
  private String clientId;
  private byte[] fileContents;
  private String thisfileContents;
  private String docId;
  private String title;
  private int wordCount;
  public static final int DOC_ID_LENGTH = 3;
  private ArrayList<Document> previousVersions;

  /**
   * Constructs a Document object.
   *
   * @param userId represents the userid
   * @param clientId represents the clientid
   * @param file represents the file object
   * @param docId the document's id
   * @param title the title of the doc
   * @param wordCount the word count of the document
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
    if (file != null) {
      this.fileContents = file.getBytes();
      this.thisfileContents = "#" + Base64.getEncoder().encodeToString(file.getBytes());
    }

  }
  /**
   * Constructs a Default Document object.
   *
   * @param userId represents the userid
   * @param clientId represents the clientid
   * @param file represents the file object
   * @param docId the document's id
   */
  public Document(String userId, String clientId, MultipartFile file, String docId) throws IOException {
    this.userId = userId;
    this.clientId = clientId;
    this.docId = docId;
    this.title = "Untitled Document";
    this.wordCount = 0;
    this.previousVersions = new ArrayList<>(DOC_ID_LENGTH);
    this.previousVersions.add(new Document());
    if (file != null) {
      this.fileContents = file.getBytes();
      this.thisfileContents = "#" + Base64.getEncoder().encodeToString(file.getBytes());
    }
  }

  public Document(String userId, String clientId, MultipartFile file, String docId,
                  String title, int wordCount, ArrayList<Document> previousVersions) throws IOException {
    this.userId = userId;
    this.clientId = clientId;
    this.docId = docId;
    this.title = title;
    this.wordCount = wordCount;
    this.previousVersions = previousVersions;
    if (file != null) {
      this.fileContents = file.getBytes();
      this.thisfileContents = "#" + Base64.getEncoder().encodeToString(file.getBytes());
    }
  }

  private Document() {
    this.userId = "";
    this.clientId = "";
    this.docId = "";
    this.title = "";
    this.wordCount = 0;
    this.fileContents = new byte[0];
    this.thisfileContents = "#";
  }

  public ArrayList<Document> getPreviousVersions() {
    return this.previousVersions;
  }

  public void setPreviousVersions(ArrayList<Document> newPreviousVersions) {
    this.previousVersions = newPreviousVersions;
  }

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
   * @return A string with the title.
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Sets the title of the document.
   *
   * @param title A string for the title.
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * retrieves the document's word count.
   *
   * @return an int representing how many words exist
   */
  public int getWordCount() {
    return this.wordCount;
  }


  public String getFileString() {
    return this.thisfileContents;
  }

  public void setFileString(String newString) {
    this.thisfileContents = newString;
  }

  /**
   * updates the word count of the document.
   *
   * @param wordCount count of words
   */
  public void setWordCount(int wordCount) {
    this.wordCount = wordCount;
  }

  /**
   * Gives a String representation of the object.
   *
   * @return a string.
   */
  @Override
  public String toString() {
    return title + ": " + wordCount;
  }

  /**
   * Converts the JSON format to a Document object.
   *
   * @param map A hashmap containing the key value pairs
   * @return A Document wrapper type
   */
  public static Document convertToDocument(HashMap<String, Object> map) throws IOException {
    String userId = (String) map.get("userId");
    String clientId = (String) map.get("clientId");
    String docId = (String) map.get("docId");
    String title = (String) map.get("title");
    ArrayList<Document> previous = (ArrayList<Document>)map.get("previousVersions");
    String fileString = (String) map.get("fileString");
    byte[] fileContents = Base64.getDecoder().decode(fileString.substring(1));
    int wordCount = ((Long) map.get("wordCount")).intValue();

    return new Document(userId, clientId, createFile(fileContents, title), docId, title, wordCount, previous);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Document document = (Document) o;

    return (this.getWordCount() == document.getWordCount())
        && (this.getTitle().equals(document.getTitle()))
        && (this.getDocId().equals(document.getDocId()))
        && (this.getClientId().equals(document.getClientId()))
        && (this.getUserId().equals(document.getUserId()));
  }


  public static int countWords(byte[] contents) throws IOException {
    int wordCount = 0;

    try {
      String text = new String(contents, StandardCharsets.UTF_8);
      StringReader stringReader = new StringReader(text);

      try (BufferedReader reader = new BufferedReader(stringReader)) {
        String line;
        while ((line = reader.readLine()) != null) {
          String[] words = line.split("\\s+");
          wordCount += words.length;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return wordCount;
  }

  public static String generateDocumentId() {
    String timestamp = String.valueOf(System.currentTimeMillis());
    StringBuilder netId = new StringBuilder();
    Random random = new Random();

    for (int i = 0; i < DOC_ID_LENGTH; i++) {
      netId.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(random.nextInt(26)));
    }
    return "doc" + netId + timestamp;
  }


  public static MultipartFile createFile(byte[] contents, String name) {
    return new ByteArrayMultipart(contents, name, name, "text/plain");
  }

  public String generateUsageStatistics() {
    String result = "";
    result += "This document belongs to the following network: " + clientId + "\n";
    result += "The word count is: " + wordCount + "\n";
    result += "There are " + countUsers() + " able to see the document.\n";
    result += "There is/are " + (previousVersions.size() - 1) + "previous versions on record";
    return result;
  }

  private int countUsers() {
    int users = 1;
    for (int i = 0; i < userId.length(); i++) {
      if (userId.charAt(i) == '/') {
        users += 1;
      }
    }
    return users;
  }


  public String compareTo(@NotNull Document other) {
    String result = "";
    int wordCountDiff = this.wordCount - other.wordCount;
    int userCountDiff = this.countUsers() - other.countUsers();
    int versionCountDiff = (this.previousVersions.size() - 1) - (other.previousVersions.size() - 1);

    if (wordCountDiff > 0) {
      result += this.getTitle() + " has " + wordCountDiff + " more words than " + other.getTitle();
    } else if (wordCountDiff < 0) {
      result += this.getTitle() + " has " + (-1 * wordCountDiff) + " less words than " + other.getTitle();
    } else {
      result += this.getTitle() + " has the same word count " + other.getTitle();
    }
    result += "\n";

    if (userCountDiff > 0) {
      result += this.getTitle() + " has " + userCountDiff + " more users than " + other.getTitle();
    } else if (userCountDiff < 0) {
      result += this.getTitle() + " has " + (-1 * userCountDiff) + " less users than " + other.getTitle();
    } else {
      result += this.getTitle() + " has the same user count " + other.getTitle();
    }
    result += "\n";

    if (versionCountDiff > 0) {
      result += this.getTitle() + " has " + versionCountDiff + " more versions than " + other.getTitle();
    } else if (versionCountDiff < 0) {
      result += this.getTitle() + " has " + (-1 * versionCountDiff) + " less versions than " + other.getTitle();
    } else {
      result += this.getTitle() + " has the same version count " + other.getTitle();
    }

    return result;
  }

}