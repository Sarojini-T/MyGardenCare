package com.sarojini.MyGardenCare;

import com.sarojini.MyGardenCare.dtos.UserPlantCreateRequest;
import com.sarojini.MyGardenCare.dtos.UserPlantResponse;
import com.sarojini.MyGardenCare.dtos.UserPlantUpdateRequest;
import com.sarojini.MyGardenCare.dtos.UserUpdateRequest;
import com.sarojini.MyGardenCare.entities.Plant;
import com.sarojini.MyGardenCare.entities.User;
import com.sarojini.MyGardenCare.entities.UserPlant;
import com.sarojini.MyGardenCare.enums.ContainerSize;
import com.sarojini.MyGardenCare.enums.PlantContainer;
import com.sarojini.MyGardenCare.enums.PlantLocation;
import com.sarojini.MyGardenCare.exceptions.ConflictException;
import com.sarojini.MyGardenCare.repositories.PlantRepository;
import com.sarojini.MyGardenCare.repositories.UserPlantRepository;
import com.sarojini.MyGardenCare.repositories.UserRepository;
import com.sarojini.MyGardenCare.services.UserPlantService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPlantTest {
    @Mock
    private UserPlantRepository userPlantRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlantRepository plantRepository;

    @InjectMocks
    private UserPlantService userPlantService;

    @Test
    public void getAllUserPlants_Success(){
        User user = new User("user01", "user01@gmail.com", "123");

        Plant tomatoPlant = createPlant("Heirloom Tomato", "Solanum lycopersicum", 1L);

        Plant peaceLilyPlant = createPlant("Peace Lily", "Spathiphyllum", 2L);

        UserPlant tomatoUserPlant = createNewUserPlant("Tomato", user, tomatoPlant, PlantContainer.POT, PlantLocation.INDOOR, 1L);

        UserPlant peaceLilyUserPlant = createNewUserPlant("Lily", user, peaceLilyPlant, PlantContainer.WINDOW_BOX, PlantLocation.OUTDOOR, 2L);

        List<UserPlant> mockUserPlantList = List.of(tomatoUserPlant, peaceLilyUserPlant);

        when(userRepository.findByUsernameIgnoreCase(user.getUsername())).thenReturn(Optional.of(user));
        when(userPlantRepository.findByUser(user)).thenReturn(mockUserPlantList);

        List<UserPlantResponse> userPlantResponseList = userPlantService.getAllUserPlants(user.getUsername());

        assertNotNull(userPlantResponseList);
        assertEquals(2, userPlantResponseList.size());
        assertEquals("Tomato", userPlantResponseList.get(0).getNickname());
        assertEquals("Lily", userPlantResponseList.get(1).getNickname());

        verify(userPlantRepository, times(1)).findByUser(user);
    }

    @Test
    public void getAllUserPlants_ThrowsEntityNotFoundException_WhenUserDoesNotExist(){
        when(userRepository.findByUsernameIgnoreCase("user01")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userPlantService.getAllUserPlants("user01");
        });
    }

    @Test
    public void getAllUserPlantsByPlantName_Success(){
        User user = new User("user01", "user01@gmail.com", "123");

        Plant tomatoPlant = createPlant("Heirloom Tomato", "Solanum lycopersicum", 1L);

        UserPlant tomatoUserPlant1 = createNewUserPlant("Tomato1", user, tomatoPlant, PlantContainer.POT, PlantLocation.INDOOR, 1L);

        UserPlant tomatoUserPlant2 = createNewUserPlant("Tomato2", user, tomatoPlant, PlantContainer.POT, PlantLocation.INDOOR, 2L);

        List<UserPlant> mockUserPlantList = List.of(tomatoUserPlant1, tomatoUserPlant2);

        when(userRepository.findByUsernameIgnoreCase("user01")).thenReturn(Optional.of(user));
        when(plantRepository.findByCommonName("Heirloom Tomato")).thenReturn(Optional.of(tomatoPlant));
        when(userPlantRepository.findByUserAndPlantId(user,1L)).thenReturn(mockUserPlantList);

        List<UserPlantResponse> userPlantResponseList = userPlantService.getAllUserPlantsByPlantName(user.getUsername(), "Heirloom Tomato");

        assertNotNull(userPlantResponseList);
        assertEquals(2, userPlantResponseList.size());
        assertEquals("Tomato1", userPlantResponseList.get(0).getNickname());
        assertEquals("Tomato2", userPlantResponseList.get(1).getNickname());

        verify(userPlantRepository, times(1)).findByUserAndPlantId(user, 1L);
    }

    @Test
    public void getUserPlantsByPlantName_ThrowsEntityNotFoundException_WhenPlantDoesNotExist(){
        User user = new User("user01", "user01@gmail.com", "123");

        when(userRepository.findByUsernameIgnoreCase("user01")).thenReturn(Optional.of(user));
        when(plantRepository.findByCommonName("Bell Pepper")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userPlantService.getAllUserPlantsByPlantName(user.getUsername(), "Bell Pepper");
        });
    }

    @Test
    public void getUserPlantById_Success(){
        User user = new User("user01", "user01@gmail.com", "123");

        Plant tomatoPlant = createPlant("Heirloom Tomato", "Solanum lycopersicum", 1L);

        UserPlant tomatoUserPlant = createNewUserPlant("Tomato", user, tomatoPlant, PlantContainer.POT, PlantLocation.INDOOR, 1L);

        when(userRepository.findByUsernameIgnoreCase("user01")).thenReturn(Optional.of(user));
        when(userPlantRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(tomatoUserPlant));

        UserPlantResponse userPlantResponse = userPlantService.getUserPlantById(1L, user.getUsername());

        assertNotNull(userPlantResponse);
        assertEquals(1L, userPlantResponse.getUserPlantId());

        verify(userPlantRepository, times(1)).findByIdAndUser(1L, user);
    }

    @Test
    public void getUserPlantById_ThrowsEntityNotFoundException_WhenUserPlantDoesNotExist(){
        User user = new User("user01", "user01@gmail.com", "123");

        when(userRepository.findByUsernameIgnoreCase("user01")).thenReturn(Optional.of(user));

        assertThrows(EntityNotFoundException.class, () -> {
            userPlantService.getUserPlantById(1L, user.getUsername());
        });
    }

    @Test
    public void createUserPlant_Success(){
        User user = new User("user01", "user01@gmail.com", "123");

        Plant tomatoPlant = createPlant("Heirloom Tomato", "Solanum lycopersicum", 1L);

        UserPlantCreateRequest createReq = getUserPlantCreateRequest(1L, "Tomato", PlantContainer.POT, PlantLocation.INDOOR);

        when(userRepository.findByUsernameIgnoreCase("user01")).thenReturn(Optional.of(user));
        when(plantRepository.findById(1L)).thenReturn(Optional.of(tomatoPlant));
        when(userPlantRepository.save(any(UserPlant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserPlantResponse userPlantResponse = userPlantService.createUserPlant(user.getUsername(), createReq);

        assertNotNull(userPlantResponse);
        assertEquals("Tomato", userPlantResponse.getNickname());

        verify(userPlantRepository, times(1)).save(any(UserPlant.class));
    }

    @Test
    public void createUserPlant_ThrowsEntityNotFoundException_WhenUserNotFound(){
        UserPlantCreateRequest createReq = getUserPlantCreateRequest(1L, "Tomato", PlantContainer.POT, PlantLocation.INDOOR);

        when(userRepository.findByUsernameIgnoreCase("user01")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userPlantService.createUserPlant("user01", createReq);
        });
    }

    @Test
    public void createUserPlant_ThrowsEntityNotFoundException_WhenPlantNotFound(){
        User user = new User("user01", "user01@gmail.com", "123");

        UserPlantCreateRequest createReq = getUserPlantCreateRequest(1L, "Tomato", PlantContainer.POT, PlantLocation.INDOOR);

        when(userRepository.findByUsernameIgnoreCase("user01")).thenReturn(Optional.of(user));
        when(plantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userPlantService.createUserPlant(user.getUsername(), createReq);
        });
    }

    @Test
    public void createUserPlant_ThrowsIllegalArgumentException_WhenNicknameIsBlank(){
        User user = new User("user01", "user01@gmail.com", "123");

        Plant tomatoPlant = createPlant("Heirloom Tomato", "Solanum lycopersicum", 1L);

        UserPlantCreateRequest createReq = getUserPlantCreateRequest(1L, "", PlantContainer.POT, PlantLocation.INDOOR);

        when(userRepository.findByUsernameIgnoreCase("user01")).thenReturn(Optional.of(user));
        when(plantRepository.findById(1L)).thenReturn(Optional.of(tomatoPlant));

        assertThrows(IllegalArgumentException.class, () -> {
            userPlantService.createUserPlant(user.getUsername(), createReq);
        });

    }

    @Test
    public void createUserPlant_ThrowsConflictException_WhenNicknameIsDuplicate(){
        User user = new User("user01", "user01@gmail.com", "123");

        Plant tomatoPlant = createPlant("Heirloom Tomato", "Solanum lycopersicum", 1L);

        UserPlantCreateRequest createReq = getUserPlantCreateRequest(1L, "Tomato", PlantContainer.POT, PlantLocation.OUTDOOR);

        UserPlant existingUserPlant = createNewUserPlant("Tomato", user, tomatoPlant, PlantContainer.POT, PlantLocation.INDOOR, 2L);

        when(userRepository.findByUsernameIgnoreCase("user01")).thenReturn(Optional.of(user));
        when(userPlantRepository.existsByUserAndNicknameIgnoreCase(user,"Tomato")).thenReturn(true);
        when(plantRepository.findById(1L)).thenReturn(Optional.of(tomatoPlant));

        assertThrows(ConflictException.class, () -> {
            userPlantService.createUserPlant(user.getUsername(), createReq);
        });
    }

    @Test
    public void createUserPlant_ThrowsIllegalArgumentException_WhenPlantContainerRulesViolated(){
        User user = new User("user01", "user01@gmail.com", "123");

        Plant tomatoPlant = createPlant("Heirloom Tomato", "Solanum lycopersicum", 1L);

        UserPlantCreateRequest createReq = getUserPlantCreateRequest(1L, "Tomato", PlantContainer.OUTDOOR_GROUND, PlantLocation.INDOOR);
        createReq.setContainerSize(ContainerSize.MEDIUM);
        createReq.setHasDrainage(true);

        when(userRepository.findByUsernameIgnoreCase(user.getUsername())).thenReturn(Optional.of(user));
        when(plantRepository.findById(1L)).thenReturn(Optional.of(tomatoPlant));

        assertThrows(IllegalArgumentException.class, () -> {
            userPlantService.createUserPlant(user.getUsername(), createReq);
        });
    }

//    @Test
//    public void updateUserPlantById_Success(){
//        User user = new User("user01", "user01@gmail.com", "123");
//
//        Plant tomatoPlant = createPlant("Heirloom Tomato", "Solanum lycopersicum", 1L);
//
//        UserPlant existingUserPlant = createNewUserPlant("Tomato", user, tomatoPlant, PlantContainer.POT, PlantLocation.INDOOR, 1L);
//
//        when(userRepository.findByUsernameIgnoreCase(user.getUsername())).thenReturn(Optional.of(user));
//        when(plantRepository.findById(1L)).thenReturn(Optional.of(tomatoPlant));
//        when(userPlantRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(existingUserPlant));
//        when(userPlantRepository.existsByUserAndNicknameIgnoreCase(user, "Tomato1")).thenReturn(false);
//
//        UserPlantUpdateRequest updateReq = getUserPlantUpdateRequest(Optional.of("Tomato1"),
//                Optional.empty(),
//                Optional.empty(),
//                Optional.empty(),
//                Optional.empty(),
//                Optional.empty());
//        UserPlantResponse userPlantResponse = userPlantService.updateUserPlantById(user.getUsername(), 1L, updateReq);
//
//        assertNotNull(userPlantResponse);
//        assertEquals("Tomato1", userPlantResponse.getNickname());
//
//        verify(userPlantRepository, times(1)).findById(1L);
//    }


    private Plant createPlant(String plantName, String scientificName, Long id){
        Plant plant = new Plant(plantName, scientificName);
        ReflectionTestUtils.setField(plant, "id", id);

       return plant;
    }

    private UserPlant createNewUserPlant(String nickname, User user, Plant plant, PlantContainer plantContainer,
                                         PlantLocation plantLocation, Long id){
        UserPlant userPlant = new UserPlant(nickname, user, plant, plantContainer, plantLocation);
        ReflectionTestUtils.setField(userPlant, "id", id);

        return userPlant;
    }

    private UserPlantCreateRequest getUserPlantCreateRequest(Long id, String nickname, PlantContainer plantContainer,
                                                             PlantLocation plantLocation){
        UserPlantCreateRequest createReq = new UserPlantCreateRequest();
        createReq.setPlantId(id);
        createReq.setNickname(nickname);
        createReq.setPlantContainer(plantContainer);
        createReq.setPlantLocation(plantLocation);

        return createReq;
    }

    private UserPlantUpdateRequest getUserPlantUpdateRequest(Optional<String> updatedNickname,
                                                             Optional<String> updatedPlantName,
                                                             Optional<PlantContainer> updatedPlantContainer,
                                                             Optional<PlantLocation> updatedPlantLocation,
                                                             Optional<ContainerSize> updatedContainerSize,
                                                             Optional<Boolean> updatedHasDrainage){
        String nickname = updatedNickname.isPresent() ? updatedNickname.get() : null;
        String plantName = updatedPlantName.isPresent() ? updatedPlantName.get() : null;
        PlantContainer plantContainer = updatedPlantContainer.isPresent() ? updatedPlantContainer.get() : null;
        PlantLocation plantLocation = updatedPlantLocation.isPresent() ? updatedPlantLocation.get() : null;
        ContainerSize containerSize = updatedContainerSize.isPresent() ? updatedContainerSize.get() : null;
        Boolean hasDrainage = updatedHasDrainage.isPresent() ? updatedHasDrainage.get() : null;

        UserPlantUpdateRequest updateReq = new UserPlantUpdateRequest();
        updateReq.setNickname(nickname);
        updateReq.setPlantName(plantName);
        updateReq.setPlantContainer(plantContainer);
        updateReq.setPlantLocation(plantLocation);
        updateReq.setContainerSize(containerSize);
        updateReq.setHasDrainage(hasDrainage);

        return updateReq;



    }
}
