package com.tuempresa.facturacion.domain.ports.out;

import com.tuempresa.facturacion.domain.model.Compra;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CompraPersistencePort {
    Compra save(Compra compra);
    Optional<Compra> findById(Long id);
    List<Compra> findAll();
    List<Compra> findByFechaEmisionBetween(LocalDate startDate, LocalDate endDate);
}
