package com.zoctan.api.entity;

import lombok.Data;
import javax.persistence.Id;
import java.util.Date;
import java.util.Map;

@Data
public class UserTestRecord {
    @Id
    private Long id;
    private Long userId;
    private Long testId;
    private Map<String, String> answers;
    private Integer totalScore;
    private String resultText;
    private Date createdAt;

    // 用于接收 JOIN 查询中的 psychological_test.title
    private String testTitle;

    // getter 和 setter
    public String getTestTitle() {
        return testTitle;
    }

    public void setTestTitle(String testTitle) {
        this.testTitle = testTitle;
    }
}