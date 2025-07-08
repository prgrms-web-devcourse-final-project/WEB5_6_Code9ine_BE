package com.grepp.spring.app.model.store.repos;

import com.grepp.spring.app.model.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StoreRepository extends JpaRepository<Store, Long> {
}
