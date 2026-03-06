package com.sarojini.MyGardenCare.services;

import com.sarojini.MyGardenCare.dtos.UserCreateRequest;
import com.sarojini.MyGardenCare.dtos.UserResponse;
import com.sarojini.MyGardenCare.dtos.UserUpdateRequest;
import com.sarojini.MyGardenCare.entities.User;
import com.sarojini.MyGardenCare.exceptions.ConflictException;
import com.sarojini.MyGardenCare.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getAllUsers(){
        Iterable<User> allUsers = userRepository.findAll();

        List<UserResponse> userResponses = new ArrayList<>();
        for(User user : allUsers){
            userResponses.add(mapUserToUserResponse(user));
        }
        return userResponses;
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
        String username = userCreateReq.getUsername().trim();
        String email = userCreateReq.getEmail().trim();
        validateUsernameAndEmail(username, email, null);

        userCreateReq.setUsername(username);
        userCreateReq.setEmail(email);
        User newUser = mapUserCreateReqToUser(userCreateReq);
        User savedUser = userRepository.save(newUser);
        return mapUserToUserResponse(savedUser);
    }

    @Transactional
    public UserResponse updateMyProfile(String username, UserUpdateRequest userUpdateReq){
        User userToUpdate = getUserByUsernameHelper(username);

        String normalizedUsername = StringUtils.hasText(userUpdateReq.getUsername()) ? userUpdateReq.getUsername().trim() : null;
        String normalizedEmail = StringUtils.hasText(userUpdateReq.getEmail()) ? userUpdateReq.getEmail().trim() : null;
        String normalizedPassword = StringUtils.hasText(userUpdateReq.getPassword()) ? userUpdateReq.getPassword().trim() : null;
        String normalizedZipcode = userUpdateReq.getZipcode() != null ? userUpdateReq.getZipcode().trim() : null;

        validateUsernameAndEmail(normalizedUsername, normalizedEmail, userToUpdate);

        applyPatchUpdate(normalizedUsername, normalizedEmail, normalizedPassword, normalizedZipcode, userToUpdate);

        userRepository.save(userToUpdate);

        return mapUserToUserResponse(userToUpdate);
    }

    public void deleteById(Long id){
        User userToDelete = getUserByIdHelper(id);

        userRepository.delete(userToDelete);
    }

    public void deleteByUsername(String username){
        User userToDelete = getUserByUsernameHelper(username);
        userRepository.delete(userToDelete);
    }

    private User getUserByIdHelper(Long id){
        Optional<User> userByIdOptional = userRepository.findById(id);
        if(userByIdOptional.isEmpty()) throw new EntityNotFoundException("User " + id + " not found.");
        return userByIdOptional.get();
    }

    private User getUserByUsernameHelper(String username){
        Optional<User> userByUsernameOptional = userRepository.findByUsernameIgnoreCase(username);
        if(userByUsernameOptional.isEmpty()) throw new EntityNotFoundException("User " + username + " not found.");
        return userByUsernameOptional.get();
    }

    private UserResponse mapUserToUserResponse(User user){
        Long id = user.getId();
        String username = user.getUsername();
        String email = user.getEmail();
        String zipcode = user.getZipcode();

        return new UserResponse(id, username, email, zipcode);
    }

    private User mapUserCreateReqToUser(UserCreateRequest userCreateReq){
        String username = userCreateReq.getUsername();
        String email = userCreateReq.getEmail();
        String encodedPassword = passwordEncoder.encode(userCreateReq.getPassword());
        String zipcode = userCreateReq.getZipcode();

        User newUser = new User(username, email, encodedPassword);
        if(zipcode != null) newUser.updateZipCode(zipcode);
        return newUser;
    }

    private void validateUsernameAndEmail(String username, String email, User... userToUpdate){
        if (username != null) {
            Optional<User> userByUsernameOptional = userRepository.findByUsernameIgnoreCase(username);

            if(userByUsernameOptional.isPresent()){
                User userByUsername = userByUsernameOptional.get();
                if(userToUpdate == null || !userToUpdate[0].getId().equals(userByUsername.getId())){
                    throw new ConflictException("Username already taken");
                }
            }
        }

        if(email != null) {
            Optional<User> userByEmailOptional = userRepository.findByEmailIgnoreCase(email);
            if(userByEmailOptional.isPresent()){
                User userByEmail = userByEmailOptional.get();
                if(userToUpdate != null || !userToUpdate[0].getId().equals(userByEmail.getId())){
                    throw new ConflictException("Email exists already");
                }
            }
        }
    }

    private void applyPatchUpdate(String username,
                                  String email,
                                  String password,
                                  String zipcode,
                                  User userToUpdate){
        if(username != null) userToUpdate.updateUsername(username);
        if(email != null) userToUpdate.updateEmail(email);
        if(password != null) userToUpdate.updatePassword(passwordEncoder.encode(password));
        if(zipcode != null){
            if(zipcode.isBlank()) userToUpdate.deleteZipcode();
            else userToUpdate.updateZipCode(zipcode);
        }
    }
}
