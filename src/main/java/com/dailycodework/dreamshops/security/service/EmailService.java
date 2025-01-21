package com.dailycodework.dreamshops.security.service;

import com.dailycodework.dreamshops.model.Order;
import com.dailycodework.dreamshops.security.entities.MailBody;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    public void sendVerificationEmail(MailBody mailBody){
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(mailBody.to());
            helper.setSubject(mailBody.subject());
            helper.setText(mailBody.text() , true); // Set to `true` for HTML content

            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    public void sendOrderConfirmation(Order order){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(order.getUser().getEmail());
        message.setSubject("Order confirmation");
        //TODO: include arrival data of order
        message.setText("Your order has been confirmed. Order ID " + order.getOrderId() );
        emailSender.send(message);
    }
}
