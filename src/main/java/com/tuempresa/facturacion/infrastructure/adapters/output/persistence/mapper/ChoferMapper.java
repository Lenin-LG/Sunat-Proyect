package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper;

import com.tuempresa.facturacion.domain.model.Chofer;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.ChoferEntity;

public class ChoferMapper {

    public static Chofer toDomain(ChoferEntity entity) {
        if (entity == null) return null;
        return Chofer.builder()
                .id(entity.getId())
                .tipoDocumento(entity.getTipoDocumento())
                .numeroDocumento(entity.getNumeroDocumento())
                .nombre(entity.getNombre())
                .licenciaConducir(entity.getLicenciaConducir())
                .build();
    }

    public static ChoferEntity toEntity(Chofer domain) {
        if (domain == null) return null;
        ChoferEntity entity = new ChoferEntity();
        entity.setId(domain.getId());
        entity.setTipoDocumento(domain.getTipoDocumento());
        entity.setNumeroDocumento(domain.getNumeroDocumento());
        entity.setNombre(domain.getNombre());
        entity.setLicenciaConducir(domain.getLicenciaConducir());
        return entity;
    }
}
