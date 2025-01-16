package com.dailycodework.dreamshops.security.controller;

import com.dailycodework.dreamshops.repository.UserRepository;
import com.dailycodework.dreamshops.security.entities.RefreshToken;
import com.dailycodework.dreamshops.model.User;
import com.dailycodework.dreamshops.security.service.AuthService;
import com.dailycodework.dreamshops.security.service.JwtService;
import com.dailycodework.dreamshops.security.service.RefreshTokenService;
import com.dailycodework.dreamshops.security.utils.AuthResponse;
import com.dailycodework.dreamshops.security.utils.LoginRequest;
import com.dailycodework.dreamshops.security.utils.RefreshTokenRequest;
import com.dailycodework.dreamshops.security.utils.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/")
public class AuthController {

    private final AuthService authService ;
    private final JwtService jwtService ;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository ;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest){
        AuthResponse authResponse = authService.register(registerRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest){
        AuthResponse authResponse = authService.login(loginRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        // verify refresh token
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
        User user = refreshToken.getUser();
        // generate a new access token
        String accessToken = jwtService.generateToken(user);
       // generate new refresh token
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        // delete old refresh token
        refreshTokenService.revokeRefreshToken(refreshToken.getRefreshToken());

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken.getRefreshToken())
                .build());
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token){
         authService.verifyUser(token);
        return ResponseEntity.ok("Email verified successfully");
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerificationToken(@RequestBody String email){
        authService.resentVerificationToken(email);
        return ResponseEntity.ok("Verification email resend successfully");
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<String> deleteAccount(@RequestParam String email){
       authService.deleteAccount(email);
       return ResponseEntity.ok("Account deleted successfully");
    }


}
