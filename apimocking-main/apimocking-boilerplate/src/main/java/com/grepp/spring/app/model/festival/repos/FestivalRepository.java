package com.grepp.spring.app.model.festival.repos;

import com.grepp.spring.app.model.festival.domain.Festival;
import com.grepp.spring.app.model.festival.model.DetailFestivalPlaceResponse;
import com.grepp.spring.app.model.store.dto.PlaceResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface FestivalRepository extends JpaRepository<Festival, Long> {

    @Query("SELECT new com.grepp.spring.app.model.store.dto.PlaceResponse(" +
            "null, f.festivalId, null, f.name, f.address, null, 'festival', " +
            "null, null, null, f.latitude, f.longitude, f.url) " +
            "FROM Festival f " +
            "WHERE f.location = :location")
    List<PlaceResponse> search(@Param("location") String location);

    @Query("SELECT new com.grepp.spring.app.model.festival.model.DetailFestivalPlaceResponse(" +
            "f.festivalId, f.name,f.address, f.category, 'festival', f.target, f.url, f.startAt, f.endAt, f.latitude, f.longitude) " +
            "FROM Festival f " +
            "WHERE f.festivalId = :festivalId")
    DetailFestivalPlaceResponse getDetailFestivalSearch(@Param("festivalId") Long festivalId);

}