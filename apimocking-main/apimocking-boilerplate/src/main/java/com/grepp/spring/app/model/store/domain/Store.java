package com.grepp.spring.app.model.store.domain;

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
public class Store {

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
    private Long storeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column
    private Double longitude;

    @Column
    private Double latitude;

    @Column
    private String category;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime modifiedAt;

    @Column
    private Boolean activated;

    @Column
    private String location;

    @Column
    private String firstMenu;

    @Column
    private Integer firstPrice;

    @Column
    private String secondMenu;

    @Column
    private Integer secondPrice;

    @Column
    private String thirdMenu;

    @Column
    private Integer thirdPrice;

    @Column
    private String contact;

    @Column
    private String sido;

    @OneToMany(mappedBy = "store")
    private Set<PlaceBookmark> placeBookmarks = new HashSet<>();

}
