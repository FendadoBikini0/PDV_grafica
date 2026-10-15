package com.example.demo.controller;

import com.example.demo.entity.Produto;
import com.example.demo.repository.ItemPedidoRepository;
import com.example.demo.repository.ProdutoRepository;
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
public class ProdutoController {

    private final ProdutoRepository produtoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    public ProdutoController(
            ProdutoRepository produtoRepository,
            ItemPedidoRepository itemPedidoRepository) {
        this.produtoRepository = produtoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    @GetMapping("/produtos")
    public String listar(@RequestParam(required = false) Long editar, Model model) {
        Produto produto = editar == null
                ? new Produto()
                : produtoRepository.findById(editar)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        if (!produto.isAtivo()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado");
        }

        model.addAttribute("produtos", produtoRepository.findByAtivoTrue());
        model.addAttribute("produto", produto);
        return "produtos";
    }

    @PostMapping("/produtos/salvar")
    public String salvar(@ModelAttribute("produto") Produto produto, Model model) {
        Produto produtoExistente = null;
        if (produto.getId() != null) {
            produtoExistente = produtoRepository.findById(produto.getId())
                    .filter(Produto::isAtivo)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Produto não encontrado"));
        }

        String erro = validar(produto);
        if (erro != null) {
            model.addAttribute("produtos", produtoRepository.findByAtivoTrue());
            model.addAttribute("erro", erro);
            return "produtos";
        }

        Produto produtoParaSalvar = produtoExistente == null
                ? produto
                : produtoExistente;
        produtoParaSalvar.setNome(produto.getNome().trim());
        produtoParaSalvar.setCodigo(produto.getCodigo().trim());
        produtoParaSalvar.setPreco(produto.getPreco());
        produtoParaSalvar.setEstoque(produto.getEstoque());
        produtoRepository.save(produtoParaSalvar);
        return "redirect:/produtos";
    }

    @PostMapping("/produtos/desativar")
    public String desativar(
            @RequestParam Long id,
            RedirectAttributes redirectAttributes) {

        Produto produto = produtoRepository.findById(id).orElse(null);
        if (produto == null) {
            redirectAttributes.addFlashAttribute(
                    "erro", "Produto não encontrado.");
            return "redirect:/produtos";
        }

        if (!produto.isAtivo()) {
            redirectAttributes.addFlashAttribute(
                    "erro", "Este produto já está desativado.");
            return "redirect:/produtos";
        }

        if (itemPedidoRepository.existeEmPedidoNaoEntregue(id)) {
            redirectAttributes.addFlashAttribute(
                    "erro", "Não é possível desativar este produto, "
                            + "pois ele está vinculado a um pedido que ainda não foi entregue.");
            return "redirect:/produtos";
        }

        produto.setAtivo(false);
        produtoRepository.save(produto);
        redirectAttributes.addFlashAttribute(
                "sucesso", "Produto desativado com sucesso.");
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
                ? produtoRepository.existsByCodigoAndAtivoTrue(codigo)
                : produtoRepository.existsByCodigoAndAtivoTrueAndIdNot(codigo, produto.getId());
        if (codigoEmUso) {
            return "Já existe um produto cadastrado com esse código.";
        }
        return null;
    }
}
