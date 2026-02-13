package org.example.services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Sends password reset emails via Gmail SMTP.
 * Requires a Gmail App Password (not regular password).
 */
public class EmailService {

    // ========== CONFIGURE THESE ==========
    private static final String SENDER_EMAIL = "khalloufimaram10@gmail.com";
    private static final String SENDER_PASSWORD = "YOUR_APP_PASSWORD_HERE"; // Gmail App Password
    // =====================================

    private final Properties props;

    public EmailService() {
        props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
    }

    /**
     * Sends a password reset email with the given token.
     */
    public boolean sendResetEmail(String toEmail, String resetToken) {
        try {
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("MindGrow - Password Reset");

            String htmlContent = """
                <html>
                <body style="font-family: 'Segoe UI', Arial, sans-serif; background-color: #F0F5F2; padding: 40px;">
                  <div style="max-width: 500px; margin: auto; background: white; border-radius: 16px; padding: 40px; box-shadow: 0 4px 20px rgba(0,0,0,0.08);">
                    <h2 style="color: #5E8B6E; text-align: center;">🌱 MindGrow</h2>
                    <h3 style="color: #333; text-align: center;">Password Reset Request</h3>
                    <p style="color: #666; text-align: center;">Use the following code to reset your password:</p>
                    <div style="background: #F0F5F2; border: 2px solid #D4AC4D; border-radius: 12px; padding: 20px; text-align: center; margin: 20px 0;">
                      <span style="font-size: 32px; font-weight: bold; color: #5E8B6E; letter-spacing: 8px;">%s</span>
                    </div>
                    <p style="color: #999; text-align: center; font-size: 13px;">This code expires in 15 minutes.<br>If you didn't request this, please ignore this email.</p>
                  </div>
                </body>
                </html>
                """.formatted(resetToken);

            message.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(message);

            System.out.println("Reset email sent to: " + toEmail);
            return true;

        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
