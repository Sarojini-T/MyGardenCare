package com.sarojini.MyGardenCare.services;

import com.sarojini.MyGardenCare.dtos.UserCreateRequest;
import com.sarojini.MyGardenCare.dtos.UserResponse;
import com.sarojini.MyGardenCare.dtos.UserUpdateRequest;
import com.sarojini.MyGardenCare.entities.User;
import com.sarojini.MyGardenCare.exceptions.ConflictException;
import com.sarojini.MyGardenCare.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
        if(userByUsernameOptional.isEmpty()) throw new EntityNotFoundException("User " + username + " not found");
        return mapUserToUserResponse(userByUsernameOptional.get());
    }

    public UserResponse createNewUser(UserCreateRequest userCreateReq){
        if (userRepository.existsByUsernameIgnoreCase(userCreateReq.getUsername())) {
            throw new ConflictException("Username already taken");
        }
        if(userRepository.existsByEmailIgnoreCase(userCreateReq.getEmail())){
            throw new ConflictException("Email already taken");
        }

        User newUser = mapUserCreateReqToUser(userCreateReq);
        User savedUser = userRepository.save(newUser);
        return mapUserToUserResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUserById(Long id, UserUpdateRequest userUpdateReq){
        User userToUpdate = getUserByIdHelper(id);

        String reqUsername = userUpdateReq.getUsername();
        String reqEmail = userUpdateReq.getEmail();
        String reqPassword = userUpdateReq.getPassword();
        String reqZipcode = userUpdateReq.getZipcode();

        if(StringUtils.hasText(reqUsername) && !userToUpdate.getUsername().equals(reqUsername)){
            if (userRepository.existsByUsernameIgnoreCase(reqUsername)) throw new ConflictException("Username already taken");
            userToUpdate.updateUsername(reqUsername);
        }
        if(StringUtils.hasText(reqEmail)){
            if(userRepository.existsByEmailIgnoreCase(reqEmail)) throw new ConflictException("Email exists already");
            userToUpdate.updateEmail(reqEmail);
        }
        if(StringUtils.hasText(reqPassword)){
            userToUpdate.updatePassword(reqPassword);
        }
        if(reqZipcode != null){
            if(reqZipcode.isBlank()) userToUpdate.deleteZipcode();
            else userToUpdate.updateZipCode(reqZipcode);
        }

        return mapUserToUserResponse(userToUpdate);
    }

    public void deleteById(Long id){
        User userToDelete = getUserByIdHelper(id);

        userRepository.delete(userToDelete);
    }

    public User getUserByIdHelper(Long id){
        Optional<User> userByIdOptional = userRepository.findById(id);
        if(userByIdOptional.isEmpty()) throw new EntityNotFoundException("User " + id + " not found.");
        return userByIdOptional.get();
    }

    public UserResponse mapUserToUserResponse(User user){
        Long id = user.getId();
        String username = user.getUsername();
        String email = user.getEmail();
        String zipcode = user.getZipcode();

        return new UserResponse(id, username, email, zipcode);
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
}
