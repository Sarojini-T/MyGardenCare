package com.sarojini.MyGardenCare.services;

import com.sarojini.MyGardenCare.entities.Plant;
import com.sarojini.MyGardenCare.entities.UserPlant;
import com.sarojini.MyGardenCare.enums.ContainerSize;
import com.sarojini.MyGardenCare.enums.PlantContainer;
import com.sarojini.MyGardenCare.enums.PlantLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
    Generates recommendations on how to care for the plant the user wants to add to their garden by
    taking the information returned from the Permapeople API, comparing it to the information provided
    by the user (container, containerSize, plantLocation etc.) and recommending best practices.
    */
@Service
@RequiredArgsConstructor
public class PlantRecommendationService {
    private final Map<String, LightRecommendationBasedOnLocation> LIGHT_RULES = Map.of(
            "full sun",
                new LightRecommendationBasedOnLocation(
                        " Requires direct exposure to sunlight for the majority of the day.",
                                " Place near a south or west facing window.",
                                " Place in south-facing or southwest facing areas."),

         "partial sun/shade",
                 new LightRecommendationBasedOnLocation(
                         " Requires direct sunlight for a portion of the day (3-6 hours), then put in a shaded area.",
                                 " Place near a north or east facing window.",
                                 " Place in shaded areas like under trees or covered porches."
    ),
            "indirect light",
                 new LightRecommendationBasedOnLocation(
                         " Requires limited exposure to direct sunlight.",
                                 " Place in an area 2-5 feet away from windows that receive lots of sunlight.",
                                 " Place in a shaded area like under a tree or covered patio."
    ),
            "full shade",
                 new LightRecommendationBasedOnLocation(
                         " Requires less than 3 hours of direct sunlight.",
                                 " Place near east-facing window or a brightly lit room/corner away from direct sun.",
                                 " Place near the north facing side of the outdoor area or under covered area like porch or patio."
    ));

    private record LightRecommendationBasedOnLocation(String recommendation, String indoor, String outdoor){}

    public List<String> addRecommendationsToPlant(UserPlant userPlant){
        Plant plant = userPlant.getPlant();

        List<String> recommendations = new ArrayList<>();

        PlantContainer plantContainer= userPlant.getPlantContainer();
        PlantLocation plantLocation = userPlant.getPlantLocation();
        ContainerSize containerSize = userPlant.getContainerSize();
        Boolean hasDrainage = userPlant.getHasDrainage();

        String lightRequirements = plant.getLightRequirement() != null ? plant.getLightRequirement().toLowerCase().trim() : "unavailable";
        String waterRequirements = plant.getWaterRequirement() != null ? plant.getWaterRequirement().toLowerCase().trim() : "unavailable";
        String soilType = plant.getSoilType() != null ? plant.getSoilType().toLowerCase().trim() : "unavailable";
        String lifeCycle = plant.getLifeCycle() != null ? plant.getLifeCycle().toLowerCase().trim() : "unavailable";
        Double heightInMeters = plant.getHeightInMeters();
        Double widthInMeters = plant.getWidthInMeters();
        String growth = plant.getGrowth() != null ? plant.getGrowth().toLowerCase().trim() : "unavailable";


        addLightRequirementRecommendations(recommendations, lightRequirements, plantLocation);
        addSoilTypeRecommendations(soilType, recommendations);
        addWaterRequirementsRecommendations(waterRequirements, recommendations);


       if(isInAnEnclosedContainer(plantContainer)){
           if(hasDrainage != null && !hasDrainage){
                recommendations.add("If possible add drainage holes to your container to allow excess water to escape and prevent root rot. If not, consider repotting into a container that contains them.");
           }
       }

        // Check if the container size matches the plant's growth patterns
        if (containerSize == ContainerSize.SMALL) {
            if (!growth.equals("unavailable") && (growth.contains("fast") || growth.contains("bushy"))) {
                recommendations.add(String.format("This plant has a %s growth pattern and will likely need to be repotted into a larger container soon.", growth.toLowerCase()));
            }

              // A small container can typically hold a plant between the heigh of 0.3 - 0.6 meters and a width between 0.3 - 0.45 meters
            if (heightInMeters != null && widthInMeters != null && (heightInMeters > 0.6 || widthInMeters > 0.45)) {
                recommendations.add(String.format("The container is too small for this plant as it can reach a height of %.2f meters and grow %.2f meters wide, which can restrict its growth. Consider repotting into a larger container or the ground.",
                        heightInMeters, widthInMeters));
            }

        } else if (containerSize == ContainerSize.MEDIUM) {
            // A medium container can typically hold a plant up to 1.2 meters tall and between 0.6 - 0.9 meters wide
            if (heightInMeters != null && widthInMeters != null && (heightInMeters > 1.2 || widthInMeters > 0.9)) {
                recommendations.add(String.format("The container is too small for this plant as it can reach a height of %.2f meters and grow %.2f meters wide. Consider repotting into a larger container or the ground to not restrict its growth.",
                        heightInMeters, widthInMeters));
            }

            if (!growth.equals("unavailable") && growth.contains("fast")) {
                recommendations.add("The plant has a fast growth pattern and it may need to be pruned regularly to maintain its shape and health.");
            }

        } else if (containerSize == ContainerSize.LARGE) {
            if (heightInMeters != null && heightInMeters > 2.0) {
                recommendations.add(String.format("This plant can reach a height of %.2f meter, which can make it top-heavy. Make sure the container is weighted or sheltered from wind.",
                        heightInMeters));
            }
        }

        if(!lifeCycle.equals("unavailable")){
            if(lifeCycle.contains("perennial")){
                recommendations.add("Has a perennial lifecycle, which means it returns yearly. Prune dead foliage in late winter or early spring to encourage fresh growth. Water well to develop strong roots.");
            }
            if(lifeCycle.contains("annual")){
                recommendations.add("Has an annual lifecycle, which means it completes its life cycle in one season. Requires consistent moisture and regular fertilization since they grow quickly.");
            }
            if(lifeCycle.contains("biennial")){
                recommendations.add("Has a biennial lifecycle, which means it takes two years to grow. It will grow foliage in the first year and bloom in the second. Plant in early spring and maintain consistent moisture.");
            }
        } else {
            recommendations.add("Lifecycle : unavailable");
        }
        return recommendations;
    }

