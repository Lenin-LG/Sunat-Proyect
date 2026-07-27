package com.tuempresa.facturacion.application.service;

import com.tuempresa.facturacion.domain.model.Empresa;
import com.tuempresa.facturacion.domain.ports.in.AdministrarEmpresaUseCase;
import com.tuempresa.facturacion.domain.ports.out.EmpresaPersistencePort;

public class EmpresaService implements AdministrarEmpresaUseCase {

    private final EmpresaPersistencePort empresaPersistencePort;

    public EmpresaService(EmpresaPersistencePort empresaPersistencePort) {
        this.empresaPersistencePort = empresaPersistencePort;
    }

    @Override
    public Empresa registrarOActualizar(Empresa empresa) {
        Empresa existing = empresaPersistencePort.findByRuc(empresa.getRuc());
        if (existing != null) {
            existing.setRazonSocial(empresa.getRazonSocial());
            existing.setNombreComercial(empresa.getNombreComercial());
            existing.setUbigeo(empresa.getUbigeo());
            existing.setDepartamento(empresa.getDepartamento());
            existing.setProvincia(empresa.getProvincia());
            existing.setDistrito(empresa.getDistrito());
            existing.setDireccionFiscal(empresa.getDireccionFiscal());
            
            existing.setUsuarioSolProduccion(empresa.getUsuarioSolProduccion());
            existing.setPasswordSolProduccion(empresa.getPasswordSolProduccion());
            existing.setModoProduccion(empresa.isModoProduccion());
            existing.setCertificadoBase64(empresa.getCertificadoBase64());
            existing.setCertificadoPassword(empresa.getCertificadoPassword());
            
            return empresaPersistencePort.save(existing);
        } else {
            return empresaPersistencePort.save(empresa);
        }
    }

    @Override
    public Empresa obtenerPorRuc(String ruc) {
        Empresa empresa = empresaPersistencePort.findByRuc(ruc);
        if (empresa == null) {
            throw new IllegalArgumentException("Empresa con RUC " + ruc + " no encontrada.");
        }
        return empresa;
    }
}
