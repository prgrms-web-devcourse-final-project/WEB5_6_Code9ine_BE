package com.grepp.spring.app.model.community_post.domain;

import com.grepp.spring.app.model.community_bookmark.domain.CommunityBookmark;
import com.grepp.spring.app.model.community_comment.domain.CommunityComment;
import com.grepp.spring.app.model.community_like.domain.CommunityLike;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.post_image.domain.PostImage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class CommunityPost {

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
    private Long postId;


    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private Boolean activated;

    @Column
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "post")
    private Set<CommunityComment> comments = new HashSet<>();

    @OneToMany(mappedBy = "post")
    private Set<CommunityLike> liked = new HashSet<>();

    @OneToMany(mappedBy = "post")
    private Set<CommunityBookmark> bookmarks = new HashSet<>();

    @OneToMany(mappedBy = "post")
    private Set<PostImage> images = new HashSet<>();

}
