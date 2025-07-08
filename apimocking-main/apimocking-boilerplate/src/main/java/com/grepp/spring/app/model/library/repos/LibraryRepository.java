package com.grepp.spring.app.model.library.repos;

import com.grepp.spring.app.model.library.domain.Library;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LibraryRepository extends JpaRepository<Library, Long> {
}
