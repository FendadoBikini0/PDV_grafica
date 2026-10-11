package com.example.demo.controller;

import com.example.demo.entity.Funcionario;
import com.example.demo.repository.FuncionarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class FuncionarioController {

    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioController(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    @GetMapping("/funcionarios")
    public String listar(
            @RequestParam(required = false) Long editar,
            Model model) {

        Funcionario funcionario = editar == null
                ? new Funcionario()
                : funcionarioRepository.findById(editar)
                    .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Funcionário não encontrado"));

        model.addAttribute("funcionarios", funcionarioRepository.findAll());
        model.addAttribute("funcionario", funcionario);

        return "funcionarios";
    }

    @PostMapping("/funcionarios/salvar")
    public String salvar(
            @ModelAttribute("funcionario") Funcionario funcionario,
            Model model) {

        if (funcionario.getNome() == null
                || funcionario.getNome().isBlank()
                || funcionario.getEmail() == null
                || funcionario.getEmail().isBlank()) {

            model.addAttribute("funcionarios", funcionarioRepository.findAll());
            model.addAttribute("erro",
                    "Preencha o nome e o e-mail do funcionário.");

            return "funcionarios";
        }

        funcionario.setNome(funcionario.getNome().trim());
        funcionario.setEmail(funcionario.getEmail().trim());

        if (funcionario.getNumero() != null) {
            funcionario.setNumero(funcionario.getNumero().trim());
        }

        funcionarioRepository.save(funcionario);

        return "redirect:/funcionarios";
    }
}
