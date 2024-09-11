package com.ignaciocassi.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentRequest {

    private Payment payment;

    public PaymentRequest(@JsonProperty("payment") Payment payment) {
        this.payment = payment;
    }

    public Payment getPayment() {
        return payment;
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "payment=" + payment +
                '}';
    }

}
