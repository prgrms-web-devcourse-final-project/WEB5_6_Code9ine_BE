package com.grepp.spring.app.model.library.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Library {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private String location;

    @Column
    private Boolean activated;

    @Column
    private LocalDateTime createdAt;

    @Column(name = "modified_At")
    private LocalDateTime modifiedAt;

    @Column
    private String contact;

}
