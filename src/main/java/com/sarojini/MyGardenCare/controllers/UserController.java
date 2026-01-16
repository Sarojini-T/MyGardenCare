package com.sarojini.MyGardenCare.controllers;
import com.sarojini.MyGardenCare.entities.User;
import com.sarojini.MyGardenCare.repositories.UserRepository;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(final UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id){
        Optional<User> userByIdOptional = this.userRepository.findById(id);

        if(!userByIdOptional.isPresent()){
            return ResponseEntity.notFound().build();
        }

        User userById = userByIdOptional.get();
        return ResponseEntity.ok(userById);
    }

    @GetMapping("/search")
    public ResponseEntity<User> searchUser(@RequestParam(name = "username", required = false) String username, @RequestParam(name = "email", required = false) String email){
//        if(username != null  && email != null){
//            Optional<User> userByUsernameAndEmailOptional =  this.userRepository.findByUsernameAndEmail(username, email);
//
//            if(!userByUsernameAndEmailOptional.isPresent()){
//                return ResponseEntity.notFound().build();
//            }
//            User userByUsernameAndEmail = userByUsernameAndEmailOptional.get();
//            return ResponseEntity.ok(userByUsernameAndEmail);
//        }
//        else

            Optional<User> userByUsernameOptional =  this.userRepository.findByUsername(username);
            if(!userByUsernameOptional.isPresent()) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(userByUsernameOptional.get());

//        else if(email != null ){
//            Optional<User> userByEmailOptional =  this.userRepository.findByEmail(email);
//
//            if(!userByEmailOptional.isPresent()){
//                return ResponseEntity.notFound().build();
//            }
//            User userByEmail = userByEmailOptional.get();
//            return ResponseEntity.ok(userByEmail);
//        }else {
//            return ResponseEntity.notFound().build();
//        }
    }


   @PostMapping
    public ResponseEntity<User> createNewUser(@RequestBody User user){
        User newUser = this.userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
   }

   @PutMapping("/{id}")
   public ResponseEntity<User> updateUserById(@PathVariable("id") Long id, @RequestBody User user){
        Optional<User> userToUpdateOptional = this.userRepository.findById(id);

        if(!userToUpdateOptional.isPresent()){
            return ResponseEntity.notFound().build();
        }

        User userToUpdate = userToUpdateOptional.get();

        if(user.getUsername() != null){
            userToUpdate.setUsername(user.getUsername());
        }
        if(user.getEmail() != null){
            userToUpdate.setEmail(user.getEmail());
        }
        if(user.getPassword() != null){
            userToUpdate.setPassword(user.getPassword());
        }
        if(user.getCity() != null){
            userToUpdate.setCity(user.getCity());
        }
        if(user.getState() != null){
            userToUpdate.setState(user.getState());
        }

        User updatedUser = this.userRepository.save(userToUpdate);
        return ResponseEntity.ok(updatedUser);
   }

    @PutMapping("/update")
    public ResponseEntity<User> updateUser(@RequestParam(name="username", required = false) String username, @RequestParam(name="email", required = false) String email, @RequestBody User user){
        Optional<User> userToUpdateOptional;

        if(username != null && email != null){
           userToUpdateOptional = this.userRepository.findByUsernameAndEmail(username, email);
       }
       else  if(username != null){
            userToUpdateOptional = this.userRepository.findByUsername(username);
        }
       else if (email != null){
            userToUpdateOptional = this.userRepository.findByEmail(email);
       } else{
            return ResponseEntity.badRequest().build();
        }

        if(!userToUpdateOptional.isPresent()){
            return ResponseEntity.notFound().build();
        }

        User userToUpdate = userToUpdateOptional.get();

        if(user.getUsername() != null){
            userToUpdate.setUsername(user.getUsername());
        }
        if(user.getEmail() != null){
            userToUpdate.setEmail(user.getEmail());
        }
        if(user.getPassword() != null){
            userToUpdate.setPassword(user.getPassword());
        }
        if(user.getCity() != null){
            userToUpdate.setCity(user.getCity());
        }
        if(user.getState() != null){
            userToUpdate.setState(user.getState());
        }

        User updatedUser = this.userRepository.save(userToUpdate);
        return ResponseEntity.ok(updatedUser);

    }

   @DeleteMapping("/{id}")
   public ResponseEntity<User> deleteById(@PathVariable("id") Long id){
        Optional<User> userToDeleteOptional = this.userRepository.findById(id);

        if(!userToDeleteOptional.isPresent()){
            return ResponseEntity.notFound().build();
        }

        User userToDelete = userToDeleteOptional.get();
        this.userRepository.delete(userToDelete);
        return ResponseEntity.noContent().build();
   }

    @DeleteMapping("/delete")
    public ResponseEntity<User> deleteByUsername(@RequestParam(name = "username", required = false) String username, @RequestParam(name="email", required = false) String email) {
        Optional<User> userToDeleteOptional;
        if (username != null && email != null){
            userToDeleteOptional = this.userRepository.findByUsernameAndEmail(username, email);
        }
        else if(username != null){
            userToDeleteOptional = this.userRepository.findByUsername(username);
        }
        else if( email != null){
            userToDeleteOptional = this.userRepository.findByEmail(email);
        }
        else{
            return ResponseEntity.badRequest().build();
        }

        if(!userToDeleteOptional.isPresent()){
            return ResponseEntity.notFound().build();
        }

        User userToDelete = userToDeleteOptional.get();
        this.userRepository.delete(userToDelete);
        return ResponseEntity.noContent().build();
    }

}
