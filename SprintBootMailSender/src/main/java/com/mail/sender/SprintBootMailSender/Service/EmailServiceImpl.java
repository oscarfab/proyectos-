package com.mail.sender.SprintBootMailSender.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Service
public class EmailServiceImpl implements IEmailService {
    @Value("${email.sender}")
    private String emailUser;
    @Autowired
    private JavaMailSender mailSender;
    @Override
    public void sendEmail(String[] toUser, String subject, String menssage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailUser);
        message.setTo(toUser);
        message.setSubject(subject);
        message.setText(menssage);
        mailSender.send(message);
    }

    @Override
    public void sendEmailwithFile(String[] toUser, String subject, String file) {

    }


    @Override
    public void sendEmailwithFile(String[] toUser, String subject, String message, String filePath) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            mimeMessageHelper.setFrom(emailUser);
            mimeMessageHelper.setTo(toUser);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(message, true);

            // Verificar archivo antes de adjuntar
            File file = new File(filePath);
            System.out.println("=== ADJUNTANDO ARCHIVO ===");
            System.out.println("Ruta: " + filePath);
            System.out.println("Existe: " + file.exists());
            System.out.println("Tamaño: " + file.length() + " bytes");
            System.out.println("Nombre: " + file.getName());

            if (file.exists() && file.length() > 0) {
                mimeMessageHelper.addAttachment(file.getName(), file);
                System.out.println("✅ Archivo adjuntado correctamente");
            } else {
                System.out.println("❌ ERROR: Archivo no existe o está vacío");
                throw new RuntimeException("Archivo no válido para adjuntar");
            }

            mailSender.send(mimeMessage);
            System.out.println("✅ Email enviado con archivo adjunto");

        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el archivo: " + e.getMessage(), e);
        }
    }
}
