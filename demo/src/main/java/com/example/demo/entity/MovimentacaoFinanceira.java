package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
	name = "movimentacoes_financeiras",
	uniqueConstraints = @UniqueConstraint(
		name = "uk_movimentacao_origem_referencia",
		columnNames = {"origem", "referencia_id"}
	)
)
@Getter
@Setter
@NoArgsConstructor
public class MovimentacaoFinanceira {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 150)
	private String descricao;

	@Column(nullable = false, length = 80)
	private String categoria;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private TipoMovimentacao tipo;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal valor;

	@Column(nullable = false)
	private LocalDateTime data;

	@Column(nullable = false, length = 20)
	private String origem = "MANUAL";

	@Column(name = "referencia_id")
	private Long referenciaId;
}
