package com.mail.sender.SprintBootMailSender.Service;

public interface IEmailService {
    void sendEmail(String[] toUser, String subject, String menssage);
    void sendEmailwithFile(String[] toUser, String subject, String file);

    void sendEmailwithFile(String[] toUser, String subject, String message, String file);
}
