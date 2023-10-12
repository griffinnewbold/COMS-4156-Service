package com.dev.sweproject;


import java.io.File;

/**
 * Document class contains information about a document.
 */
public class Document {
  private String clientId;
  private File file;
  private byte[] fileContains;
  private String docId;
  private String title;
  private String type;
  private int wordCount;

  /**
   * Constructs a Document object.
   *
   *
   * @param clientId represents the clientid
   * @param file represents the file object
   * @param docId the document's id
   * @param title the title of the doc
   * @param type the type of document
   * @param wordCount the word count of the document
   */
  public Document(String clientId, File file, String docId,
                  String title, String type, int wordCount) {
    this.clientId = clientId;
    this.file = file;
    // this.fileContains = readFile(file);
    this.docId = docId;
    this.title = title;
    this.type = type;
    this.wordCount = wordCount;
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
   * Retrieves the file object.
   *
   * @return a File object
   */
  public File getFile() {
    return this.file;
  }

  /**
   * Reassigns the file object.
   *
   * @param file the new file
   */
  public void setFile(File file) {
    this.file = file;
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

}