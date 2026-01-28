package com.sarojini.MyGardenCare.entities;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;

@Entity
@Table(name="app_users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String zipcode;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public User(String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void updateZipCode(String zipcode){
        if(!zipcode.matches("\\d{5}")) throw new IllegalArgumentException("Zipcode requires 5 digits");
        this.zipcode = zipcode;
    }

    public void deleteZipcode(){
        this.zipcode = null;
    }

    public void updateUsername(String username){
        if(username == null || username.isBlank()) throw new IllegalArgumentException("Username cannot be empty");
        this.username = username;
    }

    public void updateEmail(String email){
        this.email = email;
    }

    public void updatePassword(String password){
        this.password = password;
    }
}