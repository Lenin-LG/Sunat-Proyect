package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.repository;

import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.VehiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VehiculoJpaRepository extends JpaRepository<VehiculoEntity, Long> {
    Optional<VehiculoEntity> findByPlaca(String placa);
}
