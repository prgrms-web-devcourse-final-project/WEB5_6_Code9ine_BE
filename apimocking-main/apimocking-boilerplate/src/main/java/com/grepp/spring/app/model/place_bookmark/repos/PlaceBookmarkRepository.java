package com.grepp.spring.app.model.place_bookmark.repos;

import com.grepp.spring.app.model.festival.domain.Festival;
import com.grepp.spring.app.model.library.domain.Library;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.place_bookmark.domain.PlaceBookmark;
import com.grepp.spring.app.model.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface PlaceBookmarkRepository extends JpaRepository<PlaceBookmark, Long> {

    PlaceBookmark findFirstByMember(Member member);

    PlaceBookmark findFirstByFestival(Festival festival);

    PlaceBookmark findFirstByStore(Store store);

    PlaceBookmark findFirstByLibrary(Library library);

    // 멤버별 활성화된 장소 북마크 목록 조회
    List<PlaceBookmark> findByMemberAndActivatedTrue(Member member);

    // 특정 멤버의 특정 스토어 북마크 조회
    Optional<PlaceBookmark> findByMemberAndStore(Member member, Store store);

    // 특정 멤버의 특정 페스티벌 북마크 조회
    Optional<PlaceBookmark> findByMemberAndFestival(Member member, Festival festival);

    // 특정 멤버의 특정 도서관 북마크 조회
    Optional<PlaceBookmark> findByMemberAndLibrary(Member member, Library library);

    // 특정 멤버의 특정 스토어 북마크 존재 여부 확인
    boolean existsByMemberAndStore(Member member, Store store);

    // 특정 멤버의 특정 페스티벌 북마크 존재 여부 확인
    boolean existsByMemberAndFestival(Member member, Festival festival);

    // 특정 멤버의 특정 도서관 북마크 존재 여부 확인
    boolean existsByMemberAndLibrary(Member member, Library library);
}
