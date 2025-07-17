package com.grepp.spring.app.model.store.service;

import com.grepp.spring.app.model.festival.repos.FestivalRepository;
import com.grepp.spring.app.model.library.repos.LibraryRepository;
import com.grepp.spring.app.model.store.dto.PlaceResponse;
import com.grepp.spring.app.model.store.repos.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreSearchService {

    private final StringRedisTemplate stringRedisTemplate;
    private final StoreRepository storeRepository;
    private final LibraryRepository libraryRepository;
    private final FestivalRepository festivalRepository;

    public List<PlaceResponse> search(String location, List<String> type, List<String> category) {
        stringRedisTemplate.opsForZSet().incrementScore("popular_keywords", location, 1);

        List<PlaceResponse> result = new ArrayList<>();

        if (type.contains("store")) {
            result.addAll(storeRepository.search(location, type));
        }
        if (type.contains("festival")) {
            result.addAll(festivalRepository.search(location));
        }
        if (type.contains("library")) {
            result.addAll(libraryRepository.search(location));
        }

        return result;
    }

}
