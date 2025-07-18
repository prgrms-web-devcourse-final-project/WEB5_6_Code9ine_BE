package com.grepp.spring.app.model.member.domain;

import com.google.type.Decimal;
import com.grepp.spring.app.model.attendance.domain.Attendance;
import com.grepp.spring.app.model.budget.domain.Budget;
import com.grepp.spring.app.model.challenge_count.domain.ChallengeCount;
import com.grepp.spring.app.model.community.domain.CommunityPost;
import com.grepp.spring.app.model.notification.domain.Notification;
import com.grepp.spring.app.model.place_bookmark.domain.PlaceBookmark;
import com.grepp.spring.app.model.achieved_title.domain.AchievedTitle;
import com.grepp.spring.infra.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Member extends BaseEntity {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "primary_sequence",
            sequenceName = "primary_sequence",
            allocationSize = 1,
            initialValue = 10000
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "primary_sequence"
    )
    private Long memberId;

    @Column(nullable = true, unique = true, length = 50)
    private String email;

    @Column(nullable = true) // 소셜 로그인 사용자를 위해 nullable = true로 변경
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private String role;

    @Column
    private String phoneNumber;

    @Column(nullable = false)
    private String nickname;

    @Column
    private String profileImage;

    @Column(nullable = false, columnDefinition = "integer default 1")
    private Integer level = 1;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer totalExp = 0;

    @Column
    private BigDecimal goalAmount;

    @Column
    private String goalStuff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipped_title_id")
    private AchievedTitle equippedTitle; // 장착(대표) 칭호

    @OneToMany(mappedBy = "member")
    private Set<CommunityPost> posts = new HashSet<>();

    @OneToMany(mappedBy = "member")
    private Set<Notification> notifications = new HashSet<>();

    @OneToMany(mappedBy = "member")
    private Set<ChallengeCount> challengeCounts = new HashSet<>();

    @OneToMany(mappedBy = "member")
    private Set<Attendance> attendances = new HashSet<>();

    @OneToMany(mappedBy = "member")
    private Set<Budget> budgets = new HashSet<>();

    @OneToMany(mappedBy = "member")
    private Set<PlaceBookmark> placeBookmarks = new HashSet<>();

    // --- 소셜 로그인 관련 필드 ---
    @Column(nullable = true, unique = true)
    private String kakaoId; // 카카오 고유 ID (providerId → kakaoId로 명확히)

    @Column
    private String socialEmail; // 소셜 계정의 이메일 (있을 때만)
    // ---------------------------------

}
