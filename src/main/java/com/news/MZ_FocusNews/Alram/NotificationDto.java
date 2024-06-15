package com.news.MZ_FocusNews.Alram;

public class NotificationDto {
    private String userId;
    private String newsTitle;

    public NotificationDto(String userId, String newsTitle) {
        this.userId = userId;
        this.newsTitle = newsTitle;
    }

    // Getters and Setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }
}