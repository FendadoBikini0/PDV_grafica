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

	private void salvar(TipoMovimentacao tipo, String valor, String origem, Long referenciaId) {
		movimentacaoFinanceiraRepository.saveAndFlush(criar(tipo, valor, origem, referenciaId));
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
