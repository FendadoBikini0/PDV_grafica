package com.example.demo.controller;

import com.example.demo.dto.UsuarioForm;
import com.example.demo.entity.Perfil;
import com.example.demo.repository.FuncionarioRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.UsuarioService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final FuncionarioRepository funcionarioRepository;

    public UsuarioController(
            UsuarioService usuarioService,
            UsuarioRepository usuarioRepository,
            FuncionarioRepository funcionarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    @GetMapping
    public String listar(Model model) {
        carregarPagina(model, new UsuarioForm());
        return "usuarios";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        carregarPagina(model, new UsuarioForm());
        return "usuarios";
    }

    @PostMapping("/salvar")
    public String salvar(
            @ModelAttribute("usuarioForm") UsuarioForm form,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            usuarioService.cadastrar(
                    form.getLogin(),
                    form.getSenha(),
                    form.getFuncionarioId(),
                    form.getPerfil());

            redirectAttributes.addFlashAttribute(
                    "sucesso", "Usuário cadastrado com sucesso.");

            return "redirect:/usuarios";

        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            carregarPagina(model, form);
            return "usuarios";
        }
    }

    private void carregarPagina(Model model, UsuarioForm form) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("usuarioForm", form);
        carregarFuncionarios(model);
    }

    private void carregarFuncionarios(Model model) {
        model.addAttribute(
                "funcionarios", funcionarioRepository.findAll());
        model.addAttribute("perfis", Perfil.values());
    }
}
