package com.grepp.spring.app.model.member.repos;

import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.model.TopSaversResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmailIgnoreCase(String email);
    java.util.Optional<Member> findByEmail(String email);
    
    // 아이디(이메일) 찾기용 메서드 (여러 개 반환 가능)
    java.util.List<Member> findByNameAndPhoneNumber(String name, String phoneNumber);
    
    // 비밀번호 찾기용 메서드
    java.util.Optional<Member> findByEmailIgnoreCase(String email);


    @Query("""
    SELECT new com.grepp.spring.app.model.member.model.TopSaversResponse(
        m.memberId, m.nickname, m.level, m.profileImage, at.name
    )
    FROM AchievedTitle at
    JOIN at.member m
    ORDER BY m.level DESC
""")
    List<TopSaversResponse> getTopSavers(Pageable pageable);

    // --- 소셜 로그인 관련 메서드 추가 ---
    
    // kakaoId로만 조회하는 메서드로 변경
    java.util.Optional<Member> findByKakaoId(String kakaoId);
    
    // kakaoId로만 조회하는 메서드로 변경
    boolean existsByKakaoId(String kakaoId);
    
    // 소셜 이메일로 계정 조회
    java.util.Optional<Member> findBySocialEmail(String socialEmail);
    
    // 소셜 이메일로 계정 존재 여부 확인
    boolean existsBySocialEmail(String socialEmail);
    // ---------------------------------

    // 관리자 모든 유저 조회
    Page<Member> findByRoleEquals(String role, Pageable pageable);

    // 관리자 유저 닉네임으로 검색
    Optional<Member> findByNicknameAndRole(String nickname, String role);

    // 관리자 유저 차단
    Optional<Member> findByMemberIdAndRole(Long memberId, String role);

    // 관리자 당일 통계(방문자)
    int countByLastLoginedAt(LocalDate today);

    // 관리자 당일 통계(회원가입 수)
    int countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
