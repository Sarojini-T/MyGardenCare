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
import com.sarojini.MyGardenCare.services.PlantRecommendationService;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserPlantTest {
    @Mock
    private UserPlantRepository userPlantRepository;

    @Mock
    private PlantRepository plantRepository;

    @Mock
    private PlantRecommendationService plantRecommendationService;

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

        UserPlantResponseDto tomatoUserPlantResp = new UserPlantResponseDto();
        tomatoUserPlantResp.setNickname("Tomato");
        tomatoUserPlantResp.setPlantId(1L);
        List<String> tomatoPlantRecs = List.of("Place near a south or west facing window.", "If possible add drainage holes to your container");
        tomatoUserPlantResp.setPlantCareRecommendations(tomatoPlantRecs);

        UserPlantResponseDto peaceLilyUserPlantResp = new UserPlantResponseDto();
        peaceLilyUserPlantResp.setNickname("Lily");
        peaceLilyUserPlantResp.setPlantId(2L);
        List<String> peaceLilyPlantRecs = List.of("Place in a shaded area");
        peaceLilyUserPlantResp.setPlantCareRecommendations(peaceLilyPlantRecs);

        when(userPlantRepository.findByUser(user)).thenReturn(mockUserPlantList);
        when(plantRecommendationService.addRecommendationsToPlant(tomatoUserPlant)).thenReturn(tomatoPlantRecs);
        when(plantRecommendationService.addRecommendationsToPlant(peaceLilyUserPlant)).thenReturn(peaceLilyPlantRecs);

        List<UserPlantResponseDto> userPlantResponseDtoList = userPlantService.getAllUserPlants(user);

        assertNotNull(userPlantResponseDtoList);
        assertEquals(2, userPlantResponseDtoList.size());
        assertEquals("Tomato", userPlantResponseDtoList.get(0).getNickname());
        assertEquals("Lily", userPlantResponseDtoList.get(1).getNickname());
        assertNotNull(userPlantResponseDtoList.get(0).getPlantCareRecommendations());
        assertNotNull(userPlantResponseDtoList.get(1).getPlantCareRecommendations());

        verify(userPlantRepository, times(1)).findByUser(user);
        verify(plantRecommendationService, times(1)).addRecommendationsToPlant(tomatoUserPlant);
        verify(plantRecommendationService, times(1)).addRecommendationsToPlant(peaceLilyUserPlant);
    }

    @Test
    public void getAllUserPlantsByPlantName_Success(){
        Plant tomatoPlant = createPlant("Heirloom Tomato", "Solanum lycopersicum", 1L);

        UserPlant tomatoUserPlant1 = createNewUserPlant("Tomato1", user, tomatoPlant, PlantContainer.POT, PlantLocation.INDOOR, 1L);
        UserPlant tomatoUserPlant2 = createNewUserPlant("Tomato2", user, tomatoPlant, PlantContainer.POT, PlantLocation.INDOOR, 2L);

        List<UserPlant> mockUserPlantList = List.of(tomatoUserPlant1, tomatoUserPlant2);

        List<String> tomatoPlantRecs = List.of("Place near a south or west facing window.", "If possible add drainage holes to your container");

        UserPlantResponseDto tomatoUserPlant1Resp = new UserPlantResponseDto();
        tomatoUserPlant1Resp.setNickname("Tomato");
        tomatoUserPlant1Resp.setPlantId(1L);
        tomatoUserPlant1Resp.setPlantCareRecommendations(tomatoPlantRecs);

        UserPlantResponseDto tomatoUserPlant2Resp = new UserPlantResponseDto();
        tomatoUserPlant2Resp.setNickname("Tomato");
        tomatoUserPlant2Resp.setPlantId(1L);
        tomatoUserPlant2Resp.setPlantCareRecommendations(tomatoPlantRecs);

        when(plantRepository.findByCommonName("Heirloom Tomato")).thenReturn(Optional.of(tomatoPlant));
        when(userPlantRepository.findByUserAndPlantId(user,1L)).thenReturn(mockUserPlantList);
        when(plantRecommendationService.addRecommendationsToPlant(tomatoUserPlant1)).thenReturn(tomatoPlantRecs);
        when(plantRecommendationService.addRecommendationsToPlant(tomatoUserPlant2)).thenReturn(tomatoPlantRecs);


        List<UserPlantResponseDto> userPlantResponseDtoList = userPlantService.getAllUserPlantsByPlantName(user, "Heirloom Tomato");

        assertNotNull(userPlantResponseDtoList);
        assertEquals(2, userPlantResponseDtoList.size());
        assertEquals("Tomato1", userPlantResponseDtoList.get(0).getNickname());
        assertEquals("Tomato2", userPlantResponseDtoList.get(1).getNickname());
        assertNotNull(userPlantResponseDtoList.get(0).getPlantCareRecommendations());
        assertNotNull(userPlantResponseDtoList.get(1).getPlantCareRecommendations());

        verify(userPlantRepository, times(1)).findByUserAndPlantId(user, 1L);
        verify(plantRecommendationService, times(1)).addRecommendationsToPlant(tomatoUserPlant1);
        verify(plantRecommendationService, times(1)).addRecommendationsToPlant(tomatoUserPlant2);

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

        List<String> tomatoPlantRecs = List.of("Place near a south or west facing window.", "If possible add drainage holes to your container");

        UserPlantResponseDto tomatoPlantResp = new UserPlantResponseDto();
        tomatoPlantResp.setNickname("Tomato");
        tomatoPlantResp.setPlantCareRecommendations(tomatoPlantRecs);

        when(userPlantRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(tomatoUserPlant));
        when(plantRecommendationService.addRecommendationsToPlant(tomatoUserPlant)).thenReturn(tomatoPlantRecs);

        UserPlantResponseDto userPlantResponseDto = userPlantService.getUserPlantById(1L, user);

        assertNotNull(userPlantResponseDto);
        assertEquals(1L, userPlantResponseDto.getUserPlantId());
        assertNotNull(userPlantResponseDto.getPlantCareRecommendations());

        verify(userPlantRepository, times(1)).findByIdAndUser(1L, user);
        verify(plantRecommendationService, times(1)).addRecommendationsToPlant(tomatoUserPlant);
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

        List<String> tomatoPlantRecs = List.of("Place near a south or west facing window.", "If possible add drainage holes to your container");

        when(plantRepository.findById(1L)).thenReturn(Optional.of(tomatoPlant));
        when(userPlantRepository.save(any(UserPlant.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(plantRecommendationService.addRecommendationsToPlant(any(UserPlant.class))).thenReturn(tomatoPlantRecs);

        UserPlantResponseDto userPlantResponseDto = userPlantService.createUserPlant(user, createReq);

        assertNotNull(userPlantResponseDto);
        assertEquals("Tomato", userPlantResponseDto.getNickname());
        assertEquals(2, userPlantResponseDto.getPlantCareRecommendations().size());

        verify(userPlantRepository, times(1)).save(any(UserPlant.class));
        verify(plantRecommendationService, times(1)).addRecommendationsToPlant(any(UserPlant.class));
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

        List<String> updatedRecs = List.of("Move to a sunny spot outdoors");

        when(userPlantRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(existingUserPlant));
        when(userPlantRepository.existsByUserAndNicknameIgnoreCase(user, "Tomato1")).thenReturn(false);
        when(plantRecommendationService.addRecommendationsToPlant(existingUserPlant)).thenReturn(updatedRecs);

        UserPlantResponseDto userPlantResponseDto = userPlantService.updateUserPlantById(user, 1L, updateReq);

        assertNotNull(userPlantResponseDto);
        assertEquals("Tomato1", userPlantResponseDto.getNickname());
        assertNotNull(userPlantResponseDto.getPlantCareRecommendations());

        verify(userPlantRepository, times(1)).findByIdAndUser(1L, user);
        verify(userPlantRepository, times(1)).existsByUserAndNicknameIgnoreCase(user, "Tomato1");
        verify(plantRecommendationService, times(1)).addRecommendationsToPlant(existingUserPlant);
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
        List<String> outdoorRecs = List.of("Ensure soil has good drainage");

        when(userPlantRepository.findByIdAndUser(1l, user)).thenReturn(Optional.of(existingUserPlant));
        when(plantRecommendationService.addRecommendationsToPlant(existingUserPlant)).thenReturn(outdoorRecs);

        UserPlantResponseDto userPlantResponseDto = userPlantService.updateUserPlantById(user, 1L, updateReq);

        assertNull(userPlantResponseDto.getContainerSize());
        assertNull(userPlantResponseDto.getHasDrainage());
        assertEquals(PlantLocation.OUTDOOR, userPlantResponseDto.getPlantLocation());
        assertNotNull(userPlantResponseDto.getPlantCareRecommendations());

        verify(userPlantRepository, times(1)).findByIdAndUser(1l, user);
        verify(plantRecommendationService, times(1)).addRecommendationsToPlant(existingUserPlant);
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