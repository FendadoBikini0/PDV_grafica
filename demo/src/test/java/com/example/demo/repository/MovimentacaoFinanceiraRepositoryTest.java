package com.example.demo.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.MovimentacaoFinanceira;
import com.example.demo.entity.TipoMovimentacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MovimentacaoFinanceiraRepositoryTest {

	@Autowired
	private MovimentacaoFinanceiraRepository movimentacaoFinanceiraRepository;

	@Test
	void somaEntradasESaidasSeparadamente() {
		salvar(TipoMovimentacao.ENTRADA, "20.00", "VENDA", 1L);
		salvar(TipoMovimentacao.ENTRADA, "5.50", "MANUAL", null);
		salvar(TipoMovimentacao.SAIDA, "8.00", "COMPRA_ESTOQUE", 2L);

		assertEquals(
			new BigDecimal("25.50"),
			movimentacaoFinanceiraRepository.somarPorTipo(TipoMovimentacao.ENTRADA)
		);
		assertEquals(
			new BigDecimal("8.00"),
			movimentacaoFinanceiraRepository.somarPorTipo(TipoMovimentacao.SAIDA)
		);
	}

	@Test
	void retornaZeroQuandoNaoHaMovimentacoesDoTipo() {
		assertEquals(
			BigDecimal.ZERO,
			movimentacaoFinanceiraRepository.somarPorTipo(TipoMovimentacao.ENTRADA)
		);
	}

	@Test
	void impedeMovimentacoesDuplicadasParaMesmaOrigemEReferencia() {
		salvar(TipoMovimentacao.ENTRADA, "20.00", "VENDA", 42L);
		MovimentacaoFinanceira duplicada = criar(
			TipoMovimentacao.ENTRADA,
			"20.00",
			"VENDA",
			42L
		);

		assertThrows(
			DataIntegrityViolationException.class,
			() -> movimentacaoFinanceiraRepository.saveAndFlush(duplicada)
		);
	}

	@Test
	void listaMovimentacoesNoIntervaloEmOrdemDecrescente() {
		LocalDateTime inicio = LocalDateTime.of(2026, 10, 1, 0, 0);
		LocalDateTime fim = LocalDateTime.of(2026, 10, 31, 23, 59, 59);
		salvar(
				TipoMovimentacao.ENTRADA, "3.00", "MANUAL", null,
				inicio.minusSeconds(1));
		MovimentacaoFinanceira dentroMaisAntiga = salvar(
				TipoMovimentacao.ENTRADA, "10.00", "MANUAL", null,
				inicio.plusDays(2));
		MovimentacaoFinanceira dentroMaisRecente = salvar(
				TipoMovimentacao.SAIDA, "4.00", "MANUAL", null,
				fim.minusDays(2));
		salvar(
				TipoMovimentacao.SAIDA, "7.00", "MANUAL", null,
				fim.plusSeconds(1));

		assertEquals(
				java.util.List.of(dentroMaisRecente.getId(), dentroMaisAntiga.getId()),
				movimentacaoFinanceiraRepository
						.findByDataBetweenOrderByDataDesc(inicio, fim).stream()
						.map(MovimentacaoFinanceira::getId)
						.toList());
	}

	@Test
	void somaValoresPorTipoDentroDoIntervalo() {
		LocalDateTime inicio = LocalDateTime.of(2026, 10, 1, 0, 0);
		LocalDateTime fim = LocalDateTime.of(2026, 10, 31, 23, 59, 59);
		salvar(
				TipoMovimentacao.ENTRADA, "100.00", "MANUAL", null,
				inicio.plusDays(1));
		salvar(
				TipoMovimentacao.ENTRADA, "25.50", "MANUAL", null,
				fim.minusDays(1));
		salvar(
				TipoMovimentacao.SAIDA, "20.00", "MANUAL", null,
				inicio.plusDays(1));
		salvar(
				TipoMovimentacao.ENTRADA, "500.00", "MANUAL", null,
				fim.plusSeconds(1));

		assertEquals(
				new BigDecimal("125.50"),
				movimentacaoFinanceiraRepository.somarPorTipoNoPeriodo(
						TipoMovimentacao.ENTRADA, inicio, fim));
		assertEquals(
				new BigDecimal("20.00"),
				movimentacaoFinanceiraRepository.somarPorTipoNoPeriodo(
						TipoMovimentacao.SAIDA, inicio, fim));
	}

	@Test
	void incluiUltimoDiaDoPeriodoMasExcluiMeiaNoiteDoDiaSeguinte() {
		LocalDateTime inicio = LocalDateTime.of(2026, 10, 31, 0, 0);
		LocalDateTime fimExclusivo = LocalDateTime.of(2026, 11, 1, 0, 0);
		salvar(
				TipoMovimentacao.ENTRADA, "12.00", "MANUAL", null,
				fimExclusivo.minusSeconds(1));
		salvar(
				TipoMovimentacao.ENTRADA, "100.00", "MANUAL", null,
				fimExclusivo);

		assertEquals(
				new BigDecimal("12.00"),
				movimentacaoFinanceiraRepository.somarPorTipoNoPeriodo(
						TipoMovimentacao.ENTRADA, inicio, fimExclusivo));
		assertEquals(
				1,
				movimentacaoFinanceiraRepository
						.findByDataGreaterThanEqualAndDataLessThanOrderByDataDesc(
								inicio, fimExclusivo)
						.size());
	}

	private void salvar(TipoMovimentacao tipo, String valor, String origem, Long referenciaId) {
		salvar(tipo, valor, origem, referenciaId, LocalDateTime.now());
	}

	private MovimentacaoFinanceira salvar(
			TipoMovimentacao tipo,
			String valor,
			String origem,
			Long referenciaId,
			LocalDateTime data) {
		MovimentacaoFinanceira movimentacao = criar(tipo, valor, origem, referenciaId);
		movimentacao.setData(data);
		return movimentacaoFinanceiraRepository.saveAndFlush(movimentacao);
	}

	private MovimentacaoFinanceira criar(
		TipoMovimentacao tipo,
		String valor,
		String origem,
		Long referenciaId
	) {
		MovimentacaoFinanceira movimentacao = new MovimentacaoFinanceira();
		movimentacao.setDescricao("Movimentação de teste");
		movimentacao.setCategoria("Teste");
		movimentacao.setTipo(tipo);
		movimentacao.setValor(new BigDecimal(valor));
		movimentacao.setData(LocalDateTime.now());
		movimentacao.setOrigem(origem);
		movimentacao.setReferenciaId(referenciaId);
		return movimentacao;
	}
}
