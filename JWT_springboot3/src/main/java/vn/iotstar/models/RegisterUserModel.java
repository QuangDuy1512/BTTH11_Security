package vn.iotstar.models;

import lombok.Data;

@Data
public class RegisterUserModel {
    private String username;
    private String password;
    private String email;
}
