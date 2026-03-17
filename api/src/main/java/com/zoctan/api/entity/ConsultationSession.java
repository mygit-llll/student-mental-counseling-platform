package com.zoctan.api.entity;

import lombok.Data;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
public class ConsultationSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;          // 学生ID
    private Long counselorId;     // 咨询师ID
    private Integer status;       // 1: 进行中, 2: 已结束
    private Date createdAt;
    private String sessionKeyForStudent;
    private String sessionKeyForCounselor;
}