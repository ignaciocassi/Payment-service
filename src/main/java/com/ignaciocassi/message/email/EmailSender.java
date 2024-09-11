package com.ignaciocassi.message.email;

import com.ignaciocassi.message.Message;
import com.ignaciocassi.message.MessageSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@ConditionalOnProperty(
        value = "gmail.enabled",
        havingValue = "true"
)
public class EmailSender implements MessageSender {

    private final JavaMailSender javaMailSender;

    public EmailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public boolean sendMessage(Message message) {
        boolean send = false;
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        try {
            helper.setTo(message.getTo());
            helper.setText(message.getBody(), false);
            helper.setSubject(message.getSubject());
            javaMailSender.send(mimeMessage);
            send = true;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return send;
    }

}
