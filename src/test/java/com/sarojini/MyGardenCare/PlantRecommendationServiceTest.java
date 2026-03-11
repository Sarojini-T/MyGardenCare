package com.sarojini.MyGardenCare;

import com.sarojini.MyGardenCare.entities.Plant;
import com.sarojini.MyGardenCare.entities.User;
import com.sarojini.MyGardenCare.entities.UserPlant;
import com.sarojini.MyGardenCare.enums.ContainerSize;
import com.sarojini.MyGardenCare.enums.PlantContainer;
import com.sarojini.MyGardenCare.enums.PlantLocation;
import com.sarojini.MyGardenCare.services.PlantRecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlantRecommendationServiceTest {
    private PlantRecommendationService plantRecommendationService;
    private UserPlant userPlant;

    @BeforeEach
    void setUp(){
        Plant plant = new Plant("testPlant", "testScientificName");
        plant.setLightRequirement("Full sun, Partial sun/shade");
        plant.setWaterRequirement("Medium");
        plant.setGrowth("Fast");
        plant.setLifeCycle("Annual");
        plant.setSoilType("Loamy, Light (sandy)");
        plant.setHeightInMeters(1.5);
        plant.setWidthInMeters(0.6);

        User user = new User("user01", "user01@gmail.com", "abc");

        userPlant = new UserPlant("Tomato1", user, plant, PlantContainer.POT, PlantLocation.OUTDOOR);
        userPlant.setContainerSize(ContainerSize.MEDIUM);
        userPlant.setHasDrainage(true);

        plantRecommendationService = new PlantRecommendationService();
    }

    @Test
    void shouldAddLightRequirementRecommendations_WhenLightRequirementIsNotNull(){
        List<String> recommendations = plantRecommendationService.addRecommendationsToPlant(userPlant);

        assertNotNull(recommendations);
        assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("Requires direct exposure")));
        assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("south-facing or southwest facing areas")));
        assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("direct sunlight for a portion of the day")));
        assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("shaded areas like under trees or covered porches")));
    }

    @Test
    void shouldAddIndoorSpecificLightRecommendations_WhenPlantLocationIsIndoor(){
        userPlant.setPlantLocation(PlantLocation.INDOOR);
        userPlant.getPlant().setLightRequirement("Indirect light, Full shade");

        List<String> recommendations = plantRecommendationService.addRecommendationsToPlant(userPlant);

        assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("2-5 feet away from windows")));
        assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("brightly lit room/corner away from direct sun")));
    }

    @Test
    void shouldMarkAsUnavailable_WhenLightRequirementIsNull(){
        userPlant.getPlant().setLightRequirement(null);

        List<String> recommendations = plantRecommendationService.addRecommendationsToPlant(userPlant);

        assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("Light requirements : unavailable")));
    }

    @Test
    void shouldNotAddSpecificLightRequirements_WhenLightRequirementIsNotInLightRulesMap(){
        userPlant.getPlant().setLightRequirement("Direct Light");

        List<String> recommendations = plantRecommendationService.addRecommendationsToPlant(userPlant);

        assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("Light requirements : direct light")));
        assertFalse(recommendations.stream().anyMatch(rec -> rec.contains("Requires direct exposure")));
        assertFalse(recommendations.stream().anyMatch(rec -> rec.contains("south-facing or southwest facing areas")));
        assertFalse(recommendations.stream().anyMatch(rec -> rec.contains("direct sunlight for a portion of the day")));
        assertFalse(recommendations.stream().anyMatch(rec -> rec.contains("shaded areas like under trees or covered porches")));
    }


    @Test
    void shouldAddSoilTypeRecommendations_WhenSoilTypeIsNotNull(){
        userPlant.getPlant().setSoilType("light (sandy), medium, heavy (clay), loamy, peaty");

        List<String> recommendations = plantRecommendationService.addRecommendationsToPlant(userPlant);

        assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("Sandy soil drains quickly")));
        assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("Add mulch or compost yearly")));
        assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("Can become waterlogged easily")));
        assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("Avoid over-tilling")));
        assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("is very acidic")));

    }

    @Test
    void shouldMarkAsUnavailable_WhenSoilTypeIsNull(){
        userPlant.getPlant().setSoilType(null);

        List<String> recommendations = plantRecommendationService.addRecommendationsToPlant(userPlant);

        assertNotNull(recommendations);
        assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("Recommended soil type : unavailable")));
    }

    @Test
    void shouldAddWaterRequirementsRecommendations_WhenWaterRequirementsAreNotNull(){
       userPlant.getPlant().setWaterRequirement("medium, moist, dry");

        List<String> recommendations = plantRecommendationService.addRecommendationsToPlant(userPlant);

        assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("Check if the top inch")));
        assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("Check that soil remains damp")));
        assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("Make sure soil is completely dry")));
    }

    @Test
    void shouldMarkAsUnavailable_WhenWaterRequirementsAreNull(){
        userPlant.getPlant().setWaterRequirement(null);

        List<String> recommendations = plantRecommendationService.addRecommendationsToPlant(userPlant);

        assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("Water requirements : unavailable")));
    }

    @Test
    void shouldGenerateDrainageRecommendations_WhenInEnclosedContainer(){
        userPlant.setHasDrainage(false);

        List<String> recommendations = plantRecommendationService.addRecommendationsToPlant(userPlant);

        assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("add drainage holes")));
    }

    @Test
    void shouldNotGenerateDrainageRecommendations_WhenNotInEnclosedContainer(){
        userPlant.setPlantContainer(PlantContainer.OUTDOOR_GROUND);

        List<String> recommendations = plantRecommendationService.addRecommendationsToPlant(userPlant);

        assertFalse(recommendations.stream().anyMatch(rec -> rec.contains("add drainage holes")));
    }

    @Test
    void shouldNotGenerateDrainageRecommendations_WhenHasDrainageIsNull(){
        userPlant.setHasDrainage(null);

        List<String> recommendations = plantRecommendationService.addRecommendationsToPlant(userPlant);

        assertFalse(recommendations.stream().anyMatch(rec -> rec.contains("add drainage holes")));
    }

    @Test
    void shouldAddRecommendationsForContainer_WhenContainerSizeIsTooSmall(){
        ContainerSize[] containerSizeArr = {ContainerSize.SMALL, ContainerSize.MEDIUM, ContainerSize.LARGE};
        Double[] heightArr = {0.7, 1.3, 2.1};
        Double[] widthArr = {0.5, 0.10, 1.0};

        for(int i = 0; i < 3; i++){
            userPlant.setContainerSize(containerSizeArr[i]);
            userPlant.getPlant().setHeightInMeters(heightArr[i]);
            userPlant.getPlant().setWidthInMeters(widthArr[i]);

            List<String> recommendations = plantRecommendationService.addRecommendationsToPlant(userPlant);

            assertNotNull(recommendations);
            if(userPlant.getContainerSize() == ContainerSize.SMALL){
                assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("will likely need to be repotted into a larger")));
                assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("The container is too small for this plant")));
            } else if(userPlant.getContainerSize() == ContainerSize.MEDIUM){
                assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("not restrict its growth")));
                assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("it may need to be pruned regularly")));
            } else{
                assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("can make it top-heavy")));
            }
        }
    }

    @Test
    void shouldNotAddRecommendationsForHeightAndWidth_WhenHeightAndWidthMatchContainerSize(){
        ContainerSize[] containerSizeArr = {ContainerSize.SMALL, ContainerSize.MEDIUM, ContainerSize.LARGE};
        Double[] heightArr = {0.6, 1.2, 2.0};
        Double[] widthArr = {0.45, 0.9, 0.0};

        for(int i = 0; i < 3; i++){
            userPlant.setContainerSize(containerSizeArr[i]);
            userPlant.getPlant().setHeightInMeters(heightArr[i]);
            userPlant.getPlant().setWidthInMeters(widthArr[i]);

            List<String> recommendations = plantRecommendationService.addRecommendationsToPlant(userPlant);

            assertNotNull(recommendations);
            if(userPlant.getContainerSize() == ContainerSize.SMALL){
                assertFalse(recommendations.stream().anyMatch(rec -> rec.contains("The container is too small for this plant")));
            } else if(userPlant.getContainerSize() == ContainerSize.MEDIUM){
                assertFalse(recommendations.stream().anyMatch(rec -> rec.contains("repotting into a larger container or the ground to not restrict its growth")));
            } else{
                assertFalse(recommendations.stream().anyMatch(rec -> rec.contains("can make it top-heavy")));
            }
        }
    }

    @Test
    void shouldNotAddRecommendationsForHeightAndWidth_WhenHeightAndWidthAreNull(){
        userPlant.getPlant().setHeightInMeters(null);
        userPlant.getPlant().setWidthInMeters(null);

        ContainerSize[] containerSizeArr = {ContainerSize.SMALL, ContainerSize.MEDIUM, ContainerSize.LARGE};

        for(int i = 0; i < 3; i++){
            userPlant.setContainerSize(containerSizeArr[i]);

            List<String> recommendations = plantRecommendationService.addRecommendationsToPlant(userPlant);

            if(userPlant.getContainerSize() == ContainerSize.SMALL){
                assertFalse(recommendations.stream().anyMatch(rec -> rec.contains("The container is too small")));
            } else if(userPlant.getContainerSize() == ContainerSize.MEDIUM){
                assertFalse(recommendations.stream().anyMatch(rec -> rec.contains("not restrict its growth.")));
            } else{
                assertFalse(recommendations.stream().anyMatch(rec -> rec.contains("make it top-heavy")));
            }
        }

    }

    @Test
    void shouldNotAddRecommendationsForGrowth_WhenGrowthIsNull(){
        userPlant.getPlant().setGrowth(null);

        ContainerSize[] containerSizeArr = {ContainerSize.SMALL, ContainerSize.MEDIUM};

        for(int i = 0; i < 2; i++){
            userPlant.setContainerSize(containerSizeArr[i]);

            List<String> recommendations = plantRecommendationService.addRecommendationsToPlant(userPlant);

            if(userPlant.getContainerSize() == ContainerSize.SMALL){
                assertFalse(recommendations.stream().anyMatch(rec -> rec.contains("will likely need to be repotted")));
            } else if(userPlant.getContainerSize() == ContainerSize.MEDIUM){
                assertFalse(recommendations.stream().anyMatch(rec -> rec.contains("may need to be pruned regularly")));
            } else{
                assertFalse(recommendations.stream().anyMatch(rec -> rec.contains("make it top-heavy")));
            }
        }
    }

    @Test
    void shouldNotAddRecommendationsForContainer_WhenContainerSizeIsNull(){
        userPlant.setContainerSize(null);

        List<String> recommendations = plantRecommendationService.addRecommendationsToPlant(userPlant);

        assertFalse(recommendations.stream().anyMatch(rec -> rec.contains("will likely need to be repotted into a larger")));
        assertFalse(recommendations.stream().anyMatch(rec -> rec.contains("not restrict its growth")));
        assertFalse(recommendations.stream().anyMatch(rec -> rec.contains("can make it top-heavy")));
    }


    @Test
    void shouldAddLifecycleRecommendation_WhenLifeCycleIsNotNull(){
        String[] lifecycleArr = {"perennial", "annual", "biennial"};

        for(int i = 0; i < 3; i++){
            userPlant.getPlant().setLifeCycle(lifecycleArr[i]);
            List<String> recommendations = plantRecommendationService.addRecommendationsToPlant(userPlant);

            if(lifecycleArr[i].equals("perennial")){
                assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("Has a perennial lifecycle")));
            } else if(lifecycleArr[i].equals("annual")){
                assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("Has an annual lifecycle")));
            } else{
                assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("Has a biennial lifecycle")));
            }
        }
    }

    @Test
    void shouldMarkLifecycleAsUnavailable_WhenLifeCycleIsNull(){
        userPlant.getPlant().setLifeCycle(null);

        List<String> recommendations = plantRecommendationService.addRecommendationsToPlant(userPlant);

        assertTrue(recommendations.stream().anyMatch(rec -> rec.contains("Lifecycle : unavailable")));

    }


    @Test
    void shouldSkipSizeWarnings_WhenDimensionsAreNull(){
        // Proves the code doesn't crash, and successfully skips branches when data is missing
        userPlant.setContainerSize(ContainerSize.LARGE);
        userPlant.getPlant().setHeightInMeters(null);
        userPlant.getPlant().setWidthInMeters(null);

        List<String> recommendations = plantRecommendationService.addRecommendationsToPlant(userPlant);

        assertFalse(recommendations.stream().anyMatch(rec -> rec.contains("top-heavy")));
    }
}
