package com.mail.sender.SprintBootMailSender.Controller;

import com.mail.sender.SprintBootMailSender.Domain.EmailDto;
import com.mail.sender.SprintBootMailSender.Domain.EmailFileDto;
import com.mail.sender.SprintBootMailSender.Service.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/B1")
public class MailController {
    @Autowired
    private IEmailService emailService;
    @PostMapping("/sendMessage")
    public ResponseEntity<?>receiveRequestEmail(@RequestBody EmailDto emailDto){
        System.out.println("mensaje recivido"+emailDto);
        emailService.sendEmail(emailDto.getToUser(),emailDto.getSubjet(),emailDto.getMessage());
        Map<String,String> response = new HashMap<>();
        response.put("estado","enviado");

        return ResponseEntity.ok("Email enviado exitosamente");


    }
    @PostMapping("/sendMessageFile")
    public ResponseEntity<?> receiveResponseEmailWithFile(@ModelAttribute EmailFileDto emailFileDto) {
        try {
            // Validaciones básicas
            if (emailFileDto.getFile().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No se proporcionó archivo"));
            }

            String fileName = emailFileDto.getFile().getOriginalFilename();

            // Debugging - Ver datos recibidos
            System.out.println("=== DEBUGGING ARCHIVO ===");
            System.out.println("Nombre archivo: " + fileName);
            System.out.println("Tamaño archivo subido: " + emailFileDto.getFile().getSize() + " bytes");
            System.out.println("Emails destinatarios: " + Arrays.toString(emailFileDto.getToUser()));
            System.out.println("========================");

            // Crear directorio y guardar archivo
            Path path = Paths.get("src/mail/resources/FILES/" + fileName);
            Files.createDirectories(path.getParent());
            Files.copy(emailFileDto.getFile().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            File file = path.toFile();

            // Verificar que el archivo se guardó correctamente
            System.out.println("Archivo guardado en: " + file.getAbsolutePath());
            System.out.println("Archivo existe: " + file.exists());
            System.out.println("Tamaño archivo guardado: " + file.length() + " bytes");

            if (!file.exists() || file.length() == 0) {
                throw new RuntimeException("Error: archivo no se guardó correctamente");
            }

            // Limpiar emails de posibles comillas
            String[] cleanEmails = Arrays.stream(emailFileDto.getToUser())
                    .map(email -> email.replaceAll("^\"|\"$", "").trim())
                    .toArray(String[]::new);

            // Enviar email
            emailService.sendEmailwithFile(
                    cleanEmails,
                    emailFileDto.getSubjet(),
                    emailFileDto.getMessage(),
                    file.getAbsolutePath()
            );

            Map<String, String> response = new HashMap<>();
            response.put("estado", "enviado");
            response.put("archivo", fileName);
            response.put("tamaño", file.length() + " bytes");
            response.put("ruta", file.getAbsolutePath());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("estado", "error");
            errorResponse.put("mensaje", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
