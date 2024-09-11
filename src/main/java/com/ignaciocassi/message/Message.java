package com.ignaciocassi.message;

public class Message {

    private final String to;

    private final String subject;

    private final String body;


    public Message(String s, String subject, String body) {
        to = s;
        this.subject = subject;
        this.body = body;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }
}
