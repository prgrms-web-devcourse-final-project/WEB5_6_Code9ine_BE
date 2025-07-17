package com.grepp.spring.app.model.store.repos;

import com.grepp.spring.app.model.store.domain.Store;
import com.grepp.spring.app.model.store.dto.PlaceResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query("SELECT new com.grepp.spring.app.model.store.dto.PlaceResponse(" +
            "s.storeId, null, null, s.name, s.address, s.category, 'store', " +
            "s.contact, s.firstMenu, s.firstPrice, s.latitude, s.longitude, null) " +
            "FROM Store s " +
            "WHERE s.location = :location " +
            "AND (:categories IS NULL OR s.category IN :categories)")
    List<PlaceResponse> search(@Param("location") String location,
                               @Param("categories") List<String> categories);
}
