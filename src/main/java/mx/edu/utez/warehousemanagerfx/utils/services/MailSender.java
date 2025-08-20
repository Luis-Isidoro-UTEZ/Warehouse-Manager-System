package mx.edu.utez.warehousemanagerfx.utils.services;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class MailSender {

    public static void sendMail(String to, String subject, String body) {
        String from = "";
        String mailKey = ""; //<--- APP password provided by Google

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Let's sign in to Google
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication
            getPasswordAuthentication() {
                return new PasswordAuthentication(from, mailKey);
            }
        });

        try {
            Message m = new MimeMessage(session);
            m.setFrom(new InternetAddress(from)); // Who sends
            m.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            ); // To whom the message is for
            m.setSubject(subject);

            /*
            // Example of HTML content
            String htmlContent = """
                    <h1 style='color: blue;'>Hello <User></h1>
                    <p>This is a sample <strong>HTML-formatted email</strong>.</p>
                    <p style='color: green;'>Remember to study for Wednesday<em> JavaFX</em> !!</p>
                    <img src='https://i.pinimg.com/236x/51/30/77/5130770e4cdec78276415c649837bef0.jpg&#39; />
                    """;*/

            // Set message content to HTML
            m.setContent(body, "text/html; charset=utf-8");

            // We are going to send
            Transport.send(m);
            System.out.println("The message was sent successfully.");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}