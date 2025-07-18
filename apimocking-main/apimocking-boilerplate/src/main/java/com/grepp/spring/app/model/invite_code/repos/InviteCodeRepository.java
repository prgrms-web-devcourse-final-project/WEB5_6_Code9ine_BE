package com.grepp.spring.app.model.invite_code.repos;

import com.grepp.spring.app.model.invite_code.domain.InviteCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface InviteCodeRepository extends JpaRepository<InviteCode, Long> {

    // 코드로 초대코드 조회
    Optional<InviteCode> findByCode(String code);

    // 만료된 초대코드들 삭제
    @Modifying
    @Query("DELETE FROM InviteCode ic WHERE ic.expiresAt < :now")
    void deleteExpiredCodes(@Param("now") LocalDateTime now);

    // 특정 사용자가 생성한 유효한 초대코드가 있는지 확인
    boolean existsByCreator_MemberIdAndUsedFalseAndExpiresAtAfter(Long memberId, LocalDateTime now);
} 