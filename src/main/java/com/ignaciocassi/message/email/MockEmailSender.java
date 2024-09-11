package com.ignaciocassi.message.email;

import com.ignaciocassi.message.Message;
import com.ignaciocassi.message.MessageSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        value = "gmail.enabled",
        havingValue = "false"
)
public class MockEmailSender implements MessageSender {
    @Override
    public boolean sendMessage(Message message) {
        return true;
    }
}
