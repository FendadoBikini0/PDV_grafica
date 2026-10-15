package com.example.demo.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

	List<MovimentacaoFinanceira>
			findByDataGreaterThanEqualAndDataLessThanOrderByDataDesc(
					LocalDateTime inicio,
					LocalDateTime fim);

	List<MovimentacaoFinanceira> findByDataBetweenOrderByDataDesc(
			LocalDateTime inicio,
			LocalDateTime fim);

	@Query("""
		SELECT COALESCE(SUM(m.valor), 0)
		FROM MovimentacaoFinanceira m
		WHERE m.tipo = :tipo
		  AND m.data >= :inicio
		  AND m.data < :fim
		""")
	BigDecimal somarPorTipoNoPeriodo(
			@Param("tipo") TipoMovimentacao tipo,
			@Param("inicio") LocalDateTime inicio,
			@Param("fim") LocalDateTime fim);
}
