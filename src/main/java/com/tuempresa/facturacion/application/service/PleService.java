package com.tuempresa.facturacion.application.service;

import com.tuempresa.facturacion.domain.model.Comprobante;
import com.tuempresa.facturacion.domain.model.Compra;
import com.tuempresa.facturacion.domain.model.PleFile;
import com.tuempresa.facturacion.domain.ports.in.GenerarPleUseCase;
import com.tuempresa.facturacion.domain.ports.out.ComprobantePersistencePort;
import com.tuempresa.facturacion.domain.ports.out.CompraPersistencePort;
import com.tuempresa.facturacion.domain.ports.out.EntidadPersistencePort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class PleService implements GenerarPleUseCase {

    private final ComprobantePersistencePort comprobantePersistencePort;
    private final CompraPersistencePort compraPersistencePort;
    private final EntidadPersistencePort entidadPersistencePort;
    private final String rucEmisor;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PleService(ComprobantePersistencePort comprobantePersistencePort,
                      CompraPersistencePort compraPersistencePort,
                      EntidadPersistencePort entidadPersistencePort,
                      String rucEmisor) {
        this.comprobantePersistencePort = comprobantePersistencePort;
        this.compraPersistencePort = compraPersistencePort;
        this.entidadPersistencePort = entidadPersistencePort;
        this.rucEmisor = rucEmisor;
    }

    @Override
    public PleFile generarVentas(int mes, int anio) {
        LocalDate startDate = LocalDate.of(anio, mes, 1);
        LocalDate endDate = startDate.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth());

        List<Comprobante> comprobantes = comprobantePersistencePort.findByFechaEmisionBetween(startDate, endDate);

        String period = "%d%02d00".formatted(anio, mes);
        StringBuilder sb = new StringBuilder();
        int counter = 1;

        for (Comprobante c : comprobantes) {
            String seatCode = "M" + String.format("%06d", counter);
            String fechaEmi = c.getFechaEmision() != null ? c.getFechaEmision().format(DATE_FORMATTER) : "";

            BigDecimal totalGravada = c.getTotalGravada() != null ? c.getTotalGravada() : BigDecimal.ZERO;
            BigDecimal totalIgv = c.getTotalIgv() != null ? c.getTotalIgv() : BigDecimal.ZERO;
            BigDecimal totalPagar = c.getTotalPagar() != null ? c.getTotalPagar() : BigDecimal.ZERO;

            if ("07".equals(c.getTipoDocumento())) {
                totalGravada = totalGravada.negate();
                totalIgv = totalIgv.negate();
                totalPagar = totalPagar.negate();
            }

            String clienteTipoDoc = c.getClienteTipoDocumento() != null ? c.getClienteTipoDocumento() : "0";
            String clienteNroDoc = c.getClienteNumeroDocumento() != null ? c.getClienteNumeroDocumento() : "";
            String clienteNombre = c.getClienteNombre() != null ? c.getClienteNombre() : "";

            String refFecha = "";
            String refTipo = "";
            String refSerie = "";
            String refNumero = "";
            if (c.getDocumentoModificadoId() != null && !c.getDocumentoModificadoId().isBlank()) {
                refTipo = c.getDocumentoModificadoTipo() != null ? c.getDocumentoModificadoTipo() : "01";
                String[] parts = c.getDocumentoModificadoId().split("-");
                refSerie = parts[0];
                if (parts.length > 1) {
                    refNumero = parts[1];
                }
                refFecha = c.getFechaEmision().format(DATE_FORMATTER);
            }

            String state = c.getEstado() == Comprobante.EstadoComprobante.RECHAZADO ? "2" : "1";

            sb.append(period).append("|")
              .append(counter).append("|")
              .append(seatCode).append("|")
              .append(fechaEmi).append("|")
              .append("").append("|") // Vencimiento
              .append(c.getTipoDocumento()).append("|")
              .append(c.getSerie()).append("|")
              .append(c.getNumero()).append("|")
              .append("").append("|") // Numero Final
              .append(clienteTipoDoc).append("|")
              .append(clienteNroDoc).append("|")
              .append(clienteNombre).append("|")
              .append("").append("|") // Exportación
              .append(formatNum(totalGravada)).append("|")
              .append("").append("|") // Base descuento
              .append(formatNum(totalIgv)).append("|")
              .append("").append("|") // Igv descuento
              .append("0.00").append("|") // Exonerado
              .append("0.00").append("|") // Inafecto
              .append("0.00").append("|") // ISC
              .append("0.00").append("|") // Arroz BI
              .append("0.00").append("|") // Arroz IGV
              .append(formatNum(c.getTotalImpuestoBolsa())).append("|")
              .append("0.00").append("|") // Otros
              .append(formatNum(totalPagar)).append("|")
              .append("PEN").append("|")
              .append("1.000").append("|")
              .append(refFecha).append("|")
              .append(refTipo).append("|")
              .append(refSerie).append("|")
              .append(refNumero).append("|")
              .append("").append("|") // Contrato
              .append("").append("|") // Error
              .append("").append("|") // Medio de Pago
              .append(state).append("|")
              .append("\r\n");

            counter++;
        }

        String hasData = comprobantes.isEmpty() ? "0" : "1";
        String filename = "LE%s%s%02d00140100001%s11.txt".formatted(rucEmisor, anio, mes, hasData);

        return PleFile.builder()
                .filename(filename)
                .content(sb.toString())
                .build();
    }

    @Override
    public PleFile generarCompras(int mes, int anio) {
        LocalDate startDate = LocalDate.of(anio, mes, 1);
        LocalDate endDate = startDate.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth());

        List<Compra> compras = compraPersistencePort.findByFechaEmisionBetween(startDate, endDate);

        String period = "%d%02d00".formatted(anio, mes);
        StringBuilder sb = new StringBuilder();
        int counter = 1;

        for (Compra c : compras) {
            String seatCode = "M" + String.format("%06d", counter);
            String fechaEmi = c.getFechaEmision() != null ? c.getFechaEmision().format(DATE_FORMATTER) : "";

            BigDecimal totalGravada = c.getTotalGravada() != null ? c.getTotalGravada() : BigDecimal.ZERO;
            BigDecimal totalIgv = c.getTotalIgv() != null ? c.getTotalIgv() : BigDecimal.ZERO;
            BigDecimal totalPagar = c.getTotalPagar() != null ? c.getTotalPagar() : BigDecimal.ZERO;

            if ("07".equals(c.getTipoDocumento())) {
                totalGravada = totalGravada.negate();
                totalIgv = totalIgv.negate();
                totalPagar = totalPagar.negate();
            }

            String provTipoDoc = "6";
            String provNroDoc = "";
            String provNombre = "";

            if (c.getProveedorId() != null) {
                var provOpt = entidadPersistencePort.findById(c.getProveedorId());
                if (provOpt.isPresent()) {
                    var prov = provOpt.get();
                    provTipoDoc = prov.getTipoEntidadId() != null ? prov.getTipoEntidadId() : "6";
                    provNroDoc = prov.getNumeroDocumento() != null ? prov.getNumeroDocumento() : "";
                    provNombre = prov.getNombreRazonSocial() != null ? prov.getNombreRazonSocial() : "";
                }
            }

            String state = "1";

            sb.append(period).append("|")
              .append(counter).append("|")
              .append(seatCode).append("|")
              .append(fechaEmi).append("|")
              .append("").append("|") // Vencimiento
              .append(c.getTipoDocumento()).append("|")
              .append(c.getSerie()).append("|")
              .append("").append("|") // Año DUA
              .append(c.getNumero()).append("|")
              .append("").append("|") // Final
              .append(provTipoDoc).append("|")
              .append(provNroDoc).append("|")
              .append(provNombre).append("|")
              .append(formatNum(totalGravada)).append("|")
              .append(formatNum(totalIgv)).append("|")
              .append("0.00").append("|") // Base 2
              .append("0.00").append("|") // Igv 2
              .append("0.00").append("|") // Base 3
              .append("0.00").append("|") // Igv 3
              .append("0.00").append("|") // No gravadas
              .append("0.00").append("|") // ISC
              .append("0.00").append("|") // ICBPER
              .append("0.00").append("|") // Otros
              .append(formatNum(totalPagar)).append("|")
              .append("PEN").append("|")
              .append("1.000").append("|")
              .append("").append("|") // DR fecha
              .append("").append("|") // DR tipo
              .append("").append("|") // DR serie
              .append("").append("|") // DR dua
              .append("").append("|") // DR numero
              .append("").append("|") // detraccion fecha
              .append("").append("|") // detraccion numero
              .append("").append("|") // retencion
              .append("").append("|") // clasificacion bienes
              .append("").append("|") // contrato
              .append("").append("|") // error 1
              .append("").append("|") // error 2
              .append("").append("|") // error 3
              .append("").append("|") // error 4
              .append("").append("|") // medio pago
              .append(state).append("|")
              .append("\r\n");

            counter++;
        }

        String hasData = compras.isEmpty() ? "0" : "1";
        String filename = "LE%s%s%02d00080100001%s11.txt".formatted(rucEmisor, anio, mes, hasData);

        return PleFile.builder()
                .filename(filename)
                .content(sb.toString())
                .build();
    }

    private String formatNum(BigDecimal val) {
        if (val == null) return "0.00";
        return String.format(Locale.US, "%.2f", val);
    }
}
