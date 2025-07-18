package com.grepp.spring.app.model.admin.service;

import com.grepp.spring.app.model.admin.dto.AdminStoreMenuResponse;
import com.grepp.spring.app.model.admin.dto.AdminStoreResponse;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.app.model.store.domain.Store;
import com.grepp.spring.app.model.store.repos.StoreRepository;
import com.grepp.spring.infra.payload.PageParam;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminStoreServiceImpl implements AdminStoreService {

    private final StoreRepository storeRepository;

    // 관리자 모든 가게 조회
    @Transactional(readOnly = true)
    @Override
    public List<AdminStoreResponse> getAllStores(PageParam pageParam) {
        Pageable pageable = pageParam.toPageable(Sort.by(Sort.Direction.ASC, "storeId"));

        Page<Store> storePage = storeRepository.findAllByActivatedTrue(pageable);

        return storePage.getContent().stream().map(store -> {
            List<AdminStoreMenuResponse> menus = new ArrayList<>();

            if (store.getFirstMenu() != null) {
                menus.add(new AdminStoreMenuResponse(store.getFirstMenu(), store.getFirstPrice()));
            }
            if (store.getSecondMenu() != null) {
                menus.add(new AdminStoreMenuResponse(store.getSecondMenu(), store.getSecondPrice()));
            }
            if (store.getThirdMenu() != null) {
                menus.add(new AdminStoreMenuResponse(store.getThirdMenu(), store.getThirdPrice()));
            }

            return new AdminStoreResponse(
                store.getStoreId(),
                store.getName(),
                store.getAddress(),
                store.getCategory(),
                menus
            );
        }).collect(Collectors.toList());
    }
}
