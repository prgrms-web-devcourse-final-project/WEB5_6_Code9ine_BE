package com.grepp.spring.app.model.member.service;

import com.grepp.spring.app.model.attendance.domain.Attendance;
import com.grepp.spring.app.model.attendance.repos.AttendanceRepository;
import com.grepp.spring.app.model.budget.domain.Budget;
import com.grepp.spring.app.model.budget.repos.BudgetRepository;
import com.grepp.spring.app.model.challenge_count.domain.ChallengeCount;
import com.grepp.spring.app.model.challenge_count.repos.ChallengeCountRepository;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.model.MemberDTO;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.app.model.notification.domain.Notification;
import com.grepp.spring.app.model.notification.repos.NotificationRepository;
import com.grepp.spring.app.model.place_bookmark.domain.PlaceBookmark;
import com.grepp.spring.app.model.place_bookmark.repos.PlaceBookmarkRepository;
import com.grepp.spring.util.NotFoundException;
import com.grepp.spring.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;
    private final ChallengeCountRepository challengeCountRepository;
    private final AttendanceRepository attendanceRepository;
    private final BudgetRepository budgetRepository;
    private final PlaceBookmarkRepository placeBookmarkRepository;

    public MemberService(final MemberRepository memberRepository,
            final NotificationRepository notificationRepository,
            final ChallengeCountRepository challengeCountRepository,
            final AttendanceRepository attendanceRepository,
            final BudgetRepository budgetRepository,
            final PlaceBookmarkRepository placeBookmarkRepository) {
        this.memberRepository = memberRepository;
        this.notificationRepository = notificationRepository;
        this.challengeCountRepository = challengeCountRepository;
        this.attendanceRepository = attendanceRepository;
        this.budgetRepository = budgetRepository;
        this.placeBookmarkRepository = placeBookmarkRepository;
    }

    public List<MemberDTO> findAll() {
        final List<Member> members = memberRepository.findAll(Sort.by("memberId"));
        return members.stream()
                .map(member -> mapToDTO(member, new MemberDTO()))
                .toList();
    }

    public MemberDTO get(final Long memberId) {
        return memberRepository.findById(memberId)
                .map(member -> mapToDTO(member, new MemberDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final MemberDTO memberDTO) {
        final Member member = new Member();
        mapToEntity(memberDTO, member);
        return memberRepository.save(member).getMemberId();
    }

    public void update(final Long memberId, final MemberDTO memberDTO) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(memberDTO, member);
        memberRepository.save(member);
    }

    public void delete(final Long memberId) {
        memberRepository.deleteById(memberId);
    }

    public MemberDTO mapToDTO(final Member member, final MemberDTO memberDTO) {
        memberDTO.setMemberId(member.getMemberId());
        memberDTO.setEmail(member.getEmail());
        memberDTO.setPassword(member.getPassword());
        memberDTO.setName(member.getName());
        memberDTO.setRole(member.getRole());
        memberDTO.setPhoneNumber(member.getPhoneNumber());
        memberDTO.setActivated(member.getActivated());
        memberDTO.setCreatedAt(member.getCreatedAt());
        memberDTO.setModifiedAt(member.getModifiedAt());
        memberDTO.setNickname(member.getNickname());
        memberDTO.setProfileImage(member.getProfileImage());
        memberDTO.setLevel(member.getLevel());
        memberDTO.setTotalExp(member.getTotalExp());
        // 소셜 로그인 관련 필드 매핑
        memberDTO.setKakaoId(member.getKakaoId());
        return memberDTO;
    }

    private Member mapToEntity(final MemberDTO memberDTO, final Member member) {
        member.setEmail(memberDTO.getEmail());
        member.setPassword(memberDTO.getPassword());
        member.setName(memberDTO.getName());
        member.setRole(memberDTO.getRole());
        member.setPhoneNumber(memberDTO.getPhoneNumber());
        member.setActivated(memberDTO.getActivated());
        member.setCreatedAt(memberDTO.getCreatedAt());
        member.setModifiedAt(memberDTO.getModifiedAt());
        member.setNickname(memberDTO.getNickname());
        member.setProfileImage(memberDTO.getProfileImage());
        member.setLevel(memberDTO.getLevel());
        member.setTotalExp(memberDTO.getTotalExp());
        // 소셜 로그인 관련 필드 매핑
        member.setKakaoId(memberDTO.getKakaoId());
        return member;
    }

    public boolean emailExists(final String email) {
        return memberRepository.existsByEmailIgnoreCase(email);
    }

    // --- 소셜 로그인 관련 메서드 추가 ---
    
    // provider와 providerId로 소셜 계정 조회
    public java.util.Optional<Member> findByKakaoId(String kakaoId) {
        return memberRepository.findByKakaoId(kakaoId);
    }
    
    // provider와 providerId로 소셜 계정 존재 여부 확인
    public boolean existsByKakaoId(String kakaoId) {
        return memberRepository.existsByKakaoId(kakaoId);
    }
    
    // 소셜 이메일로 계정 조회
    public java.util.Optional<Member> findBySocialEmail(String socialEmail) {
        return memberRepository.findBySocialEmail(socialEmail);
    }
    
    // 소셜 이메일로 계정 존재 여부 확인
    public boolean existsBySocialEmail(String socialEmail) {
        return memberRepository.existsBySocialEmail(socialEmail);
    }
    
    // 카카오 로그인 처리 메서드
    public Long processKakaoLogin(String email, String nickname, Long kakaoId) {
        // 카카오 ID로 기존 회원 조회
        java.util.Optional<Member> existingMember = memberRepository.findByKakaoId(kakaoId.toString());
        
        if (existingMember.isPresent()) {
            // 기존 카카오 회원이면 로그인 처리
            return existingMember.get().getMemberId();
        }
        
        // 이메일로 기존 회원 조회 (일반 회원가입한 경우)
        java.util.Optional<Member> memberByEmail = memberRepository.findByEmailIgnoreCase(email);
        if (memberByEmail.isPresent()) {
            // 기존 이메일 회원이면 소셜 정보 추가
            Member member = memberByEmail.get();
            member.setSocialEmail(email);
            memberRepository.save(member);
            return member.getMemberId();
        }
        
        // 새로운 카카오 회원 생성
        Member newMember = new Member();
        newMember.setEmail(email);
        newMember.setNickname(nickname);
        newMember.setName(nickname); // 카카오 닉네임을 이름으로 사용
        newMember.setKakaoId(kakaoId.toString());
        
        return memberRepository.save(newMember).getMemberId();
    }
    // ---------------------------------

    public ReferencedWarning getReferencedWarning(final Long memberId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundException::new);
        final Notification memberNotification = notificationRepository.findFirstByMember(member);
        if (memberNotification != null) {
            referencedWarning.setKey("member.notification.member.referenced");
            referencedWarning.addParam(memberNotification.getNotificationId());
            return referencedWarning;
        }
        final ChallengeCount memberChallengeCount = challengeCountRepository.findFirstByMember(member);
        if (memberChallengeCount != null) {
            referencedWarning.setKey("member.challengeCount.member.referenced");
            referencedWarning.addParam(memberChallengeCount.getChallengeCountId());
            return referencedWarning;
        }
        final Attendance memberAttendance = attendanceRepository.findFirstByMember(member);
        if (memberAttendance != null) {
            referencedWarning.setKey("member.attendance.member.referenced");
            referencedWarning.addParam(memberAttendance.getAttendanceId());
            return referencedWarning;
        }
        final java.util.Optional<Budget> memberBudgetOpt = budgetRepository.findFirstByMember(member);
        if (memberBudgetOpt.isPresent()) {
            Budget memberBudget = memberBudgetOpt.get();
            referencedWarning.setKey("member.budget.member.referenced");
            referencedWarning.addParam(memberBudget.getBudgetId());
            return referencedWarning;
        }
        final PlaceBookmark memberPlaceBookmark = placeBookmarkRepository.findFirstByMember(member);
        if (memberPlaceBookmark != null) {
            referencedWarning.setKey("member.placeBookmark.member.referenced");
            referencedWarning.addParam(memberPlaceBookmark.getPBookmarkId());
            return referencedWarning;
        }
        return null;
    }

    // id 조회 후 member 객체 반환, 없으면 예외 처리
    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }
}
