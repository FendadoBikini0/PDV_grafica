package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "funcionarios")
@Getter
@NoArgsConstructor
public class Funcionario extends Pessoa {

	@OneToMany(mappedBy = "responsavel")
	private List<Pedido> pedidosRegistrados = new ArrayList<>();

	public void registrarPedido(Pedido pedido) {
		pedidosRegistrados.add(pedido);
		pedido.setResponsavel(this);
	}
}