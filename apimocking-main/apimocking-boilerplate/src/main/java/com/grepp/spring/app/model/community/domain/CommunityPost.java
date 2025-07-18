package com.grepp.spring.app.model.community.domain;

import com.grepp.spring.app.model.challenge.code.ChallengeCategory;
import com.grepp.spring.app.model.challenge.code.CommunityCategory;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.post_image.domain.PostImage;
import com.grepp.spring.infra.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "community_post")
public class CommunityPost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int likeCount = 0;

    @Column(nullable = false)
    private int commentCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommunityCategory category;

    @Enumerated(EnumType.STRING)
    private ChallengeCategory challenge;

    @OneToMany(mappedBy = "post")
    private Set<CommunityComment> comments = new HashSet<>();

    @OneToMany(mappedBy = "post")
    private Set<CommunityLike> liked = new HashSet<>();

    @OneToMany(mappedBy = "post")
    private Set<CommunityBookmark> bookmarks = new HashSet<>();

    @OneToMany(mappedBy = "post")
    private Set<PostImage> images = new HashSet<>();

}
