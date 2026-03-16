package com.zoctan.api.controller;

import com.zoctan.api.core.response.Result;
import com.zoctan.api.core.response.ResultGenerator;
import com.zoctan.api.dto.ConsultationSessionDTO;
import com.zoctan.api.entity.Account;
import com.zoctan.api.entity.ConsultationMessage;
import com.zoctan.api.entity.ConsultationSession;
import com.zoctan.api.mapper.AccountMapper;
import com.zoctan.api.service.ConsultationService;
import com.zoctan.api.util.ContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.zoctan.api.dto.CounselorDTO;

import java.util.List;

@RestController
@RequestMapping("/api/consult")
@RequiredArgsConstructor
public class ConsultationController {

    private final ConsultationService consultationService;
    private final AccountMapper accountMapper;


    /**
     * 创建或获取会话（用户发起）
     */
    @PostMapping(value = "/sessions",
            consumes = "text/plain")
    public Result<Long> createSession(@RequestParam Long counselorId,
                                      @RequestBody String sessionKeyEncrypted) {
        Long currentUserId = ContextUtils.getCurrentAccountId();
        if (currentUserId == null) {
            return ResultGenerator.genFailedResult("未登录");
        }

        Long sessionId = consultationService.createSession(currentUserId, counselorId, sessionKeyEncrypted);
        return ResultGenerator.genOkResult(sessionId);
    }

    /**
     * 发送消息
     */
    @PostMapping("/messages")
    public Result<Void> sendMessage(@RequestParam Long sessionId,
                                    @RequestBody String encryptedContent) {
        Long currentUserId = ContextUtils.getCurrentAccountId();
        if (currentUserId == null) {
            return ResultGenerator.genFailedResult("未登录");
        }
        if (!consultationService.isParticipant(sessionId, currentUserId)) {
            return ResultGenerator.genFailedResult("无权发送消息");
        }
        consultationService.sendMessage(sessionId, currentUserId, encryptedContent);
        return ResultGenerator.genOkResult();
    }

    /**
     * 获取会话消息历史
     */
    @GetMapping("/sessions/{sessionId}/messages")
    public Result<List<ConsultationMessage>> getMessages(@PathVariable Long sessionId) {
        Long currentUserId = ContextUtils.getCurrentAccountId();
        if (currentUserId == null) {
            return ResultGenerator.genFailedResult("未登录");
        }
        if (!consultationService.isParticipant(sessionId, currentUserId)) {
            return ResultGenerator.genFailedResult("无权查看该会话");
        }
        List<ConsultationMessage> messages = consultationService.getMessages(sessionId);
        return ResultGenerator.genOkResult(messages);
    }

    /**
     * 获取所有可用咨询师列表（role_id = 2）
     */
    @GetMapping("/counselors")
    public Result<List<CounselorDTO>> getCounselors() {
        List<CounselorDTO> counselors = consultationService.getCounselors();
        return ResultGenerator.genOkResult(counselors);
    }

    /**
     * 获取当前用户的所有会话
     */
    @GetMapping("/sessions/me")
    public Result<List<ConsultationSessionDTO>> getMySessions() {
        Long currentUserId = ContextUtils.getCurrentAccountId();
        if (currentUserId == null) {
            return ResultGenerator.genFailedResult("未登录");
        }

        List<ConsultationSessionDTO> sessions = consultationService.getMySessions(currentUserId);
        return ResultGenerator.genOkResult(sessions);
    }

    /**
     * 获取会话详情（用于聊天页显示对方信息）
     */
    @GetMapping("/sessions/{sessionId}")
    public Result<ConsultationSessionDTO> getSession(@PathVariable Long sessionId) {
        Long currentUserId = ContextUtils.getCurrentAccountId();
        if (currentUserId == null) {
            return ResultGenerator.genFailedResult("未登录");
        }
        if (!consultationService.isParticipant(sessionId, currentUserId)) {
            return ResultGenerator.genFailedResult("无权访问该会话");
        }

        // 根据 sessionId + userId 构造 DTO

        ConsultationSession session = consultationService.getSessionById(sessionId);
        ConsultationSessionDTO dto = new ConsultationSessionDTO();

        dto.setId(session.getId());
        dto.setStatus(session.getStatus());
        dto.setCreatedAt(session.getCreatedAt().toString());
        dto.setUserId(session.getUserId());
        dto.setCounselorId(session.getCounselorId());
        Account user = accountMapper.selectByPrimaryKey(session.getUserId());
        Account counselor = accountMapper.selectByPrimaryKey(session.getCounselorId());

        dto.setUserName(user != null ? user.getName() : "未知用户");
        dto.setCounselorName(counselor != null ? counselor.getName() : "未知咨询师");

        // 判断对方是谁
        Long otherId;
        String otherName;
        if (session.getUserId().equals(currentUserId)) {
            otherId = session.getCounselorId();
            otherName = dto.getCounselorName();
        } else {
            otherId = session.getUserId();
            otherName = dto.getUserName();
        }
        dto.setOtherId(otherId);
        dto.setOtherName(otherName);
        dto.setOtherAvatar(null);

        return ResultGenerator.genOkResult(dto);
    }
}