package com.zoctan.api.mapper;

import com.zoctan.api.entity.KnowledgeCategory;
import com.zoctan.api.core.mapper.MyMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KnowledgeCategoryMapper extends MyMapper<KnowledgeCategory> {
}