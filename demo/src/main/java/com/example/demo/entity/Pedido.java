package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String numero;

	@Column(nullable = false)
	private LocalDateTime data;

	private String status;

	@Column(precision = 12, scale = 2)
	private BigDecimal total = BigDecimal.ZERO;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "cliente_id", nullable = false)
	private Cliente cliente;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "responsavel_id", nullable = false)
	private Funcionario responsavel;

	@ManyToMany
	@JoinTable(name = "pedido_produtos",
		joinColumns = @JoinColumn(name = "pedido_id"),
		inverseJoinColumns = @JoinColumn(name = "produto_id"))
	private Set<Produto> produtos = new HashSet<>();

	@ManyToMany
	@JoinTable(name = "pedido_servicos",
		joinColumns = @JoinColumn(name = "pedido_id"),
		inverseJoinColumns = @JoinColumn(name = "servico_id"))
	private Set<Servico> servicos = new HashSet<>();

	@OneToMany(mappedBy = "pedido")
	private List<Pagamento> pagamentos = new ArrayList<>();

	public BigDecimal calcularTotal() {
		BigDecimal totalProdutos = produtos.stream()
			.map(Produto::getPreco)
			.filter(preco -> preco != null)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal totalServicos = servicos.stream()
			.map(Servico::getPreco)
			.filter(preco -> preco != null)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		total = totalProdutos.add(totalServicos);
		return total;
	}

	public void fechar() {
		status = "FECHADO";
	}
}