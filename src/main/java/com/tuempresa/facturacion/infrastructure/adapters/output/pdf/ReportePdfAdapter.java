package com.tuempresa.facturacion.infrastructure.adapters.output.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.tuempresa.facturacion.domain.model.Comprobante;
import com.tuempresa.facturacion.domain.model.Empresa;
import com.tuempresa.facturacion.domain.ports.out.ReportePdfPort;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Component
public class ReportePdfAdapter implements ReportePdfPort {

    private final TemplateEngine templateEngine;

    public ReportePdfAdapter(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public byte[] generarFacturaPdf(Comprobante comprobante, Empresa empresa) {
        try {
            // 1. Generar texto del Código QR
            String qrText = String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s|",
                    empresa.getRuc(),
                    comprobante.getTipoDocumento(),
                    comprobante.getSerie(),
                    comprobante.getNumero(),
                    comprobante.getTotalIgv() != null ? comprobante.getTotalIgv() : "0.00",
                    comprobante.getTotalPagar() != null ? comprobante.getTotalPagar() : "0.00",
                    comprobante.getFechaEmision(),
                    comprobante.getClienteTipoDocumento(),
                    comprobante.getClienteNumeroDocumento()
            );

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrText, BarcodeFormat.QR_CODE, 200, 200);

            ByteArrayOutputStream qrOs = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", qrOs);
            String qrBase64 = Base64.getEncoder().encodeToString(qrOs.toByteArray());

            // 2. Preparar contexto de Thymeleaf
            Context context = new Context();
            context.setVariable("c", comprobante);
            context.setVariable("empresa", empresa);
            context.setVariable("qrCode", "data:image/png;base64," + qrBase64);

            // 3. Procesar plantilla HTML
            String htmlContent = templateEngine.process("factura_template", context);

            // 4. Renderizar a PDF bytes
            ByteArrayOutputStream pdfOs = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, "/");
            builder.toStream(pdfOs);
            builder.run();

            return pdfOs.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de factura", e);
        }
    }
}
