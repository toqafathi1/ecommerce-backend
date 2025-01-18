package com.dailycodework.dreamshops.security.controller;

import com.dailycodework.dreamshops.security.repositories.ForgotPasswordRepository;
import com.dailycodework.dreamshops.security.service.ForgotPasswordService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class OtpCleanupScheduler {
    private final ForgotPasswordService forgotPasswordService;

    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredOtps(){
        forgotPasswordService.deleteExpiredOtps(new Date());
    }
}
