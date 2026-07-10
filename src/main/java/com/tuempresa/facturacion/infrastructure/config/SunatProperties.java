package com.tuempresa.facturacion.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuracion del ambiente SUNAT.
 *
 * IMPORTANTE (aprendido del analisis del proyecto legacy en PHP):
 * - Las credenciales NUNCA deben ir hardcodeadas ni viajar en la URL (GET).
 * - password se define via variable de entorno SUNAT_PASSWORD_SOL, nunca en el
 * yml en texto plano.
 * - El certificado digital (.p12) NO se versiona en el repositorio; se monta
 * como secreto
 * (variable de entorno / volumen / vault) en tiempo de despliegue.
 *
 * Fuente de los endpoints y credenciales de prueba:
 * - Manual del Programador SUNAT: https://cpe.sunat.gob.pe/guias-y-manuales
 * - Pautas servicio BETA:
 * https://orientacion.sunat.gob.pe/12-pautas-servicio-beta
 */
@ConfigurationProperties(prefix = "sunat")
public class SunatProperties {

    /** RUC del emisor. En Beta: 20000000001 */
    private String ruc;

    /** Usuario secundario SOL. En Beta: MODDATOS */
    private String usuarioSol;

    /** Password del usuario secundario SOL. En Beta: moddatos */
    private String passwordSol;

    /** true = produccion, false = beta */
    private boolean produccion = false;

    private String endpointBeta = "https://e-beta.sunat.gob.pe/ol-ti-itcpfegem-beta/billService";
    private String endpointProduccion = "https://e-factura.sunat.gob.pe/ol-ti-itcpfegem/billService";

    /** Ruta al certificado digital (.p12 en produccion, autofirmado en pruebas) */
    private String certificadoPath;
    private String certificadoPassword;

    public String getEndpointActivo() {
        return produccion ? endpointProduccion : endpointBeta;
    }

    public String getUsernameToken() {
        return ruc + usuarioSol;
    }

    // Getters y setters

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getUsuarioSol() {
        return usuarioSol;
    }

    public void setUsuarioSol(String usuarioSol) {
        this.usuarioSol = usuarioSol;
    }

    public String getPasswordSol() {
        return passwordSol;
    }

    public void setPasswordSol(String passwordSol) {
        this.passwordSol = passwordSol;
    }

    public boolean isProduccion() {
        return produccion;
    }

    public void setProduccion(boolean produccion) {
        this.produccion = produccion;
    }

    public String getEndpointBeta() {
        return endpointBeta;
    }

    public void setEndpointBeta(String endpointBeta) {
        this.endpointBeta = endpointBeta;
    }

    public String getEndpointProduccion() {
        return endpointProduccion;
    }

    public void setEndpointProduccion(String endpointProduccion) {
        this.endpointProduccion = endpointProduccion;
    }

    public String getCertificadoPath() {
        return certificadoPath;
    }

    public void setCertificadoPath(String certificadoPath) {
        this.certificadoPath = certificadoPath;
    }

    public String getCertificadoPassword() {
        return certificadoPassword;
    }

    public void setCertificadoPassword(String certificadoPassword) {
        this.certificadoPassword = certificadoPassword;
    }
}
