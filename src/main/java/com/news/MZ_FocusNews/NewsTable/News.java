package com.news.MZ_FocusNews.NewsTable;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Integer newsId;

    @Column(name = "view")
    private int view;

    @Column(name = "link", length = 600, nullable = false)
    private String link;

    @Column(name = "summary", length = 300)
    private String summary;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "category", length = 15, nullable = false)
    private String category;

    @Column(name = "date", columnDefinition = "DATETIME", nullable = false)
    private LocalDateTime date;

    @Column(name = "related_news1")
    private Integer relatedNews1;

    @Column(name = "related_news2")
    private Integer relatedNews2;

    @Column(name = "publish", length = 20)
    private String publish;

    @Column(name = "img_Url", length = 500)
    private String imgUrl;

    // Getters and setters
    public Integer getNewsId() {
        return newsId;
    }

    public void setNewsId(Integer newsId) {
        this.newsId = newsId;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getRelatedNews1() {
        return relatedNews1;
    }

    public void setRelatedNews1(Integer relatedNews1) {
        this.relatedNews1 = relatedNews1;
    }

    public Integer getRelatedNews2() {
        return relatedNews2;
    }

    public void setRelatedNews2(Integer relatedNews2) {
        this.relatedNews2 = relatedNews2;
    }

    public String getPublish() {
        return publish;
    }

    public void setPublish(String publish) {
        this.publish = publish;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
