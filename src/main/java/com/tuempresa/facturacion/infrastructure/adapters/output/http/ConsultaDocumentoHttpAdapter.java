package com.tuempresa.facturacion.infrastructure.adapters.output.http;

import com.tuempresa.facturacion.domain.model.Entidad;
import com.tuempresa.facturacion.domain.ports.out.ConsultaDocumentoPort;
import org.springframework.stereotype.Component;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Component
public class ConsultaDocumentoHttpAdapter implements ConsultaDocumentoPort {

    @Value("${consulta.token:}")
    private String token;

    @Value("${consulta.url-dni:https://api.apisperu.net/v1/dni/}")
    private String urlDni;

    @Value("${consulta.url-ruc:https://api.apisperu.net/v1/ruc/}")
    private String urlRuc;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Optional<Entidad> consultarDni(String dni) {
        if (dni == null || dni.length() != 8) {
            return Optional.empty();
        }
        if (token == null || token.isBlank()) {
            return Optional.of(Entidad.builder()
                    .tipoEntidadId("1")
                    .numeroDocumento(dni)
                    .nombreRazonSocial("MOCK DNI USUARIO " + dni)
                    .direccion("CALLE LOS MOCKERS 123, LIMA")
                    .correo("dni_" + dni + "@mock.com")
                    .build());
        }
        try {
            String url = urlDni + dni + "?token=" + token;
            Map response = restTemplate.getForObject(url, Map.class);
            if (response != null) {
                String nombre = (String) response.get("nombre");
                if (nombre == null) {
                    nombre = (String) response.get("nombres");
                }
                return Optional.of(Entidad.builder()
                        .tipoEntidadId("1")
                        .numeroDocumento(dni)
                        .nombreRazonSocial(nombre != null ? nombre : "DNI " + dni)
                        .direccion((String) response.getOrDefault("direccion", "LIMA"))
                        .correo("dni_" + dni + "@apisperu.com")
                        .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Entidad> consultarRuc(String ruc) {
        if (ruc == null || ruc.length() != 11) {
            return Optional.empty();
        }
        if (token == null || token.isBlank()) {
            return Optional.of(Entidad.builder()
                    .tipoEntidadId("6")
                    .numeroDocumento(ruc)
                    .nombreRazonSocial("MOCK RUC EMPRESA " + ruc + " S.A.C.")
                    .direccion("AV. INDUSTRIAL 456, LIMA")
                    .correo("ruc_" + ruc + "@mock.com")
                    .build());
        }
        try {
            String url = urlRuc + ruc + "?token=" + token;
            Map response = restTemplate.getForObject(url, Map.class);
            if (response != null) {
                String razonSocial = (String) response.get("razonSocial");
                if (razonSocial == null) {
                    razonSocial = (String) response.get("nombre");
                }
                return Optional.of(Entidad.builder()
                        .tipoEntidadId("6")
                        .numeroDocumento(ruc)
                        .nombreRazonSocial(razonSocial != null ? razonSocial : "RUC " + ruc)
                        .direccion((String) response.getOrDefault("direccion", "LIMA"))
                        .correo("ruc_" + ruc + "@apisperu.com")
                        .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
