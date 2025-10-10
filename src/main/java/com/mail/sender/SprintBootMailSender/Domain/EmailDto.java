package com.mail.sender.SprintBootMailSender.Domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailDto {
    private String[] toUser;
    private String Subjet;
    private String message;

}