    private Boolean isInAnEnclosedContainer(PlantContainer container){
        return container == PlantContainer.POT
                || container == PlantContainer.WINDOW_BOX
                || container == PlantContainer.HANGING_BASKET
                || container == PlantContainer.RAISED_BEDS;
    }

     private void addLightRequirementRecommendations(List<String> recommendations,
                                                    String lightRequirements,
                                                    PlantLocation plantLocation){
         StringBuilder lightReqRec = new StringBuilder(String.format("Light requirements : %s.", lightRequirements));

         if(!lightRequirements.equals("unavailable")){
             String[] lightRequirementsArr = lightRequirements.split(",");

             for(String req : lightRequirementsArr){
                 String trimmedReq = req.trim();

                 LightRecommendationBasedOnLocation currRecs = LIGHT_RULES.get(trimmedReq);

                 if(currRecs != null){
                     String currGeneralRec = currRecs.recommendation();
                     String currRecByLocation = plantLocation == PlantLocation.INDOOR ? currRecs.indoor() : currRecs.outdoor();

                     lightReqRec.append(currGeneralRec).append(currRecByLocation);
                 }
             }
         }

         recommendations.add(lightReqRec.toString());
     }

     private void addSoilTypeRecommendations(String soilType,
                                             List<String> recommendations){
         StringBuilder soilRecs = new StringBuilder(String.format("Recommended soil type : %s.", soilType));

         if (!soilType.equals("unavailable")) {
             if (soilType.contains("light (sandy)")) {
                 soilRecs.append(" Sandy soil drains quickly and has trouble holding nutrients. Mix in compost or manure to improve moisture retention and nutrient density.");
             }
             if (soilType.contains("medium")) {
                 soilRecs.append(" Add mulch or compost yearly to maintain soil's structure and prevent it from compacting.");
             }
             if (soilType.contains("heavy (clay)")) {
                 soilRecs.append(" Can become waterlogged easily, avoid digging when wet and add coarse organic matter like wood chips or straw to create air pockets for the roots.");
             }
             if (soilType.contains("loamy")) {
                 soilRecs.append(" Avoid over-tilling to not break down the soil's natural structure.");
             }
             if( soilType.contains("peaty")){
                 soilRecs.append(" Retains moisture and is very acidic, if plant needs a neutral pH, add lime.");
             }
         }
         recommendations.add(soilRecs.toString());
     }

    private void addWaterRequirementsRecommendations (String waterRequirements,
                                                                 List<String> recommendations){
        StringBuilder waterRecs = new StringBuilder(String.format("Water requirements : %s.", waterRequirements));

        if (!waterRequirements.equals("unavailable")) {
            if (waterRequirements.contains("dry")) {
                waterRecs.append(" Make sure soil is completely dry before watering again.");
            }
            if (waterRequirements.contains("moist")) {
                waterRecs.append(" Check that soil remains damp");
            }
            if (waterRequirements.contains("medium")) {
                waterRecs.append(" Check if the top inch of the soil is dry before watering.");
            }
        }

        recommendations.add(waterRecs.toString());
    }
}

