package com.example.demo.dto;

import com.example.demo.entity.Perfil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioForm {

    private String login;
    private String senha;
    private Long funcionarioId;
    private Perfil perfil;
}
