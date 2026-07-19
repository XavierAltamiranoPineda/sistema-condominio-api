package com.residencial;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {

    @Test
    void generarPassword() {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder(12);

        String hash = encoder.encode("Admin2024!");

        System.out.println(hash);
    }
}