package com.zoctan.api.mapper;

import com.zoctan.api.dto.KnowledgeArticleDTO;
import com.zoctan.api.entity.KnowledgeArticle;
import com.zoctan.api.core.mapper.MyMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface KnowledgeArticleMapper extends MyMapper<KnowledgeArticle> {
    List<KnowledgeArticleDTO> selectForAdminWithDetails(
            @Param("title") String title,
            @Param("authorId") Long authorId,
            @Param("status") Byte status
    );
}