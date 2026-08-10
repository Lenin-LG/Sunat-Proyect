package com.tuempresa.facturacion.application.service;

import com.tuempresa.facturacion.domain.model.Entidad;
import com.tuempresa.facturacion.domain.ports.in.AdministrarClienteUseCase;
import com.tuempresa.facturacion.domain.ports.out.ConsultaDocumentoPort;
import com.tuempresa.facturacion.domain.ports.out.EntidadPersistencePort;
import java.util.List;

public class ClienteService implements AdministrarClienteUseCase {

    private final EntidadPersistencePort entidadPersistencePort;
    private final ConsultaDocumentoPort consultaDocumentoPort;

    public ClienteService(EntidadPersistencePort entidadPersistencePort, ConsultaDocumentoPort consultaDocumentoPort) {
        this.entidadPersistencePort = entidadPersistencePort;
        this.consultaDocumentoPort = consultaDocumentoPort;
    }

    @Override
    public Entidad registrar(Entidad entidad) {
        return entidadPersistencePort.findByNumeroDocumento(entidad.getNumeroDocumento())
                .map(existing -> {
                    existing.setNombreRazonSocial(entidad.getNombreRazonSocial());
                    existing.setDireccion(entidad.getDireccion());
                    existing.setCorreo(entidad.getCorreo());
                    existing.setTipoEntidadId(entidad.getTipoEntidadId());
                    return entidadPersistencePort.save(existing);
                })
                .orElseGet(() -> entidadPersistencePort.save(entidad));
    }

    @Override
    public Entidad actualizar(Long id, Entidad entidad) {
        Entidad existing = obtener(id);
        existing.setTipoEntidadId(entidad.getTipoEntidadId());
        existing.setNumeroDocumento(entidad.getNumeroDocumento());
        existing.setNombreRazonSocial(entidad.getNombreRazonSocial());
        existing.setDireccion(entidad.getDireccion());
        existing.setCorreo(entidad.getCorreo());
        return entidadPersistencePort.save(existing);
    }

    @Override
    public Entidad obtener(Long id) {
        return entidadPersistencePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la entidad con id: " + id));
    }

    @Override
    public List<Entidad> listar() {
        return entidadPersistencePort.findAll();
    }

    @Override
    public void eliminar(Long id) {
        entidadPersistencePort.deleteById(id);
    }

    @Override
    public Entidad buscarPorDocumentoAuto(String tipoDoc, String numeroDoc) {
        return entidadPersistencePort.findByNumeroDocumento(numeroDoc)
                .orElseGet(() -> {
                    Entidad consultada;
                    if ("1".equals(tipoDoc)) {
                        consultada = consultaDocumentoPort.consultarDni(numeroDoc)
                                .orElseThrow(() -> new IllegalArgumentException("No se pudo consultar el DNI " + numeroDoc));
                    } else if ("6".equals(tipoDoc)) {
                        consultada = consultaDocumentoPort.consultarRuc(numeroDoc)
                                .orElseThrow(() -> new IllegalArgumentException("No se pudo consultar el RUC " + numeroDoc));
                    } else {
                        throw new IllegalArgumentException("Tipo de documento no soportado para consulta automática: " + tipoDoc);
                    }
                    return consultada;
                });
    }
}
