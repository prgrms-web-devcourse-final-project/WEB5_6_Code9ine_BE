package com.grepp.spring.app.model.member.service;

import com.grepp.spring.app.model.member.model.TopSaversResponse;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainPageMemberService {

    private final MemberRepository memberRepository;

    public List<TopSaversResponse> getTopSavers() {
        Pageable pageable = PageRequest.of(0, 3);
        return memberRepository.getTopSavers(pageable);
    }
}
