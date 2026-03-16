package com.zoctan.api.entity;

import lombok.Data;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.List;

@Data
public class PsychologicalTest {
    @Id
    private Long id;
    private String title;
    private String description;
    private Integer totalQuestions;

    @Transient
    private List<PsychologicalQuestion> questions; // 非数据库字段
}