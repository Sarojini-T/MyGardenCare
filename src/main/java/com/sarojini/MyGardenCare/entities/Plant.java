package com.sarojini.MyGardenCare.entities;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="plants")
public class Plant {
    public enum PlantLocation{
        WINDOW_SILL,
        INDOOR_POT,
        RAISED_BEDS,
        BALCONY,
        OUTDOOR_GROUND
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="plant_type")
    private String plantType;

    @Column(name="soil_planted_in")
    private String soilPlantedIn;

    @Column(name="date_planted")
    private LocalDate datePlanted;

    @Enumerated(EnumType.STRING)
    @Column(name="planted_location")
    private PlantLocation plantedLocation;

    @Column(name="watering_interval_days")
    private Integer wateringIntervalDays;

    @Column(name="is_alive")
    private Boolean isAlive;

    public Long getId(){
        return this.id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getPlantType(){
        return this.plantType;
    }

    public void setPlantType(String plantType){
        this.plantType = plantType;
    }

    public String getSoilPlantedIn(){
        return this.soilPlantedIn;
    }

    public void setSoilPlantedIn(String soilPlantedIn){
        this.soilPlantedIn = soilPlantedIn;
    }

    public LocalDate getDatedPlanted(){
        return this.datePlanted;
    }

    public void setDatePlanted(LocalDate datePlanted){
        this.datePlanted = datePlanted;
    }

    public PlantLocation getPlantedLocation(){
        return this.plantedLocation;
    }

    public void setPlantedLocation(PlantLocation plantedLocation){
        this.plantedLocation = plantedLocation;
    }

    public Integer getWateringIntervalDays(){
        return this.wateringIntervalDays;
    }

    public void setWateringIntervalDays(Integer wateringIntervalDays){
        this.wateringIntervalDays = wateringIntervalDays;
    }

    public Boolean getIsAlive(){
        return this.isAlive;
    }

    public void setIsAlive(Boolean isAlive){
        this.isAlive = isAlive;
    }
}
