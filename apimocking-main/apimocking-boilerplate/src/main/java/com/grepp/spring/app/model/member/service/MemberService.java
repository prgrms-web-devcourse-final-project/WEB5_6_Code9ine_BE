package com.grepp.spring.app.model.member.service;

import com.grepp.spring.app.model.attendance.domain.Attendance;
import com.grepp.spring.app.model.attendance.repos.AttendanceRepository;
import com.grepp.spring.app.model.budget.domain.Budget;
import com.grepp.spring.app.model.budget.repos.BudgetRepository;
import com.grepp.spring.app.model.challenge_count.domain.ChallengeCount;
import com.grepp.spring.app.model.challenge_count.repos.ChallengeCountRepository;
import com.grepp.spring.app.model.community_post.domain.CommunityPost;
import com.grepp.spring.app.model.community_post.repos.CommunityPostRepository;
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
    private final CommunityPostRepository communityPostRepository;
    private final NotificationRepository notificationRepository;
    private final ChallengeCountRepository challengeCountRepository;
    private final AttendanceRepository attendanceRepository;
    private final BudgetRepository budgetRepository;
    private final PlaceBookmarkRepository placeBookmarkRepository;

    public MemberService(final MemberRepository memberRepository,
            final CommunityPostRepository communityPostRepository,
            final NotificationRepository notificationRepository,
            final ChallengeCountRepository challengeCountRepository,
            final AttendanceRepository attendanceRepository,
            final BudgetRepository budgetRepository,
            final PlaceBookmarkRepository placeBookmarkRepository) {
        this.memberRepository = memberRepository;
        this.communityPostRepository = communityPostRepository;
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
        return member;
    }

    public boolean emailExists(final String email) {
        return memberRepository.existsByEmailIgnoreCase(email);
    }

    public ReferencedWarning getReferencedWarning(final Long memberId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundException::new);
        final CommunityPost memberCommunityPost = communityPostRepository.findFirstByMember(member);
        if (memberCommunityPost != null) {
            referencedWarning.setKey("member.communityPost.member.referenced");
            referencedWarning.addParam(memberCommunityPost.getPostId());
            return referencedWarning;
        }
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
        final Budget memberBudget = budgetRepository.findFirstByMember(member);
        if (memberBudget != null) {
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

}
