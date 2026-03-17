package com.zoctan.api.dto;

import lombok.Data;

@Data
public class CreateSessionRequest {
    private Long counselorId;
    private String encryptedKeyForStudent;
    private String encryptedKeyForCounselor;
}
