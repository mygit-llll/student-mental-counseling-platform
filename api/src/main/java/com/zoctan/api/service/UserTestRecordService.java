package com.zoctan.api.service;

import com.zoctan.api.entity.UserTestRecord;

import java.util.List;

public interface UserTestRecordService {
    UserTestRecord findUserTestResultByUsernameAndTestId(String username, Long testId);
    List<UserTestRecord> findUserTestHistoryByUsername(String username);
}
