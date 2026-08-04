package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper;

import com.tuempresa.facturacion.domain.model.CatalogoSunat;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.CatalogoSunatEntity;

public class CatalogoSunatMapper {

    public static CatalogoSunat toDomain(CatalogoSunatEntity entity) {
        if (entity == null) return null;
        return CatalogoSunat.builder()
                .id(entity.getId())
                .catalogoCodigo(entity.getCatalogoCodigo())
                .elementoCodigo(entity.getElementoCodigo())
                .descripcion(entity.getDescripcion())
                .abreviatura(entity.getAbreviatura())
                .build();
    }

    public static CatalogoSunatEntity toEntity(CatalogoSunat domain) {
        if (domain == null) return null;
        CatalogoSunatEntity entity = new CatalogoSunatEntity();
        entity.setId(domain.getId());
        entity.setCatalogoCodigo(domain.getCatalogoCodigo());
        entity.setElementoCodigo(domain.getElementoCodigo());
        entity.setDescripcion(domain.getDescripcion());
        entity.setAbreviatura(domain.getAbreviatura());
        return entity;
    }
}
