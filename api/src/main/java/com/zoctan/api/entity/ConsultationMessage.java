package com.zoctan.api.entity;

import lombok.Data;

import java.time.LocalDateTime;
import javax.persistence.Id;

@Data
public class ConsultationMessage {
    @Id
    private Long id;
    private Long sessionId;
    private Long senderId;        // 发送者ID（user 或 counselor）
    private String encryptedContent; // AES加密后的消息（Base64字符串）
    private LocalDateTime sentAt;
}