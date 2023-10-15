package com.dev.sweproject;

import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

//guide to implementing interface: https://www.baeldung.com/java-convert-byte-array-to-multipartfile
public class ByteArrayMultipart implements MultipartFile {

  private final byte[] content;
  private final String name;
  private final String originalFilename;
  private final String contentType;

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
  public byte[] getBytes() throws IOException {
    return content;
  }

  @Override
  @NonNull
  public InputStream getInputStream() throws IOException {
    return new java.io.ByteArrayInputStream(content);
  }

  @Override
  public void transferTo(java.io.File dest) throws IOException, IllegalStateException {

  }
}
