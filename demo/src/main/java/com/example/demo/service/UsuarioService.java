package com.example.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Funcionario;
import com.example.demo.entity.Perfil;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.FuncionarioRepository;
import com.example.demo.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            FuncionarioRepository funcionarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario cadastrar(
            String login,
            String senha,
            Long funcionarioId,
            Perfil perfil) {

        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("O login é obrigatório.");
        }

        login = login.trim();

        if (usuarioRepository.existsByLogin(login)) {
            throw new IllegalArgumentException("Esse login já está cadastrado.");
        }

        if (senha == null || senha.length() < 8) {
            throw new IllegalArgumentException(
                    "A senha deve possuir pelo menos 8 caracteres.");
        }

        if (funcionarioId == null) {
            throw new IllegalArgumentException("Selecione um funcionário.");
        }

        if (perfil == null) {
            throw new IllegalArgumentException("Selecione um perfil de acesso.");
        }

        if (usuarioRepository.existsByFuncionarioId(funcionarioId)) {
            throw new IllegalArgumentException(
                    "Esse funcionário já possui um usuário.");
        }

        Funcionario funcionario = funcionarioRepository
                .findById(funcionarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Funcionário não encontrado."));

        Usuario usuario = new Usuario();
        usuario.setLogin(login);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setFuncionario(funcionario);
        usuario.setPerfil(perfil);

        return usuarioRepository.save(usuario);
    }
}
