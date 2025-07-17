package com.grepp.spring.app.model.library.repos;

import com.grepp.spring.app.model.library.domain.Library;
import com.grepp.spring.app.model.store.dto.PlaceResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface LibraryRepository extends JpaRepository<Library, Long> {


    @Query("SELECT new com.grepp.spring.app.model.store.dto.PlaceResponse(" +
            "null, null, l.libraryId, l.name, l.address, null, 'library', " +
            "l.contact, null, null, l.latitude, l.longitude, l.url) " +
            "FROM Library l " +
            "WHERE l.location = :location")
    List<PlaceResponse> search(@Param("location") String location);
}
