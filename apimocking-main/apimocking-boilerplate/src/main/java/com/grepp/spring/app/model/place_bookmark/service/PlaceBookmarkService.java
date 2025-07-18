package com.grepp.spring.app.model.place_bookmark.service;

import com.grepp.spring.app.model.festival.domain.Festival;
import com.grepp.spring.app.model.festival.repos.FestivalRepository;
import com.grepp.spring.app.model.library.domain.Library;
import com.grepp.spring.app.model.library.repos.LibraryRepository;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.app.model.place_bookmark.domain.PlaceBookmark;
import com.grepp.spring.app.model.place_bookmark.model.BookmarkResponse;
import com.grepp.spring.app.model.place_bookmark.model.BookmarkToggleRequest;
import com.grepp.spring.app.model.place_bookmark.model.PlaceBookmarkDTO;
import com.grepp.spring.app.model.place_bookmark.repos.PlaceBookmarkRepository;
import com.grepp.spring.app.model.store.domain.Store;
import com.grepp.spring.app.model.store.repos.StoreRepository;
import com.grepp.spring.util.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
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


    // 멤버별 장소 북마크 목록 조회
    public List<Map<String, Object>> getMemberPlaceBookmarks(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("멤버를 찾을 수 없습니다."));

        List<PlaceBookmark> bookmarks = placeBookmarkRepository.findByMemberAndActivatedTrue(member);

        return bookmarks.stream()
                .map(this::convertToResponseMap)
                .toList();
    }

    // 장소 북마크 해제
    public void unbookmarkPlace(Long memberId, Long placeId, String placeType) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("멤버를 찾을 수 없습니다."));

        PlaceBookmark bookmark = null;

        switch (placeType.toLowerCase()) {
            case "store" -> {
                Store store = storeRepository.findById(placeId)
                        .orElseThrow(() -> new NotFoundException("스토어를 찾을 수 없습니다."));
                bookmark = placeBookmarkRepository.findByMemberAndStore(member, store)
                        .orElseThrow(() -> new NotFoundException("북마크를 찾을 수 없습니다."));
            }
            case "festival" -> {
                Festival festival = festivalRepository.findById(placeId)
                        .orElseThrow(() -> new NotFoundException("페스티벌을 찾을 수 없습니다."));
                bookmark = placeBookmarkRepository.findByMemberAndFestival(member, festival)
                        .orElseThrow(() -> new NotFoundException("북마크를 찾을 수 없습니다."));
            }
            case "library" -> {
                Library library = libraryRepository.findById(placeId)
                        .orElseThrow(() -> new NotFoundException("도서관을 찾을 수 없습니다."));
                bookmark = placeBookmarkRepository.findByMemberAndLibrary(member, library)
                        .orElseThrow(() -> new NotFoundException("북마크를 찾을 수 없습니다."));
            }
            default -> throw new IllegalArgumentException("지원하지 않는 장소 타입입니다: " + placeType);
        }

        // 북마크 비활성화 (삭제 대신 비활성화)
        bookmark.setActivated(false);
        placeBookmarkRepository.save(bookmark);
    }

    // PlaceBookmark를 응답용 Map으로 변환
    private Map<String, Object> convertToResponseMap(PlaceBookmark bookmark) {
        Map<String, Object> result = new HashMap<>();

        // placeId는 pBookmarkId 사용
        result.put("placeId", bookmark.getPBookmarkId().toString());
        result.put("bookmarkedAt", bookmark.getCreatedAt());

        if (bookmark.getStore() != null) {
            Store store = bookmark.getStore();
            result.put("storeId", store.getStoreId().toString());
            result.put("name", store.getName());
            result.put("address", store.getAddress());
            result.put("category", store.getCategory());
            result.put("type", "store");
            result.put("contact", store.getContact());
            result.put("firstmenu", store.getFirstMenu());
            result.put("firstprice", store.getFirstPrice());
            result.put("secondmenu", store.getSecondMenu());
            result.put("secondprice", store.getSecondPrice());
            result.put("thirdmenu", store.getThirdMenu());
            result.put("thirdprice", store.getThirdPrice());
        } else if (bookmark.getFestival() != null) {
            Festival festival = bookmark.getFestival();
            result.put("festivalId", festival.getFestivalId().toString());
            result.put("name", festival.getName());
            result.put("category", festival.getCategory());
            result.put("type", "festival");
            result.put("address", festival.getAddress());
            result.put("target", festival.getTarget());
            result.put("url", festival.getUrl());
            result.put("startAt", festival.getStartAt());
            result.put("endAt", festival.getEndAt());
        } else if (bookmark.getLibrary() != null) {
            Library library = bookmark.getLibrary();
            result.put("libraryId", library.getLibraryId().toString());
            result.put("name", library.getName());
            result.put("type", "library");
            result.put("address", library.getAddress());
            result.put("url", library.getUrl());
        }

        return result;
    }

    private PlaceBookmarkDTO mapToDTO(final PlaceBookmark placeBookmark,
            final PlaceBookmarkDTO placeBookmarkDTO) {
        placeBookmarkDTO.setPBookmarkId(placeBookmark.getPBookmarkId());
        placeBookmarkDTO.setCreatedAt(placeBookmark.getCreatedAt());
        placeBookmarkDTO.setActivatedAt(placeBookmark.getActivated());
        placeBookmarkDTO.setMember(placeBookmark.getMember() == null ? null : placeBookmark.getMember().getMemberId());
        placeBookmarkDTO.setFestival(placeBookmark.getFestival() == null ? null : placeBookmark.getFestival().getFestivalId());
        placeBookmarkDTO.setStore(placeBookmark.getStore() == null ? null : placeBookmark.getStore().getStoreId());
        placeBookmarkDTO.setLibrary(placeBookmark.getLibrary() == null ? null : placeBookmark.getLibrary().getLibraryId());
        return placeBookmarkDTO;
    }

    private PlaceBookmark mapToEntity(final PlaceBookmarkDTO placeBookmarkDTO,
            final PlaceBookmark placeBookmark) {
        placeBookmark.setCreatedAt(placeBookmarkDTO.getCreatedAt());
        placeBookmark.setActivated(placeBookmarkDTO.getActivatedAt());
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


    // 김찬우
    public BookmarkResponse toggleBookmark(Long memberId, BookmarkToggleRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        String type = request.getType();
        Long id = request.getId();

        // 장소 객체 조회 + 기존 북마크 조회
        switch (type.toLowerCase()) {
            case "store" -> {
                Store store = storeRepository.findById(id).orElseThrow();
                Optional<PlaceBookmark> existing = placeBookmarkRepository.findByMemberAndStore(member, store);
                if (existing.isPresent()) {
                    placeBookmarkRepository.delete(existing.get());
                    return new BookmarkResponse(id, false, "북마크가 제거되었습니다.");
                }
                PlaceBookmark bookmark = new PlaceBookmark();
                bookmark.setMember(member);
                bookmark.setStore(store);
                placeBookmarkRepository.save(bookmark);
                return new BookmarkResponse(id, true, "북마크가 추가되었습니다.");
            }

            case "festival" -> {
                Festival festival = festivalRepository.findById(id).orElseThrow();
                Optional<PlaceBookmark> existing = placeBookmarkRepository.findByMemberAndFestival(member, festival);
                if (existing.isPresent()) {
                    placeBookmarkRepository.delete(existing.get());
                    return new BookmarkResponse(id, false, "북마크가 제거되었습니다.");
                }
                PlaceBookmark bookmark = new PlaceBookmark();
                bookmark.setMember(member);
                bookmark.setFestival(festival);
                placeBookmarkRepository.save(bookmark);
                return new BookmarkResponse(id, true, "북마크가 추가되었습니다.");
            }

            case "library" -> {
                Library library = libraryRepository.findById(id).orElseThrow();
                Optional<PlaceBookmark> existing = placeBookmarkRepository.findByMemberAndLibrary(member, library);
                if (existing.isPresent()) {
                    placeBookmarkRepository.delete(existing.get());
                    return new BookmarkResponse(id, false, "북마크가 제거되었습니다.");
                }
                PlaceBookmark bookmark = new PlaceBookmark();
                bookmark.setMember(member);
                bookmark.setLibrary(library);
                placeBookmarkRepository.save(bookmark);
                return new BookmarkResponse(id, true, "북마크가 추가되었습니다.");
            }

            default -> throw new IllegalArgumentException("잘못된 장소 타입입니다.");
        }
    }

}
