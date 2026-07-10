package com.tuempresa.facturacion.application.service;

import com.tuempresa.facturacion.domain.model.Comprobante;
import com.tuempresa.facturacion.domain.model.ComprobanteDetalle;
import com.tuempresa.facturacion.domain.model.Empresa;
import com.tuempresa.facturacion.domain.model.RespuestaSunat;
import com.tuempresa.facturacion.domain.ports.in.EmitirComprobanteUseCase;
import com.tuempresa.facturacion.domain.ports.in.dto.ComprobanteCommand;
import com.tuempresa.facturacion.domain.ports.in.dto.ItemCommand;
import com.tuempresa.facturacion.domain.ports.out.*;
import org.w3c.dom.Document;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ComprobanteService implements EmitirComprobanteUseCase {

    private static final BigDecimal IGV = new BigDecimal("0.18");

    private final ComprobantePersistencePort comprobantePersistencePort;
    private final EmpresaPersistencePort empresaPersistencePort;
    private final XmlBuilderPort xmlBuilderPort;
    private final FirmaDigitalPort firmaDigitalPort;
    private final SunatSoapPort sunatSoapPort;
    private final String rucEmisor;
    private final PrivateKey privateKey;
    private final X509Certificate certificado;

    public ComprobanteService(ComprobantePersistencePort comprobantePersistencePort,
                              EmpresaPersistencePort empresaPersistencePort,
                              XmlBuilderPort xmlBuilderPort,
                              FirmaDigitalPort firmaDigitalPort,
                              SunatSoapPort sunatSoapPort,
                              String rucEmisor,
                              PrivateKey privateKey,
                              X509Certificate certificado) {
        this.comprobantePersistencePort = comprobantePersistencePort;
        this.empresaPersistencePort = empresaPersistencePort;
        this.xmlBuilderPort = xmlBuilderPort;
        this.firmaDigitalPort = firmaDigitalPort;
        this.sunatSoapPort = sunatSoapPort;
        this.rucEmisor = rucEmisor;
        this.privateKey = privateKey;
        this.certificado = certificado;
    }

    @Override
    public Comprobante emitir(ComprobanteCommand command) {
        Empresa empresa = empresaPersistencePort.findByRuc(rucEmisor);
        if (empresa == null) {
            throw new IllegalStateException(
                    "No existe una Empresa registrada con RUC " + rucEmisor
                    + ". Registrela antes de emitir comprobantes.");
        }

        Comprobante comprobante = construirEntidad(command, empresa);
        comprobante = comprobantePersistencePort.save(comprobante);

        Document xml = xmlBuilderPort.construir(comprobante, empresa);
        Document xmlFirmado = firmaDigitalPort.firmar(xml, privateKey, certificado);
        try {
            javax.xml.transform.TransformerFactory.newInstance().newTransformer()
                .transform(new javax.xml.transform.dom.DOMSource(xmlFirmado),
                        new javax.xml.transform.stream.StreamResult(new java.io.File("debug_firmado.xml")));
        } catch (Exception e) { e.printStackTrace(); }

        String nombreArchivo = comprobante.getNombreArchivo(empresa.getRuc());
        RespuestaSunat respuesta = sunatSoapPort.enviarComprobante(nombreArchivo, xmlFirmado);

        actualizarEstado(comprobante, respuesta);
        return comprobantePersistencePort.save(comprobante);
    }

    private Comprobante construirEntidad(ComprobanteCommand command, Empresa empresa) {
        Comprobante comprobante = new Comprobante();
        comprobante.setTipoDocumento(command.getTipoDocumento());
        comprobante.setSerie(command.getSerie());
        comprobante.setNumero(siguienteNumero(command.getTipoDocumento(), command.getSerie()));
        comprobante.setFechaEmision(LocalDate.now());
        comprobante.setClienteTipoDocumento(command.getClienteTipoDocumento());
        comprobante.setClienteNumeroDocumento(command.getClienteNumeroDocumento());
        comprobante.setClienteNombre(command.getClienteNombre());

        BigDecimal totalGravada = BigDecimal.ZERO;
        for (ItemCommand itemReq : command.getItems()) {
            ComprobanteDetalle detalle = new ComprobanteDetalle();
            detalle.setDescripcion(itemReq.getDescripcion());
            detalle.setCantidad(itemReq.getCantidad());
            detalle.setPrecioUnitario(itemReq.getPrecioUnitario());
            detalle.setCodigoProductoSunat(itemReq.getCodigoProductoSunat());
            comprobante.getDetalles().add(detalle);
            totalGravada = totalGravada.add(detalle.getValorVenta());
        }

        totalGravada = totalGravada.setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalIgv = totalGravada.multiply(IGV).setScale(2, RoundingMode.HALF_UP);

        comprobante.setTotalGravada(totalGravada);
        comprobante.setTotalIgv(totalIgv);
        comprobante.setTotalPagar(totalGravada.add(totalIgv));

        return comprobante;
    }

    private Integer siguienteNumero(String tipoDocumento, String serie) {
        return comprobantePersistencePort.findTopByTipoDocumentoAndSerieOrderByNumeroDesc(tipoDocumento, serie)
                .map(c -> c.getNumero() + 1)
                .orElse(1);
    }

    private void actualizarEstado(Comprobante comprobante, RespuestaSunat respuesta) {
        comprobante.setEnviadoEn(LocalDateTime.now());
        comprobante.setSunatResponseCode(respuesta.getResponseCode());
        comprobante.setSunatDescription(respuesta.getDescription());
        comprobante.setEstado(respuesta.isAceptado()
                ? Comprobante.EstadoComprobante.ACEPTADO
                : Comprobante.EstadoComprobante.RECHAZADO);
    }
}
