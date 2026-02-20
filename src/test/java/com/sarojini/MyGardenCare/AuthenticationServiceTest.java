package com.sarojini.MyGardenCare;

import com.sarojini.MyGardenCare.repositories.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class AuthenticationService {
    @Mock UserRepository userRepository;
    @InjectMocks AuthenticationService authenticationService;
}
