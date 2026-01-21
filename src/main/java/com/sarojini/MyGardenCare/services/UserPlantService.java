package com.sarojini.MyGardenCare.services;

import com.sarojini.MyGardenCare.dtos.UserPlantCreateRequest;
import com.sarojini.MyGardenCare.dtos.UserPlantResponse;
import com.sarojini.MyGardenCare.dtos.UserPlantUpdateRequest;
import com.sarojini.MyGardenCare.entities.Plant;
import com.sarojini.MyGardenCare.entities.User;
import com.sarojini.MyGardenCare.entities.UserPlant;
import com.sarojini.MyGardenCare.enums.PlantContainer;
import com.sarojini.MyGardenCare.enums.PlantLocation;
import com.sarojini.MyGardenCare.enums.PotSize;
import com.sarojini.MyGardenCare.repositories.PlantRepository;
import com.sarojini.MyGardenCare.repositories.UserPlantRepository;
import com.sarojini.MyGardenCare.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class UserPlantService {
    private final UserPlantRepository userPlantRepository;
    private final UserRepository userRepository;
    private final PlantRepository plantRepository;

    public UserPlantService(UserPlantRepository userPlantRepository,
                            UserRepository userRepository,
                            PlantRepository plantRepository){
        this.userPlantRepository = userPlantRepository;
        this.userRepository = userRepository;
        this.plantRepository = plantRepository;
    }

    public List<UserPlantResponse> getAllUserPlants(String username){
        User user = getUser(username);
        List<UserPlant> userPlants = userPlantRepository.findByUser(user);

        return userPlantListToResponse(userPlants);
    }

    public List<UserPlantResponse> getAllUserPlantsByPlantName(String username, String plantName){
        User user = getUser(username);
        List<Plant> plantList = getPlants(plantName);
        List<UserPlant> userPlantsByPlantName = userPlantRepository.findByUserAndPlantIn(user, plantList);

        return userPlantListToResponse(userPlantsByPlantName);
    }


    public UserPlantResponse getUserPlantById(Long id, String username){
        User user = getUser(username);

        Optional<UserPlant> userPlantByIdOptional = userPlantRepository.findByIdAndUser(id, user);
        if(!userPlantByIdOptional.isPresent())  throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Plant " + id + " not found");
        UserPlant userPlantById = userPlantByIdOptional.get();

        return mapUserPlantToResponseDto(userPlantById);
    }


    public UserPlantResponse createUserPlant(String username, UserPlantCreateRequest createReq){
        PlantContainer plantContainer = createReq.getPlantContainer();
        PlantLocation plantLocation = createReq.getPlantLocation();
        PotSize potSize = createReq.getPotSize();
        Boolean hasDrainage = createReq.getHasDrainage();

        validatePlantContainerRules(plantContainer, plantLocation, potSize, hasDrainage);

        User user = getUser(username);

        UserPlant newUserPlant = mapCreateReqDtoToUserPlant(user,createReq);
        UserPlant savedUserPlant = userPlantRepository.save(newUserPlant);

        return  mapUserPlantToResponseDto(savedUserPlant);
   }

   @Transactional
   public  UserPlantResponse updateUserPlantById(String username, Long id, UserPlantUpdateRequest updateReq){
        User user = getUser(username);

        Optional<UserPlant> userPlantToUpdateOptional = userPlantRepository.findByIdAndUser(id, user);
        if(!userPlantToUpdateOptional.isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Plant " + id + " not found");
        UserPlant userPlantToUpdate =  userPlantToUpdateOptional.get();

       PlantContainer plantContainer = updateReq.getPlantContainer() != null ? updateReq.getPlantContainer() : userPlantToUpdate.getPlantContainer();
       PlantLocation plantLocation = updateReq.getPlantLocation() != null ? updateReq.getPlantLocation() : userPlantToUpdate.getPlantLocation();
       PotSize potSize = updateReq.getPotSize() != null ? updateReq.getPotSize() : userPlantToUpdate.getPotSize();
       Boolean hasDrainage = updateReq.getHasDrainage() != null ? updateReq.getHasDrainage() : userPlantToUpdate.getHasDrainage();

       validatePlantContainerRules(plantContainer, plantLocation, potSize, hasDrainage);


       if (plantContainer != null) {
           userPlantToUpdate.setPlantContainer(plantContainer);
       }
       if (potSize != null) {
           userPlantToUpdate.setPotSize(updateReq.getPotSize());
       }
       if(hasDrainage != null && !hasDrainage.equals(userPlantToUpdate.getHasDrainage())){
           userPlantToUpdate.setHasDrainage(hasDrainage);
       }
       if (plantLocation != null) {
           userPlantToUpdate.setPlantLocation(plantLocation);
       }
       if (updateReq.getSoilType() != null) {
           userPlantToUpdate.setSoilType(updateReq.getSoilType());
       }
        return mapUserPlantToResponseDto(userPlantToUpdate);
   }

   public void deleteUserPlantById(String username , Long id){
        User user = getUser(username);
        userPlantRepository.deleteByUserAndId(user, id);
   }

   @Transactional
   public void deleteUserPlantsByName(String username, String plantName){
        User user = getUser(username);
        List<Plant> plantList = getPlants(plantName);
        userPlantRepository.deleteByUserAndPlantIn(user, plantList);
   }

   @Transactional
   public void deleteAllUserPlants(String username){
        User user = getUser(username);
        userPlantRepository.deleteByUser(user);
   }

    private User getUser(String username){
        Optional<User> userOptional = userRepository.findByUsernameIgnoreCase(username);
        if(!userOptional.isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + username + " not found");
        return userOptional.get();
    }

    private List<Plant> getPlants(String plantName){
        List<Plant> plantList = plantRepository.searchByAnyName(plantName);
        if(plantList.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, plantName + " not found");
        return plantList;
    }

    private Plant getPlantById(Long id){
        Optional<Plant> plantByIdOptional =  plantRepository.findById(id);
        if(!plantByIdOptional.isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant " + id + " not found");
        return plantByIdOptional.get();
    }

    private UserPlantResponse mapUserPlantToResponseDto(UserPlant userPlant){
        UserPlantResponse response = new UserPlantResponse();
        response.setUserPlantId(userPlant.getId());
        response.setPlantId(userPlant.getPlant().getId());
        response.setPlantName(userPlant.getPlant().getCommonName());
        response.setNickname(userPlant.getNickname());
        response.setPlantContainer(userPlant.getPlantContainer());
        response.setPotSize(userPlant.getPotSize());
        response.setHasDrainage(userPlant.getHasDrainage());
        response.setSoilType(userPlant.getSoilType());
        response.setPlantLocation(userPlant.getPlantLocation());

        return response;
    }

    private UserPlant mapCreateReqDtoToUserPlant(User user, UserPlantCreateRequest createReq){
        Plant plant = getPlantById(createReq.getPlantId());
        String nickname = createReq.getNickname();
        PlantContainer plantContainer = createReq.getPlantContainer();
        PlantLocation plantLocation = createReq.getPlantLocation();

        UserPlant newUserPlant = new UserPlant(nickname, user, plant, plantContainer, plantLocation);
        newUserPlant.setPotSize(createReq.getPotSize());
        newUserPlant.setHasDrainage(createReq.getHasDrainage());
        newUserPlant.setSoilType(createReq.getSoilType());

        return newUserPlant;
    }

    private List<UserPlantResponse> userPlantListToResponse(List<UserPlant> userPlants){
        List<UserPlantResponse> userPlantResponseList = new ArrayList<>();
        for(UserPlant userPlant : userPlants){
            userPlantResponseList.add(mapUserPlantToResponseDto(userPlant));
        }
        return userPlantResponseList;
    }

    public void validatePlantContainerRules(PlantContainer plantContainer,
                                            PlantLocation plantLocation,
                                            PotSize potSize,
                                            Boolean hasDrainage){
        if(plantContainer ==  PlantContainer.OUTDOOR_GROUND &&
                plantLocation == PlantLocation.INDOOR){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot select outdoor_ground if plantLocation is indoor"
            );
        }
        if(plantContainer != PlantContainer.POT && potSize != null){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot select pot size if container isn't a pot"
            );
        }

        if(plantContainer != PlantContainer.POT && hasDrainage != null){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot select has drainage if container isn't a pot"
            );
        }
    }
}
