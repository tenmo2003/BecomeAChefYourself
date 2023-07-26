package com.example.test.utils;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;


public class MailSender {
    private static final String title = "Verify your email to create your Sales Management account";

    private static String extractUsername(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex >= 0) {
            return email.substring(0, atIndex);
        } else {
            return null;
        }
    }

    static final String username = "becomechef2003@gmail.com";
    static final String password = "lfwfcyllaegdtgfh";
    private static boolean mailConnected = false;
    private static Properties props;
    private static Session session;

    private static void sendMail(String emailReceiver, String mailHeader, String mailContent) {
        if (!mailConnected) {
            props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.port", "465");
            session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });
        }

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(emailReceiver));
            message.setSubject(mailHeader);
            message.setText(mailContent);
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static int sendResetPasswordMail(String emailReceiver) {
        int securityCode = (int) (Math.random() * 999999 + 100000);
        String title = "Đổi mật khẩu";
        String verifyMailContent = "Mã xác thực của bạn để đổi mật khẩu là: " + securityCode;
        sendMail(emailReceiver, title, verifyMailContent);
        return securityCode;
    }
}

