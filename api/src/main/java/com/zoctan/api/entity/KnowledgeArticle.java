package com.zoctan.api.entity;

import javax.persistence.*;

@Table(name = "knowledge_article")
public class KnowledgeArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private String images;          // JSON string: ["url1", "url2", ...]
    private Long categoryId;
    private Long authorId;
    private Byte status;            // 1=待审, 2=已发布, 3=驳回, 4=删除
    private java.util.Date createTime;
    private java.util.Date publishTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public Byte getStatus() { return status; }
    public void setStatus(Byte status) { this.status = status; }

    public java.util.Date getCreateTime() { return createTime; }
    public void setCreateTime(java.util.Date createTime) { this.createTime = createTime; }

    public java.util.Date getPublishTime() { return publishTime; }
    public void setPublishTime(java.util.Date publishTime) { this.publishTime = publishTime; }
}