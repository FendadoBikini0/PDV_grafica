package com.example.demo.controller;

import com.example.demo.entity.Produto;
import com.example.demo.repository.ItemPedidoRepository;
import com.example.demo.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoControllerTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ItemPedidoRepository itemPedidoRepository;

    @InjectMocks
    private ProdutoController produtoController;

    @Test
    void desativarProdutoInexistenteInformaErroSemConsultarPedidos() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());
        RedirectAttributesModelMap redirectAttributes =
                new RedirectAttributesModelMap();

        String view = produtoController.desativar(1L, redirectAttributes);

        assertEquals("redirect:/produtos", view);
        assertEquals(
                "Produto não encontrado.",
                redirectAttributes.getFlashAttributes().get("erro"));
        verify(itemPedidoRepository, never()).existeEmPedidoNaoEntregue(1L);
        verify(produtoRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void desativarProdutoComPedidoNaoEntregueInformaErro() {
        Produto produto = novoProdutoAtivo();
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(itemPedidoRepository.existeEmPedidoNaoEntregue(1L)).thenReturn(true);
        RedirectAttributesModelMap redirectAttributes =
                new RedirectAttributesModelMap();

        String view = produtoController.desativar(1L, redirectAttributes);

        assertEquals("redirect:/produtos", view);
        assertEquals(
                "Não é possível desativar este produto, pois ele está vinculado a um pedido que ainda não foi entregue.",
                redirectAttributes.getFlashAttributes().get("erro"));
        verify(produtoRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void desativarProdutoSemPedidoNaoEntreguePreservaRegistro() {
        Produto produto = novoProdutoAtivo();
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(itemPedidoRepository.existeEmPedidoNaoEntregue(1L)).thenReturn(false);
        RedirectAttributesModelMap redirectAttributes =
                new RedirectAttributesModelMap();

        String view = produtoController.desativar(1L, redirectAttributes);

        assertEquals("redirect:/produtos", view);
        assertEquals(
                "Produto desativado com sucesso.",
                redirectAttributes.getFlashAttributes().get("sucesso"));
        verify(produtoRepository).save(argThat(salvo -> salvo.getId().equals(1L)
                && !salvo.isAtivo()));
    }

    @Test
    void produtoJaDesativadoNaoEProcessadoNovamente() {
        Produto produto = novoProdutoAtivo();
        produto.setAtivo(false);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        RedirectAttributesModelMap redirectAttributes =
                new RedirectAttributesModelMap();

        String view = produtoController.desativar(1L, redirectAttributes);

        assertEquals("redirect:/produtos", view);
        assertEquals(
                "Este produto já está desativado.",
                redirectAttributes.getFlashAttributes().get("erro"));
        verify(itemPedidoRepository, never()).existeEmPedidoNaoEntregue(1L);
        verify(produtoRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    private Produto novoProdutoAtivo() {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto");
        return produto;
    }
}
