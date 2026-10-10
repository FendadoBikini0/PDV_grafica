package com.example.demo.controller;

import com.example.demo.entity.Servico;
import com.example.demo.repository.ServicoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ServicoController {

    private final ServicoRepository servicoRepository;

    public ServicoController(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    @GetMapping("/servicos")
    public String listar(@RequestParam(required = false) Long editar, Model model) {
        Servico servico = editar == null
                ? new Servico()
                : servicoRepository.findById(editar)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado"));

        model.addAttribute("servicos", servicoRepository.findAll());
        model.addAttribute("servico", servico);
        return "servicos";
    }

    @PostMapping("/servicos/salvar")
    public String salvar(@ModelAttribute("servico") Servico servico, Model model) {
        if (servico.getTipo() == null || servico.getTipo().isBlank() || servico.getPreco() == null) {
            model.addAttribute("servicos", servicoRepository.findAll());
            model.addAttribute("erro", "Preencha o tipo e o preço do serviço.");
            return "servicos";
        }
        if (servico.getPreco().signum() < 0) {
            model.addAttribute("servicos", servicoRepository.findAll());
            model.addAttribute("erro", "O preço não pode ser negativo.");
            return "servicos";
        }

        servico.setTipo(servico.getTipo().trim());
        if (servico.getDescricao() != null) {
            servico.setDescricao(servico.getDescricao().trim());
        }
        servicoRepository.save(servico);
        return "redirect:/servicos";
    }
}
