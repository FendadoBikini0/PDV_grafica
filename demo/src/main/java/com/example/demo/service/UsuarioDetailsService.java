package com.example.demo.service;

import com.example.demo.repository.UsuarioRepository;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login)
            throws UsernameNotFoundException {

        var usuario = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado."));

        return User.withUsername(usuario.getLogin())
                .password(usuario.getSenha())
                .authorities(List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + usuario.getPerfil().name())
                ))
                .build();
    }
}
