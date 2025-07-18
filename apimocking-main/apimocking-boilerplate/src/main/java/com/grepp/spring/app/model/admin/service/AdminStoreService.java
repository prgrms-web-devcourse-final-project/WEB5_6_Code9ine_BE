package com.grepp.spring.app.model.admin.service;

import com.grepp.spring.app.model.admin.dto.AdminStoreResponse;
import com.grepp.spring.infra.payload.PageParam;
import java.util.List;

public interface AdminStoreService {

    // 관리자 모든 가게 조회
    List<AdminStoreResponse> getAllStores(PageParam pageParam);

    // 관리자 지정 카테고리로 가게 조회
    List<AdminStoreResponse> getStoreByCategory(String category, PageParam pageParam);
}
