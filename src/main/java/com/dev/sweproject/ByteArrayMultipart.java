package com.dev.sweproject;

import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

/**
 * ByteArrayMultipart class contains the process that allows for the encapsulation of the properties of a file in
 * memory as a byte array, and the methods that allow for convenient handling and processing of file data.
 *
 * Guide to implementing interface: https://www.baeldung.com/java-convert-byte-array-to-multipartfile
 */
public class ByteArrayMultipart implements MultipartFile {

  /**
   * The content of the file in a byte array.
   * */
  private final byte[] content;
  /**
   * The name of the file; what it'd end up being named if the file had a previous name.
   * */
  private final String name;
  /**
   * The original name of the file; what it was already named.
   * */
  private final String originalFilename;
  /**
   * The type of content that our file contains
   * */
  private final String contentType;

  /**
   * ByteArrayMultipart constructor
   *
   * @param content represents the content.
   * @param name represents the file name.
   * @param originalFilename represents the original name of the file; what it was already named.
   * @param contentType represents the type of content that our file contains.
   * */
  public ByteArrayMultipart(byte[] content, String name, String originalFilename, String contentType) {
    this.content = content;
    this.name = name;
    this.originalFilename = originalFilename;
    this.contentType = contentType;
  }

  @Override
  @NonNull
  public String getName() {
    return name;
  }

  @Override
  public String getOriginalFilename() {
    return originalFilename;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public boolean isEmpty() {
    return content.length == 0;
  }

  @Override
  public long getSize() {
    return content.length;
  }

  @Override
  @NonNull
  public byte[] getBytes() { return content; }

  @Override
  @NonNull
  public InputStream getInputStream() { return new java.io.ByteArrayInputStream(content); }

  @Override
  public void transferTo(java.io.File dest) throws IllegalStateException {}
}