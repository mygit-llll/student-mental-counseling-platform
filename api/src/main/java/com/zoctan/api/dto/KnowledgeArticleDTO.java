package com.zoctan.api.dto;

import com.zoctan.api.entity.KnowledgeArticle;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class KnowledgeArticleDTO extends KnowledgeArticle {
    private String authorName;     // 新增
    private String categoryName;   // 新增
}