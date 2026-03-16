package com.zoctan.api.entity;

import lombok.Data;
import java.util.Date;
import javax.persistence.Id;

@Data
public class ConsultationSession {
    @Id
    private Long id;
    private Long userId;          // 学生ID
    private Long counselorId;     // 咨询师ID
    private String sessionKeyEncrypted; // 加密后的会话密钥（前端生成并加密上传）
    private Integer status;       // 1: 进行中, 2: 已结束
    private Date createdAt;
}