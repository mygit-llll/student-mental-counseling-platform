package com.zoctan.api.entity;

import javax.persistence.Id;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class PsychologicalQuestion {
    @Id
    private Long id;
    private Long testId;
    private String content;
    private Map<String, Integer> scoreMap;   // {"A":1, "B":2}
    private List<String> options;            // ["从不", "偶尔", ...]
    private Integer sortOrder;
}