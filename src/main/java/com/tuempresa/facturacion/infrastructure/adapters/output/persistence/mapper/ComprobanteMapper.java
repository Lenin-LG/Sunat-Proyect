package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper;

import com.tuempresa.facturacion.domain.model.Comprobante;
import com.tuempresa.facturacion.domain.model.ComprobanteDetalle;
import com.tuempresa.facturacion.domain.model.Cuota;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.ComprobanteDetalleEntity;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.ComprobanteEntity;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.CuotaEntity;

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

        domain.setFormaPago(entity.getFormaPago());
        domain.setDetraccionCodigo(entity.getDetraccionCodigo());
        domain.setDetraccionPorcentaje(entity.getDetraccionPorcentaje());
        domain.setDetraccionMonto(entity.getDetraccionMonto());
        domain.setDescuentoGlobal(entity.getDescuentoGlobal());
        domain.setTotalImpuestoBolsa(entity.getTotalImpuestoBolsa());
        domain.setAnticipoReferencia(entity.getAnticipoReferencia());
        domain.setSaldoPendiente(entity.getSaldoPendiente());
        domain.setDocumentoModificadoId(entity.getDocumentoModificadoId());
        domain.setDocumentoModificadoTipo(entity.getDocumentoModificadoTipo());
        domain.setNotaMotivoCodigo(entity.getNotaMotivoCodigo());
        domain.setNotaMotivoDescripcion(entity.getNotaMotivoDescripcion());

        if (entity.getDetalles() != null) {
            domain.setDetalles(entity.getDetalles().stream()
                    .map(ComprobanteMapper::toDomainDetalle)
                    .collect(Collectors.toList()));
        }

        if (entity.getCuotas() != null) {
            domain.setCuotas(entity.getCuotas().stream()
                    .map(ComprobanteMapper::toDomainCuota)
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

        entity.setFormaPago(domain.getFormaPago());
        entity.setDetraccionCodigo(domain.getDetraccionCodigo());
        entity.setDetraccionPorcentaje(domain.getDetraccionPorcentaje());
        entity.setDetraccionMonto(domain.getDetraccionMonto());
        entity.setDescuentoGlobal(domain.getDescuentoGlobal());
        entity.setTotalImpuestoBolsa(domain.getTotalImpuestoBolsa());
        entity.setAnticipoReferencia(domain.getAnticipoReferencia());
        entity.setSaldoPendiente(domain.getSaldoPendiente());
        entity.setDocumentoModificadoId(domain.getDocumentoModificadoId());
        entity.setDocumentoModificadoTipo(domain.getDocumentoModificadoTipo());
        entity.setNotaMotivoCodigo(domain.getNotaMotivoCodigo());
        entity.setNotaMotivoDescripcion(domain.getNotaMotivoDescripcion());

        if (domain.getDetalles() != null) {
            entity.setDetalles(domain.getDetalles().stream()
                    .map(d -> {
                        ComprobanteDetalleEntity de = toEntityDetalle(d);
                        de.setComprobante(entity);
                        return de;
                    })
                    .collect(Collectors.toList()));
        }

        if (domain.getCuotas() != null) {
            entity.setCuotas(domain.getCuotas().stream()
                    .map(c -> {
                        CuotaEntity ce = toEntityCuota(c);
                        ce.setComprobante(entity);
                        return ce;
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
        domain.setTipoUnidad(entity.getTipoUnidad());
        domain.setTipoAfectacionIgv(entity.getTipoAfectacionIgv());
        domain.setImpuestoBolsa(entity.getImpuestoBolsa());
        domain.setCodigoInterno(entity.getCodigoInterno());
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
        entity.setTipoUnidad(domain.getTipoUnidad());
        entity.setTipoAfectacionIgv(domain.getTipoAfectacionIgv());
        entity.setImpuestoBolsa(domain.getImpuestoBolsa());
        entity.setCodigoInterno(domain.getCodigoInterno());
        return entity;
    }

    private static Cuota toDomainCuota(CuotaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Cuota.builder()
                .id(entity.getId())
                .numeroCuota(entity.getNumeroCuota())
                .monto(entity.getMonto())
                .fechaVencimiento(entity.getFechaVencimiento())
                .build();
    }

    private static CuotaEntity toEntityCuota(Cuota domain) {
        if (domain == null) {
            return null;
        }
        CuotaEntity entity = new CuotaEntity();
        entity.setId(domain.getId());
        entity.setNumeroCuota(domain.getNumeroCuota());
        entity.setMonto(domain.getMonto());
        entity.setFechaVencimiento(domain.getFechaVencimiento());
        return entity;
    }
}
