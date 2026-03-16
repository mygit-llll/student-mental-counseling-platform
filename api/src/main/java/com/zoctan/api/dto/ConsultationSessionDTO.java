package com.zoctan.api.dto;


import lombok.Data;

import javax.persistence.Id;

@Data
public class ConsultationSessionDTO {
    @Id
    private Long id;
    private Long otherId;      // 对方 ID
    private String otherName;  // 对方姓名（可能是学生 or 咨询师）
    private String otherAvatar;
    private Integer status;
    private String createdAt;
    private String lastMessage;

    private String sessionKeyEncrypted;
    public String getSessionKeyEncrypted() {
        return sessionKeyEncrypted;
    }

    public void setSessionKeyEncrypted(String sessionKeyEncrypted) {
        this.sessionKeyEncrypted = sessionKeyEncrypted;
    }

    private Long userId;
    private Long counselorId;

    private String userName;
    private String counselorName;
}
