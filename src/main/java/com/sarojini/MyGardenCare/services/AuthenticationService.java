package com.sarojini.MyGardenCare.services;

import com.sarojini.MyGardenCare.dtos.AuthenticationRequestDto;
import com.sarojini.MyGardenCare.dtos.AuthenticationResponseDto;
import com.sarojini.MyGardenCare.dtos.UserCreateRequestDto;
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

    public AuthenticationResponseDto register (UserCreateRequestDto createReq){
        userService.createNewUser(createReq);

        User savedUser = userRepository.findByUsernameIgnoreCase(createReq.getUsername()).get();

        String jwtToken = jwtService.generateToken(savedUser);
        return AuthenticationResponseDto.builder().token(jwtToken).build();
    }

    public AuthenticationResponseDto authenticate(AuthenticationRequestDto authReq){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authReq.getUsername(),
                        authReq.getPassword()
                )
        );

        User user = userRepository.findByUsernameIgnoreCase(authReq.getUsername()).get();

        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponseDto.builder().token(jwtToken).build();
    }
}
