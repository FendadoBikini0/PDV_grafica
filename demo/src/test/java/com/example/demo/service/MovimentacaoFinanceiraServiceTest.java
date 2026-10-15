package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.example.demo.entity.MovimentacaoFinanceira;
import com.example.demo.entity.TipoMovimentacao;
import com.example.demo.repository.MovimentacaoFinanceiraRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovimentacaoFinanceiraServiceTest {

	@Mock
	private MovimentacaoFinanceiraRepository repository;

	@InjectMocks
	private MovimentacaoFinanceiraService service;

	@Test
	void registraMovimentacaoManualComValoresNormalizadosEDefineData() {
		MovimentacaoFinanceira movimentacao = criarMovimentacao();
		movimentacao.setDescricao("  Venda manual  ");
		movimentacao.setCategoria("  Vendas  ");
		movimentacao.setId(12L);
		when(repository.save(movimentacao)).thenReturn(movimentacao);

		MovimentacaoFinanceira resultado = service.registrarManual(movimentacao);

		assertSame(movimentacao, resultado);
		assertEquals("Venda manual", movimentacao.getDescricao());
		assertEquals("Vendas", movimentacao.getCategoria());
		assertEquals("MANUAL", movimentacao.getOrigem());
		assertNull(movimentacao.getReferenciaId());
		assertNull(movimentacao.getId());
		org.junit.jupiter.api.Assertions.assertNotNull(movimentacao.getData());
		verify(repository).save(movimentacao);
	}

	@Test
	void preservaDataInformada() {
		LocalDateTime data = LocalDateTime.of(2026, 10, 13, 12, 0);
		MovimentacaoFinanceira movimentacao = criarMovimentacao();
		movimentacao.setData(data);

		service.registrarManual(movimentacao);

		assertEquals(data, movimentacao.getData());
		verify(repository).save(movimentacao);
	}

	@Test
	void rejeitaMovimentacaoNula() {
		assertThrows(IllegalArgumentException.class, () -> service.registrarManual(null));

		verify(repository, never()).save(any(MovimentacaoFinanceira.class));
	}

	@Test
	void rejeitaDescricaoAusenteOuEmBranco() {
		MovimentacaoFinanceira semDescricao = criarMovimentacao();
		semDescricao.setDescricao("  ");
		assertThrows(IllegalArgumentException.class, () -> service.registrarManual(semDescricao));

		MovimentacaoFinanceira descricaoNula = criarMovimentacao();
		descricaoNula.setDescricao(null);
		assertThrows(IllegalArgumentException.class, () -> service.registrarManual(descricaoNula));

		verify(repository, never()).save(any(MovimentacaoFinanceira.class));
	}

	@Test
	void rejeitaCategoriaAusenteOuEmBranco() {
		MovimentacaoFinanceira semCategoria = criarMovimentacao();
		semCategoria.setCategoria("  ");
		assertThrows(IllegalArgumentException.class, () -> service.registrarManual(semCategoria));

		MovimentacaoFinanceira categoriaNula = criarMovimentacao();
		categoriaNula.setCategoria(null);
		assertThrows(IllegalArgumentException.class, () -> service.registrarManual(categoriaNula));

		verify(repository, never()).save(any(MovimentacaoFinanceira.class));
	}

	@Test
	void rejeitaTipoAusente() {
		MovimentacaoFinanceira movimentacao = criarMovimentacao();
		movimentacao.setTipo(null);

		assertThrows(IllegalArgumentException.class, () -> service.registrarManual(movimentacao));

		verify(repository, never()).save(any(MovimentacaoFinanceira.class));
	}

	@Test
	void rejeitaValorNuloZeroOuNegativo() {
		MovimentacaoFinanceira valorNulo = criarMovimentacao();
		valorNulo.setValor(null);
		assertThrows(IllegalArgumentException.class, () -> service.registrarManual(valorNulo));

		MovimentacaoFinanceira valorZero = criarMovimentacao();
		valorZero.setValor(BigDecimal.ZERO);
		assertThrows(IllegalArgumentException.class, () -> service.registrarManual(valorZero));

		MovimentacaoFinanceira valorNegativo = criarMovimentacao();
		valorNegativo.setValor(new BigDecimal("-0.01"));
		assertThrows(IllegalArgumentException.class, () -> service.registrarManual(valorNegativo));

		verify(repository, never()).save(any(MovimentacaoFinanceira.class));
	}

	@Test
	void rejeitaOrigemAutomaticaOuReferencia() {
		MovimentacaoFinanceira origemAutomatica = criarMovimentacao();
		origemAutomatica.setOrigem("VENDA");
		assertThrows(
				IllegalArgumentException.class,
				() -> service.registrarManual(origemAutomatica));

		MovimentacaoFinanceira comReferencia = criarMovimentacao();
		comReferencia.setReferenciaId(7L);
		assertThrows(
				IllegalArgumentException.class,
				() -> service.registrarManual(comReferencia));

		verify(repository, never()).save(any(MovimentacaoFinanceira.class));
	}

	@Test
	void listaMovimentacoesDaMaisRecenteParaMaisAntiga() {
		List<MovimentacaoFinanceira> movimentacoes = List.of(criarMovimentacao());
		when(repository.findAll(any(Sort.class))).thenReturn(movimentacoes);

		assertSame(movimentacoes, service.listarTodas());

		verify(repository).findAll(Sort.by(Sort.Direction.DESC, "data"));
	}

	@Test
	void listaMovimentacoesDoPeriodoIncluindoTodoODiaFinal() {
		LocalDate dataInicial = LocalDate.of(2026, 10, 1);
		LocalDate dataFinal = LocalDate.of(2026, 10, 31);
		List<MovimentacaoFinanceira> movimentacoes = List.of(criarMovimentacao());
		LocalDateTime fimExclusivo = dataFinal.plusDays(1).atStartOfDay();
		when(repository.findByDataGreaterThanEqualAndDataLessThanOrderByDataDesc(
				dataInicial.atStartOfDay(), fimExclusivo))
				.thenReturn(movimentacoes);

		assertSame(movimentacoes, service.listarPorPeriodo(dataInicial, dataFinal));

		verify(repository)
				.findByDataGreaterThanEqualAndDataLessThanOrderByDataDesc(
						dataInicial.atStartOfDay(), fimExclusivo);
	}

	@Test
	void rejeitaPeriodoNuloOuComDataInicialPosterior() {
		assertThrows(
				IllegalArgumentException.class,
				() -> service.listarPorPeriodo(null, LocalDate.now()));
		assertThrows(
				IllegalArgumentException.class,
				() -> service.totalEntradasNoPeriodo(
						LocalDate.of(2026, 10, 2), LocalDate.of(2026, 10, 1)));
		assertThrows(
				IllegalArgumentException.class,
				() -> service.totalSaidasNoPeriodo(
						LocalDate.of(2026, 10, 1), null));

		verifyNoInteractions(repository);
	}

	@Test
	void calculaTotaisDoPeriodoComLimiteFinalExclusivo() {
		LocalDate dataInicial = LocalDate.of(2026, 10, 1);
		LocalDate dataFinal = LocalDate.of(2026, 10, 31);
		when(repository.somarPorTipoNoPeriodo(
				TipoMovimentacao.ENTRADA,
				dataInicial.atStartOfDay(),
				dataFinal.plusDays(1).atStartOfDay()))
				.thenReturn(new BigDecimal("90.00"));
		when(repository.somarPorTipoNoPeriodo(
				TipoMovimentacao.SAIDA,
				dataInicial.atStartOfDay(),
				dataFinal.plusDays(1).atStartOfDay()))
				.thenReturn(new BigDecimal("30.00"));

		assertEquals(
				new BigDecimal("90.00"),
				service.totalEntradasNoPeriodo(dataInicial, dataFinal));
		assertEquals(
				new BigDecimal("30.00"),
				service.totalSaidasNoPeriodo(dataInicial, dataFinal));
	}

	@Test
	void listaSomenteAsDezMovimentacoesMaisRecentesSemApagarAsDemais() {
		List<MovimentacaoFinanceira> movimentacoes =
				java.util.stream.IntStream.range(0, 12)
						.mapToObj(indice -> criarMovimentacao())
						.toList();
		when(repository.findAll(any(Sort.class))).thenReturn(movimentacoes);

		List<MovimentacaoFinanceira> recentes = service.listarRecentes();

		assertEquals(10, recentes.size());
		assertEquals(movimentacoes.subList(0, 10), recentes);
		verify(repository).findAll(Sort.by(Sort.Direction.DESC, "data"));
	}

	@Test
	void calculaEntradasSaidasESaldoAtual() {
		when(repository.somarPorTipo(TipoMovimentacao.ENTRADA))
				.thenReturn(new BigDecimal("125.50"));
		when(repository.somarPorTipo(TipoMovimentacao.SAIDA))
				.thenReturn(new BigDecimal("40.25"));

		assertEquals(new BigDecimal("125.50"), service.totalEntradas());
		assertEquals(new BigDecimal("40.25"), service.totalSaidas());
		assertEquals(new BigDecimal("85.25"), service.saldoAtual());

		verify(repository, org.mockito.Mockito.times(2))
				.somarPorTipo(TipoMovimentacao.ENTRADA);
		verify(repository, org.mockito.Mockito.times(2))
				.somarPorTipo(TipoMovimentacao.SAIDA);
	}

	private MovimentacaoFinanceira criarMovimentacao() {
		MovimentacaoFinanceira movimentacao = new MovimentacaoFinanceira();
		movimentacao.setDescricao("Venda manual");
		movimentacao.setCategoria("Vendas");
		movimentacao.setTipo(TipoMovimentacao.ENTRADA);
		movimentacao.setValor(new BigDecimal("10.00"));
		return movimentacao;
	}
}
