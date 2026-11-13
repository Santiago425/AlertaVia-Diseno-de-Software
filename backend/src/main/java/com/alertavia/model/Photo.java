package com.alertavia.model;

import java.time.LocalDateTime;

public class Photo {

    private String url;
    private LocalDateTime uploadedAt;

    protected Photo() {
    }

    public Photo(String url) {
        this.url = url;
        this.uploadedAt = LocalDateTime.now();
    }

    public String getUrl() {
        return url;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
}
