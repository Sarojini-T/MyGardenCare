package com.sarojini.MyGardenCare.services;

import com.sarojini.MyGardenCare.dtos.UserPlantCreateRequest;
import com.sarojini.MyGardenCare.dtos.UserPlantResponse;
import com.sarojini.MyGardenCare.dtos.UserPlantUpdateRequest;
import com.sarojini.MyGardenCare.entities.Plant;
import com.sarojini.MyGardenCare.entities.User;
import com.sarojini.MyGardenCare.entities.UserPlant;
import com.sarojini.MyGardenCare.enums.PlantContainer;
import com.sarojini.MyGardenCare.enums.PlantLocation;
import com.sarojini.MyGardenCare.enums.ContainerSize;
import com.sarojini.MyGardenCare.exceptions.ConflictException;
import com.sarojini.MyGardenCare.repositories.PlantRepository;
import com.sarojini.MyGardenCare.repositories.UserPlantRepository;
import com.sarojini.MyGardenCare.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.sarojini.MyGardenCare.enums.*;

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

        Plant plant = getPlantByPlantName(plantName);

        Long plantId = plant.getId();

        List<UserPlant> userPlants = userPlantRepository.findByUserAndPlantId(user, plantId);

        return userPlantListToResponse(userPlants);
    }


    public UserPlantResponse getUserPlantById(Long id, String username){
        User user = getUser(username);

        UserPlant userPlantById = getUserPlantByIdAndUser(id, user);

        return mapUserPlantToResponseDto(userPlantById);
    }


    public UserPlantResponse createUserPlant(String username, UserPlantCreateRequest createReq){
        String nickname = createReq.getNickname();
        PlantContainer plantContainer = createReq.getPlantContainer();
        PlantLocation plantLocation = createReq.getPlantLocation();
        ContainerSize containerSize = createReq.getContainerSize();
        Boolean hasDrainage = createReq.getHasDrainage();

        User user = getUser(username);

        UserPlant newUserPlant = mapCreateReqDtoToUserPlant(user, createReq);

        validateNickname(nickname, user);
        validateReqAgainstPlantContainerRules(plantContainer, plantLocation, containerSize, hasDrainage);

        UserPlant savedUserPlant = userPlantRepository.save(newUserPlant);

        return  mapUserPlantToResponseDto(savedUserPlant);
    }

    @Transactional
    public UserPlantResponse updateUserPlantById(String username, Long id, UserPlantUpdateRequest updateReq){
        User user = getUser(username);

        UserPlant userPlantToUpdate =  getUserPlantByIdAndUser(id, user);

        PlantContainer plantContainer = updateReq.getPlantContainer() != null ? updateReq.getPlantContainer() : userPlantToUpdate.getPlantContainer();
        PlantLocation plantLocation = updateReq.getPlantLocation();
        ContainerSize containerSize = updateReq.getContainerSize();
        Boolean hasDrainage = updateReq.getHasDrainage();
        String nickname = updateReq.getNickname();
        String plantName = updateReq.getPlantName();

        validateReqAgainstPlantContainerRules(plantContainer, plantLocation, containerSize, hasDrainage);

        if(plantContainer.equals(PlantContainer.OUTDOOR_GROUND)){
            userPlantToUpdate.setContainerSize(null);
            userPlantToUpdate.setHasDrainage(null);
            userPlantToUpdate.setPlantLocation(PlantLocation.OUTDOOR);
        }

        validateNickname(nickname, user);

        applyPatchUpdates(plantContainer, plantLocation, nickname, plantName, containerSize, hasDrainage, userPlantToUpdate);

        return mapUserPlantToResponseDto(userPlantToUpdate);
    }

    public void deleteUserPlantById(String username , Long id){
        User user = getUser(username);
        userPlantRepository.deleteByUserAndId(user, id);
    }


    @Transactional
    public void deleteAllUserPlants(String username){
        User user = getUser(username);
        userPlantRepository.deleteByUser(user);
    }

    private User getUser(String username){
        Optional<User> userOptional = userRepository.findByUsernameIgnoreCase(username);
        if(userOptional.isEmpty()) throw new EntityNotFoundException("User " + username + " not found");
        return userOptional.get();
    }

    private Plant getPlantById(Long id){
        Optional<Plant> plantByIdOptional =  plantRepository.findById(id);
        if(plantByIdOptional.isEmpty()) throw new EntityNotFoundException("Plant " + id + " not found");
        return plantByIdOptional.get();
    }

    private UserPlant getUserPlantByIdAndUser(Long id, User user){
        Optional<UserPlant> userPlantToUpdateOptional = userPlantRepository.findByIdAndUser(id, user);
        if(userPlantToUpdateOptional.isEmpty()) throw new EntityNotFoundException("User Plant " + id + " not found");
        return userPlantToUpdateOptional.get();
    }


    private Plant getPlantByPlantName(String plantName){
        Optional<Plant> plantOptional = plantRepository.findByCommonName(plantName);
        if(plantOptional.isEmpty()) throw new EntityNotFoundException(String.format(plantName + " not found"));
        return plantOptional.get();
    }

    private UserPlantResponse mapUserPlantToResponseDto(UserPlant userPlant){
        UserPlantResponse response = new UserPlantResponse();
        response.setUserPlantId(userPlant.getId());
        response.setPlantId(userPlant.getPlant().getId());
        response.setPlantName(userPlant.getPlant().getCommonName());
        response.setNickname(userPlant.getNickname());
        response.setPlantContainer(userPlant.getPlantContainer());
        response.setContainerSize(userPlant.getContainerSize());
        response.setHasDrainage(userPlant.getHasDrainage());
        response.setPlantLocation(userPlant.getPlantLocation());

        return response;
    }

    private UserPlant mapCreateReqDtoToUserPlant(User user, UserPlantCreateRequest createReq){
        String nickname = createReq.getNickname();
        Plant plant = getPlantById(createReq.getPlantId());
        PlantContainer plantContainer = createReq.getPlantContainer();
        PlantLocation plantLocation = createReq.getPlantLocation();
        ContainerSize containerSize = createReq.getContainerSize();
        Boolean hasDrainage = createReq.getHasDrainage();

        UserPlant newUserPlant = new UserPlant(nickname, user, plant, plantContainer, plantLocation);
        if(containerSize != null) newUserPlant.setContainerSize(containerSize);
        if(hasDrainage != null) newUserPlant.setHasDrainage(hasDrainage);

        return newUserPlant;
    }

    private List<UserPlantResponse> userPlantListToResponse(List<UserPlant> userPlants){
        List<UserPlantResponse> userPlantResponseList = new ArrayList<>();
        for(UserPlant userPlant : userPlants){
            userPlantResponseList.add(mapUserPlantToResponseDto(userPlant));
        }
        return userPlantResponseList;
    }

    public void validateReqAgainstPlantContainerRules(PlantContainer plantContainer,
                                            PlantLocation plantLocation,
                                            ContainerSize containerSize,
                                            Boolean hasDrainage){
        if(plantContainer.equals(PlantContainer.OUTDOOR_GROUND)){
            if(containerSize != null) throw new IllegalArgumentException("Cannot set containerSize for OUTDOOR_GROUND");

            if(hasDrainage != null) throw new IllegalArgumentException("OUTDOOR_GROUND doesn't need drainage");

            if(plantLocation != null && plantLocation.equals(PlantLocation.INDOOR)) throw new IllegalArgumentException("plantLocation cannot be INDOOR for OUTDOOR_GROUND");
        }
    }


    private void validateNickname(String nickname, User user){
        if(nickname != null){
            String trimmedNickname = nickname.trim();
            if(trimmedNickname.isBlank()) throw new IllegalArgumentException("Nickname cannot be blank");
            if(userPlantRepository.existsByUserAndNicknameIgnoreCase(user, trimmedNickname)) {
                throw new ConflictException("Nickname must be unique");
            }
        }
    }

    private void applyPatchUpdates(PlantContainer plantContainer,
                                   PlantLocation plantLocation,
                                   String nickname,
                                   String plantName,
                                   ContainerSize containerSize,
                                   Boolean hasDrainage,
                                   UserPlant userPlantToUpdate){
        if(plantContainer != null) userPlantToUpdate.setPlantContainer(plantContainer);
        if(plantLocation != null) userPlantToUpdate.setPlantLocation(plantLocation);
        if(nickname != null) userPlantToUpdate.setNickname(nickname);
        if(plantName != null) userPlantToUpdate.setPlant(getPlantByPlantName(plantName));
        if(containerSize != null) userPlantToUpdate.setContainerSize(containerSize);
        if(hasDrainage != null) userPlantToUpdate.setHasDrainage(hasDrainage);
    }
}