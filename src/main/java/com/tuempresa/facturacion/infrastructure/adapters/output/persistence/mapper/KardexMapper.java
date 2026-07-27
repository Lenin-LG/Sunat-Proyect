package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper;

import com.tuempresa.facturacion.domain.model.Kardex;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.KardexEntity;

public class KardexMapper {

    public static Kardex toDomain(KardexEntity entity) {
        if (entity == null) return null;
        return Kardex.builder()
                .id(entity.getId())
                .productoId(entity.getProductoId())
                .tipoMovimiento(entity.getTipoMovimiento())
                .cantidad(entity.getCantidad())
                .precioUnitario(entity.getPrecioUnitario())
                .stockResultante(entity.getStockResultante())
                .creadoEn(entity.getCreadoEn())
                .build();
    }

    public static KardexEntity toEntity(Kardex domain) {
        if (domain == null) return null;
        KardexEntity entity = new KardexEntity();
        entity.setId(domain.getId());
        entity.setProductoId(domain.getProductoId());
        entity.setTipoMovimiento(domain.getTipoMovimiento());
        entity.setCantidad(domain.getCantidad());
        entity.setPrecioUnitario(domain.getPrecioUnitario());
        entity.setStockResultante(domain.getStockResultante());
        if (domain.getCreadoEn() != null) {
            entity.setCreadoEn(domain.getCreadoEn());
        }
        return entity;
    }
}
