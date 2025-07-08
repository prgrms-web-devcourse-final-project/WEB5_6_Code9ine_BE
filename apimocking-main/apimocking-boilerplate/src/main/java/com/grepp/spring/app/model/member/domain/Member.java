package com.grepp.spring.app.model.member.domain;

import com.grepp.spring.app.model.attendance.domain.Attendance;
import com.grepp.spring.app.model.budget.domain.Budget;
import com.grepp.spring.app.model.challenge_count.domain.ChallengeCount;
import com.grepp.spring.app.model.community_post.domain.CommunityPost;
import com.grepp.spring.app.model.notification.domain.Notification;
import com.grepp.spring.app.model.place_bookmark.domain.PlaceBookmark;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Member {

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

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private String role;

    @Column
    private String phoneNumber;

    @Column
    private Boolean activated;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime modifiedAt;

    @Column(nullable = false)
    private String nickname;

    @Column
    private String profileImage;

    @Column
    private Integer level;

    @Column
    private Integer totalExp;

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

}
