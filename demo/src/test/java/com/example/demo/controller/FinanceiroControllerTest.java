package com.example.demo.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import static org.mockito.Mockito.never;
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
		when(service.listarRecentes()).thenReturn(movimentacoes);
		when(service.totalEntradas()).thenReturn(new BigDecimal("100.00"));
		when(service.totalSaidas()).thenReturn(new BigDecimal("25.00"));
		when(service.saldoAtual()).thenReturn(new BigDecimal("75.00"));
		ExtendedModelMap model = new ExtendedModelMap();

		String view = controller.listar(null, null, model);

		assertEquals("financeiro", view);
		assertInstanceOf(MovimentacaoFinanceira.class, model.getAttribute("movimentacao"));
		assertEquals(movimentacoes, model.getAttribute("movimentacoes"));
		assertEquals(new BigDecimal("100.00"), model.getAttribute("totalEntradas"));
		assertEquals(new BigDecimal("25.00"), model.getAttribute("totalSaidas"));
		assertEquals(new BigDecimal("75.00"), model.getAttribute("saldoAtual"));
		assertEquals(false, model.getAttribute("relatorioGerado"));
	}

	@Test
	void geraRelatorioComTotaisESaldoDoPeriodo() {
		LocalDate inicio = LocalDate.of(2026, 10, 1);
		LocalDate fim = LocalDate.of(2026, 10, 31);
		List<MovimentacaoFinanceira> periodo = List.of(new MovimentacaoFinanceira());
		when(service.listarRecentes()).thenReturn(List.of());
		when(service.totalEntradas()).thenReturn(new BigDecimal("500.00"));
		when(service.totalSaidas()).thenReturn(new BigDecimal("100.00"));
		when(service.saldoAtual()).thenReturn(new BigDecimal("400.00"));
		when(service.listarPorPeriodo(inicio, fim)).thenReturn(periodo);
		when(service.totalEntradasNoPeriodo(inicio, fim))
				.thenReturn(new BigDecimal("80.00"));
		when(service.totalSaidasNoPeriodo(inicio, fim))
				.thenReturn(new BigDecimal("30.00"));
		ExtendedModelMap model = new ExtendedModelMap();

		assertEquals("financeiro", controller.listar(inicio, fim, model));

		assertEquals(true, model.getAttribute("relatorioGerado"));
		assertEquals(periodo, model.getAttribute("movimentacoesPeriodo"));
		assertEquals(new BigDecimal("80.00"), model.getAttribute("entradasPeriodo"));
		assertEquals(new BigDecimal("30.00"), model.getAttribute("saidasPeriodo"));
		assertEquals(new BigDecimal("50.00"), model.getAttribute("saldoPeriodo"));
		assertEquals(new BigDecimal("400.00"), model.getAttribute("saldoAtual"));
	}

	@Test
	void informaErroQuandoUmaDataDoRelatorioEstaAusente() {
		when(service.listarRecentes()).thenReturn(List.of());
		when(service.totalEntradas()).thenReturn(BigDecimal.ZERO);
		when(service.totalSaidas()).thenReturn(BigDecimal.ZERO);
		when(service.saldoAtual()).thenReturn(BigDecimal.ZERO);
		ExtendedModelMap model = new ExtendedModelMap();

		assertEquals(
				"financeiro",
				controller.listar(LocalDate.of(2026, 10, 1), null, model));

		assertEquals(
				"Informe a data inicial e a data final.",
				model.getAttribute("erroRelatorio"));
		assertEquals(false, model.getAttribute("relatorioGerado"));
		verify(service, never()).listarPorPeriodo(
				LocalDate.of(2026, 10, 1), null);
		verify(service, never()).totalEntradasNoPeriodo(
				LocalDate.of(2026, 10, 1), null);
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
