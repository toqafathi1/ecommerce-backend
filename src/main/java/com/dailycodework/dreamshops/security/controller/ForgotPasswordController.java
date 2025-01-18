package com.dailycodework.dreamshops.security.controller;

import com.dailycodework.dreamshops.exceptions.ResourceNotFoundException;
import com.dailycodework.dreamshops.repository.UserRepository;
import com.dailycodework.dreamshops.security.entities.ForgotPassword;
import com.dailycodework.dreamshops.security.entities.MailBody;
import com.dailycodework.dreamshops.model.User;
import com.dailycodework.dreamshops.security.repositories.ForgotPasswordRepository;
import com.dailycodework.dreamshops.security.service.EmailService;
import com.dailycodework.dreamshops.security.service.ForgotPasswordService;
import com.dailycodework.dreamshops.security.utils.ChangePassword;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {

    private final UserRepository userRepository ;
    private final EmailService emailService ;
    private final ForgotPasswordRepository forgotPasswordRepository ;
    private final PasswordEncoder passwordEncoder;
   private final ForgotPasswordService forgotPasswordService;

    // send mail for email verification
    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("user not found " ));

        // Delete any existing OTP records for user
        forgotPasswordService.deleteByUser(user);

        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is the OTP for your Forgot Password request : " + otp)
                .subject("OTP for Forgot Password request")
                .build();

        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 40 * 1000))
                .user(user)
                .build();

        emailService.sendVerificationEmail(mailBody);
//        try {
//            emailService.sendVerificationEmail(mailBody);
//        } catch (MessagingException e) {
//            forgotPasswordRepository.delete(fp); // Delete the OTP if email sending fails
//            throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
//        }

        forgotPasswordRepository.save(fp);

        return ResponseEntity.ok("Email sent for verification!");
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide an valid email!"));

        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp, user)
                .orElseThrow(() -> new RuntimeException("Invalid OTP for email: " + email));

        if (fp.getExpirationTime().before(Date.from(Instant.now()))) {
            forgotPasswordRepository.deleteById(fp.getFpid());
            return new ResponseEntity<>("OTP has expired!", HttpStatus.EXPECTATION_FAILED);
        }

        return ResponseEntity.ok("OTP verified!");
    }

    @Transactional
    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@Valid @RequestBody ChangePassword changePassword,
                                                        @PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found !"));

        ForgotPassword fp = forgotPasswordRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("OTP not verified "));

        if(fp.getExpirationTime().before(Date.from(Instant.now()))){
            forgotPasswordRepository.deleteById(fp.getFpid());
            return new ResponseEntity<>("OTP has expired!", HttpStatus.BAD_REQUEST);
        }

        if (!Objects.equals(changePassword.password(), changePassword.repeatPassword())) {
            return new ResponseEntity<>("Please enter the password again!", HttpStatus.EXPECTATION_FAILED);
        }

        String encodedPassword = passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(email, encodedPassword);
        forgotPasswordRepository.deleteById(fp.getFpid());

        return ResponseEntity.ok("Password has been changed!");
    }

    private Integer otpGenerator() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }


}
