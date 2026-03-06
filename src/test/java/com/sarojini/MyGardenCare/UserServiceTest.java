package com.sarojini.MyGardenCare;

import com.sarojini.MyGardenCare.dtos.UserCreateRequest;
import com.sarojini.MyGardenCare.dtos.UserResponse;
import com.sarojini.MyGardenCare.dtos.UserUpdateRequest;
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
    public void getUserById_Success(){
        User existingUser = new User("user01", "user01@gmail.com", "123");
        ReflectionTestUtils.setField(existingUser, "id", 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        UserResponse userResponse = userService.getUserById(1L);

        assertNotNull(userResponse);
        assertEquals(1L, userResponse.getId());
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

        UserResponse userResponse = userService.getUserByUsername("user01");

        assertNotNull(userResponse);
        assertEquals("user01", userResponse.getUsername());
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

        UserCreateRequest createReq = userCreateRequestHelper("user01",
                "user01@gmail.com",
                "123",
                "12345");

        when(passwordEncoder.encode("123")).thenReturn("encoded123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse userResponse = userService.createNewUser(createReq);

        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertNotNull(userResponse);
        assertEquals("user01", userResponse.getUsername());
        assertEquals("user01@gmail.com", userResponse.getEmail());
        assertEquals("12345", userResponse.getZipcode());
        assertEquals("encoded123", capturedUser.getPassword());

        verify(passwordEncoder, times(1)).encode("123");
    }

    @Test
    public void createNewUser_NormalizeEmailOrUsername_Success(){
        UserCreateRequest createReq = userCreateRequestHelper("user01  ",
                "  user01@gmail.com  ",
                "123",
                "12345");

        when(passwordEncoder.encode("123")).thenReturn("encoded123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse userResponse = userService.createNewUser(createReq);

        assertNotNull(userResponse);
        assertEquals("user01", userResponse.getUsername());
        assertEquals("user01@gmail.com", userResponse.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("123");
    }

    @Test
    public void createNewUser_ThrowsConflictException_WhenUsernameOrEmailIsDuplicate(){
        UserCreateRequest createReq = userCreateRequestHelper("user01",
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

        when(passwordEncoder.encode("abc")).thenReturn("encodedAbc");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        UserUpdateRequest updateReq = userUpdateRequestHelper(Optional.of("user02"),
                Optional.of(" "),
                Optional.of("abc"),
                Optional.of("02156"));

        UserResponse updatedUser = userService.updateMyProfile(existingUser.getUsername(), updateReq);

        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertEquals("user02", updatedUser.getUsername());
        assertEquals("user01@gmail.com", updatedUser.getEmail());
        assertEquals("encodedAbc", capturedUser.getPassword());
        assertEquals("02156",updatedUser.getZipcode());

        verify(passwordEncoder, times(1)).encode("abc");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void updateMyProfile_SetZipcodeToNull_WhenUpdatedZipcodeIsBlank(){
        User existingUser = new User("user01", "user01@gmail.com", "123");
        existingUser.updateZipCode("12345");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        UserUpdateRequest updateReq = userUpdateRequestHelper(Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(""));

        UserResponse updatedUser = userService.updateMyProfile( existingUser.getUsername(), updateReq);

        assertNull(updatedUser.getZipcode());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void updateMyProfile_Success_WhenUsernameOrEmailIsSameAsCurrent(){
        User existingUser = new User("user01", "user01@gmail.com", "123");
        ReflectionTestUtils.setField(existingUser, "id", 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        UserUpdateRequest updateReq = userUpdateRequestHelper(Optional.of("user01"),
                Optional.of("user01@gmail.com"),
                Optional.empty(),
                Optional.empty());

        UserResponse userResponse = userService.updateMyProfile(existingUser.getUsername(), updateReq);

        assertEquals("user01", userResponse.getUsername());
        assertEquals("user01@gmail.com", userResponse.getEmail());

        verify(userRepository, times(1)).findById(1L);
    }


    @Test
    public void updateMyProfile_ThrowEntityNotFoundException_WhenUserDoesNotExist(){
        UserUpdateRequest updateReq = userUpdateRequestHelper(Optional.of("user02"),
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

        when(userRepository.findById(1L)).thenReturn(Optional.of(userToUpdate));
        when(userRepository.findByUsernameIgnoreCase("user02")).thenReturn(Optional.of(otherExistingUser));

        UserUpdateRequest updateReq = userUpdateRequestHelper(Optional.of("user02"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

        assertThrows(ConflictException.class, () -> {
            userService.updateMyProfile(userToUpdate.getUsername(), updateReq);
        });
    }

    @Test
    public void updateMyProfileThrowConflictException_WhenEmailIsDuplicate(){
        User userToUpdate = new User("user01", "user01@gmail.com", "123");
        ReflectionTestUtils.setField(userToUpdate, "id", 1L);

        User otherExistingUser = new User("user02", "user02@gmail.com", "123");
        ReflectionTestUtils.setField(otherExistingUser , "id", 2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userToUpdate));
        when(userRepository.findByEmailIgnoreCase("user02@gmail.com")).thenReturn(Optional.of(otherExistingUser));

        UserUpdateRequest updateReq = userUpdateRequestHelper(Optional.empty(),
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

    private UserCreateRequest userCreateRequestHelper(String username,
                                                      String email,
                                                      String password,
                                                      String zipcode){
        UserCreateRequest createReq = new UserCreateRequest();
        createReq.setUsername(username);
        createReq.setEmail(email);
        createReq.setPassword(password);
        createReq.setZipcode(zipcode);

        return createReq;
    }

    private UserUpdateRequest userUpdateRequestHelper(Optional<String> username,
                                                      Optional<String> email,
                                                      Optional<String> password,
                                                      Optional<String> zipcode){
        UserUpdateRequest updateReq = new UserUpdateRequest();
        if(username.isPresent()) updateReq.setUsername(username.get());
        if(email.isPresent()) updateReq.setEmail(email.get());
        if(password.isPresent()) updateReq.setPassword(password.get());
        if(zipcode.isPresent()) updateReq.setZipcode(zipcode.get());

        return updateReq;
    }
}
