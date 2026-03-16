package com.zoctan.api.mapper;

import com.zoctan.api.core.mapper.MyMapper;
import com.zoctan.api.entity.ConsultationSession;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConsultationSessionMapper extends MyMapper<ConsultationSession> {
    ConsultationSession findByUserIdAndCounselorId(@Param("userId") Long userId, @Param("counselorId") Long counselorId);

    List<ConsultationSession> findByUserIdOrCounselorId(@Param("userId") Long userId);
}