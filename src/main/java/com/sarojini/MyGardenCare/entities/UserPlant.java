package com.MyGardenCare.entities;
import com.MyGardenCare.enums.PlantContainer;
import com.MyGardenCare.enums.PlantLocation;
import com.MyGardenCare.enums.PotSize;
import com.sarojini.MyGardenCare.enums.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_plants", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "nickname"})
})
public class UserPlant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;

    @Enumerated(EnumType.STRING)
    @Column(name = "plant_container", nullable = false)
    private PlantContainer plantContainer;

    @Enumerated(EnumType.STRING)
    @Column(name = "plant_location", nullable = false)
    private PlantLocation plantLocation;

    @Enumerated(EnumType.STRING)
    @Column(name =  "pot_size")
    private PotSize potSize;

    @Column(name = "has_drainage")
    private Boolean hasDrainage;

    @Column(name = "soil_type")
    private String soilType;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

   public UserPlant(String nickname, User user, Plant plant,
                    PlantContainer plantContainer, PlantLocation plantLocation){
       this.nickname = nickname;
       this.user = user;
       this.plant = plant;
       this.plantContainer = plantContainer;
       this.plantLocation =  plantLocation;
   }
}
