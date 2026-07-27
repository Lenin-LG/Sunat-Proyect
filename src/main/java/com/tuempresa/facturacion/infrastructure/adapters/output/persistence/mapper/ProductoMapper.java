package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper;

import com.tuempresa.facturacion.domain.model.Producto;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.ProductoEntity;

public class ProductoMapper {

    public static Producto toDomain(ProductoEntity entity) {
        if (entity == null) return null;
        return Producto.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .descripcion(entity.getDescripcion())
                .precioUnitario(entity.getPrecioUnitario())
                .tipoAfectacionIgvId(entity.getTipoAfectacionIgvId())
                .unidadMedidaId(entity.getUnidadMedidaId())
                .stockActual(entity.getStockActual())
                .build();
    }

    public static ProductoEntity toEntity(Producto domain) {
        if (domain == null) return null;
        ProductoEntity entity = new ProductoEntity();
        entity.setId(domain.getId());
        entity.setCodigo(domain.getCodigo());
        entity.setDescripcion(domain.getDescripcion());
        entity.setPrecioUnitario(domain.getPrecioUnitario());
        entity.setTipoAfectacionIgvId(domain.getTipoAfectacionIgvId());
        entity.setUnidadMedidaId(domain.getUnidadMedidaId());
        entity.setStockActual(domain.getStockActual());
        return entity;
    }
}
