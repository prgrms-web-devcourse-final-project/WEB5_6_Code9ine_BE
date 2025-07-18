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
import com.grepp.spring.app.model.place_bookmark.repos.PlaceBookmarkRepository;
import com.grepp.spring.app.model.store.domain.Store;
import com.grepp.spring.app.model.store.repos.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaceBookmarkService {

    private final MemberRepository memberRepository;
    private final PlaceBookmarkRepository placeBookmarkRepository;
    private final StoreRepository storeRepository;
    private final FestivalRepository festivalRepository;
    private final LibraryRepository libraryRepository;

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
