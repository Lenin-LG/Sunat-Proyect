package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.repository;

import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.CobroPagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CobroPagoJpaRepository extends JpaRepository<CobroPagoEntity, Long> {
    List<CobroPagoEntity> findByComprobanteId(Long comprobanteId);
}
