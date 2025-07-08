package com.grepp.spring.app.model.store.service;

import com.grepp.spring.app.model.place_bookmark.domain.PlaceBookmark;
import com.grepp.spring.app.model.place_bookmark.repos.PlaceBookmarkRepository;
import com.grepp.spring.app.model.store.domain.Store;
import com.grepp.spring.app.model.store.model.StoreDTO;
import com.grepp.spring.app.model.store.repos.StoreRepository;
import com.grepp.spring.util.NotFoundException;
import com.grepp.spring.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final PlaceBookmarkRepository placeBookmarkRepository;

    public StoreService(final StoreRepository storeRepository,
            final PlaceBookmarkRepository placeBookmarkRepository) {
        this.storeRepository = storeRepository;
        this.placeBookmarkRepository = placeBookmarkRepository;
    }

    public List<StoreDTO> findAll() {
        final List<Store> stores = storeRepository.findAll(Sort.by("storeId"));
        return stores.stream()
                .map(store -> mapToDTO(store, new StoreDTO()))
                .toList();
    }

    public StoreDTO get(final Long storeId) {
        return storeRepository.findById(storeId)
                .map(store -> mapToDTO(store, new StoreDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final StoreDTO storeDTO) {
        final Store store = new Store();
        mapToEntity(storeDTO, store);
        return storeRepository.save(store).getStoreId();
    }

    public void update(final Long storeId, final StoreDTO storeDTO) {
        final Store store = storeRepository.findById(storeId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(storeDTO, store);
        storeRepository.save(store);
    }

    public void delete(final Long storeId) {
        storeRepository.deleteById(storeId);
    }

    private StoreDTO mapToDTO(final Store store, final StoreDTO storeDTO) {
        storeDTO.setStoreId(store.getStoreId());
        storeDTO.setName(store.getName());
        storeDTO.setAddress(store.getAddress());
        storeDTO.setLongitude(store.getLongitude());
        storeDTO.setLatitude(store.getLatitude());
        storeDTO.setCategory(store.getCategory());
        storeDTO.setCreatedAt(store.getCreatedAt());
        storeDTO.setModifiedAt(store.getModifiedAt());
        storeDTO.setActivated(store.getActivated());
        storeDTO.setLocation(store.getLocation());
        storeDTO.setFirstMenu(store.getFirstMenu());
        storeDTO.setFirstPrice(store.getFirstPrice());
        storeDTO.setSecondMenu(store.getSecondMenu());
        storeDTO.setSecondPrice(store.getSecondPrice());
        storeDTO.setThirdMenu(store.getThirdMenu());
        storeDTO.setThirdPrice(store.getThirdPrice());
        storeDTO.setContact(store.getContact());
        storeDTO.setSido(store.getSido());
        return storeDTO;
    }

    private Store mapToEntity(final StoreDTO storeDTO, final Store store) {
        store.setName(storeDTO.getName());
        store.setAddress(storeDTO.getAddress());
        store.setLongitude(storeDTO.getLongitude());
        store.setLatitude(storeDTO.getLatitude());
        store.setCategory(storeDTO.getCategory());
        store.setCreatedAt(storeDTO.getCreatedAt());
        store.setModifiedAt(storeDTO.getModifiedAt());
        store.setActivated(storeDTO.getActivated());
        store.setLocation(storeDTO.getLocation());
        store.setFirstMenu(storeDTO.getFirstMenu());
        store.setFirstPrice(storeDTO.getFirstPrice());
        store.setSecondMenu(storeDTO.getSecondMenu());
        store.setSecondPrice(storeDTO.getSecondPrice());
        store.setThirdMenu(storeDTO.getThirdMenu());
        store.setThirdPrice(storeDTO.getThirdPrice());
        store.setContact(storeDTO.getContact());
        store.setSido(storeDTO.getSido());
        return store;
    }

    public ReferencedWarning getReferencedWarning(final Long storeId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Store store = storeRepository.findById(storeId)
                .orElseThrow(NotFoundException::new);
        final PlaceBookmark storePlaceBookmark = placeBookmarkRepository.findFirstByStore(store);
        if (storePlaceBookmark != null) {
            referencedWarning.setKey("store.placeBookmark.store.referenced");
            referencedWarning.addParam(storePlaceBookmark.getPBookmarkId());
            return referencedWarning;
        }
        return null;
    }

}
