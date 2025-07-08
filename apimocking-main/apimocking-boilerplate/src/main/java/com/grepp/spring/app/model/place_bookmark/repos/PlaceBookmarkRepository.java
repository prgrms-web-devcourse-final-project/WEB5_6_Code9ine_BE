package com.grepp.spring.app.model.place_bookmark.repos;

import com.grepp.spring.app.model.festival.domain.Festival;
import com.grepp.spring.app.model.library.domain.Library;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.place_bookmark.domain.PlaceBookmark;
import com.grepp.spring.app.model.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlaceBookmarkRepository extends JpaRepository<PlaceBookmark, Long> {

    PlaceBookmark findFirstByMember(Member member);

    PlaceBookmark findFirstByFestival(Festival festival);

    PlaceBookmark findFirstByStore(Store store);

    PlaceBookmark findFirstByLibrary(Library library);

}
