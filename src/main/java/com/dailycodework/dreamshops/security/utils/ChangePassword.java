package com.dailycodework.dreamshops.security.utils;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangePassword(@NotBlank(message = "The password field can't be blank")
                             @Column(nullable = false)
                             @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{8,16}$" ,
                                     message = "Password must contain 8-16 characters, at least one digit, one uppercase letter," +
                                             " one lowercase letter, and one special character, and must not contain spaces.") String password
                             , String repeatPassword) {
}
