package com.ignaciocassi.message;

import com.ignaciocassi.payment.Payment;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageSender messageSender;

    public MessageService(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public boolean sendPaymentNotificationMessage(String to, Payment payment) {
        String subject = "You made a purchase with your credit card.";
        String body = "Your credit card was charged: \n"
                        + "Description: " + payment.getDescription() + "\n"
                        + "Amount: " + payment.getCurrency().toString()
                        + " " + payment.getAmount() + ".";
        Message message = new Message(to, subject, body);
        return messageSender.sendMessage(message);

    }

}
