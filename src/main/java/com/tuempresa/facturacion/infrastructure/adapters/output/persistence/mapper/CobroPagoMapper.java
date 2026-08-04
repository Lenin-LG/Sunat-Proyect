package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper;

import com.tuempresa.facturacion.domain.model.CobroPago;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.CobroPagoEntity;

public class CobroPagoMapper {

    public static CobroPago toDomain(CobroPagoEntity entity) {
        if (entity == null) return null;
        return CobroPago.builder()
                .id(entity.getId())
                .comprobanteId(entity.getComprobanteId())
                .monto(entity.getMonto())
                .metodoPago(entity.getMetodoPago())
                .fechaPago(entity.getFechaPago())
                .referencia(entity.getReferencia())
                .creadoEn(entity.getCreadoEn())
                .build();
    }

    public static CobroPagoEntity toEntity(CobroPago domain) {
        if (domain == null) return null;
        CobroPagoEntity entity = new CobroPagoEntity();
        entity.setId(domain.getId());
        entity.setComprobanteId(domain.getComprobanteId());
        entity.setMonto(domain.getMonto());
        entity.setMetodoPago(domain.getMetodoPago());
        entity.setFechaPago(domain.getFechaPago());
        entity.setReferencia(domain.getReferencia());
        if (domain.getCreadoEn() != null) {
            entity.setCreadoEn(domain.getCreadoEn());
        }
        return entity;
    }
}
