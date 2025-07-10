package com.grepp.spring.app.model.festival.domain;

import com.grepp.spring.app.model.place_bookmark.domain.PlaceBookmark;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Festival {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long festivalId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column
    private String category;

    @Column
    private String address;

    @Column
    private LocalDate startAt;

    @Column
    private String target;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime modifiedAt;

    @Column
    private Boolean activated;

    @Column
    private String url;

    @Column
    private Double longitude;

    @Column
    private Double latitude;

    @Column
    private LocalDate endAt;

    @OneToMany(mappedBy = "festival")
    private Set<PlaceBookmark> placeBookmarks = new HashSet<>();

}
