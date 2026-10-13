package com.example.demo.controller;

import com.example.demo.entity.Funcionario;
import com.example.demo.repository.FuncionarioRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FuncionarioController {

    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioRepository usuarioRepository;

    public FuncionarioController(
            FuncionarioRepository funcionarioRepository,
            UsuarioRepository usuarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.usuarioRepository = usuarioRepository;
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

    @PostMapping("/funcionarios/excluir")
    public String excluir(
            @RequestParam Long id,
            RedirectAttributes redirectAttributes) {

        if (!funcionarioRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute(
                    "erro", "Funcionário não encontrado.");
            return "redirect:/funcionarios";
        }

        if (usuarioRepository.existsByFuncionarioId(id)) {
            redirectAttributes.addFlashAttribute(
                    "erro", "Não é possível excluir este funcionário, "
                            + "pois ele está associado a um usuário do sistema.");
            return "redirect:/funcionarios";
        }

        if (funcionarioRepository.possuiPedidos(id)) {
            redirectAttributes.addFlashAttribute(
                    "erro", "Não é possível excluir este funcionário, "
                            + "pois existem pedidos registrados por ele.");
            return "redirect:/funcionarios";
        }

        try {
            funcionarioRepository.deleteById(id);
            redirectAttributes.addFlashAttribute(
                    "sucesso", "Funcionário excluído com sucesso.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute(
                    "erro", "Não foi possível excluir este funcionário "
                            + "porque existem registros vinculados a ele.");
        }

        return "redirect:/funcionarios";
    }
}
