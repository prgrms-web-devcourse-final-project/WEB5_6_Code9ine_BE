package com.grepp.spring.infra.ocr.service;


import com.grepp.spring.app.model.challenge.service.ChallengeService;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.infra.error.exceptions.BadRequestException;
import com.grepp.spring.infra.error.exceptions.NotFoundException;
import com.grepp.spring.infra.ocr.NaverOcrClient;
import com.grepp.spring.infra.ocr.dto.ReceiptDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptOcrService {

    private final NaverOcrClient naverOcrClient;
    @Qualifier("customStringRedisTemplate")
    private final RedisTemplate<String, String> customStringRedisTemplate;
    private final ChallengeService challengeService;
    private final MemberRepository memberRepository;

    private static final int DAILY_LIMIT = 3;
    private static final String REDIS_KEY_PREFIX = "ocr_limit";

    public ReceiptDataDto extractReceiptData(MultipartFile file, Long memberId) {

        String key = generateKey(memberId);
        log.info("[extractReceiptData] Redis key: {}", key);

        Long count = customStringRedisTemplate.opsForValue().increment(key);
        log.info("[extractReceiptData] Redis count: {}", count);

        // 최초 요청 시 TTL 설정
        if (count == 1) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay(); // 다음 자정
            long secondsUntilMidnight = Duration.between(now, nextMidnight).getSeconds();
            customStringRedisTemplate.expire(key, secondsUntilMidnight, TimeUnit.SECONDS);
            Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("해당 멤버가 없습니다."));
            challengeService.handle_receiptChallenge(member);
        }

        if (count != null && count > DAILY_LIMIT) {
            log.warn("[extractReceiptData] OCR 호출 제한 초과: memberId={}, count={}", memberId, count);
            throw new BadRequestException("오늘의 OCR 사용 가능 횟수를 초과했습니다.");
        }

        return naverOcrClient.requestOcr(file);
    }

    private String generateKey(Long memberId) {
        return String.format("%s:%d:%s", REDIS_KEY_PREFIX, memberId, LocalDate.now());
    }
}
