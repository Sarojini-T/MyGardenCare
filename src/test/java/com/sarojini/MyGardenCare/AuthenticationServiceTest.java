package com.sarojini.MyGardenCare;

import com.sarojini.MyGardenCare.config.JwtService;
import com.sarojini.MyGardenCare.dtos.AuthenticationRequest;
import com.sarojini.MyGardenCare.dtos.AuthenticationResponse;
import com.sarojini.MyGardenCare.dtos.UserCreateRequest;

import com.sarojini.MyGardenCare.entities.User;
import com.sarojini.MyGardenCare.repositories.UserRepository;
import com.sarojini.MyGardenCare.services.AuthenticationService;
import com.sarojini.MyGardenCare.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User mockUser;

    @BeforeEach
    void setUp(){
        mockUser = new User("user1", "user1@gmail.com", "123");
    }

    @Test
    void register_ReturnsJwtToken_Success(){
        UserCreateRequest createReq = new UserCreateRequest();
        createReq.setUsername("user1");
        createReq.setEmail("user1@gmail.com");
        createReq.setPassword("123");

        String expectedJwt = "1.2.3";

        when(userRepository.findByUsernameIgnoreCase("user1")).thenReturn(Optional.of(mockUser));
        when(jwtService.generateToken(mockUser)).thenReturn(expectedJwt);

        AuthenticationResponse authResponse = authenticationService.register(createReq);

        assertNotNull(authResponse);
        assertEquals(expectedJwt, authResponse.getToken());

        verify(userService, times(1)).createNewUser(createReq);
        verify(userRepository, times(1)).findByUsernameIgnoreCase("user1");
        verify(jwtService, times(1)).generateToken(mockUser);
    }

    @Test
    void authenticate_ReturnJwtToken_WhenCredentialsAreValid(){
        AuthenticationRequest authReq = new AuthenticationRequest("user1", "123");
        String expectedJwt = "1.2.3";

        when(userRepository.findByUsernameIgnoreCase(mockUser.getUsername())).thenReturn(Optional.of(mockUser));
        when(jwtService.generateToken(mockUser)).thenReturn(expectedJwt);

        AuthenticationResponse authResp = authenticationService.authenticate(authReq);

        assertNotNull(authResp);
        assertEquals(expectedJwt, authResp.getToken());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticate_ThrowsBadCredentialsException_WhenCredentialsAreInvalid(){
        AuthenticationRequest authReq = new AuthenticationRequest("user1", "123");

        doThrow(new BadCredentialsException("Credentials are invalid"))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(BadCredentialsException.class, () -> {
            authenticationService.authenticate(authReq);
        });

        verify(userRepository, never()).findByUsernameIgnoreCase(anyString());
        verify(jwtService, never()).generateToken(any());
    }
}
