package com.sarojini.MyGardenCare.services;

import com.sarojini.MyGardenCare.dtos.UserCreateRequest;
import com.sarojini.MyGardenCare.dtos.UserResponse;
import com.sarojini.MyGardenCare.dtos.UserUpdateRequest;
import com.sarojini.MyGardenCare.entities.User;
import com.sarojini.MyGardenCare.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getUserById(Long id){
        User userById = getUserByIdHelper(id);
        return mapUserToUserResponse(userById);
    }

    public UserResponse getUserByUsername(String username){
        Optional<User> userByUsernameOptional = userRepository.findByUsernameIgnoreCase(username);
        if(userByUsernameOptional.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + username + " not found.");
        return mapUserToUserResponse(userByUsernameOptional.get());
    }

    public UserResponse createNewUser(UserCreateRequest userCreateReq){
        if (userRepository.existsByUsernameIgnoreCase(userCreateReq.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        if(userRepository.existsByEmailIgnoreCase(userCreateReq.getEmail())){
            throw new RuntimeException("Email already taken");
        }

        User newUser = mapUserCreateReqToUser(userCreateReq);
        User savedUser = userRepository.save(newUser);
        return mapUserToUserResponse(savedUser);
    }

    public UserResponse updateUserById(Long id, UserUpdateRequest userUpdateReq){
        User userToUpdate = getUserByIdHelper(id);

        String reqUsername = userUpdateReq.getUsername();
        String reqEmail = userUpdateReq.getEmail();
        String reqPassword = userUpdateReq.getPassword();
        String reqZipcode = userUpdateReq.getZipcode();

        if(reqUsername != null && !userToUpdate.getUsername().equals(reqUsername)){
            if (userRepository.existsByUsernameIgnoreCase(reqUsername)) throw new RuntimeException("Username already taken");
            userToUpdate.updateUsername(reqUsername);
        }
        if(reqEmail != null){
            if(userRepository.existsByEmailIgnoreCase(reqEmail)) throw new RuntimeException("Email exists already");
            userToUpdate.updateEmail(reqEmail);
        }
        if(reqPassword != null){
            userToUpdate.updatePassword(reqPassword);
        }
        if(reqZipcode != null){
            if(reqZipcode.isBlank()) userToUpdate.deleteZipcode();
            else userToUpdate.updateZipCode(reqZipcode);
        }

        User updatedUser = userRepository.save(userToUpdate);
        return mapUserToUserResponse(updatedUser);
    }

    public void deleteById(Long id){
        userRepository.deleteById(id);
    }

    public UserResponse mapUserToUserResponse(User user){
        Long id = user.getId();
        String username = user.getUsername();
        String email = user.getEmail();
        String zipcode = user.getZipcode();

        return  new UserResponse(id, username, email, zipcode);
    }

    public User mapUserCreateReqToUser(UserCreateRequest userCreateReq){
        String username = userCreateReq.getUsername();
        String email = userCreateReq.getEmail();
        String password = userCreateReq.getPassword();
        String zipcode = userCreateReq.getZipcode();

        User newUser = new User(username, email, password);
        if(zipcode != null) newUser.updateZipCode(zipcode);
        return newUser;
    }

    public User getUserByIdHelper(Long id){
        Optional<User> userByIdOptional = userRepository.findById(id);
        if(userByIdOptional.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + id + " not found.");
        return userByIdOptional.get();
    }
}
