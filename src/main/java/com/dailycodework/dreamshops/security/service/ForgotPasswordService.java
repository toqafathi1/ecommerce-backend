package com.dailycodework.dreamshops.security.service;

import com.dailycodework.dreamshops.model.User;
import com.dailycodework.dreamshops.security.repositories.ForgotPasswordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {
    private final ForgotPasswordRepository forgotPasswordRepository;

    @Transactional
    public void deleteByUser(User user){
        forgotPasswordRepository.deleteByUser(user);
    }
    @Transactional
    public void deleteExpiredOtps(Date currentTime){
        forgotPasswordRepository.deleteExpiredOtps( currentTime);
    }

}
