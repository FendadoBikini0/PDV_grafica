package com.example.demo.service;

import com.example.demo.entity.Funcionario;
import com.example.demo.entity.Perfil;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.FuncionarioRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminBootstrapService {

    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminBootstrapService(
            FuncionarioRepository funcionarioRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.funcionarioRepository = funcionarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void criarAdministrador(
            String login, String senha, String nome, String email) {

        if (usuarioRepository.count() > 0) {
            return;
        }

        Funcionario funcionario = new Funcionario();
        funcionario.setNome(nome);
        funcionario.setEmail(email);
        funcionario = funcionarioRepository.save(funcionario);

        Usuario usuario = new Usuario();
        usuario.setLogin(login);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setPerfil(Perfil.ADMINISTRADOR);
        usuario.setFuncionario(funcionario);

        usuarioRepository.save(usuario);
    }
}
