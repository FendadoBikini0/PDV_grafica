package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

public class PedidoForm {

    private String numero;
    private Long clienteId;
    private Long responsavelId;
    private List<ItemPedidoForm> itens = new ArrayList<>();

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getResponsavelId() {
        return responsavelId;
    }

    public void setResponsavelId(Long responsavelId) {
        this.responsavelId = responsavelId;
    }

    public List<ItemPedidoForm> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoForm> itens) {
        this.itens = itens;
    }

    public static class ItemPedidoForm {

        private String tipo;
        private Long referenciaId;
        private Integer quantidade;

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public Long getReferenciaId() {
            return referenciaId;
        }

        public void setReferenciaId(Long referenciaId) {
            this.referenciaId = referenciaId;
        }

        public Integer getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(Integer quantidade) {
            this.quantidade = quantidade;
        }
    }
}
