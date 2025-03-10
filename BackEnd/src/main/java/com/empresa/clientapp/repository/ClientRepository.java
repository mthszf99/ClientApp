package com.empresa.clientapp.repository;

import com.empresa.clientapp.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Cliente, Long> {
}
