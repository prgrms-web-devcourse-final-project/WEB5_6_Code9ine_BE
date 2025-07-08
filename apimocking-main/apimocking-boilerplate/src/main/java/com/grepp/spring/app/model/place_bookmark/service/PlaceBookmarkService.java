package com.grepp.spring.app.model.place_bookmark.service;

import com.grepp.spring.app.model.festival.domain.Festival;
import com.grepp.spring.app.model.festival.repos.FestivalRepository;
import com.grepp.spring.app.model.library.domain.Library;
import com.grepp.spring.app.model.library.repos.LibraryRepository;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.app.model.place_bookmark.domain.PlaceBookmark;
import com.grepp.spring.app.model.place_bookmark.model.PlaceBookmarkDTO;
import com.grepp.spring.app.model.place_bookmark.repos.PlaceBookmarkRepository;
import com.grepp.spring.app.model.store.domain.Store;
import com.grepp.spring.app.model.store.repos.StoreRepository;
import com.grepp.spring.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PlaceBookmarkService {

    private final PlaceBookmarkRepository placeBookmarkRepository;
    private final MemberRepository memberRepository;
    private final FestivalRepository festivalRepository;
    private final StoreRepository storeRepository;
    private final LibraryRepository libraryRepository;

    public PlaceBookmarkService(final PlaceBookmarkRepository placeBookmarkRepository,
            final MemberRepository memberRepository, final FestivalRepository festivalRepository,
            final StoreRepository storeRepository, final LibraryRepository libraryRepository) {
        this.placeBookmarkRepository = placeBookmarkRepository;
        this.memberRepository = memberRepository;
        this.festivalRepository = festivalRepository;
        this.storeRepository = storeRepository;
        this.libraryRepository = libraryRepository;
    }

    public List<PlaceBookmarkDTO> findAll() {
        final List<PlaceBookmark> placeBookmarks = placeBookmarkRepository.findAll(Sort.by("pBookmarkId"));
        return placeBookmarks.stream()
                .map(placeBookmark -> mapToDTO(placeBookmark, new PlaceBookmarkDTO()))
                .toList();
    }

    public PlaceBookmarkDTO get(final Long pBookmarkId) {
        return placeBookmarkRepository.findById(pBookmarkId)
                .map(placeBookmark -> mapToDTO(placeBookmark, new PlaceBookmarkDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PlaceBookmarkDTO placeBookmarkDTO) {
        final PlaceBookmark placeBookmark = new PlaceBookmark();
        mapToEntity(placeBookmarkDTO, placeBookmark);
        return placeBookmarkRepository.save(placeBookmark).getPBookmarkId();
    }

    public void update(final Long pBookmarkId, final PlaceBookmarkDTO placeBookmarkDTO) {
        final PlaceBookmark placeBookmark = placeBookmarkRepository.findById(pBookmarkId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(placeBookmarkDTO, placeBookmark);
        placeBookmarkRepository.save(placeBookmark);
    }

    public void delete(final Long pBookmarkId) {
        placeBookmarkRepository.deleteById(pBookmarkId);
    }

    private PlaceBookmarkDTO mapToDTO(final PlaceBookmark placeBookmark,
            final PlaceBookmarkDTO placeBookmarkDTO) {
        placeBookmarkDTO.setPBookmarkId(placeBookmark.getPBookmarkId());
        placeBookmarkDTO.setCreatedAt(placeBookmark.getCreatedAt());
        placeBookmarkDTO.setActivatedAt(placeBookmark.getActivatedAt());
        placeBookmarkDTO.setMember(placeBookmark.getMember() == null ? null : placeBookmark.getMember().getMemberId());
        placeBookmarkDTO.setFestival(placeBookmark.getFestival() == null ? null : placeBookmark.getFestival().getFestivalId());
        placeBookmarkDTO.setStore(placeBookmark.getStore() == null ? null : placeBookmark.getStore().getStoreId());
        placeBookmarkDTO.setLibrary(placeBookmark.getLibrary() == null ? null : placeBookmark.getLibrary().getLibraryId());
        return placeBookmarkDTO;
    }

    private PlaceBookmark mapToEntity(final PlaceBookmarkDTO placeBookmarkDTO,
            final PlaceBookmark placeBookmark) {
        placeBookmark.setCreatedAt(placeBookmarkDTO.getCreatedAt());
        placeBookmark.setActivatedAt(placeBookmarkDTO.getActivatedAt());
        final Member member = placeBookmarkDTO.getMember() == null ? null : memberRepository.findById(placeBookmarkDTO.getMember())
                .orElseThrow(() -> new NotFoundException("member not found"));
        placeBookmark.setMember(member);
        final Festival festival = placeBookmarkDTO.getFestival() == null ? null : festivalRepository.findById(placeBookmarkDTO.getFestival())
                .orElseThrow(() -> new NotFoundException("festival not found"));
        placeBookmark.setFestival(festival);
        final Store store = placeBookmarkDTO.getStore() == null ? null : storeRepository.findById(placeBookmarkDTO.getStore())
                .orElseThrow(() -> new NotFoundException("store not found"));
        placeBookmark.setStore(store);
        final Library library = placeBookmarkDTO.getLibrary() == null ? null : libraryRepository.findById(placeBookmarkDTO.getLibrary())
                .orElseThrow(() -> new NotFoundException("library not found"));
        placeBookmark.setLibrary(library);
        return placeBookmark;
    }

}
