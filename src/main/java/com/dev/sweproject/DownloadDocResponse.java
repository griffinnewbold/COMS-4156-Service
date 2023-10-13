package com.dev.sweproject;

import org.springframework.web.multipart.MultipartFile;

public class DownloadDocResponse {

    public int versions = 0;

    // documentV1 is the newest, documentV3 is the oldest
    public MultipartFile documentV1;
    public MultipartFile documentV2;
    public MultipartFile documentV3;

    public DocumentStatistics statistics;

    public DownloadDocResponse() {
    }

    public void setVersions(int _versions) {
        this.versions = _versions;
    }

    public void setDocumentV1(MultipartFile _doc) {
        this.documentV1 = _doc;
    }
    public void setDocumentV2(MultipartFile _doc) {
        this.documentV2 = _doc;
    }
    public void setDocumentV3(MultipartFile _doc) {
        this.documentV3 = _doc;
    }
    public void setStats(DocumentStatistics _stats) {
        this.statistics = _stats;
    }
}
