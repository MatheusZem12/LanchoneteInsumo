package br.com.insumo.lanchonete;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptTest {
    
    @Test
    public void generateBCryptHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "senha123";
        String hash = encoder.encode(password);
        System.out.println("Hash BCrypt para 'senha123': " + hash);
    }
}
