package com.yemifirefox.photoappuserservice.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateUserRequest {

    @NotNull(message = "firstName cannot be null")
    @Size(min=2, message = "firstName must not be less than 2 characters")
    private String firstName;
    @NotNull(message = "lastName cannot be null")
    @Size(min=2, message = "lastName must not be less than 2 characters")
    private String lastName;
    @NotNull(message = "email cannot be null")
    @Email
    private String email;
    @NotNull(message = "Password cannot be null")
    @Size(min = 8, max = 16, message = "Password must be must be equal or greater than 8 characters and less or equals to 16 characters")
    private String password;


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
