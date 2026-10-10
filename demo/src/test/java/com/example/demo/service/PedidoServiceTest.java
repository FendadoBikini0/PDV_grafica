package com.example.demo.service;

import com.example.demo.entity.Cliente;
import com.example.demo.entity.Funcionario;
import com.example.demo.entity.ItemPedido;
import com.example.demo.entity.Pedido;
import com.example.demo.entity.Produto;
import com.example.demo.entity.Servico;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ProdutoRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void deveCriarPedidoECalcularTotal() {
        when(pedidoRepository.existsByNumero("PED-001"))
                .thenReturn(false);

        when(pedidoRepository.save(any(Pedido.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        Pedido pedido = new Pedido();
        pedido.setNumero("PED-001");
        pedido.setCliente(new Cliente());
        pedido.setResponsavel(new Funcionario());

        ItemPedido item = new ItemPedido();
        item.setProduto(new Produto());
        item.setQuantidade(2);
        item.setPrecoUnitario(new BigDecimal("15.00"));

        pedido.adicionarItem(item);

        Pedido resultado = pedidoService.criarPedido(pedido);

        assertEquals(
                new BigDecimal("30.00"),
                resultado.getTotal()
        );

        assertSame(pedido, resultado);
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void deveRejeitarNumeroDuplicado() {
        when(pedidoRepository.existsByNumero("PED-001"))
                .thenReturn(true);

        Pedido pedido = new Pedido();
        pedido.setNumero("PED-001");

        assertThrows(
                IllegalArgumentException.class,
                () -> pedidoService.criarPedido(pedido)
        );

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void deveRejeitarPedidoSemItens() {
        when(pedidoRepository.existsByNumero("PED-002"))
                .thenReturn(false);

        Pedido pedido = new Pedido();
        pedido.setNumero("PED-002");
        pedido.setCliente(new Cliente());
        pedido.setResponsavel(new Funcionario());

        assertThrows(
                IllegalArgumentException.class,
                () -> pedidoService.criarPedido(pedido)
        );

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void deveRejeitarItemSemProdutoOuServico() {
        when(pedidoRepository.existsByNumero("PED-003"))
                .thenReturn(false);

        Pedido pedido = new Pedido();
        pedido.setNumero("PED-003");
        pedido.setCliente(new Cliente());
        pedido.setResponsavel(new Funcionario());

        ItemPedido item = new ItemPedido();
        item.setQuantidade(1);
        item.setPrecoUnitario(BigDecimal.ZERO);
        pedido.adicionarItem(item);

        assertThrows(
                IllegalArgumentException.class,
                () -> pedidoService.criarPedido(pedido)
        );

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void deveBaixarEstoqueAoEntregarPedidoEIgnorarServicos() {
        Produto produto = criarProduto(1L, "Produto A", 5);
        Pedido pedido = criarPedidoPronto(10L);
        pedido.adicionarItem(criarItemProduto(produto, 2));

        ItemPedido itemServico = new ItemPedido();
        itemServico.setServico(new Servico());
        itemServico.setQuantidade(3);
        itemServico.setPrecoUnitario(BigDecimal.ONE);
        pedido.adicionarItem(itemServico);

        when(pedidoRepository.findById(10L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        Pedido resultado = pedidoService.entregarPedido(10L);

        assertSame(pedido, resultado);
        assertEquals("ENTREGUE", pedido.getStatus());
        assertEquals(3, produto.getEstoque());
        verify(produtoRepository).save(produto);
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void deveSomarLinhasDoMesmoProdutoAntesDeValidarEstoque() {
        Produto produto = criarProduto(2L, "Produto B", 5);
        Pedido pedido = criarPedidoPronto(11L);
        pedido.adicionarItem(criarItemProduto(produto, 2));
        pedido.adicionarItem(criarItemProduto(produto, 3));

        when(pedidoRepository.findById(11L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        pedidoService.entregarPedido(11L);

        assertEquals(0, produto.getEstoque());
        verify(produtoRepository).save(produto);
    }

    @Test
    void deveManterEstoqueIntactoSeAlgumProdutoNaoTiverEstoqueSuficiente() {
        Produto produtoDisponivel = criarProduto(3L, "Produto C", 4);
        Produto produtoInsuficiente = criarProduto(4L, "Produto D", 1);
        Pedido pedido = criarPedidoPronto(12L);
        pedido.adicionarItem(criarItemProduto(produtoDisponivel, 2));
        pedido.adicionarItem(criarItemProduto(produtoInsuficiente, 2));

        when(pedidoRepository.findById(12L)).thenReturn(Optional.of(pedido));

        assertThrows(
                IllegalArgumentException.class,
                () -> pedidoService.entregarPedido(12L)
        );

        assertEquals(4, produtoDisponivel.getEstoque());
        assertEquals(1, produtoInsuficiente.getEstoque());
        assertEquals("PRONTO", pedido.getStatus());
        verifyNoInteractions(produtoRepository);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void deveRejeitarSegundaEntregaSemBaixarEstoqueNovamente() {
        Produto produto = criarProduto(5L, "Produto E", 3);
        Pedido pedido = criarPedidoPronto(13L);
        pedido.adicionarItem(criarItemProduto(produto, 1));
        pedido.setStatus("ENTREGUE");

        when(pedidoRepository.findById(13L)).thenReturn(Optional.of(pedido));

        assertThrows(
                IllegalArgumentException.class,
                () -> pedidoService.entregarPedido(13L)
        );

        assertEquals(3, produto.getEstoque());
        verifyNoInteractions(produtoRepository);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    private Produto criarProduto(Long id, String nome, int estoque) {
        Produto produto = new Produto();
        produto.setId(id);
        produto.setNome(nome);
        produto.setEstoque(estoque);
        return produto;
    }

    private Pedido criarPedidoPronto(Long id) {
        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setStatus("PRONTO");
        return pedido;
    }

    private ItemPedido criarItemProduto(Produto produto, int quantidade) {
        ItemPedido item = new ItemPedido();
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(BigDecimal.ONE);
        return item;
    }
}
