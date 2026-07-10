package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper;

import com.tuempresa.facturacion.domain.model.Comprobante;
import com.tuempresa.facturacion.domain.model.ComprobanteDetalle;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.ComprobanteDetalleEntity;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.ComprobanteEntity;

import java.util.stream.Collectors;

public class ComprobanteMapper {

    public static Comprobante toDomain(ComprobanteEntity entity) {
        if (entity == null) {
            return null;
        }
        Comprobante domain = new Comprobante();
        domain.setId(entity.getId());
        domain.setTipoDocumento(entity.getTipoDocumento());
        domain.setSerie(entity.getSerie());
        domain.setNumero(entity.getNumero());
        domain.setFechaEmision(entity.getFechaEmision());
        domain.setClienteTipoDocumento(entity.getClienteTipoDocumento());
        domain.setClienteNumeroDocumento(entity.getClienteNumeroDocumento());
        domain.setClienteNombre(entity.getClienteNombre());
        domain.setTotalGravada(entity.getTotalGravada());
        domain.setTotalIgv(entity.getTotalIgv());
        domain.setTotalPagar(entity.getTotalPagar());
        domain.setEstado(entity.getEstado());
        domain.setSunatResponseCode(entity.getSunatResponseCode());
        domain.setSunatDescription(entity.getSunatDescription());
        domain.setCreadoEn(entity.getCreadoEn());
        domain.setEnviadoEn(entity.getEnviadoEn());

        if (entity.getDetalles() != null) {
            domain.setDetalles(entity.getDetalles().stream()
                    .map(ComprobanteMapper::toDomainDetalle)
                    .collect(Collectors.toList()));
        }
        return domain;
    }

    public static ComprobanteEntity toEntity(Comprobante domain) {
        if (domain == null) {
            return null;
        }
        ComprobanteEntity entity = new ComprobanteEntity();
        entity.setId(domain.getId());
        entity.setTipoDocumento(domain.getTipoDocumento());
        entity.setSerie(domain.getSerie());
        entity.setNumero(domain.getNumero());
        entity.setFechaEmision(domain.getFechaEmision());
        entity.setClienteTipoDocumento(domain.getClienteTipoDocumento());
        entity.setClienteNumeroDocumento(domain.getClienteNumeroDocumento());
        entity.setClienteNombre(domain.getClienteNombre());
        entity.setTotalGravada(domain.getTotalGravada());
        entity.setTotalIgv(domain.getTotalIgv());
        entity.setTotalPagar(domain.getTotalPagar());
        entity.setEstado(domain.getEstado());
        entity.setSunatResponseCode(domain.getSunatResponseCode());
        entity.setSunatDescription(domain.getSunatDescription());
        entity.setCreadoEn(domain.getCreadoEn());
        entity.setEnviadoEn(domain.getEnviadoEn());

        if (domain.getDetalles() != null) {
            entity.setDetalles(domain.getDetalles().stream()
                    .map(d -> {
                        ComprobanteDetalleEntity de = toEntityDetalle(d);
                        de.setComprobante(entity);
                        return de;
                    })
                    .collect(Collectors.toList()));
        }
        return entity;
    }

    private static ComprobanteDetalle toDomainDetalle(ComprobanteDetalleEntity entity) {
        if (entity == null) {
            return null;
        }
        ComprobanteDetalle domain = new ComprobanteDetalle();
        domain.setId(entity.getId());
        domain.setDescripcion(entity.getDescripcion());
        domain.setCantidad(entity.getCantidad());
        domain.setPrecioUnitario(entity.getPrecioUnitario());
        domain.setCodigoProductoSunat(entity.getCodigoProductoSunat());
        return domain;
    }

    private static ComprobanteDetalleEntity toEntityDetalle(ComprobanteDetalle domain) {
        if (domain == null) {
            return null;
        }
        ComprobanteDetalleEntity entity = new ComprobanteDetalleEntity();
        entity.setId(domain.getId());
        entity.setDescripcion(domain.getDescripcion());
        entity.setCantidad(domain.getCantidad());
        entity.setPrecioUnitario(domain.getPrecioUnitario());
        entity.setCodigoProductoSunat(domain.getCodigoProductoSunat());
        return entity;
    }
}
