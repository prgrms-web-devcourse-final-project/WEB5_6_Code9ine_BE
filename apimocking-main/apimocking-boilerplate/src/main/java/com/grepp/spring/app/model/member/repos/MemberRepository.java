package com.grepp.spring.app.model.member.repos;

import com.grepp.spring.app.model.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmailIgnoreCase(String email);
    java.util.Optional<Member> findByEmail(String email);
    
    // 아이디(이메일) 찾기용 메서드 (여러 개 반환 가능)
    java.util.List<Member> findByNameAndPhoneNumber(String name, String phoneNumber);
    
    // 비밀번호 찾기용 메서드
    java.util.Optional<Member> findByEmailIgnoreCase(String email);

}
