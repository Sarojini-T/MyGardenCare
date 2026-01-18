package com.sarojini.MyGardenCare.entities;
import com.sarojini.MyGardenCare.enums.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_plants", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "plant_id", "plant_container",
        "pot_size", "soil_type", "has_drainage", "plant_location"})
})
@Setter
@Getter
@NoArgsConstructor
public class UserPlant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
}
