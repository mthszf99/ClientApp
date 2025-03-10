package com.empresa.clientapp.repository;

import com.empresa.clientapp.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    List<Venda> findByClienteId(Long clienteId);

    @Query("SELECT v FROM Venda v WHERE v.dataVenda BETWEEN :inicio AND :fim")
    List<Venda> findByPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT v FROM Venda v JOIN v.itens i WHERE i.produto.id = :produtoId")
    List<Venda> findByProdutoId(@Param("produtoId") Long produtoId);
}
