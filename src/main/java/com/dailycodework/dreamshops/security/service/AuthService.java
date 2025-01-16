package com.dailycodework.dreamshops.security.service;

import com.dailycodework.dreamshops.exceptions.AlreadyExistsException;
import com.dailycodework.dreamshops.exceptions.ResourceNotFoundException;
import com.dailycodework.dreamshops.model.User;
import com.dailycodework.dreamshops.repository.UserRepository;
import com.dailycodework.dreamshops.security.entities.MailBody;
import com.dailycodework.dreamshops.security.entities.RefreshToken;
import com.dailycodework.dreamshops.security.entities.UserRole;
import com.dailycodework.dreamshops.security.utils.AuthResponse;
import com.dailycodework.dreamshops.security.utils.LoginRequest;
import com.dailycodework.dreamshops.security.utils.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder ;
    private final UserRepository userRepository ;
    private final JwtService jwtService ;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService ;

    public AuthResponse register(RegisterRequest registerRequest){
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            throw  new AlreadyExistsException("User with " + registerRequest.getEmail() + " already exists");
        }

        var user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.USER)
                .verified(false) // user not verified initially
                .verificationCodeExpiresAt(LocalDateTime.now().plusMinutes(5))
                .verificationToken(UUID.randomUUID().toString()) // generate random token
                .build();
        User savedUser = userRepository.save(user);
        // send verification email
        String verificationUrl = "http://localhost:8080/api/v1/auth/verify?token="+savedUser.getVerificationToken();
        MailBody mailBody = MailBody.builder()
                .to(savedUser.getEmail())
                .subject("Verify Your Email")
                .text("Please click the link below to verify your email : \n" + verificationUrl)
                .build();
        emailService.sendVerificationEmail(mailBody);

        var accessToken = jwtService.generateToken(savedUser);
        var refreshToken = refreshTokenService.createRefreshToken(savedUser.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    public  AuthResponse login(LoginRequest loginRequest){
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()-> new UsernameNotFoundException("User not found!"));

        if(!user.isVerified()){
            throw new RuntimeException("Account not verified. please verify your account.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("wrong password try again! " + e);
        }

        var accessToken = jwtService.generateToken(user);
         var refreshToken = refreshTokenService.createRefreshToken(loginRequest.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    public void verifyUser(String verificationToken){
        Optional<User> optionalUser = userRepository.findByVerificationToken(verificationToken);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())){
                throw new RuntimeException("Verification token has expired");
            }
            if(!user.getVerificationToken().equals(verificationToken)){
                throw new RuntimeException("Invalid verification token");
            }
            user.setVerified(true);
            user.setVerificationToken(null);
            user.setVerificationCodeExpiresAt(null);
            userRepository.save(user);
        }else {
            throw new RuntimeException("Invalid verification token");
        }
    }

    public void resentVerificationToken(String email){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            if (user.isVerified()){
                throw  new RuntimeException("Account is already verified");
            }
            user.setVerificationToken(UUID.randomUUID().toString());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(5));
            userRepository.save(user);

            String verificationUrl = "http://localhost:8080/api/v1/auth/verify?token=" + user.getVerificationToken();
            MailBody mailBody = MailBody.builder()
                    .to(user.getEmail())
                    .subject("Verify Your Email")
                    .text("Please click the link below to verify your email: \n" + verificationUrl)
                    .build();
            emailService.sendVerificationEmail(mailBody);
        }else {
            throw new RuntimeException("User not found");
        }
    }

    @Transactional
    public void deleteAccount(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("User not found!"));
        // Revoke all refresh tokens for the user
        List<RefreshToken> refreshTokens = user.getRefreshTokens();
        for (RefreshToken refreshToken : refreshTokens) {
            refreshTokenService.revokeRefreshToken(refreshToken.getRefreshToken());
        }
        userRepository.delete(user);
    }
}
