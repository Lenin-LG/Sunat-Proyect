package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.repository;

import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.GuiaRemisionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GuiaRemisionJpaRepository extends JpaRepository<GuiaRemisionEntity, Long> {
    Optional<GuiaRemisionEntity> findTopByTipoGuiaAndSerieOrderByNumeroDesc(String tipoGuia, String serie);
}
