package com.empresa.clientapp.repository;

import com.empresa.clientapp.model.ItemOrdemCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemOrdemCompraRepository extends JpaRepository<ItemOrdemCompra, Long> {
}

