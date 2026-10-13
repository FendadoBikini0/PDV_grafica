package com.example.demo.repository;

import com.example.demo.entity.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    @Query("""
            SELECT COUNT(i) > 0
            FROM ItemPedido i
            WHERE i.produto.id = :produtoId
              AND (i.pedido.status IS NULL OR UPPER(i.pedido.status) <> 'ENTREGUE')
            """)
    boolean existeEmPedidoNaoEntregue(@Param("produtoId") Long produtoId);
}
