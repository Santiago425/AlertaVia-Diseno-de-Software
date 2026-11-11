package com.alertavia.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PushNotificationClient {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationClient.class);

    private final ObjectProvider<JavaMailSender> mailSender;
    private final String smtpHost;
    private final String from;

    public PushNotificationClient(ObjectProvider<JavaMailSender> mailSender,
                                  @Value("${spring.mail.host:}") String smtpHost,
                                  @Value("${app.notifications.mail-from}") String from) {
        this.mailSender = mailSender;
        this.smtpHost = smtpHost;
        this.from = from;
    }

    @Async
    public void send(String to, String subject, String message) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (!StringUtils.hasText(smtpHost) || sender == null) {
            log.info("Notificación para {}: {}", to, message);
            return;
        }
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(from);
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(message);
            sender.send(mail);
        } catch (MailException ex) {
            log.warn("No se pudo enviar el correo a {}: {}", to, ex.getMessage());
        }
    }
}
