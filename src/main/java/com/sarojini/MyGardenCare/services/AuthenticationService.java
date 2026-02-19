package com.sarojini.MyGardenCare.services;

import com.sarojini.MyGardenCare.dtos.AuthenticationRequest;
import com.sarojini.MyGardenCare.dtos.AuthenticationResponse;
import com.sarojini.MyGardenCare.dtos.UserCreateRequest;
import com.sarojini.MyGardenCare.entities.User;
import com.sarojini.MyGardenCare.repositories.UserRepository;
import com.sarojini.MyGardenCare.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register (UserCreateRequest createReq){
        userService.createNewUser(createReq);

        User savedUser = userRepository.findByUsernameIgnoreCase(createReq.getUsername()).get();

        String jwtToken = jwtService.generateToken(savedUser);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authReq){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authReq.getUsername(),
                        authReq.getPassword()
                )
        );

        User user = userRepository.findByUsernameIgnoreCase(authReq.getUsername()).get();

        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
}
