package com.grepp.spring.app.model.store.repos;

import com.grepp.spring.app.model.store.domain.Store;
import com.grepp.spring.app.model.store.dto.DetailStorePlaceResponse;
import com.grepp.spring.app.model.store.dto.PlaceResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;


public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query("SELECT new com.grepp.spring.app.model.store.dto.PlaceResponse(" +
        "s.storeId, null, null, s.name, s.address, s.category, 'store', " +
        "s.contact, s.firstMenu, s.firstPrice, s.latitude, s.longitude, null) " +
        "FROM Store s " +
        "WHERE s.location = :location " +
        "AND (:#{#categories == null || #categories.isEmpty()} = true OR s.category IN :categories)")
    List<PlaceResponse> search(@Param("location") String location,
        @Param("categories") List<String> categories);

    @Query("SELECT new com.grepp.spring.app.model.store.dto.DetailStorePlaceResponse(" +
        "s.storeId, s.name, s.address, s.category,'store', s.contact, " +
        "s.firstMenu, s.firstPrice, s.secondMenu, s.secondPrice, " +
        "s.thirdMenu, s.thirdPrice, s.longitude, s.latitude) " +
        "FROM Store s WHERE s.storeId = :storeId")
    DetailStorePlaceResponse getDetailStoreSearch (@Param("storeId") Long storeId);

    // 관리자 활성화된 모든 가게 조회
    List<Store> findAllByActivatedTrue();

    // 관리자 지정 카테고리로 활성화된 모든 가게 조회
    List<Store> findAllByCategoryAndActivatedTrue(String category);
}
