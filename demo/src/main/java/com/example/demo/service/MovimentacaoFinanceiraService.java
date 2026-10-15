package com.example.demo.service;

import java.math.BigDecimal;
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
}
