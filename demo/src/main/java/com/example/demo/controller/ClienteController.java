package com.example.demo.controller;

import com.example.demo.entity.Cliente;
import com.example.demo.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ClienteController {

    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @GetMapping("/clientes")
    public String listar(@RequestParam(required = false) Long editar, Model model) {
        Cliente cliente = editar == null
                ? new Cliente()
                : clienteRepository.findById(editar)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("cliente", cliente);
        return "clientes";
    }

    @PostMapping("/clientes/salvar")
    public String salvar(@ModelAttribute("cliente") Cliente cliente, Model model) {
        if (cliente.getNome() == null || cliente.getNome().isBlank()
                || cliente.getEmail() == null || cliente.getEmail().isBlank()) {
            model.addAttribute("clientes", clienteRepository.findAll());
            model.addAttribute("erro", "Preencha o nome e o e-mail do cliente.");
            return "clientes";
        }

        cliente.setNome(cliente.getNome().trim());
        cliente.setEmail(cliente.getEmail().trim());
        if (cliente.getNumero() != null) {
            cliente.setNumero(cliente.getNumero().trim());
        }
        clienteRepository.save(cliente);
        return "redirect:/clientes";
    }
}
