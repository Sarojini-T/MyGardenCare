package com.sarojini.MyGardenCare.entities;
import jakarta.persistence.*;

@Entity
@Table(name="plants")
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "common_name", nullable = false)
    private String commonName;

    @Column(name = "alternate_names", columnDefinition = "TEXT")
    private String alternateNames;

    @Column(name = "scientific_name", nullable =  false, unique = true)
    private String scientificName;

    @Column(name = "water_requirement")
    private String waterRequirement;

    @Column(name = "light_requirement", columnDefinition = "TEXT")
    private String lightRequirement;

    @Column(name = "soil_type", columnDefinition = "TEXT")
    private String soilType;

    @Column(name = "life_cycle", columnDefinition =  "TEXT")
    private String lifeCycle;

    @Column
    private Integer height;

    @Column
    private Integer width;

    @Column
    private String growth;

    public Long getId(){
        return this.id;
    }

    public void setId(Long id){
        this.id =  id;
    }

    public String getCommonName(){
        return this.commonName;
    }

    public void setCommonName(String commonName){
        this.commonName = commonName;
    }

    public String getAlternateNames(){
        return this.alternateNames;
    }

    public void setAlternateNames(String alternateName){
        if(this.alternateNames == null) {
            this.alternateNames =  alternateName;
        }else{
            this.alternateNames += ", " + alternateName;
        }

    }

    public String getScientificName(){
        return this.scientificName;
    }

    public void setScientificName(String scientificName){
        this.scientificName = scientificName;
    }

    public String getWaterRequirement(){
        return this.waterRequirement;
    }

    public void setWaterRequirement(String waterRequirement){
        this.waterRequirement = waterRequirement;
    }

    public String getLightRequirement(){
        return this.lightRequirement;
    }

    public void setLightRequirement(String lightRequirement){
        if(this.lightRequirement == null){
            this.lightRequirement = lightRequirement;
        } else{
            this.lightRequirement += ", " + lightRequirement;
        }

    }

    public String getSoilType(){
        return this.soilType;
    }

    public void setSoilType(String soilType){
        if(this.soilType == null){
            this.soilType = soilType;
        } else{
            this.soilType += ", " + soilType;
        }
    }

    public String getLifeCycle(){
        return this.lifeCycle;
    }

    public void setLifeCycle(String lifeCycle){
        if(this.lifeCycle == null){
            this.lifeCycle = lifeCycle;
        } else{
            this.lifeCycle += ", " + lifeCycle;
        }

    }

    public Integer getHeight(){
        return this.height;
    }

    public void setHeight(Integer height){
        this.height = height;
    }

    public Integer getWidth(){
        return this.width;
    }

    public void setWidth(Integer width){
        this.width = width;
    }

    public String getGrowth(){
        return this.growth;
    }

    public void setGrowth(String growth){
        this.growth = growth;
    }
}