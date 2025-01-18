package com.dailycodework.dreamshops.security.repositories;

import com.dailycodework.dreamshops.security.entities.ForgotPassword;
import com.dailycodework.dreamshops.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword ,Integer> {

    @Query("select fp from ForgotPassword fp where fp.otp = ?1 and fp.user = ?2")
    Optional<ForgotPassword> findByOtpAndUser(Integer otp, User user);

    Optional<ForgotPassword> findByUser(User user);

    @Modifying
    @Query("delete from ForgotPassword fp where fp.user = ?1")
    void deleteByUser(User user);
    @Modifying
    @Query("delete from ForgotPassword fp where fp.expirationTime < ?1")
    void deleteExpiredOtps(Date currentTime);

}
