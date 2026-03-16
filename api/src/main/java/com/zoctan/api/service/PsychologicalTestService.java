package com.zoctan.api.service;

import com.zoctan.api.entity.PsychologicalTest;
import com.zoctan.api.entity.UserTestRecord;

import java.util.List;

public interface PsychologicalTestService {
    List<PsychologicalTest> getAllTests();
    PsychologicalTest getTestWithQuestions(Long testId);
    void submitTest(UserTestRecord record);
}