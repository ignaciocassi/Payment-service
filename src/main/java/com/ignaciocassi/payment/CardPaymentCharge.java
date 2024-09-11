package com.ignaciocassi.payment;

public class CardPaymentCharge {

    private final boolean isDebited;

    public CardPaymentCharge(boolean isDebited) {
        this.isDebited = isDebited;
    }

    public boolean isDebited() {
        return isDebited;
    }

    @Override
    public String toString() {
        return "CardPaymentCharge{" +
                "isDebited=" + isDebited +
                '}';
    }

}
