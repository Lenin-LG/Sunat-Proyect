package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.repository;

import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.KardexEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface KardexJpaRepository extends JpaRepository<KardexEntity, Long> {
    List<KardexEntity> findByProductoId(Long productoId);
}
