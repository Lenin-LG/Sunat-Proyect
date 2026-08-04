package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.repository;

import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.CompraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface CompraJpaRepository extends JpaRepository<CompraEntity, Long> {
    List<CompraEntity> findByFechaEmisionBetween(LocalDate startDate, LocalDate endDate);
}
