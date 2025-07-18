package com.grepp.spring.app.model.store.repos;

import com.grepp.spring.app.model.store.domain.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {

    // 관리자 활성화된 모든 가게 조회
    Page<Store> findAllByActivatedTrue(Pageable pageable);

    // 관리자 지정 카테고리로 활성화된 모든 가게 조회
    Page<Store> findAllByCategoryAndActivatedTrue(String category, Pageable pageable);
}
