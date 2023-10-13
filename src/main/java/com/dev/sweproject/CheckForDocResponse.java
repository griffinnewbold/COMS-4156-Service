package com.dev.sweproject;

public class CheckForDocResponse {

    public boolean documentExists = false;

    public int versions = 0;

    public CheckForDocResponse(boolean _exists, int _versions) {
        documentExists = _exists;
        versions = _versions;
    }

    public void setDocumentExists(boolean documentExists) {
        this.documentExists = documentExists;
    }

    public void setVersions(int versions) {
        this.versions = versions;
    }
}
