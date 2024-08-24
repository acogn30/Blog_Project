package com.example.blog.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
@Entity
@Table(name = "articleVo")
@EntityListeners(AuditingEntityListener.class)// 啟用審計功能
public class ArticleVo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;
    private Long authorId;
    private String title;
    private String contentTEXT;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime publishedAt;
    @LastModifiedDate
    private LocalDateTime lastEditedAt;
    // Getters and setters
    public Long getArticleId() {
        return articleId;
    }
    public void setArticleid(Long articleId) {
        this.articleId = articleId;
    }
    public Long getAuthorId() {
        return authorId;
    }
    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContentTEXT() {
        return contentTEXT;
    }
    public void setContentTEXT(String contentTEXT) {
        this.contentTEXT = contentTEXT;
    }
    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }
    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
    public LocalDateTime getLastEditedAt() {
        return lastEditedAt;
    }
    public void setLastEditedAt(LocalDateTime lastEditedAt) {
        this.lastEditedAt = lastEditedAt;
    }
    @Override
    public String toString() {
        return "ArticleVo [articleId=" + articleId + ", authorId=" + authorId + ", title=" + title
                + ", contentTEXT=" + contentTEXT + ", publishedAt=" + publishedAt + ", lastEditedAt="
                + lastEditedAt + "]";
    }
    
}