package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper;

import com.tuempresa.facturacion.domain.model.Compra;
import com.tuempresa.facturacion.domain.model.CompraDetalle;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.CompraEntity;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.CompraDetalleEntity;
import java.util.stream.Collectors;

public class CompraMapper {

    public static Compra toDomain(CompraEntity entity) {
        if (entity == null) return null;
        return Compra.builder()
                .id(entity.getId())
                .tipoDocumento(entity.getTipoDocumento())
                .serie(entity.getSerie())
                .numero(entity.getNumero())
                .proveedorId(entity.getProveedorId())
                .fechaEmision(entity.getFechaEmision())
                .totalGravada(entity.getTotalGravada())
                .totalIgv(entity.getTotalIgv())
                .totalPagar(entity.getTotalPagar())
                .creadoEn(entity.getCreadoEn())
                .detalles(entity.getDetalles() == null ? null : entity.getDetalles().stream()
                        .map(d -> CompraDetalle.builder()
                                .id(d.getId())
                                .productoId(d.getProductoId())
                                .cantidad(d.getCantidad())
                                .precioUnitario(d.getPrecioUnitario())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    public static CompraEntity toEntity(Compra domain) {
        if (domain == null) return null;
        CompraEntity entity = new CompraEntity();
        entity.setId(domain.getId());
        entity.setTipoDocumento(domain.getTipoDocumento());
        entity.setSerie(domain.getSerie());
        entity.setNumero(domain.getNumero());
        entity.setProveedorId(domain.getProveedorId());
        entity.setFechaEmision(domain.getFechaEmision());
        entity.setTotalGravada(domain.getTotalGravada());
        entity.setTotalIgv(domain.getTotalIgv());
        entity.setTotalPagar(domain.getTotalPagar());
        if (domain.getCreadoEn() != null) {
            entity.setCreadoEn(domain.getCreadoEn());
        }
        if (domain.getDetalles() != null) {
            entity.setDetalles(domain.getDetalles().stream()
                    .map(d -> {
                        CompraDetalleEntity de = new CompraDetalleEntity();
                        de.setId(d.getId());
                        de.setProductoId(d.getProductoId());
                        de.setCantidad(d.getCantidad());
                        de.setPrecioUnitario(d.getPrecioUnitario());
                        return de;
                    })
                    .collect(Collectors.toList()));
        }
        return entity;
    }
}
