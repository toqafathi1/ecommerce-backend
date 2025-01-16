package com.dailycodework.dreamshops.security.utils;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "username cannot be blank")
    @Size(min=3 , max = 57 )
    @Column(unique = true , nullable = false)
    private String username;

    @Email
    private String email;

    @NotBlank(message = "The password field can't be blank")
    @Column(nullable = false)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{8,16}$" ,
            message = "Password must contain 8-16 characters, at least one digit, one uppercase letter," +
                    " one lowercase letter, and one special character, and must not contain spaces.")
    private String password;
}
