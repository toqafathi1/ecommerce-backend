package com.dailycodework.dreamshops.security.service;

import com.dailycodework.dreamshops.exceptions.ResourceNotFoundException;
import com.dailycodework.dreamshops.repository.UserRepository;
import com.dailycodework.dreamshops.security.entities.RefreshToken;
import com.dailycodework.dreamshops.model.User;
import com.dailycodework.dreamshops.security.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository ;

    public RefreshToken createRefreshToken(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));

        RefreshToken refreshToken = user.getRefreshToken();
        if (refreshToken == null){
            long refreshTokenValidity = 7 * 24 * 60 * 60 * 1000 ;
            refreshToken = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expirationTime(Instant.now().plusMillis(refreshTokenValidity))
                    .user(user)
                    .build();
            refreshTokenRepository.save(refreshToken);
        }
        return refreshToken;
    }

    public RefreshToken verifyRefreshToken (String refreshToken){
        RefreshToken refToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found!"));

        if(refToken.getExpirationTime().compareTo(Instant.now()) < 0){
            refreshTokenRepository.delete(refToken);
            throw new RuntimeException("Refresh token is expired");
        }
        return refToken;
    }

//    // Revoke all refresh tokens for the user
//    List<RefreshToken> refreshTokens = refreshTokenRepository.findByUser(user);
//    for (RefreshToken refreshToken : refreshTokens) {
//        refreshTokenService.revokeRefreshToken(refreshToken.getRefreshToken());
//    }

    public void revokeRefreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found!"));
        refreshTokenRepository.delete(token);
    }
}
