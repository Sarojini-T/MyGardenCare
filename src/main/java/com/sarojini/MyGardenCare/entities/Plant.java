package com.MyGardenCare.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;


@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="plants")
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "common_name", nullable = false)
    private String commonName;

    @Column(name = "scientific_name", nullable =  false, unique = true)
    private String scientificName;

    @Column(name = "alternate_names", columnDefinition = "TEXT")
    private String alternateNames;

    @Column(name = "light_requirement", columnDefinition = "TEXT")
    private String lightRequirement;

    @Column(name = "soil_type", columnDefinition = "TEXT")
    private String soilType;

    @Column(name = "life_cycle", columnDefinition =  "TEXT")
    private String lifeCycle;

    @Column(name = "water_requirement")
    private String waterRequirement;

    @Column(name = "height_in_meters")
    private Double heightInMeters;

    @Column(name = "width_in_meters")
    private Double widthInMeters;

    private String growth;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public Plant(String commonName, String scientificName){
        this.commonName = commonName;
        this.scientificName = scientificName;
    }
}