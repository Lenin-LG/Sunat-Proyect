package com.tuempresa.facturacion.domain.ports.out;

public interface NotificacionEmailPort {
    void enviarComprobante(String destinatario, String asunto, String cuerpo, byte[] pdfBytes, String nombrePdf, byte[] xmlBytes, String nombreXml);
}
