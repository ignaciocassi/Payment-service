package com.ignaciocassi.utils;

import java.util.function.Predicate;

public class EmailAddressValidator implements Predicate<String> {

    @Override
    public boolean test(String s) {
        return s.matches("^(.+)@(.+)$");
    }

}
