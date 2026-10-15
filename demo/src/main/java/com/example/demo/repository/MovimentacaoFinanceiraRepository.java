package com.example.demo.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.MovimentacaoFinanceira;
import com.example.demo.entity.TipoMovimentacao;

public interface MovimentacaoFinanceiraRepository
		extends JpaRepository<MovimentacaoFinanceira, Long> {

	@Query("""
		SELECT COALESCE(SUM(m.valor), 0)
		FROM MovimentacaoFinanceira m
		WHERE m.tipo = :tipo
		""")
	BigDecimal somarPorTipo(@Param("tipo") TipoMovimentacao tipo);
}
