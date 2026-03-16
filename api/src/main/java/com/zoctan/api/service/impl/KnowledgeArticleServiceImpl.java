package com.zoctan.api.service.impl;

import com.zoctan.api.mapper.KnowledgeArticleMapper;
import com.zoctan.api.entity.KnowledgeArticle;
import com.zoctan.api.service.KnowledgeArticleService;
import com.zoctan.api.core.service.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class KnowledgeArticleServiceImpl extends AbstractService<KnowledgeArticle> implements KnowledgeArticleService {

    private final KnowledgeArticleMapper knowledgeArticleMapper;

    public KnowledgeArticleServiceImpl(KnowledgeArticleMapper knowledgeArticleMapper) {
        this.knowledgeArticleMapper = knowledgeArticleMapper;
    }
}