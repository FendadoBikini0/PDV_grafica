package com.example.demo.config;

import com.example.demo.service.AdminBootstrapService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner inicializarAdministrador(
            AdminBootstrapService adminBootstrapService) {

        return args -> {
            String login = System.getenv("ADMIN_LOGIN");
            String senha = System.getenv("ADMIN_PASSWORD");
            String nome = System.getenv("ADMIN_NOME");
            String email = System.getenv("ADMIN_EMAIL");

            if (login == null || senha == null
                    || nome == null || email == null) {
                return;
            }

            adminBootstrapService.criarAdministrador(
                    login, senha, nome, email
            );
        };
    }
}
