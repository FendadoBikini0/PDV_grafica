package com.example.demo.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "itens_pedido")
@Getter
@Setter
@NoArgsConstructor
public class ItemPedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "pedido_id", nullable = false)
	private Pedido pedido;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "produto_id")
	private Produto produto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "servico_id")
	private Servico servico;

	@Column(nullable = false)
	private Integer quantidade;

	@Column(name = "preco_unitario", nullable = false, precision = 12, scale = 2)
	private BigDecimal precoUnitario;

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public BigDecimal calcularSubtotal() {
		return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
	}

	public BigDecimal getSubtotal() {
		return calcularSubtotal();
	}

	@PrePersist
	@PreUpdate
	private void validarProdutoOuServico() {
		if ((produto == null) == (servico == null)) {
			throw new IllegalStateException("O item deve estar associado a um produto ou a um serviço, mas não aos dois.");
		}
		if (quantidade == null || quantidade <= 0) {
			throw new IllegalStateException("A quantidade do item deve ser maior que zero.");
		}
		if (precoUnitario == null || precoUnitario.signum() < 0) {
			throw new IllegalStateException("O preço unitário deve ser informado e não pode ser negativo.");
		}
	}
}
