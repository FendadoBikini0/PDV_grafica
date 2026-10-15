package com.example.demo.repository;

import com.example.demo.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    boolean existsByCodigoAndAtivoTrue(String codigo);

    boolean existsByCodigoAndAtivoTrueAndIdNot(String codigo, Long id);

    List<Produto> findByAtivoTrue();

    long countByAtivoTrue();
}
