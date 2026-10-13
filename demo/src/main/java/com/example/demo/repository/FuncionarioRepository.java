package com.example.demo.repository;

import com.example.demo.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    @Query("SELECT COUNT(p) > 0 FROM Pedido p WHERE p.responsavel.id = :id")
    boolean possuiPedidos(@Param("id") Long id);
}
