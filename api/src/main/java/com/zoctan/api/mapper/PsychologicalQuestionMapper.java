package com.zoctan.api.mapper;

import com.zoctan.api.core.mapper.MyMapper;
import com.zoctan.api.entity.PsychologicalQuestion;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PsychologicalQuestionMapper extends MyMapper<PsychologicalQuestion> {
    List<PsychologicalQuestion> selectByTestId(Long testId);
}