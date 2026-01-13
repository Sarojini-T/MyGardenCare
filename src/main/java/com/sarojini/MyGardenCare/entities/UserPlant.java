package com.sarojini.MyGardenCare.entities;
import jakarta.persistence.*;

@Entity
@Table(name = "user_plants")
public class UserPlant {
    public enum PlantContainer{
        POT,
        RAISED_BEDS,
        OUTDOOR_GROUND,
        HANGING_BASKET,
        WINDOW_BOX
    }

    public enum PotSize{
        SMALL,
        MEDIUM,
        LARGE
    }

    public enum PlantLocation{
        INDOOR,
        OUTDOOR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;

    @Enumerated(EnumType.STRING)
    @Column(name = "plant_container")
    private PlantContainer plantContainer;

    @Enumerated(EnumType.STRING)
    @Column(name =  "pot_size")
    private PotSize potSize;

    @Column(name = "has_drainage")
    private Boolean hasDrainage;

    @Column(name = "soil_type")
    private String soilType;

    @Enumerated(EnumType.STRING)
    @Column(name = "plant_location")
    private PlantLocation plantLocation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public PlantContainer getPlantContainer() {
        return plantContainer;
    }

    public void setPlantContainer(PlantContainer plantContainer) {
        this.plantContainer = plantContainer;
    }

    public PotSize getPotSize() {
        return potSize;
    }

    public void setPotSize(PotSize potSize) {
        this.potSize = potSize;
    }

    public Boolean getHasDrainage() {
        return hasDrainage;
    }

    public void setHasDrainage(Boolean hasDrainage) {
        this.hasDrainage = hasDrainage;
    }

    public String getSoilType() {
        return soilType;
    }

    public void setSoilType(String soilType) {
        this.soilType = soilType;
    }

    public PlantLocation getPlantLocation() {
        return plantLocation;
    }

    public void setPlantLocation(PlantLocation plantLocation) {
        this.plantLocation = plantLocation;
    }
}
