package com.zoctan.api.mapper;

import com.zoctan.api.core.mapper.MyMapper;
import com.zoctan.api.entity.PsychologicalTest;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PsychologicalTestMapper extends MyMapper<PsychologicalTest> {
    List<PsychologicalTest> selectAllTests();
}