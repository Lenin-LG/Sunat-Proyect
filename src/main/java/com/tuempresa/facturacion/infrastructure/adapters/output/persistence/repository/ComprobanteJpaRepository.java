package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.repository;

import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.ComprobanteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ComprobanteJpaRepository extends JpaRepository<ComprobanteEntity, Long> {
    Optional<ComprobanteEntity> findTopByTipoDocumentoAndSerieOrderByNumeroDesc(String tipoDocumento, String serie);
    List<ComprobanteEntity> findByFechaEmisionBetween(LocalDate startDate, LocalDate endDate);
}
