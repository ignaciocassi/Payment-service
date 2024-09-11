package com.ignaciocassi.message;

import org.springframework.stereotype.Service;

@Service
public interface MessageSender {

    boolean sendMessage(Message message) throws RuntimeException;

}
