package com.grepp.spring.app.model.auth.repos;

import com.grepp.spring.app.model.auth.domain.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    
    Optional<EmailVerification> findByEmailAndVerificationCode(String email, String verificationCode);
    
    Optional<EmailVerification> findByEmail(String email);
    
    @Modifying
    @Query("DELETE FROM EmailVerification e WHERE e.email = :email")
    void deleteByEmail(@Param("email") String email);
    
    @Modifying
    @Query("DELETE FROM EmailVerification e WHERE e.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpiredVerifications();
} 