package com.grepp.spring.app.model.library.domain;

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
public class Library {

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
    private Long libraryId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column
    private Double longitude;

    @Column
    private Double latitude;

    @Column
    private String url;

    @Column
    private Boolean activated;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime modifiedAt;

    @OneToMany(mappedBy = "library")
    private Set<PlaceBookmark> placeBookmarks = new HashSet<>();

}
