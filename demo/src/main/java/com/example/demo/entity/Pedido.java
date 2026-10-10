package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

	@OneToMany(mappedBy = "pedido")
	private List<ItemPedido> itens = new ArrayList<>();

	@OneToMany(mappedBy = "pedido")
	private List<Pagamento> pagamentos = new ArrayList<>();

	public void adicionarItem(ItemPedido item) {
		itens.add(item);
		item.setPedido(this);
	}

	public BigDecimal calcularTotal() {
		total = itens.stream()
			.map(ItemPedido::calcularSubtotal)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
		return total;
	}

	public void fechar() {
		if (itens.isEmpty()) {
			throw new IllegalStateException("Não é possível fechar um pedido sem itens.");
		}

		calcularTotal();
		status = "FECHADO";
	}
}