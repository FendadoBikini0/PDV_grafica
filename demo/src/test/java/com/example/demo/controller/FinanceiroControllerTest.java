package com.example.demo.controller;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.example.demo.entity.MovimentacaoFinanceira;
import com.example.demo.service.MovimentacaoFinanceiraService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinanceiroControllerTest {

	@Mock
	private MovimentacaoFinanceiraService service;

	@InjectMocks
	private FinanceiroController controller;

	@Test
	void listarPreparaResumoHistoricoEFormulario() {
		List<MovimentacaoFinanceira> movimentacoes = List.of(new MovimentacaoFinanceira());
		when(service.listarTodas()).thenReturn(movimentacoes);
		when(service.totalEntradas()).thenReturn(new BigDecimal("100.00"));
		when(service.totalSaidas()).thenReturn(new BigDecimal("25.00"));
		when(service.saldoAtual()).thenReturn(new BigDecimal("75.00"));
		ExtendedModelMap model = new ExtendedModelMap();

		String view = controller.listar(model);

		assertEquals("financeiro", view);
		assertInstanceOf(MovimentacaoFinanceira.class, model.getAttribute("movimentacao"));
		assertEquals(movimentacoes, model.getAttribute("movimentacoes"));
		assertEquals(new BigDecimal("100.00"), model.getAttribute("totalEntradas"));
		assertEquals(new BigDecimal("25.00"), model.getAttribute("totalSaidas"));
		assertEquals(new BigDecimal("75.00"), model.getAttribute("saldoAtual"));
	}

	@Test
	void salvarMovimentacaoRedirecionaComMensagemDeSucesso() {
		MovimentacaoFinanceira movimentacao = new MovimentacaoFinanceira();
		RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

		String view = controller.salvar(movimentacao, redirectAttributes);

		assertEquals("redirect:/financeiro", view);
		assertEquals(
				"Movimentação registrada com sucesso.",
				redirectAttributes.getFlashAttributes().get("sucesso"));
		verify(service).registrarManual(movimentacao);
	}

	@Test
	void salvarMovimentacaoInvalidaRedirecionaComMensagemDeErro() {
		MovimentacaoFinanceira movimentacao = new MovimentacaoFinanceira();
		RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
		when(service.registrarManual(movimentacao))
				.thenThrow(new IllegalArgumentException("A descrição é obrigatória."));

		String view = controller.salvar(movimentacao, redirectAttributes);

		assertEquals("redirect:/financeiro", view);
		assertEquals(
				"A descrição é obrigatória.",
				redirectAttributes.getFlashAttributes().get("erro"));
	}
}
