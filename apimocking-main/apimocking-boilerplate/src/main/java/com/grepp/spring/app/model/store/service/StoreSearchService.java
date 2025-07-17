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
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StoreSearchService {

    private final StringRedisTemplate stringRedisTemplate;
    private final StoreRepository storeRepository;
    private final LibraryRepository libraryRepository;
    private final FestivalRepository festivalRepository;
    private static final String SEARCH_KEY = "popular_keywords";

    public List<PlaceResponse> search(String location, List<String> type, List<String> category) {

        stringRedisTemplate.opsForZSet().incrementScore("popular_keywords", location, 1);

        List<PlaceResponse> result = new ArrayList<>();

        boolean includeStore = type.contains("store") || !category.isEmpty() || type.isEmpty();
        boolean includeFestival = type.contains("festival") || type.isEmpty();
        boolean includeLibrary = type.contains("library") || type.isEmpty();

        if (includeStore) {
            result.addAll(storeRepository.search(location, category));
        }

        if (includeFestival) {
            result.addAll(festivalRepository.search(location));
        }

        if (includeLibrary) {
            result.addAll(libraryRepository.search(location));
        }

        return result;
    }

    public List<String> getTopKeywords() {
        return Objects.requireNonNull(stringRedisTemplate.opsForZSet()
                        .reverseRange(SEARCH_KEY, 0, 4))
                .stream().toList();
    }
}
