package com.example.demo.repository;

import com.example.demo.entity.Cliente;
import com.example.demo.entity.Funcionario;
import com.example.demo.entity.ItemPedido;
import com.example.demo.entity.Pedido;
import com.example.demo.entity.Produto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemPedidoRepositoryTest {

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Test
    void bloqueiaProdutoComPedidoNaoEntregueEPermiteSemVinculosEntregues() {
        Produto pedidoAberto = salvarProduto("ABERTO");
        Produto pedidoEmProducao = salvarProduto("EM-PRODUCAO");
        Produto pedidoEntregue = salvarProduto("ENTREGUE");
        Produto semPedidos = salvarProduto("SEM-PEDIDOS");

        salvarPedidoComProduto(pedidoAberto, "ABERTO");
        salvarPedidoComProduto(pedidoEmProducao, "EM PRODUÇÃO");
        salvarPedidoComProduto(pedidoEntregue, "ENTREGUE");

        assertTrue(itemPedidoRepository.existeEmPedidoNaoEntregue(pedidoAberto.getId()));
        assertTrue(itemPedidoRepository.existeEmPedidoNaoEntregue(pedidoEmProducao.getId()));
        assertFalse(itemPedidoRepository.existeEmPedidoNaoEntregue(pedidoEntregue.getId()));
        assertFalse(itemPedidoRepository.existeEmPedidoNaoEntregue(semPedidos.getId()));
    }

    @Test
    void permiteReutilizarCodigoDeProdutoDesativado() {
        String codigo = "REUSO-" + UUID.randomUUID();
        Produto produtoDesativado = salvarProduto(codigo);
        produtoDesativado.setAtivo(false);
        produtoRepository.saveAndFlush(produtoDesativado);

        assertFalse(produtoRepository.existsByCodigoAndAtivoTrue(codigo));

        Produto produtoAtivo = salvarProduto(codigo);

        assertTrue(produtoRepository.existsByCodigoAndAtivoTrue(codigo));
        assertFalse(produtoRepository.existsByCodigoAndAtivoTrueAndIdNot(
                codigo, produtoAtivo.getId()));
        assertTrue(produtoRepository.existsByCodigoAndAtivoTrueAndIdNot(
                codigo, produtoDesativado.getId()));
    }

    private Produto salvarProduto(String codigo) {
        Produto produto = new Produto();
        produto.setNome("Produto " + codigo);
        produto.setCodigo(codigo);
        produto.setPreco(BigDecimal.ONE);
        produto.setEstoque(10);
        return produtoRepository.saveAndFlush(produto);
    }

    private void salvarPedidoComProduto(Produto produto, String status) {
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente " + status);
        cliente.setEmail(status.toLowerCase() + "@example.com");
        clienteRepository.save(cliente);

        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Funcionário " + status);
        funcionario.setEmail("funcionario-" + status.toLowerCase() + "@example.com");
        funcionarioRepository.save(funcionario);

        Pedido pedido = new Pedido();
        pedido.setNumero("PED-" + status);
        pedido.setData(LocalDateTime.now());
        pedido.setStatus(status);
        pedido.setCliente(cliente);
        pedido.setResponsavel(funcionario);

        ItemPedido item = new ItemPedido();
        item.setProduto(produto);
        item.setQuantidade(1);
        item.setPrecoUnitario(BigDecimal.ONE);
        pedido.adicionarItem(item);

        pedidoRepository.saveAndFlush(pedido);
    }
}
