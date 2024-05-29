package com.example.pfl.services;

import com.example.pfl.entities.Validation;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class NotificationService {
    JavaMailSender javaMailSender;

    public void send(Validation validation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@gilles.test");
        message.setTo(validation.getUtilisateur().getEmail());
        message.setSubject("Votre code d'activation");

        String text = String.format("Bonjour %s, <br /> Votre code d'activation est %s; A bientôt",
                validation.getUtilisateur().getPseudo(),
                validation.getCode()
        );
        message.setText(text);

        javaMailSender.send(message);
    }
}
