package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.repository;

import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.ComprobanteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComprobanteJpaRepository extends JpaRepository<ComprobanteEntity, Long> {
    Optional<ComprobanteEntity> findTopByTipoDocumentoAndSerieOrderByNumeroDesc(String tipoDocumento, String serie);
}
