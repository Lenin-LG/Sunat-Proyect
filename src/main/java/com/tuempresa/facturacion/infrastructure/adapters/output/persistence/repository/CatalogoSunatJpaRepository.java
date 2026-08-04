package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.repository;

import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.CatalogoSunatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CatalogoSunatJpaRepository extends JpaRepository<CatalogoSunatEntity, Long> {
    Optional<CatalogoSunatEntity> findByCatalogoCodigoAndElementoCodigo(String catalogoCodigo, String elementoCodigo);
}
