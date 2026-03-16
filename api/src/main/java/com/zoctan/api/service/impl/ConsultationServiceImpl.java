package com.zoctan.api.service.impl;

import com.zoctan.api.dto.ConsultationSessionDTO;
import com.zoctan.api.dto.CounselorDTO;
import com.zoctan.api.entity.Account;
import com.zoctan.api.entity.ConsultationSession;
import com.zoctan.api.entity.ConsultationMessage;
import com.zoctan.api.mapper.AccountMapper;
import com.zoctan.api.mapper.ConsultationMapper;
import com.zoctan.api.mapper.ConsultationSessionMapper;
import com.zoctan.api.mapper.ConsultationMessageMapper;
import com.zoctan.api.service.ConsultationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationSessionMapper sessionMapper;
    private final ConsultationMessageMapper messageMapper;
    private final ConsultationMapper consultationMapper;
    private final AccountMapper accountMapper;
    // 加载最后一条消息作为预览
    private String getLastMessagePreview(Long sessionId) {
        try {
            String lastEncrypted = messageMapper.selectLastEncryptedContentBySessionId(sessionId);
            if (lastEncrypted == null || lastEncrypted.isEmpty()) {
                return "暂无消息";
            }
            if (lastEncrypted.length() > 30) {
                return lastEncrypted.substring(0, 30) + "...";
            }
            return lastEncrypted;
        } catch (Exception e) {
            return "[加载失败]";
        }
    }

    @Override
    @Transactional
    public Long createSession(Long userId, Long counselorId, String sessionKeyEncrypted) {
        // 检查是否已有进行中的会话
        ConsultationSession existing = sessionMapper.findByUserIdAndCounselorId(userId, counselorId);
        if (existing != null) {
            return existing.getId();
        }

        ConsultationSession session = new ConsultationSession();
        session.setUserId(userId);
        session.setCounselorId(counselorId);
        session.setSessionKeyEncrypted(sessionKeyEncrypted);
        session.setStatus(1); // 进行中
        sessionMapper.insert(session);
        return session.getId();
    }

    @Override
    @Transactional
    public void sendMessage(Long sessionId, Long senderId, String encryptedContent) {
        ConsultationMessage message = new ConsultationMessage();
        message.setSessionId(sessionId);
        message.setSenderId(senderId);
        message.setEncryptedContent(encryptedContent);
        message.setSentAt(LocalDateTime.now());
        messageMapper.insert(message);
    }

    @Override
    public ConsultationSession getSessionById(Long sessionId) {
        return sessionMapper.selectByPrimaryKey(sessionId);
    }

    @Override
    public boolean isParticipant(Long sessionId, Long accountId) {
        ConsultationSession session = sessionMapper.selectByPrimaryKey(sessionId);
        if (session == null) return false;
        return session.getUserId().equals(accountId) || session.getCounselorId().equals(accountId);
    }

    @Override
    public List<ConsultationMessage> getMessages(Long sessionId) {
        return messageMapper.findBySessionId(sessionId);
    }

    @Override
    public List<CounselorDTO> getCounselors() {
        return consultationMapper.selectCounselorsByRoleId(2L);
    }

    @Override
    public List<ConsultationSessionDTO> getMySessions(Long userId) {
        // 同时查 user_id 和 counselor_id
        List<ConsultationSession> sessions = sessionMapper.findByUserIdOrCounselorId(userId);

        return sessions.stream().map(session -> {
            ConsultationSessionDTO dto = new ConsultationSessionDTO();
            dto.setId(session.getId());
            dto.setStatus(session.getStatus());
            dto.setCreatedAt(session.getCreatedAt().toString());

            // 判断当前用户是 user 还是 counselor，从而确定“对方”是谁
            Long otherId;
            if (session.getUserId().equals(userId)) {
                // 当前用户是学生，对方是咨询师
                otherId = session.getCounselorId();
            } else {
                // 当前用户是咨询师，对方是学生
                otherId = session.getUserId();
            }

            Account other = accountMapper.selectByPrimaryKey(otherId);
            dto.setOtherId(otherId);
            dto.setOtherName(other != null ? other.getName() : "未知用户");
            dto.setOtherAvatar(null); // 如果有头像字段可补充

            // 加载最后一条消息作为预览
            dto.setLastMessage(getLastMessagePreview(session.getId()));

            return dto;
        }).collect(Collectors.toList());
    }

}