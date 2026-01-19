package com.sarojini.MyGardenCare.dtos;
import lombok.Data;

@Data
public class UserCreateResponse {
    private Long id;
    private String username;
    private String email;
    private String zipcode;
}
