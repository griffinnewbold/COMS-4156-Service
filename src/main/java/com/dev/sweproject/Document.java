package com.dev.sweproject;


import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Document class contains information about a document.
 */
public class Document {
  private String userId;
  private String clientId;
  private byte[] fileContents;
  private String filePath;
  private String docId;
  private String title;
  private String type;
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
   * @param type the type of document
   * @param wordCount the word count of the document
   */
  public Document(String userId, String clientId, MultipartFile file, String docId,
                  String title, String type, int wordCount) throws IOException {
    this.userId = userId;
    this.clientId = clientId;
    this.docId = docId;
    this.title = title;
    this.type = type;
    this.wordCount = wordCount;
    this.previousVersions = new ArrayList<>(DOC_ID_LENGTH);
    this.fileContents = file.getBytes();

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
    this.type = "txt";
    this.wordCount = 0;
    this.previousVersions = new ArrayList<>(DOC_ID_LENGTH);
    this.fileContents = file.getBytes();
  }

  public ArrayList<Document> getPreviousVersions() {
    return this.previousVersions;
  }

  public void addPreviousVerison(Document toAdd) {
    previousVersions.add(toAdd);
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
   * Retrieves the type.
   *
   * @return String with the type
   */
  public String getType() {
    return this.type;
  }

  /**
   * Reassigns the type of the document.
   *
   * @param type new type
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * retrieves the document's word count.
   *
   * @return an int representing how many words exist
   */
  public int getWordCount() {
    return this.wordCount;
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
    String type = (String) map.get("type");
    String docId = (String) map.get("docId");
    String title = (String) map.get("title");
    byte[] fileContents = (byte[]) map.get("fileContents");
    int wordCount = ((Long) map.get("wordCount")).intValue();

    return new Document(userId, clientId, createFile(fileContents, title), docId, title, type, wordCount);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Document document = (Document) o;

    return (this.getWordCount() == document.getWordCount())
        && (this.getType().equals(document.getType()))
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

}