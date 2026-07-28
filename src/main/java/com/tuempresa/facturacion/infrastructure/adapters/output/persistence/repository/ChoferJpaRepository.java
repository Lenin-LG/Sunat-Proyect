package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.repository;

import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.ChoferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChoferJpaRepository extends JpaRepository<ChoferEntity, Long> {
    Optional<ChoferEntity> findByNumeroDocumento(String numeroDocumento);
}
