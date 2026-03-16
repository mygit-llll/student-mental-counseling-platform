package com.zoctan.api.service.impl;

import com.zoctan.api.entity.UserTestRecord;
import com.zoctan.api.mapper.UserTestRecordMapper;
import com.zoctan.api.service.UserTestRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserTestRecordServiceImpl implements UserTestRecordService {

    @Resource
    private UserTestRecordMapper userTestRecordMapper;

    @Override
    public UserTestRecord findUserTestResultByUsernameAndTestId(String username, Long testId) {
        return userTestRecordMapper.selectByUserNameAndTestId(username, testId);
    }

    @Override
    public List<UserTestRecord> findUserTestHistoryByUsername(String username) {
        return userTestRecordMapper.selectHistoryByUserName(username);
    }
}
