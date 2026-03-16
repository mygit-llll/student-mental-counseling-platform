package com.zoctan.api.service.impl;

import com.zoctan.api.entity.*;
import com.zoctan.api.mapper.*;
import com.zoctan.api.service.PsychologicalTestService;
import com.zoctan.api.core.service.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class PsychologicalTestServiceImpl extends AbstractService<PsychologicalTest> implements PsychologicalTestService {

    @Resource
    private PsychologicalTestMapper testMapper;

    @Resource
    private PsychologicalQuestionMapper questionMapper;

    @Resource
    private UserTestRecordMapper recordMapper;

    @Override
    public List<PsychologicalTest> getAllTests() {
        return testMapper.selectAllTests();
    }

    @Override
    public PsychologicalTest getTestWithQuestions(Long testId) {
        PsychologicalTest test = testMapper.selectByPrimaryKey(testId);
        if (test != null) {
            List<PsychologicalQuestion> questions = questionMapper.selectByTestId(testId);
            test.setQuestions(questions);
        }
        return test;
    }

    @Override
    public void submitTest(UserTestRecord record) {
        // 1. 获取题目分值规则
        List<PsychologicalQuestion> questions = questionMapper.selectByTestId(record.getTestId());
        int totalScore = 0;
        for (PsychologicalQuestion q : questions) {
            String selectedOption = record.getAnswers().get(q.getId().toString());
            if (selectedOption != null && q.getScoreMap().containsKey(selectedOption)) {
                totalScore += q.getScoreMap().get(selectedOption);
            }
        }

        // 2. 生成结果解读（示例）
        String resultText = interpretResult(totalScore);

        // 3. 设置总分和结果
        record.setTotalScore(totalScore);
        record.setResultText(resultText);

        // 4. 保存记录
        recordMapper.insertRecord(record);
    }

    private String interpretResult(int score) {
        if (score < 10) return "心理状态良好";
        else if (score < 20) return "轻度压力，建议放松";
        else if (score < 30) return "中度焦虑，建议咨询";
        else return "重度心理负担，请寻求专业帮助";
    }
}