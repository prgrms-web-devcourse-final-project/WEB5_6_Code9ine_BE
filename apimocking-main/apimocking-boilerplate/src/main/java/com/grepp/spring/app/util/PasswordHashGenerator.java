package com.grepp.spring.app.util;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String raw = "test";
        String hash = encoder.encode(raw);
        System.out.println("원본: " + raw);
        System.out.println("bcrypt 해시: " + hash);
    }
} 