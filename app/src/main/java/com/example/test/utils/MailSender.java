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

    public static int sendUpdateEmailVerification(String emailReceiver) {
        int securityCode = (int) (Math.random() * 999999 + 100000);
        String title = "Sửa hồ sơ";
        String verifyMailContent = "Mã xác thực cho email của bạn là: " + securityCode;
        sendMail(emailReceiver, title, verifyMailContent);
        return securityCode;
    }

    public static int sendResetPasswordMail(String emailReceiver) {
        int securityCode = (int) (Math.random() * 999999 + 100000);
        String title = "Đổi mật khẩu";
        String verifyMailContent = "Mã xác thực để đổi mật khẩu của bạn là: " + securityCode;
        sendMail(emailReceiver, title, verifyMailContent);
        return securityCode;
    }

    public static int sendVerificationMail(String emailReceiver) {
        int securityCode = (int) (Math.random() * 999999 + 100000);
        String title = "Xác thực tài khoản";
        String emailContent = "Chào bạn,\n\n"
                + "Chúc mừng bạn đã tạo thành công tài khoản trên ứng dụng Let's Cook Together. Chúng tôi rất vui mừng được chào đón bạn vào cộng đồng của chúng tôi.\n\n"
                + "Để bắt đầu trải nghiệm những tính năng tuyệt vời của ứng dụng, vui lòng xác thực tài khoản bằng cách sử dụng mã xác thực sau đây:\n\n"
                + "Mã xác thực: " + securityCode + "\n\n"
                + "Hãy nhập mã xác thực này trong trang xác thực của ứng dụng để hoàn tất quá trình xác thực tài khoản.\n\n"
                + "Nếu bạn không thực hiện yêu cầu tạo tài khoản trên ứng dụng Let's Cook Together, xin vui lòng bỏ qua email này hoặc liên hệ với chúng tôi ngay qua địa chỉ email dưới đây để chúng tôi có thể hỗ trợ bạn.\n\n"
                + "Chúng tôi rất mong được hỗ trợ và phục vụ bạn tốt nhất. Cảm ơn bạn đã lựa chọn ứng dụng của chúng tôi.\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ hỗ trợ nhóm 8";
        sendMail(emailReceiver, title, emailContent);
        return securityCode;
    }
}

