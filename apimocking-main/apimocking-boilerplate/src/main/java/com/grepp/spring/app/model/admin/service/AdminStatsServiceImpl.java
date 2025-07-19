package com.grepp.spring.app.model.admin.service;

import com.grepp.spring.app.model.admin.dto.AdminStatsResponse;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminStatsServiceImpl implements AdminStatsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public AdminStatsResponse getTodayStats() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        int signupCount = memberRepository.countByCreatedAtBetween(start, end);
        int visitorCount = memberRepository.countByLastLoginedAt(LocalDate.now());

        return new AdminStatsResponse(LocalDate.now(), visitorCount, signupCount);
    }

}
