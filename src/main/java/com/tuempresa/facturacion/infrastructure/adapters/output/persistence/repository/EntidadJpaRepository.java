package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.repository;

import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.EntidadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EntidadJpaRepository extends JpaRepository<EntidadEntity, Long> {
    Optional<EntidadEntity> findByNumeroDocumento(String numeroDocumento);
}
