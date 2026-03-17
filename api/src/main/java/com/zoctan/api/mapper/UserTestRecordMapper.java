package com.zoctan.api.mapper;

import com.zoctan.api.core.mapper.MyMapper;
import com.zoctan.api.entity.UserTestRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserTestRecordMapper extends MyMapper<UserTestRecord> {

    void insertRecord(UserTestRecord record);
    /**
     * 根据用户名和测试ID查询单条记录（含 testTitle）
     */
    UserTestRecord selectByUserNameAndTestId(@Param("name") String username, @Param("testId") Long testId);

    /**
     * 查询用户的所有测试历史记录
     */
    List<UserTestRecord> selectHistoryByUserName(@Param("name") String username);


    List<UserTestRecord> selectByScoreGreaterThan(Integer score);

    List<UserTestRecord> selectHighRiskRecordsWithUsername(Integer threshold);

}