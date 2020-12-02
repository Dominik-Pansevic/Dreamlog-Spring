package com.stubbz.springboot.dreamlog.dto;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

public class PasswordDto {

    private String oldPassword;

    @NotEmpty(message = "Password is compulsory.")
    @Length(min=5, message = "Password should be at least 5 characters long.")
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}