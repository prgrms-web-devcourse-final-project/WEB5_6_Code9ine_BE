package com.grepp.spring.app.model.festival.repos;

import com.grepp.spring.app.model.festival.domain.Festival;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FestivalRepository extends JpaRepository<Festival, Long> {
}
