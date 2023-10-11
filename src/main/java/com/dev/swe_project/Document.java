package com.dev.swe_project;

import java.util.*;
import java.io.*;
public class Document{
    private String clientID;
    private File file;
    private byte[] fileContains;
    private String docID;
    private String title;
    private String type;
    private int wordCount;

    public Document(String clientID, File file, String docID, String title, String type, int wordCount){
        this.clientID = clientID;
        this.file = file;
       // this.fileContains = readFile(file);
        this.docID = docID;
        this.title = title;
        this.type = type;
        this.wordCount = wordCount;
    }
    public String getClientID(){
        return this.clientID;
    }
    public void setClientID(String clientID){
        this.clientID = clientID;
    }
    public File getFile(){
        return this.file;
        /*maybe could do something like this as well?:
        * public void getFile(){
        *   System.out.println("File Name: " + file.getName());
            System.out.println("File Path: " + file.getPath());
        * }
        * */
    }
    public void setFile(File file){
        this.file = file;
    }
   // private byte[] readFile(File file) {
        // read file
        //return _____
    //}
    public String getDocID(){
        return this.docID;
    }
    public void setDocID(String docID){
        this.docID = docID;
    }
    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getType(){
        return this.type;
    }
    public void setType(String type){
        this.type = type;
    }
    public int getWordCount(){
        return this.wordCount;
    }
    public void setWordCount(int wordCount){
        this.wordCount = wordCount;
    }
}