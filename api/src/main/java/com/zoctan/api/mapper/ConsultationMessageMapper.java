package com.zoctan.api.mapper;

import com.zoctan.api.core.mapper.MyMapper;
import com.zoctan.api.entity.ConsultationMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConsultationMessageMapper extends MyMapper<ConsultationMessage> {
    List<ConsultationMessage> findBySessionId(@Param("sessionId") Long sessionId);

    String selectLastEncryptedContentBySessionId(@Param("sessionId") Long sessionId);
}