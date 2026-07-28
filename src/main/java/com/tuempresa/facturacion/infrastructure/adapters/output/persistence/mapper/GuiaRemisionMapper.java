package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper;

import com.tuempresa.facturacion.domain.model.GuiaRemision;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.GuiaRemisionEntity;

public class GuiaRemisionMapper {

    public static GuiaRemision toDomain(GuiaRemisionEntity entity) {
        if (entity == null) return null;
        return GuiaRemision.builder()
                .id(entity.getId())
                .tipoGuia(entity.getTipoGuia())
                .serie(entity.getSerie())
                .numero(entity.getNumero())
                .fechaEmision(entity.getFechaEmision())
                .comprobanteId(entity.getComprobanteId())
                .clienteId(entity.getClienteId())
                .choferId(entity.getChoferId())
                .vehiculoId(entity.getVehiculoId())
                .motivoTraslado(entity.getMotivoTraslado())
                .pesoTotal(entity.getPesoTotal())
                .sunatResponseCode(entity.getSunatResponseCode())
                .sunatDescription(entity.getSunatDescription())
                .creadoEn(entity.getCreadoEn())
                .build();
    }

    public static GuiaRemisionEntity toEntity(GuiaRemision domain) {
        if (domain == null) return null;
        GuiaRemisionEntity entity = new GuiaRemisionEntity();
        entity.setId(domain.getId());
        entity.setTipoGuia(domain.getTipoGuia());
        entity.setSerie(domain.getSerie());
        entity.setNumero(domain.getNumero());
        entity.setFechaEmision(domain.getFechaEmision());
        entity.setComprobanteId(domain.getComprobanteId());
        entity.setClienteId(domain.getClienteId());
        entity.setChoferId(domain.getChoferId());
        entity.setVehiculoId(domain.getVehiculoId());
        entity.setMotivoTraslado(domain.getMotivoTraslado());
        entity.setPesoTotal(domain.getPesoTotal());
        entity.setSunatResponseCode(domain.getSunatResponseCode());
        entity.setSunatDescription(domain.getSunatDescription());
        if (domain.getCreadoEn() != null) {
            entity.setCreadoEn(domain.getCreadoEn());
        }
        return entity;
    }
}
