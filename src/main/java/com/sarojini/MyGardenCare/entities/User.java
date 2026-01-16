package com.sarojini.MyGardenCare.entities;

import jakarta.persistence.*;

@Entity
@Table(name="users")
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

    @Column
    private String city;

    @Column
    private String state;

    public Long getId(){
        return this.id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getEmail(){ return this.email;}

    public void setEmail(String email){this.email = email;}

    public String getPassword(){
        return this.password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getCity(){return this.city; }

    public void setCity(String city){this.city = city;}

    public String getState(){return this.state; }

    public void setState(String state){this.state = state;}
}