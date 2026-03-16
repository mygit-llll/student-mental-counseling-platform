package com.zoctan.api.service;

import com.zoctan.api.dto.ConsultationSessionDTO;
import com.zoctan.api.dto.CounselorDTO;
import com.zoctan.api.entity.ConsultationSession;
import com.zoctan.api.entity.ConsultationMessage;

import java.util.List;

public interface ConsultationService {
    Long createSession(Long userId, Long counselorId, String sessionKeyEncrypted);
    void sendMessage(Long sessionId, Long senderId, String encryptedContent);
    ConsultationSession getSessionById(Long sessionId);
    boolean isParticipant(Long sessionId, Long accountId);

    List<ConsultationMessage> getMessages(Long sessionId);

    List<CounselorDTO> getCounselors();

    List<ConsultationSessionDTO> getMySessions(Long userId);

}
