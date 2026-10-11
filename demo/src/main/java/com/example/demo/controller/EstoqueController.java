package com.example.demo.controller;

import com.example.demo.repository.ProdutoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/estoque")
public class EstoqueController {

    private final ProdutoRepository produtoRepository;

    public EstoqueController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @GetMapping
    public String listar(Model model) {
        var produtos = produtoRepository.findAll();

        model.addAttribute("produtos", produtos);
        model.addAttribute("totalProdutos", produtos.size());

        long estoqueBaixo = produtos.stream()
                .filter(p -> p.getEstoque() != null && p.getEstoque() <= 5)
                .count();

        model.addAttribute("estoqueBaixo", estoqueBaixo);

        long semEstoque = produtos.stream()
                .filter(p -> p.getEstoque() == null || p.getEstoque() <= 0)
                .count();

        model.addAttribute("semEstoque", semEstoque);

        return "estoque";
    }
}
