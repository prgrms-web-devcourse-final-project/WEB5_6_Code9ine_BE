package com.grepp.spring.app.model.admin.service;

import com.grepp.spring.app.model.admin.dto.AdminStoreCreateRequest;
import com.grepp.spring.app.model.admin.dto.AdminStoreListResponse;
import com.grepp.spring.app.model.admin.dto.AdminStoreUpdateRequest;
import com.grepp.spring.infra.payload.PageParam;

public interface AdminStoreService {

    // 관리자 모든 가게 조회
    AdminStoreListResponse getAllStores(PageParam pageParam);

    // 관리자 지정 카테고리로 가게 조회
    AdminStoreListResponse getStoresByCategory(String category, PageParam pageParam);

    // 관리자 장소 등록
    void createStore(AdminStoreCreateRequest request);

    // 관리자 장소 수정
    void updateStore(Long storeId, AdminStoreUpdateRequest request);

    // 관리자 장소 삭제 (soft delete)
    void deleteStore(Long storeId);
}
