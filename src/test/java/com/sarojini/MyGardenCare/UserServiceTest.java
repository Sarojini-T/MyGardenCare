package com.sarojini.MyGardenCare;

import com.sarojini.MyGardenCare.dtos.UserCreateRequestDto;
import com.sarojini.MyGardenCare.dtos.UserResponseDto;
import com.sarojini.MyGardenCare.dtos.UserUpdateRequestDto;
import com.sarojini.MyGardenCare.entities.User;
import com.sarojini.MyGardenCare.exceptions.ConflictException;
import com.sarojini.MyGardenCare.repositories.UserRepository;
import com.sarojini.MyGardenCare.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    public void getAllUsers_Success(){
        User user1 = new User("user01", "user01@gmail.com", "abc");
        User user2 = new User("user02", "user02@gmail.com", "123");

        List allUsers = List.of(user1, user2);

        when(userRepository.findAll()).thenReturn(allUsers);

        List<UserResponseDto> allUsersResponse = userService.getAllUsers();

        assertEquals(2, allUsersResponse.size());
        assertEquals(user1.getUsername(), allUsersResponse.get(0).getUsername());
        assertEquals(user2.getUsername(), allUsersResponse.get(1).getUsername());
    }

    @Test
    public void getUserById_Success(){
        User existingUser = new User("user01", "user01@gmail.com", "123");
        ReflectionTestUtils.setField(existingUser, "id", 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        UserResponseDto userResponseDto = userService.getUserById(1L);

        assertNotNull(userResponseDto);
        assertEquals(1L, userResponseDto.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void getUserById_ThrowsEntityNotFoundException_WhenUserDoesNotExist(){
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.getUserById(100L);
        });
    }

    @Test
    public void getUserByUsername_Success(){
        User existingUser = new User("user01", "user01@gmail.com", "123");

        when(userRepository.findByUsernameIgnoreCase("user01")).thenReturn(Optional.of(existingUser));

        UserResponseDto userResponseDto = userService.getUserByUsername("user01");

        assertNotNull(userResponseDto);
        assertEquals("user01", userResponseDto.getUsername());
        verify(userRepository, times(1)).findByUsernameIgnoreCase("user01");
    }

    @Test
    public void getUserByUsername_ThrowsEntityNotFoundException_WhenUserDoesNotExist(){
        when(userRepository.findByUsernameIgnoreCase("user01")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.getUserByUsername("user01");
        });
    }

    @Test
    public void createNewUser_Success(){

        UserCreateRequestDto createReq = userCreateRequestHelper("user01",
                "user01@gmail.com",
                "123",
                "12345");

        when(passwordEncoder.encode("123")).thenReturn("encoded123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDto userResponseDto = userService.createNewUser(createReq);

        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertNotNull(userResponseDto);
        assertEquals("user01", userResponseDto.getUsername());
        assertEquals("user01@gmail.com", userResponseDto.getEmail());
        assertEquals("12345", userResponseDto.getZipcode());
        assertEquals("encoded123", capturedUser.getPassword());

        verify(passwordEncoder, times(1)).encode("123");
    }

    @Test
    public void createNewUser_NormalizeEmailOrUsername_Success(){
        UserCreateRequestDto createReq = userCreateRequestHelper("user01  ",
                "  user01@gmail.com  ",
                "123",
                "12345");

        when(passwordEncoder.encode("123")).thenReturn("encoded123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDto userResponseDto = userService.createNewUser(createReq);

        assertNotNull(userResponseDto);
        assertEquals("user01", userResponseDto.getUsername());
        assertEquals("user01@gmail.com", userResponseDto.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("123");
    }

    @Test
    public void createNewUser_ThrowsConflictException_WhenUsernameOrEmailIsDuplicate(){
        UserCreateRequestDto createReq = userCreateRequestHelper("user01",
                "user01@gmail.com",
                "abc",
                "12345");

        User existingUser = new User("user01", "user01@gmail.com", "123");
        ReflectionTestUtils.setField(existingUser, "id", 1L);

        when(userRepository.findByUsernameIgnoreCase("user01"))
                .thenReturn(Optional.of(existingUser));


        assertThrows(ConflictException.class, () -> {
            userService.createNewUser(createReq);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void updateMyProfile_Success(){
        User existingUser = new User("user01", "user01@gmail.com", "123");
        existingUser.updateZipCode("12345");

        when(userRepository.findByUsernameIgnoreCase(existingUser.getUsername())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("abc")).thenReturn("encodedAbc");

        UserUpdateRequestDto updateReq = userUpdateRequestHelper(Optional.of("user02"),
                Optional.of(" "),
                Optional.of("abc"),
                Optional.of("02156"));

        UserResponseDto updatedUser = userService.updateMyProfile(existingUser.getUsername(), updateReq);

        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertEquals("user02", updatedUser.getUsername());
        assertEquals("user01@gmail.com", updatedUser.getEmail());
        assertEquals("encodedAbc", capturedUser.getPassword());
        assertEquals("02156",updatedUser.getZipcode());

        verify(passwordEncoder, times(1)).encode("abc");
        verify(userRepository, times(1)).findByUsernameIgnoreCase(existingUser.getUsername());
    }

    @Test
    public void updateMyProfile_SetZipcodeToNull_WhenUpdatedZipcodeIsBlank(){
        User existingUser = new User("user01", "user01@gmail.com", "123");
        existingUser.updateZipCode("12345");

        when(userRepository.findByUsernameIgnoreCase(existingUser.getUsername())).thenReturn(Optional.of(existingUser));

        UserUpdateRequestDto updateReq = userUpdateRequestHelper(Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(""));

        UserResponseDto updatedUser = userService.updateMyProfile( existingUser.getUsername(), updateReq);

        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertNull(updatedUser.getZipcode());

        verify(userRepository, times(1)).findByUsernameIgnoreCase(existingUser.getUsername());
    }

    @Test
    public void updateMyProfile_NoChange_WhenUsernameAndEmailIsSameAsCurrent(){
        User existingUser = new User("user01", "user01@gmail.com", "123");
        ReflectionTestUtils.setField(existingUser, "id", 1L);

        when(userRepository.findByUsernameIgnoreCase(existingUser.getUsername())).thenReturn(Optional.of(existingUser));

        UserUpdateRequestDto updateReq = userUpdateRequestHelper(Optional.of("user01"),
                Optional.of("user01@gmail.com"),
                Optional.empty(),
                Optional.empty());

        UserResponseDto userResponseDto = userService.updateMyProfile(existingUser.getUsername(), updateReq);

        assertEquals("user01", userResponseDto.getUsername());
        assertEquals("user01@gmail.com", userResponseDto.getEmail());

        verify(userRepository, times(2)).findByUsernameIgnoreCase(existingUser.getUsername());
    }


    @Test
    public void updateMyProfile_ThrowEntityNotFoundException_WhenUserDoesNotExist(){
        UserUpdateRequestDto updateReq = userUpdateRequestHelper(Optional.of("user02"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.updateMyProfile(updateReq.getUsername(), updateReq);
        });
    }

    @Test
    public void updateMyProfile_ThrowConflictException_WhenUsernameIsDuplicate(){
        User userToUpdate = new User("user01", "user01@gmail.com", "123");
        ReflectionTestUtils.setField(userToUpdate, "id", 1L);

        User otherExistingUser = new User("user02", "user02@gmail.com", "123");
        ReflectionTestUtils.setField(otherExistingUser , "id", 2L);

        when(userRepository.findByUsernameIgnoreCase(userToUpdate.getUsername())).thenReturn(Optional.of(userToUpdate));
        when(userRepository.findByUsernameIgnoreCase(otherExistingUser.getUsername())).thenReturn(Optional.of(otherExistingUser));

        UserUpdateRequestDto updateReq = userUpdateRequestHelper(Optional.of("user02"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

        assertThrows(ConflictException.class, () -> {
            userService.updateMyProfile(userToUpdate.getUsername(), updateReq);
        });
    }

    @Test
    public void updateMyProfileThrowsConflictException_WhenEmailIsDuplicate(){
        User userToUpdate = new User("user01", "user01@gmail.com", "123");
        ReflectionTestUtils.setField(userToUpdate, "id", 1L);

        User otherExistingUser = new User("user02", "user02@gmail.com", "123");
        ReflectionTestUtils.setField(otherExistingUser , "id", 2L);

        when(userRepository.findByUsernameIgnoreCase(userToUpdate.getUsername())).thenReturn(Optional.of(userToUpdate));
        when(userRepository.findByEmailIgnoreCase(otherExistingUser.getEmail())).thenReturn(Optional.of(otherExistingUser));

        UserUpdateRequestDto updateReq = userUpdateRequestHelper(Optional.empty(),
                Optional.of("user02@gmail.com"),
                Optional.empty(),
                Optional.empty());

        assertThrows(ConflictException.class, () -> {
            userService.updateMyProfile(userToUpdate.getUsername(), updateReq);
        });
    }

    @Test
    public void deleteById_Success(){
        User user = new User("user01", "user01@gmail.com", "123");
        user.updateZipCode("12345");
        ReflectionTestUtils.setField(user, "id", 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteById(1L);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void deleteById_ThrowsEntityNotFoundException_WhenUserDoesNotExist(){
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.deleteById(1L);
        });
    }

    @Test
    public void deleteByUsername_Success(){
        User user = new User("user01", "user01@gmail.com", "123");
        user.updateZipCode("12345");

        when(userRepository.findByUsernameIgnoreCase(user.getUsername())).thenReturn(Optional.of(user));

        userService.deleteByUsername(user.getUsername());

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void deleteByUsername_ThrowsEntityNotFoundException_WhenUserDoesNotExist(){
        when(userRepository.findByUsernameIgnoreCase("user01")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.deleteByUsername("user01");
        });
    }


    private UserCreateRequestDto userCreateRequestHelper(String username,
                                                         String email,
                                                         String password,
                                                         String zipcode){
        UserCreateRequestDto createReq = new UserCreateRequestDto();
        createReq.setUsername(username);
        createReq.setEmail(email);
        createReq.setPassword(password);
        createReq.setZipcode(zipcode);

        return createReq;
    }

    private UserUpdateRequestDto userUpdateRequestHelper(Optional<String> username,
                                                         Optional<String> email,
                                                         Optional<String> password,
                                                         Optional<String> zipcode){
        UserUpdateRequestDto updateReq = new UserUpdateRequestDto();
        if(username.isPresent()) updateReq.setUsername(username.get());
        if(email.isPresent()) updateReq.setEmail(email.get());
        if(password.isPresent()) updateReq.setPassword(password.get());
        if(zipcode.isPresent()) updateReq.setZipcode(zipcode.get());

        return updateReq;
    }
}
