package com.example.demo.controller;

import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.ProdutoRepository;
import com.example.demo.repository.ServicoRepository;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final ServicoRepository servicoRepository;

    public HomeController(ClienteRepository clienteRepository, ProdutoRepository produtoRepository,
            ServicoRepository servicoRepository) {
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.servicoRepository = servicoRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("totalClientes", clienteRepository.count());
        model.addAttribute("totalProdutos", produtoRepository.count());
        model.addAttribute("totalServicos", servicoRepository.count());
        return "index";
    }
}
