package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper;

import com.tuempresa.facturacion.domain.model.Entidad;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.EntidadEntity;

public class EntidadMapper {

    public static Entidad toDomain(EntidadEntity entity) {
        if (entity == null) return null;
        return Entidad.builder()
                .id(entity.getId())
                .tipoEntidadId(entity.getTipoEntidadId())
                .numeroDocumento(entity.getNumeroDocumento())
                .nombreRazonSocial(entity.getNombreRazonSocial())
                .direccion(entity.getDireccion())
                .correo(entity.getCorreo())
                .build();
    }

    public static EntidadEntity toEntity(Entidad domain) {
        if (domain == null) return null;
        EntidadEntity entity = new EntidadEntity();
        entity.setId(domain.getId());
        entity.setTipoEntidadId(domain.getTipoEntidadId());
        entity.setNumeroDocumento(domain.getNumeroDocumento());
        entity.setNombreRazonSocial(domain.getNombreRazonSocial());
        entity.setDireccion(domain.getDireccion());
        entity.setCorreo(domain.getCorreo());
        return entity;
    }
}
