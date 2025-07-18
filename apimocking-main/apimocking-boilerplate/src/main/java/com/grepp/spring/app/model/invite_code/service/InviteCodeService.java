package com.grepp.spring.app.model.invite_code.service;

import com.grepp.spring.app.model.invite_code.domain.InviteCode;
import com.grepp.spring.app.model.invite_code.repos.InviteCodeRepository;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class InviteCodeService {

    private final InviteCodeRepository inviteCodeRepository;
    private final MemberService memberService;

    // 랜덤 초대코드 생성 (8자리 영문+숫자)
    private String generateRandomCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        
        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return code.toString();
    }

    // 초대코드 생성
    public String createInviteCode(Long memberId) {
        // 기존 유효한 초대코드가 있는지 확인
        if (inviteCodeRepository.existsByCreator_MemberIdAndUsedFalseAndExpiresAtAfter(memberId, LocalDateTime.now())) {
            throw new IllegalStateException("이미 유효한 초대코드가 존재합니다.");
        }

        // 만료된 초대코드들 정리
        inviteCodeRepository.deleteExpiredCodes(LocalDateTime.now());

        // 새로운 초대코드 생성
        String code;
        do {
            code = generateRandomCode();
        } while (inviteCodeRepository.findByCode(code).isPresent());

        Member creator = memberService.getMemberById(memberId);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10); // 10분 후 만료

        InviteCode inviteCode = InviteCode.builder()
                .code(code)
                .creator(creator)
                .expiresAt(expiresAt)
                .used(false)
                .build();

        inviteCodeRepository.save(inviteCode);
        return code;
    }

    // 초대코드 유효성 검사
    @Transactional(readOnly = true)
    public boolean isValidInviteCode(String code) {
        Optional<InviteCode> inviteCodeOpt = inviteCodeRepository.findByCode(code);
        return inviteCodeOpt.map(InviteCode::isValid).orElse(false);
    }

    // 초대코드 사용 처리
    public void useInviteCode(String code) {
        Optional<InviteCode> inviteCodeOpt = inviteCodeRepository.findByCode(code);
        if (inviteCodeOpt.isPresent()) {
            InviteCode inviteCode = inviteCodeOpt.get();
            if (inviteCode.isValid()) {
                inviteCode.setUsed(true);
                inviteCodeRepository.save(inviteCode);
            } else {
                throw new IllegalStateException("유효하지 않은 초대코드입니다.");
            }
        } else {
            throw new IllegalStateException("존재하지 않는 초대코드입니다.");
        }
    }

    // 만료된 초대코드 정리
    public void cleanupExpiredCodes() {
        inviteCodeRepository.deleteExpiredCodes(LocalDateTime.now());
    }
} 