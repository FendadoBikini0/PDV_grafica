package com.example.demo.controller;

import com.example.demo.entity.Produto;
import com.example.demo.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ProdutoController {

    private final ProdutoRepository produtoRepository;

    public ProdutoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @GetMapping("/produtos")
    public String listar(@RequestParam(required = false) Long editar, Model model) {
        Produto produto = editar == null
                ? new Produto()
                : produtoRepository.findById(editar)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        model.addAttribute("produtos", produtoRepository.findAll());
        model.addAttribute("produto", produto);
        return "produtos";
    }

    @PostMapping("/produtos/salvar")
    public String salvar(@ModelAttribute("produto") Produto produto, Model model) {
        String erro = validar(produto);
        if (erro != null) {
            model.addAttribute("produtos", produtoRepository.findAll());
            model.addAttribute("erro", erro);
            return "produtos";
        }

        produto.setNome(produto.getNome().trim());
        produto.setCodigo(produto.getCodigo().trim());
        produtoRepository.save(produto);
        return "redirect:/produtos";
    }

    private String validar(Produto produto) {
        if (produto.getNome() == null || produto.getNome().isBlank()
                || produto.getCodigo() == null || produto.getCodigo().isBlank()
                || produto.getPreco() == null || produto.getEstoque() == null) {
            return "Preencha o nome, o código, o preço e o estoque.";
        }
        if (produto.getPreco().signum() < 0) {
            return "O preço não pode ser negativo.";
        }
        if (produto.getEstoque() < 0) {
            return "O estoque não pode ser negativo.";
        }

        String codigo = produto.getCodigo().trim();
        boolean codigoEmUso = produto.getId() == null
                ? produtoRepository.existsByCodigo(codigo)
                : produtoRepository.existsByCodigoAndIdNot(codigo, produto.getId());
        if (codigoEmUso) {
            return "Já existe um produto cadastrado com esse código.";
        }
        return null;
    }
}
