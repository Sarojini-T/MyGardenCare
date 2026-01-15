package com.sarojini.MyGardenCare.services;

import com.sarojini.MyGardenCare.dto.UserPlantCreateRequest;
import com.sarojini.MyGardenCare.dto.UserPlantResponse;
import com.sarojini.MyGardenCare.dto.UserPlantUpdateRequest;
import com.sarojini.MyGardenCare.entities.Plant;
import com.sarojini.MyGardenCare.entities.User;
import com.sarojini.MyGardenCare.entities.UserPlant;
import com.sarojini.MyGardenCare.repositories.PlantRepository;
import com.sarojini.MyGardenCare.repositories.UserPlantRepository;
import com.sarojini.MyGardenCare.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

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

    private User getUser(String username){
        Optional<User> userOptional = userRepository.findByUsername(username);
        if(!userOptional.isPresent()) throw new RuntimeException("user " + username + " not found");
        return userOptional.get();
    }

    private List<Plant> getPlants(String plantName){
        List<Plant> plantList = plantRepository.searchByAnyName(plantName);
        if(plantList.isEmpty()) throw new RuntimeException(plantName + " not found");
        return plantList;
    }

    private Plant getPlantById(Long id){
        Optional<Plant> plantByIdOptional =  plantRepository.findById(id);
        if(!plantByIdOptional.isPresent()) throw new RuntimeException("Plant with id " + id + " not found");
        return plantByIdOptional.get();
    }

    private UserPlantResponse mapUserPlantToResponseDto(UserPlant userPlant){
        UserPlantResponse response = new UserPlantResponse();
        response.setUserPlantId(userPlant.getId());
        response.setPlantId(userPlant.getPlant().getId());
        response.setPlantName(userPlant.getPlant().getCommonName());
        response.setPlantContainer(userPlant.getPlantContainer());
        response.setPotSize(userPlant.getPotSize());
        response.setHasDrainage(userPlant.getHasDrainage());
        response.setSoilType(userPlant.getSoilType());
        response.setPlantLocation(userPlant.getPlantLocation());

        return response;
    }

    private UserPlant mapCreateReqDtoToUserPlant(UserPlantCreateRequest createReq){
        UserPlant newUserPlant = new UserPlant();
        User user = getUser(createReq.getUsername());
        Plant plant = getPlantById(createReq.getPlantId());

        newUserPlant.setUser(user);
        newUserPlant.setPlant(plant);
        newUserPlant.setPlantContainer(createReq.getPlantContainer());
        newUserPlant.setPotSize(createReq.getPotSize());
        newUserPlant.setPlantLocation(createReq.getPlantLocation());
        newUserPlant.setHasDrainage(createReq.getHasDrainage());
        newUserPlant.setSoilType(createReq.getSoilType());

        return newUserPlant;
    }

    private UserPlant mapUpdateReqDtoToUserPlant(Long id, UserPlantUpdateRequest updateReq){
        Optional<UserPlant> userPlantToUpdateOptional = userPlantRepository.findById(id);
        if(!userPlantToUpdateOptional.isPresent()) throw new RuntimeException("User Plant not found");

        UserPlant userPlantToUpdate =  userPlantToUpdateOptional.get();
        if (updateReq.getPlantContainer() != null) {
            userPlantToUpdate.setPlantContainer(updateReq.getPlantContainer());
        }
        if (updateReq.getPotSize() != null) {
            userPlantToUpdate.setPotSize(updateReq.getPotSize());
        }
        if (updateReq.getPlantLocation() != null) {
            userPlantToUpdate.setPlantLocation(updateReq.getPlantLocation());
        }
        if (updateReq.getSoilType() != null) {
            userPlantToUpdate.setSoilType(updateReq.getSoilType());
        }
        return userPlantToUpdate;
    }



    private List<UserPlantResponse> userPlantListToResponse(List<UserPlant> userPlants){
        List<UserPlantResponse> userPlantResponseList = new ArrayList<>();
        for(UserPlant userPlant : userPlants){
            userPlantResponseList.add(mapUserPlantToResponseDto(userPlant));
        }
        return userPlantResponseList;
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
        if(!userPlantByIdOptional.isPresent())  throw new RuntimeException("Plant with id " + id + " not found");
        UserPlant userPlantById = userPlantByIdOptional.get();
        return mapUserPlantToResponseDto(userPlantById);
    }



   @Transactional
    public UserPlantResponse createUserPlant(UserPlantCreateRequest createReq){
        UserPlant newUserPlant = mapCreateReqDtoToUserPlant(createReq);

       boolean isUserPlantInDB = userPlantRepository.isDuplicate(
               newUserPlant.getUser(),
               newUserPlant.getPlant(),
               newUserPlant.getPlantContainer(),
               newUserPlant.getPlantLocation(),
               newUserPlant.getPotSize(),
               newUserPlant.getHasDrainage(),
               newUserPlant.getSoilType()
       );

       if(isUserPlantInDB) throw new RuntimeException("This user plant already exists");
       else userPlantRepository.save(newUserPlant);

       return  mapUserPlantToResponseDto(newUserPlant);
   }

   @Transactional
   public  UserPlantResponse updateUserPlantById(Long id, UserPlantUpdateRequest updateReq){
        UserPlant userPlantToUpdate =  mapUpdateReqDtoToUserPlant(id, updateReq);
        UserPlant updatedUserPlant = userPlantRepository.save(userPlantToUpdate);
        return mapUserPlantToResponseDto(updatedUserPlant);
   }

   public void deleteUserPlantById(Long id){
        userPlantRepository.deleteById(id);
   }

   @Modifying
   @Transactional
   public void deleteUserPlantsByName(String username, String plantName){
        User user = getUser(username);
        List<Plant> plantList = getPlants(plantName);
        userPlantRepository.deleteByUserAndPlantIn(user, plantList);
   }

   @Modifying
   @Transactional
   public void deleteAllUserPlants(String username){
        User user = getUser(username);
        userPlantRepository.deleteByUser(user);
   }
}
