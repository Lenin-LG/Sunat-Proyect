package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper;

import com.tuempresa.facturacion.domain.model.Empresa;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.EmpresaEntity;

public class EmpresaMapper {

    public static Empresa toDomain(EmpresaEntity entity) {
        if (entity == null) {
            return null;
        }
        Empresa domain = new Empresa();
        domain.setId(entity.getId());
        domain.setRuc(entity.getRuc());
        domain.setRazonSocial(entity.getRazonSocial());
        domain.setNombreComercial(entity.getNombreComercial());
        domain.setUbigeo(entity.getUbigeo());
        domain.setDepartamento(entity.getDepartamento());
        domain.setProvincia(entity.getProvincia());
        domain.setDistrito(entity.getDistrito());
        domain.setDireccionFiscal(entity.getDireccionFiscal());
        domain.setUsuarioSolProduccion(entity.getUsuarioSolProduccion());
        domain.setPasswordSolProduccion(entity.getPasswordSolProduccion());
        domain.setModoProduccion(entity.isModoProduccion());
        domain.setCertificadoBase64(entity.getCertificadoBase64());
        domain.setCertificadoPassword(entity.getCertificadoPassword());
        return domain;
    }

    public static EmpresaEntity toEntity(Empresa domain) {
        if (domain == null) {
            return null;
        }
        EmpresaEntity entity = new EmpresaEntity();
        entity.setId(domain.getId());
        entity.setRuc(domain.getRuc());
        entity.setRazonSocial(domain.getRazonSocial());
        entity.setNombreComercial(domain.getNombreComercial());
        entity.setUbigeo(domain.getUbigeo());
        entity.setDepartamento(domain.getDepartamento());
        entity.setProvincia(domain.getProvincia());
        entity.setDistrito(domain.getDistrito());
        entity.setDireccionFiscal(domain.getDireccionFiscal());
        entity.setUsuarioSolProduccion(domain.getUsuarioSolProduccion());
        entity.setPasswordSolProduccion(domain.getPasswordSolProduccion());
        entity.setModoProduccion(domain.isModoProduccion());
        entity.setCertificadoBase64(domain.getCertificadoBase64());
        entity.setCertificadoPassword(domain.getCertificadoPassword());
        return entity;
    }
}
