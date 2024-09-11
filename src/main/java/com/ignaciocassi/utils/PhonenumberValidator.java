package com.ignaciocassi.utils;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class PhonenumberValidator implements Predicate<String> {
    @Override
    public boolean test(String s) {
        return s.length() == 13
                && s.startsWith("+")
                && s.substring(1, 13).matches("\\d{12}");
    }
}
