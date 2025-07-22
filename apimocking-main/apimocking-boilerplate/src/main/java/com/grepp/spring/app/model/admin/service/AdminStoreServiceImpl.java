package com.grepp.spring.app.model.admin.service;

import com.grepp.spring.app.model.admin.dto.AdminStoreCreateRequest;
import com.grepp.spring.app.model.admin.dto.AdminStoreListResponse;
import com.grepp.spring.app.model.admin.dto.AdminStoreMenuCreateRequest;
import com.grepp.spring.app.model.admin.dto.AdminStoreMenuResponse;
import com.grepp.spring.app.model.admin.dto.AdminStoreResponse;
import com.grepp.spring.app.model.admin.dto.AdminStoreUpdateRequest;
import com.grepp.spring.app.model.store.domain.Store;
import com.grepp.spring.app.model.store.repos.StoreRepository;
import com.grepp.spring.infra.error.exceptions.BadRequestException;
import com.grepp.spring.infra.payload.PageParam;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import com.grepp.spring.util.NotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminStoreServiceImpl implements AdminStoreService {

    private final StoreRepository storeRepository;

    // 관리자 모든 가게 조회
    @Transactional(readOnly = true)
    @Override
    public AdminStoreListResponse getAllStores() {
        List<Store> stores = storeRepository.findAllByActivatedTrue();

        List<AdminStoreResponse> result = stores.stream()
            .map(store -> new AdminStoreResponse(
                store.getStoreId(),
                store.getName(),
                store.getAddress(),
                store.getCategory(),
                mappingMenus(store)
            )).toList();

        int total = result.size();
        return new AdminStoreListResponse(total, result);
    }

    // 관리자 지정 카테고리로 가게 조회
    @Override
    @Transactional(readOnly = true)
    public AdminStoreListResponse getStoresByCategory(String category) {
        validateCategory(category);

        List<Store> stores = storeRepository.findAllByCategoryAndActivatedTrue(category);

        List<AdminStoreResponse> result = stores.stream()
            .map(store -> new AdminStoreResponse(
                store.getStoreId(),
                store.getName(),
                store.getAddress(),
                store.getCategory(),
                mappingMenus(store)
            )).toList();

        int total = result.size();

        return new AdminStoreListResponse(result.size(), result);
    }

    // 관리자 장소 등록
    @Override
    @Transactional
    public void createStore(AdminStoreCreateRequest request) {
        validateCategory(request.category());

        Store store = new Store();
        store.setName(request.name());
        store.setAddress(request.address());
        store.setCategory(request.category());
        store.setActivated(true);
        store.setCreatedAt(LocalDateTime.now());
        store.setModifiedAt(LocalDateTime.now());

        List<AdminStoreMenuCreateRequest> menus = request.menus();

        if (menus != null) {
            if (!menus.isEmpty()) {
                store.setFirstMenu(menus.getFirst().name());
                store.setFirstPrice(menus.getFirst().price());
            }
            if (menus.size() >= 2) {
                store.setSecondMenu(menus.get(1).name());
                store.setSecondPrice(menus.get(1).price());
            }
            if (menus.size() == 3) {
                store.setThirdMenu(menus.get(2).name());
                store.setThirdPrice(menus.get(2).price());
            }
        }

        storeRepository.save(store);
    }

    // 관리자 장소 수정
    @Override
    @Transactional
    public void updateStore(Long storeId, AdminStoreUpdateRequest request) {
        Store store = getActivatedStore(storeId);

        if (request.name() != null) {
            store.setName(request.name());
        }

        if (request.address() != null) {
            store.setAddress(request.address());
        }

        if (request.category() != null) {
            validateCategory(request.category());
            store.setCategory(request.category());
        }

        store.setModifiedAt(LocalDateTime.now());

        updateMenus(store, request.menus());
    }

    // 관리자 장소 삭제
    @Override
    @Transactional
    public void deleteStore(Long storeId) {
        Store store = getActivatedStore(storeId);
        store.setActivated(false);
        store.setModifiedAt(LocalDateTime.now());
    }

    // 가게 메뉴, 가격 맵핑
    private List<AdminStoreMenuResponse> mappingMenus(Store store) {
        List<AdminStoreMenuResponse> menus = new ArrayList<>();

        if (store.getFirstMenu() != null && store.getFirstPrice() != null) {
            menus.add(new AdminStoreMenuResponse(store.getFirstMenu(), store.getFirstPrice()));
        }
        if (store.getSecondMenu() != null && store.getSecondPrice() != null) {
            menus.add(new AdminStoreMenuResponse(store.getSecondMenu(), store.getSecondPrice()));
        }
        if (store.getThirdMenu() != null && store.getThirdPrice() != null) {
            menus.add(new AdminStoreMenuResponse(store.getThirdMenu(), store.getThirdPrice()));
        }

        return menus;
    }

    // 가게 메뉴, 가격 수정 메서드
    private void updateMenus(Store store, List<AdminStoreMenuCreateRequest> menus) {
        if (menus == null || menus.isEmpty()) {
            return;
        }

        if (!menus.isEmpty()) {
            AdminStoreMenuCreateRequest first = menus.getFirst();
            if (first.name() != null) {
                store.setFirstMenu(first.name());
            }
            store.setFirstPrice(first.price());
        }

        if (menus.size() >= 2) {
            AdminStoreMenuCreateRequest second = menus.get(1);
            if (second.name() != null) {
                store.setSecondMenu(second.name());
            }
            store.setSecondPrice(second.price());
        }

        if (menus.size() >= 3) {
            AdminStoreMenuCreateRequest third = menus.get(2);
            if (third.name() != null) {
                store.setThirdMenu(third.name());
            }
            store.setThirdPrice(third.price());
        }
    }

    // 활성화된 가게인지 검증 메서드
    private Store getActivatedStore(Long storeId) {
        return storeRepository.findById(storeId)
            .orElseThrow(() -> new NotFoundException("해당 가게를 찾을 수 없습니다."));
    }

    // 유효한 카테고리인지 검증 메서드
    private static void validateCategory(String category) {
        final List<String> categories = List.of("한식", "중식", "일식", "양식", "미용업", "세탁업", "숙박업");

        if (!categories.contains(category)) {
            throw new BadRequestException("유효하지 않은 카테고리입니다.");
        }
    }


}
