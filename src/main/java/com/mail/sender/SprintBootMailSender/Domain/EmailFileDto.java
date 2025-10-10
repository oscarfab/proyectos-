package com.mail.sender.SprintBootMailSender.Domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailFileDto {
    private String[] toUser;
    private String Subjet;
    private String message;
   private MultipartFile file;
}
