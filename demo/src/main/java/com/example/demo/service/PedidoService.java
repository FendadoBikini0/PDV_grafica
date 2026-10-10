package com.example.demo.service;

import com.example.demo.entity.ItemPedido;
import com.example.demo.entity.Pedido;
import com.example.demo.entity.Produto;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ProdutoRepository;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public boolean numeroExiste(String numero) {
        return pedidoRepository.existsByNumero(numero);
    }

    @Transactional
    public Pedido criarPedido(Pedido pedido) {
        if (pedido.getNumero() == null || pedido.getNumero().isBlank()) {
            throw new IllegalArgumentException("O número do pedido é obrigatório.");
        }

        if (numeroExiste(pedido.getNumero())) {
            throw new IllegalArgumentException("Já existe um pedido com esse número.");
        }

        if (pedido.getCliente() == null || pedido.getResponsavel() == null) {
            throw new IllegalArgumentException(
                "O cliente e o funcionário responsável são obrigatórios."
            );
        }

        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new IllegalArgumentException("O pedido precisa ter pelo menos um item.");
        }

        for (ItemPedido item : pedido.getItens()) {
            if (item == null) {
                throw new IllegalArgumentException("Os dados do item são inválidos.");
            }

            if ((item.getProduto() == null) == (item.getServico() == null)) {
                throw new IllegalArgumentException(
                    "Cada item deve estar associado a um produto ou a um serviço, mas não aos dois."
                );
            }

            if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
                throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
            }

            if (item.getPrecoUnitario() == null
                    || item.getPrecoUnitario().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(
                    "O preço unitário deve ser informado e não pode ser negativo."
                );
            }

            item.setPedido(pedido);
        }

        pedido.calcularTotal();

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido entregarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Pedido não encontrado."));

        return entregarPedido(pedido);
    }

    @Transactional
    public Pedido avancarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Pedido não encontrado."));

        String status = pedido.getStatus();
        if (status == null) {
            throw new IllegalArgumentException("O status do pedido é inválido.");
        }

        switch (status.toUpperCase(Locale.ROOT)) {
            case "ABERTO" -> pedido.setStatus("EM PRODUÇÃO");
            case "EM PRODUÇÃO" -> pedido.setStatus("PRONTO");
            case "PRONTO" -> {
                return entregarPedido(pedido);
            }
            case "ENTREGUE" -> throw new IllegalArgumentException(
                    "Este pedido já foi entregue.");
            default -> throw new IllegalArgumentException(
                    "O pedido possui um status inválido para avançar.");
        }

        return pedidoRepository.save(pedido);
    }

    private Pedido entregarPedido(Pedido pedido) {
        if ("ENTREGUE".equalsIgnoreCase(pedido.getStatus())) {
            throw new IllegalArgumentException(
                    "Este pedido já foi entregue.");
        }

        if (!"PRONTO".equalsIgnoreCase(pedido.getStatus())) {
            throw new IllegalArgumentException(
                    "Somente pedidos prontos podem ser entregues.");
        }

        Map<Long, Integer> quantidades = new HashMap<>();
        Map<Long, Produto> produtos = new HashMap<>();

        for (ItemPedido item : pedido.getItens()) {
            if (item.getProduto() == null) {
                continue;
            }

            Produto produto = item.getProduto();

            if (produto.getEstoque() == null || produto.getEstoque() < 0) {
                throw new IllegalArgumentException(
                        "Estoque inválido para o produto: " + produto.getNome());
            }
            if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
                throw new IllegalArgumentException(
                        "Quantidade inválida para o produto: " + produto.getNome());
            }

            Long produtoId = produto.getId();
            quantidades.merge(produtoId, item.getQuantidade(), Integer::sum);
            produtos.put(produtoId, produto);
        }

        for (Map.Entry<Long, Integer> entrada : quantidades.entrySet()) {
            Produto produto = produtos.get(entrada.getKey());

            if (produto.getEstoque() < entrada.getValue()) {
                throw new IllegalArgumentException(
                        "Estoque insuficiente para o produto: " + produto.getNome());
            }
        }

        for (Map.Entry<Long, Integer> entrada : quantidades.entrySet()) {
            Produto produto = produtos.get(entrada.getKey());
            produto.setEstoque(produto.getEstoque() - entrada.getValue());
            produtoRepository.save(produto);
        }

        pedido.setStatus("ENTREGUE");
        return pedidoRepository.save(pedido);
    }
}
