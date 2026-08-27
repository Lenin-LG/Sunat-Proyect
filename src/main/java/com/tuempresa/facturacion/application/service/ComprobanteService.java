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
    private final ProductoPersistencePort productoPersistencePort;
    private final KardexPersistencePort kardexPersistencePort;
    private final EntidadPersistencePort entidadPersistencePort;
    private final ReportePdfPort reportePdfPort;
    private final NotificacionEmailPort notificacionEmailPort;
    private final String rucEmisor;
    private final PrivateKey privateKey;
    private final X509Certificate certificado;

    public ComprobanteService(ComprobantePersistencePort comprobantePersistencePort,
                              EmpresaPersistencePort empresaPersistencePort,
                              XmlBuilderPort xmlBuilderPort,
                              FirmaDigitalPort firmaDigitalPort,
                              SunatSoapPort sunatSoapPort,
                              ProductoPersistencePort productoPersistencePort,
                              KardexPersistencePort kardexPersistencePort,
                              EntidadPersistencePort entidadPersistencePort,
                              ReportePdfPort reportePdfPort,
                              NotificacionEmailPort notificacionEmailPort,
                              String rucEmisor,
                              PrivateKey privateKey,
                              X509Certificate certificado) {
        this.comprobantePersistencePort = comprobantePersistencePort;
        this.empresaPersistencePort = empresaPersistencePort;
        this.xmlBuilderPort = xmlBuilderPort;
        this.firmaDigitalPort = firmaDigitalPort;
        this.sunatSoapPort = sunatSoapPort;
        this.productoPersistencePort = productoPersistencePort;
        this.kardexPersistencePort = kardexPersistencePort;
        this.entidadPersistencePort = entidadPersistencePort;
        this.reportePdfPort = reportePdfPort;
        this.notificacionEmailPort = notificacionEmailPort;
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

        // Descontar stock e ingresar Kardex si aplica
        for (ComprobanteDetalle detalle : comprobante.getDetalles()) {
            if (detalle.getCodigoInterno() != null && !detalle.getCodigoInterno().isBlank()) {
                productoPersistencePort.findByCodigo(detalle.getCodigoInterno()).ifPresent(prod -> {
                    BigDecimal nuevoStock = prod.getStockActual().subtract(detalle.getCantidad());
                    prod.setStockActual(nuevoStock);
                    productoPersistencePort.save(prod);

                    kardexPersistencePort.save(com.tuempresa.facturacion.domain.model.Kardex.builder()
                            .productoId(prod.getId())
                            .tipoMovimiento("VENTA")
                            .cantidad(detalle.getCantidad())
                            .precioUnitario(detalle.getPrecioUnitario())
                            .stockResultante(nuevoStock)
                            .creadoEn(LocalDateTime.now())
                            .build());
                });
            }
        }

        Document xml = xmlBuilderPort.construir(comprobante, empresa);
        com.tuempresa.facturacion.infrastructure.config.DynamicCertLoader.CertKeys keys = 
                com.tuempresa.facturacion.infrastructure.config.DynamicCertLoader.load(empresa, privateKey, certificado);
        Document xmlFirmado = firmaDigitalPort.firmar(xml, keys.privateKey, keys.certificate);
        try {
            javax.xml.transform.TransformerFactory.newInstance().newTransformer()
                .transform(new javax.xml.transform.dom.DOMSource(xmlFirmado),
                        new javax.xml.transform.stream.StreamResult(new java.io.File("debug_firmado.xml")));
        } catch (Exception e) { e.printStackTrace(); }

        String nombreArchivo = comprobante.getNombreArchivo(empresa.getRuc());
        RespuestaSunat respuesta = sunatSoapPort.enviarComprobante(nombreArchivo, xmlFirmado);

        actualizarEstado(comprobante, respuesta);
        Comprobante savedComprobante = comprobantePersistencePort.save(comprobante);

        try {
            entidadPersistencePort.findByNumeroDocumento(savedComprobante.getClienteNumeroDocumento())
                .ifPresent(cliente -> {
                    if (cliente.getCorreo() != null && !cliente.getCorreo().isBlank()) {
                        byte[] pdfBytes = reportePdfPort.generarFacturaPdf(savedComprobante, empresa);
                        byte[] xmlBytes = documentToBytes(xmlFirmado);
                        String nombrePdf = nombreArchivo + ".pdf";
                        String nombreXml = nombreArchivo + ".xml";
                        String asunto = "Comprobante Electronico " + savedComprobante.getSerie() + "-" + savedComprobante.getNumero();
                        String cuerpo = "<h3>Estimado cliente " + savedComprobante.getClienteNombre() + ",</h3>" +
                                        "<p>Adjuntamos su comprobante de pago electronico en formatos PDF y XML.</p>" +
                                        "<p>Atentamente,<br/>" + empresa.getRazonSocial() + "</p>";
                        notificacionEmailPort.enviarComprobante(cliente.getCorreo(), asunto, cuerpo, pdfBytes, nombrePdf, xmlBytes, nombreXml);
                    }
                });
        } catch (Exception e) {
            System.err.println("No se pudo enviar el correo de notificacion: " + e.getMessage());
            e.printStackTrace();
        }

        return savedComprobante;
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

        // Campos avanzados
        comprobante.setFormaPago(command.getFormaPago() != null ? command.getFormaPago() : "CONTADO");
        comprobante.setDetraccionCodigo(command.getDetraccionCodigo());
        comprobante.setDetraccionPorcentaje(command.getDetraccionPorcentaje());
        comprobante.setDetraccionMonto(command.getDetraccionMonto());
        comprobante.setDescuentoGlobal(command.getDescuentoGlobal() != null ? command.getDescuentoGlobal() : BigDecimal.ZERO);
        comprobante.setTotalImpuestoBolsa(command.getTotalImpuestoBolsa() != null ? command.getTotalImpuestoBolsa() : BigDecimal.ZERO);
        comprobante.setAnticipoReferencia(command.getAnticipoReferencia());
        comprobante.setSaldoPendiente(command.getSaldoPendiente() != null ? command.getSaldoPendiente() : BigDecimal.ZERO);

        // Notas de Crédito / Débito
        comprobante.setDocumentoModificadoId(command.getDocumentoModificadoId());
        comprobante.setDocumentoModificadoTipo(command.getDocumentoModificadoTipo());
        comprobante.setNotaMotivoCodigo(command.getNotaMotivoCodigo());
        comprobante.setNotaMotivoDescripcion(command.getNotaMotivoDescripcion());

        if (command.getCuotas() != null) {
            for (ComprobanteCommand.CuotaCommand cuotaCmd : command.getCuotas()) {
                comprobante.getCuotas().add(com.tuempresa.facturacion.domain.model.Cuota.builder()
                        .numeroCuota(cuotaCmd.getNumeroCuota())
                        .monto(cuotaCmd.getMonto())
                        .fechaVencimiento(cuotaCmd.getFechaVencimiento())
                        .build());
            }
        }

        BigDecimal totalGravada = BigDecimal.ZERO;
        BigDecimal totalPagarCalculado = BigDecimal.ZERO;
        for (ItemCommand itemReq : command.getItems()) {
            ComprobanteDetalle detalle = new ComprobanteDetalle();
            detalle.setDescripcion(itemReq.getDescripcion());
            detalle.setCantidad(itemReq.getCantidad());
            
            BigDecimal precioConIgv = itemReq.getPrecioUnitario();
            BigDecimal precioSinIgv = precioConIgv.divide(new BigDecimal("1.18"), 6, RoundingMode.HALF_UP);
            detalle.setPrecioUnitario(precioSinIgv);
            
            detalle.setCodigoProductoSunat(itemReq.getCodigoProductoSunat());
            detalle.setCodigoInterno(itemReq.getCodigoInterno());
            detalle.setTipoUnidad(itemReq.getTipoUnidad() != null ? itemReq.getTipoUnidad() : "NIU");
            detalle.setTipoAfectacionIgv(itemReq.getTipoAfectacionIgv() != null ? itemReq.getTipoAfectacionIgv() : "10");
            detalle.setImpuestoBolsa(itemReq.getImpuestoBolsa() != null ? itemReq.getImpuestoBolsa() : BigDecimal.ZERO);
            comprobante.getDetalles().add(detalle);
            totalGravada = totalGravada.add(detalle.getValorVenta());

            BigDecimal itemTotalConIgv = precioConIgv.multiply(itemReq.getCantidad()).setScale(2, RoundingMode.HALF_UP);
            totalPagarCalculado = totalPagarCalculado.add(itemTotalConIgv);
        }

        totalGravada = totalGravada.subtract(comprobante.getDescuentoGlobal()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPagar = totalPagarCalculado.subtract(comprobante.getDescuentoGlobal()).add(comprobante.getTotalImpuestoBolsa());
        BigDecimal totalIgv = totalPagar.subtract(totalGravada).subtract(comprobante.getTotalImpuestoBolsa());

        comprobante.setTotalGravada(totalGravada);
        comprobante.setTotalIgv(totalIgv);
        comprobante.setTotalPagar(totalPagar);

        return comprobante;
    }

    private Integer siguienteNumero(String tipoDocumento, String serie) {
        return comprobantePersistencePort.findTopByTipoDocumentoAndSerieOrderByNumeroDesc(tipoDocumento, serie)
                .map(c -> c.getNumero() + 1)
                .orElse(1);
    }

    private void actualizarEstado(Comprobante comprobante, RespuestaSunat respuesta) {
        comprobante.setEnviadoEn(LocalDateTime.now());
        comprobante.setSunatResponseCode(truncate(respuesta.getResponseCode(), 255));
        comprobante.setSunatDescription(truncate(respuesta.getDescription(), 1000));
        comprobante.setEstado(respuesta.isAceptado()
                ? Comprobante.EstadoComprobante.ACEPTADO
                : Comprobante.EstadoComprobante.RECHAZADO);
    }

    private String truncate(String str, int length) {
        if (str == null) return null;
        return str.length() > length ? str.substring(0, length) : str;
    }

    private byte[] documentToBytes(Document doc) {
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            javax.xml.transform.TransformerFactory.newInstance().newTransformer()
                .transform(new javax.xml.transform.dom.DOMSource(doc),
                        new javax.xml.transform.stream.StreamResult(baos));
            return baos.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }
}
