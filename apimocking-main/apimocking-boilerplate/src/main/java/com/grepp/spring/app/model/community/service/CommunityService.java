package com.grepp.spring.app.model.community.service;

import com.grepp.spring.app.model.community.dto.CommunityPostCreateRequest;
import com.grepp.spring.app.model.community.dto.CommunityPostDetailResponse;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.infra.payload.PageParam;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CommunityService {

    void createPost(CommunityPostCreateRequest request, Member member);

    List<CommunityPostDetailResponse> getPostsByCategory(String category, PageParam pageParam, Long userId);

}
