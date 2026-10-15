package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.MovimentacaoFinanceira;
import com.example.demo.entity.TipoMovimentacao;
import com.example.demo.repository.MovimentacaoFinanceiraRepository;

@Service
public class MovimentacaoFinanceiraService {

	private final MovimentacaoFinanceiraRepository repository;

	public MovimentacaoFinanceiraService(MovimentacaoFinanceiraRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public MovimentacaoFinanceira registrarManual(MovimentacaoFinanceira movimentacao) {
		if (movimentacao == null) {
			throw new IllegalArgumentException("A movimentação é obrigatória.");
		}

		if (movimentacao.getDescricao() == null || movimentacao.getDescricao().isBlank()) {
			throw new IllegalArgumentException("A descrição é obrigatória.");
		}

		if (movimentacao.getCategoria() == null || movimentacao.getCategoria().isBlank()) {
			throw new IllegalArgumentException("A categoria é obrigatória.");
		}

		if (movimentacao.getTipo() == null) {
			throw new IllegalArgumentException("Selecione entrada ou saída.");
		}

		if (movimentacao.getValor() == null
				|| movimentacao.getValor().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("O valor deve ser maior que zero.");
		}

		if (movimentacao.getOrigem() != null
				&& !"MANUAL".equals(movimentacao.getOrigem())) {
			throw new IllegalArgumentException(
					"Este método aceita apenas movimentações manuais.");
		}

		if (movimentacao.getReferenciaId() != null) {
			throw new IllegalArgumentException(
					"Movimentações manuais não devem ter referência automática.");
		}

		movimentacao.setDescricao(movimentacao.getDescricao().trim());
		movimentacao.setCategoria(movimentacao.getCategoria().trim());
		movimentacao.setOrigem("MANUAL");
		if (movimentacao.getData() == null) {
			movimentacao.setData(LocalDateTime.now());
		}
		movimentacao.setId(null);

		return repository.save(movimentacao);
	}

	@Transactional(readOnly = true)
	public List<MovimentacaoFinanceira> listarTodas() {
		return repository.findAll(Sort.by(Sort.Direction.DESC, "data"));
	}

	@Transactional(readOnly = true)
	public List<MovimentacaoFinanceira> listarRecentes() {
		return repository.findAll(Sort.by(Sort.Direction.DESC, "data"))
				.stream()
				.limit(10)
				.toList();
	}

	@Transactional(readOnly = true)
	public BigDecimal totalEntradas() {
		return repository.somarPorTipo(TipoMovimentacao.ENTRADA);
	}

	@Transactional(readOnly = true)
	public BigDecimal totalSaidas() {
		return repository.somarPorTipo(TipoMovimentacao.SAIDA);
	}

	@Transactional(readOnly = true)
	public BigDecimal saldoAtual() {
		return totalEntradas().subtract(totalSaidas());
	}

	@Transactional(readOnly = true)
	public List<MovimentacaoFinanceira> listarPorPeriodo(
			LocalDate dataInicial,
			LocalDate dataFinal) {
		validarPeriodo(dataInicial, dataFinal);
		LocalDateTime inicio = dataInicial.atStartOfDay();
		LocalDateTime fimExclusivo = dataFinal.plusDays(1).atStartOfDay();
		return repository.findByDataGreaterThanEqualAndDataLessThanOrderByDataDesc(
				inicio, fimExclusivo);
	}

	@Transactional(readOnly = true)
	public BigDecimal totalEntradasNoPeriodo(
			LocalDate dataInicial,
			LocalDate dataFinal) {
		validarPeriodo(dataInicial, dataFinal);
		return repository.somarPorTipoNoPeriodo(
				TipoMovimentacao.ENTRADA,
				dataInicial.atStartOfDay(),
				dataFinal.plusDays(1).atStartOfDay());
	}

	@Transactional(readOnly = true)
	public BigDecimal totalSaidasNoPeriodo(
			LocalDate dataInicial,
			LocalDate dataFinal) {
		validarPeriodo(dataInicial, dataFinal);
		return repository.somarPorTipoNoPeriodo(
				TipoMovimentacao.SAIDA,
				dataInicial.atStartOfDay(),
				dataFinal.plusDays(1).atStartOfDay());
	}

	private void validarPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
		if (dataInicial == null || dataFinal == null) {
			throw new IllegalArgumentException(
					"Informe a data inicial e a data final.");
		}
		if (dataInicial.isAfter(dataFinal)) {
			throw new IllegalArgumentException(
					"A data inicial não pode ser posterior à data final.");
		}
	}
}
