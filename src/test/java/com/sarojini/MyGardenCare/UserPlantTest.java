package com.sarojini.MyGardenCare;

import com.sarojini.MyGardenCare.dtos.UserPlantCreateRequestDto;
import com.sarojini.MyGardenCare.dtos.UserPlantResponseDto;
import com.sarojini.MyGardenCare.dtos.UserPlantUpdateRequestDto;
import com.sarojini.MyGardenCare.entities.Plant;
import com.sarojini.MyGardenCare.entities.User;
import com.sarojini.MyGardenCare.entities.UserPlant;
import com.sarojini.MyGardenCare.enums.ContainerSize;
import com.sarojini.MyGardenCare.enums.PlantContainer;
import com.sarojini.MyGardenCare.enums.PlantLocation;
import com.sarojini.MyGardenCare.exceptions.ConflictException;
import com.sarojini.MyGardenCare.repositories.PlantRepository;
import com.sarojini.MyGardenCare.repositories.UserPlantRepository;
import com.sarojini.MyGardenCare.services.UserPlantService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
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
    private PlantRepository plantRepository;

    @InjectMocks
    private UserPlantService userPlantService;

    private User user;

    @BeforeEach
    void setUp(){
        user = new User("user01", "user01@gmail.com", "123");
    }

    @Test
    public void getAllUserPlants_Success(){
        Plant tomatoPlant = createPlant("Heirloom Tomato", "Solanum lycopersicum", 1L);
        Plant peaceLilyPlant = createPlant("Peace Lily", "Spathiphyllum", 2L);

        UserPlant tomatoUserPlant = createNewUserPlant("Tomato", user, tomatoPlant, PlantContainer.POT, PlantLocation.INDOOR, 1L);
        UserPlant peaceLilyUserPlant = createNewUserPlant("Lily", user, peaceLilyPlant, PlantContainer.WINDOW_BOX, PlantLocation.OUTDOOR, 2L);

        List<UserPlant> mockUserPlantList = List.of(tomatoUserPlant, peaceLilyUserPlant);

        when(userPlantRepository.findByUser(user)).thenReturn(mockUserPlantList);

        List<UserPlantResponseDto> userPlantResponseDtoList = userPlantService.getAllUserPlants(user);

        assertNotNull(userPlantResponseDtoList);
        assertEquals(2, userPlantResponseDtoList.size());
        assertEquals("Tomato", userPlantResponseDtoList.get(0).getNickname());
        assertEquals("Lily", userPlantResponseDtoList.get(1).getNickname());

        verify(userPlantRepository, times(1)).findByUser(user);
    }

    @Test
    public void getAllUserPlantsByPlantName_Success(){
        Plant tomatoPlant = createPlant("Heirloom Tomato", "Solanum lycopersicum", 1L);

        UserPlant tomatoUserPlant1 = createNewUserPlant("Tomato1", user, tomatoPlant, PlantContainer.POT, PlantLocation.INDOOR, 1L);
        UserPlant tomatoUserPlant2 = createNewUserPlant("Tomato2", user, tomatoPlant, PlantContainer.POT, PlantLocation.INDOOR, 2L);

        List<UserPlant> mockUserPlantList = List.of(tomatoUserPlant1, tomatoUserPlant2);

        when(plantRepository.findByCommonName("Heirloom Tomato")).thenReturn(Optional.of(tomatoPlant));
        when(userPlantRepository.findByUserAndPlantId(user,1L)).thenReturn(mockUserPlantList);

        List<UserPlantResponseDto> userPlantResponseDtoList = userPlantService.getAllUserPlantsByPlantName(user, "Heirloom Tomato");

        assertNotNull(userPlantResponseDtoList);
        assertEquals(2, userPlantResponseDtoList.size());
        assertEquals("Tomato1", userPlantResponseDtoList.get(0).getNickname());
        assertEquals("Tomato2", userPlantResponseDtoList.get(1).getNickname());

        verify(userPlantRepository, times(1)).findByUserAndPlantId(user, 1L);
    }

    @Test
    public void getUserPlantsByPlantName_ThrowsEntityNotFoundException_WhenPlantDoesNotExist(){
        when(plantRepository.findByCommonName("Bell Pepper")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userPlantService.getAllUserPlantsByPlantName(user, "Bell Pepper");
        });
    }

    @Test
    public void getUserPlantById_Success(){
        Plant tomatoPlant = createPlant("Heirloom Tomato", "Solanum lycopersicum", 1L);

        UserPlant tomatoUserPlant = createNewUserPlant("Tomato", user, tomatoPlant, PlantContainer.POT, PlantLocation.INDOOR, 1L);

        when(userPlantRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(tomatoUserPlant));

        UserPlantResponseDto userPlantResponseDto = userPlantService.getUserPlantById(1L, user);

        assertNotNull(userPlantResponseDto);
        assertEquals(1L, userPlantResponseDto.getUserPlantId());

        verify(userPlantRepository, times(1)).findByIdAndUser(1L, user);
    }

    @Test
    public void getUserPlantById_ThrowsEntityNotFoundException_WhenUserPlantDoesNotExist(){
        when(userPlantRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userPlantService.getUserPlantById(1L, user);
        });
    }

    @Test
    public void createUserPlant_Success(){
        Plant tomatoPlant = createPlant("Heirloom Tomato", "Solanum lycopersicum", 1L);

        UserPlantCreateRequestDto createReq = getUserPlantCreateRequest(1L, "Tomato", PlantContainer.POT, PlantLocation.INDOOR);

        when(plantRepository.findById(1L)).thenReturn(Optional.of(tomatoPlant));
        when(userPlantRepository.save(any(UserPlant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserPlantResponseDto userPlantResponseDto = userPlantService.createUserPlant(user, createReq);

        assertNotNull(userPlantResponseDto);
        assertEquals("Tomato", userPlantResponseDto.getNickname());

        verify(userPlantRepository, times(1)).save(any(UserPlant.class));
    }

    @Test
    public void createUserPlant_ThrowsEntityNotFoundException_WhenPlantNotFound(){
        UserPlantCreateRequestDto createReq = getUserPlantCreateRequest(1L, "Tomato", PlantContainer.POT, PlantLocation.INDOOR);

        when(plantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userPlantService.createUserPlant(user, createReq);
        });
    }

    @Test
    public void createUserPlant_ThrowsIllegalArgumentException_WhenNicknameIsBlank(){
        Plant tomatoPlant = createPlant("Heirloom Tomato", "Solanum lycopersicum", 1L);

        UserPlantCreateRequestDto createReq = getUserPlantCreateRequest(1L, "", PlantContainer.POT, PlantLocation.INDOOR);

        when(plantRepository.findById(1L)).thenReturn(Optional.of(tomatoPlant));

        assertThrows(IllegalArgumentException.class, () -> {
            userPlantService.createUserPlant(user, createReq);
        });

    }

    @Test
    public void createUserPlant_ThrowsConflictException_WhenNicknameIsDuplicate(){
        Plant tomatoPlant = createPlant("Heirloom Tomato", "Solanum lycopersicum", 1L);

        UserPlantCreateRequestDto createReq = getUserPlantCreateRequest(1L, "Tomato", PlantContainer.POT, PlantLocation.OUTDOOR);

        when(userPlantRepository.existsByUserAndNicknameIgnoreCase(user,"Tomato")).thenReturn(true);
        when(plantRepository.findById(1L)).thenReturn(Optional.of(tomatoPlant));

        assertThrows(ConflictException.class, () -> {
            userPlantService.createUserPlant(user, createReq);
        });
    }

    @Test
    public void createUserPlant_ThrowsIllegalArgumentException_WhenPlantContainerRulesViolated(){
        Plant tomatoPlant = createPlant("Heirloom Tomato", "Solanum lycopersicum", 1L);

        UserPlantCreateRequestDto createReq = getUserPlantCreateRequest(1L, "Tomato", PlantContainer.OUTDOOR_GROUND, PlantLocation.INDOOR);
        createReq.setContainerSize(ContainerSize.MEDIUM);
        createReq.setHasDrainage(true);

        when(plantRepository.findById(1L)).thenReturn(Optional.of(tomatoPlant));

        assertThrows(IllegalArgumentException.class, () -> {
            userPlantService.createUserPlant(user, createReq);
        });
    }

    @Test
    public void updateUserPlantById_Success(){
        Plant tomatoPlant = createPlant("Heirloom Tomato", "Solanum lycopersicum", 1L);

        UserPlant existingUserPlant = createNewUserPlant("Tomato", user, tomatoPlant, PlantContainer.POT, PlantLocation.INDOOR, 1L);

        UserPlantUpdateRequestDto updateReq = getUserPlantUpdateRequest(Optional.of("Tomato1"),
                Optional.empty(),
                Optional.empty(),
                Optional.of(PlantLocation.OUTDOOR),
                Optional.of(ContainerSize.MEDIUM),
                Optional.of(true));

        when(userPlantRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(existingUserPlant));
        when(userPlantRepository.existsByUserAndNicknameIgnoreCase(user, "Tomato1")).thenReturn(false);

        UserPlantResponseDto userPlantResponseDto = userPlantService.updateUserPlantById(user, 1L, updateReq);

        assertNotNull(userPlantResponseDto);
        assertEquals("Tomato1", userPlantResponseDto.getNickname());

        verify(userPlantRepository, times(1)).findByIdAndUser(1L, user);
        verify(userPlantRepository, times(1)).existsByUserAndNicknameIgnoreCase(user, "Tomato1");
    }

    @Test
    public void updateUserPlantById_SetCertainFieldsToNull_WhenPlantContainerIsOutdoorGround(){
        Plant tomatoPlant = createPlant("Heirloom Tomato", "Solanum lycopersicum", 1L);

        UserPlant existingUserPlant = createNewUserPlant("Tomato", user, tomatoPlant, PlantContainer.POT, PlantLocation.INDOOR, 1L);
        existingUserPlant.setContainerSize(ContainerSize.MEDIUM);
        existingUserPlant.setHasDrainage(true);

        UserPlantUpdateRequestDto updateReq = getUserPlantUpdateRequest(Optional.empty(),
                Optional.empty(),
                Optional.of(PlantContainer.OUTDOOR_GROUND),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

        when(userPlantRepository.findByIdAndUser(1l, user)).thenReturn(Optional.of(existingUserPlant));

        UserPlantResponseDto userPlantResponseDto = userPlantService.updateUserPlantById(user, 1L, updateReq);

        assertNull(userPlantResponseDto.getContainerSize());
        assertNull(userPlantResponseDto.getHasDrainage());
        assertEquals(PlantLocation.OUTDOOR, userPlantResponseDto.getPlantLocation());

        verify(userPlantRepository, times(1)).findByIdAndUser(1l, user);

    }

    @Test
    public void updateUserPlantById_ThrowsIllegalArgumentException_WhenPlantContainerRulesViolated(){
        Plant tomatoPlant = createPlant("Heirloom Tomato", "Solanum lycopersicum", 1L);

        UserPlant existingUserPlant = createNewUserPlant("Tomato", user, tomatoPlant, PlantContainer.POT, PlantLocation.INDOOR, 1L);

        UserPlantUpdateRequestDto updateReq = getUserPlantUpdateRequest(Optional.empty(),
                Optional.empty(),
                Optional.of(PlantContainer.OUTDOOR_GROUND),
                Optional.of(PlantLocation.INDOOR),
                Optional.of(ContainerSize.SMALL),
                Optional.empty());

        when(userPlantRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(existingUserPlant));

        assertThrows(IllegalArgumentException.class, () -> {
            userPlantService.updateUserPlantById(user, 1L, updateReq);
        });

        verify(userPlantRepository, times(1)).findByIdAndUser(1L, user);
        verify(userPlantRepository, never()).save(any(UserPlant.class));
    }

    @Test
    public void deleteUserPlantById_Success(){
        userPlantService.deleteUserPlantById(user, 1L);

        verify(userPlantRepository, times(1)).deleteByUserAndId(user, 1L);
    }


    @Test
    public void deleteAllUserPlants_Success(){
        userPlantService.deleteAllUserPlants(user);

        verify(userPlantRepository, times(1)).deleteByUser(user);
    }


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

    private UserPlantCreateRequestDto getUserPlantCreateRequest(Long id, String nickname, PlantContainer plantContainer,
                                                                PlantLocation plantLocation){
        UserPlantCreateRequestDto createReq = new UserPlantCreateRequestDto();
        createReq.setPlantId(id);
        createReq.setNickname(nickname);
        createReq.setPlantContainer(plantContainer);
        createReq.setPlantLocation(plantLocation);

        return createReq;
    }

    private UserPlantUpdateRequestDto getUserPlantUpdateRequest(Optional<String> updatedNickname,
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

        UserPlantUpdateRequestDto updateReq = new UserPlantUpdateRequestDto();
        updateReq.setNickname(nickname);
        updateReq.setPlantName(plantName);
        updateReq.setPlantContainer(plantContainer);
        updateReq.setPlantLocation(plantLocation);
        updateReq.setContainerSize(containerSize);
        updateReq.setHasDrainage(hasDrainage);

        return updateReq;
    }
}