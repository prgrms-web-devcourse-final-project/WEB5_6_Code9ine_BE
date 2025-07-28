package com.grepp.spring.app.model.store.service;

import com.grepp.spring.app.model.festival.repos.FestivalRepository;
import com.grepp.spring.app.model.library.repos.LibraryRepository;
import com.grepp.spring.app.model.store.domain.Store;
import com.grepp.spring.app.model.store.dto.*;
import com.grepp.spring.app.model.store.repos.StoreRepository;
import com.grepp.spring.infra.error.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    public List<RegionResponse> getTopKeywords() {
        return Optional.ofNullable(stringRedisTemplate.opsForZSet().reverseRange(SEARCH_KEY, 0, 4))
                .orElse(Set.of()).stream()
                .map(RegionResponse::new) // 혹은 keyword -> new RegionResponse(keyword)
                .toList();
    }


    public DetailPlaceResponse getDetailPlaces(String type, Long id) {
        if (type.equalsIgnoreCase("store")) {
            return storeRepository.getDetailStoreSearch(id);
        } else if (type.equalsIgnoreCase("festival")) {
            return festivalRepository.getDetailFestivalSearch(id);
        } else if (type.equalsIgnoreCase("library")) {
            return libraryRepository.getDetailLibrarySearch(id);
        } else {
            throw new NotFoundException("해당하는 type이 없습니다.");
        }
    }


    public List<RandomStroeResponse> getRandomStore() {
        List<Store> stores = storeRepository.findByRandomStore(); // 여러 Store 반환

        return stores.stream()
                .map(store -> new RandomStroeResponse(
                        store.getName(),
                        store.getLocation(),
                        store.getAddress(),
                        store.getCategory(),
                        store.getContact(),
                        store.getFirstMenu(),
                        store.getFirstPrice()
                ))
                .collect(Collectors.toList());
    }
}
