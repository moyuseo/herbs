package com.tcm.vo;

import java.time.LocalDateTime;

public class NewsDetailVO {
    private Long id;
    private String title;
    private String content;
    private String summary;
    private String coverImage;
    private String category;
    private String tags;
    private String author;
    private String source;
    private Integer viewCount;
    private LocalDateTime publishedAt;
    private Long prevNewsId;
    private String prevTitle;
    private Long nextNewsId;
    private String nextTitle;

    public NewsDetailVO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Long getPrevNewsId() {
        return prevNewsId;
    }

    public void setPrevNewsId(Long prevNewsId) {
        this.prevNewsId = prevNewsId;
    }

    public String getPrevTitle() {
        return prevTitle;
    }

    public void setPrevTitle(String prevTitle) {
        this.prevTitle = prevTitle;
    }

    public Long getNextNewsId() {
        return nextNewsId;
    }

    public void setNextNewsId(Long nextNewsId) {
        this.nextNewsId = nextNewsId;
    }

    public String getNextTitle() {
        return nextTitle;
    }

    public void setNextTitle(String nextTitle) {
        this.nextTitle = nextTitle;
    }
}
