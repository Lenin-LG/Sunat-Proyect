package com.tuempresa.facturacion.infrastructure.adapters.output.email;

import com.tuempresa.facturacion.domain.ports.out.NotificacionEmailPort;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class NotificacionEmailAdapter implements NotificacionEmailPort {

    private final JavaMailSender mailSender;

    public NotificacionEmailAdapter(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarComprobante(String destinatario, String asunto, String cuerpo,
                                  byte[] pdfBytes, String nombrePdf,
                                  byte[] xmlBytes, String nombreXml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(cuerpo, true);

            if (pdfBytes != null) {
                helper.addAttachment(nombrePdf, new ByteArrayResource(pdfBytes));
            }
            if (xmlBytes != null) {
                helper.addAttachment(nombreXml, new ByteArrayResource(xmlBytes));
            }

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar correo del comprobante", e);
        }
    }
}
