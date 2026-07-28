package com.tuempresa.facturacion.infrastructure.adapters.output.persistence.mapper;

import com.tuempresa.facturacion.domain.model.Vehiculo;
import com.tuempresa.facturacion.infrastructure.adapters.output.persistence.entity.VehiculoEntity;

public class VehiculoMapper {

    public static Vehiculo toDomain(VehiculoEntity entity) {
        if (entity == null) return null;
        return Vehiculo.builder()
                .id(entity.getId())
                .placa(entity.getPlaca())
                .marca(entity.getMarca())
                .modelo(entity.getModelo())
                .nroAutorizacion(entity.getNroAutorizacion())
                .build();
    }

    public static VehiculoEntity toEntity(Vehiculo domain) {
        if (domain == null) return null;
        VehiculoEntity entity = new VehiculoEntity();
        entity.setId(domain.getId());
        entity.setPlaca(domain.getPlaca());
        entity.setMarca(domain.getMarca());
        entity.setModelo(domain.getModelo());
        entity.setNroAutorizacion(domain.getNroAutorizacion());
        return entity;
    }
}
