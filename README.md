# Facturación SUNAT — Spring Boot (Arquitectura Hexagonal)

Servicio REST desarrollado en Spring Boot para emitir Facturas y Boletas Electrónicas consumiendo el servicio SOAP `sendBill` de la SUNAT en su ambiente de pruebas (Beta).

> **Alcance del Proyecto:**
> A nivel de código Java, este sistema implementa únicamente la operación SOAP **`sendBill`** (envío síncrono de facturas y boletas individuales).
> Para comprender, mapear y realizar pruebas manuales en SoapUI de todas las demás operaciones posibles expuestas por la SUNAT (`sendSummary`, `sendPack`, `getStatus`, `getStatusAR`), puedes guiarte paso a paso con el documento [guia_pruebas_soap_sunat.md] ubicado en la raíz de este proyecto.

---

## 🏛️ Estructura del Proyecto (Arquitectura Hexagonal)

Para garantizar un código limpio, mantenible y desacoplado del framework, estructuramos el proyecto siguiendo los principios de la **Arquitectura Hexagonal (Ports & Adapters)**. A continuación se explica la función de cada capa "con manzanitas":

```text
       [Cliente / Curl]
               ↓
     [Adaptadores de Entrada] (REST / Controller)
               ↓
    ======[ Capa de Aplicación ]======
    |  - Caso de uso (UseCase)       |
    |  - Orquestador (Service)       |
    ==================================
        ↓                      ↓
 [Dominio: Model]       [Dominio: Ports (Out)]
                               ↓
                    [Adaptadores de Salida]
                     - Persistencia (JPA)
                     - SUNAT SOAP Client (sendBill)
                     - Generación XML
                     - Firma Digital
```

### 1. Capa de Dominio (`domain`)
Es código Java puro. No contiene frameworks (ni Spring, ni JPA, ni Hibernate). Aquí vive el negocio de la facturación:
- **`domain.model`**: Contiene las entidades puras Comprobante,Empresa, ComprobanteDetalle que calculan los totales y formatean nombres.
- **`domain.ports.in`**: Define qué puede hacer el sistema el puerto de entrada;EmitirComprobanteUseCase.
- **`domain.ports.out`**: Define qué necesita el sistema de afuera para funcionar (puertos de salida: persistir datos, enviar SOAP, firmar XML, construir archivos).

### 2. Capa de Aplicación (`application`)
Orquesta el flujo de negocio sin conocer detalles técnicos de infraestructura (como qué base de datos se usa o qué servidor SOAP responde):
- **`application.service`**: El ComprobanteService implementa el caso de uso. Pide a la base de datos la empresa, calcula importes, manda a construir el XML, lo firma digitalmente y lo envía a la SUNAT a través de las interfaces de los puertos de salida.

### 3. Capa de Infraestructura (`infrastructure`)
Aquí se implementa el código relacionado con tecnologías específicas (Spring Boot, JPA, XMLDSig, SOAP, etc.):
- **`adapters.input.rest`**: ComprobanteRestAdapter expone los endpoints HTTP.
- **`adapters.output.persistence`**: Traduce los puertos de persistencia a entidades JPA y repositorios Spring Data.
- **`adapters.output.soap`**: Consume el Web Service de la SUNAT utilizando la plantilla SOAP de **`sendBill`** SunatSoapAdapter.
- **`adapters.output.xml`**: Construye la estructura XML oficial de UBL 2.1.
- **`adapters.output.crypto`**: Realiza la firma digital XMLDSig SHA1/RSA.
- **`config`**: Configuración de beans de Spring ApplicationConfig e inicializadores de base de datos.

---

## 2. Equivalencia de Procesos (Manual vs Código)

Cada clase de infraestructura corresponde exactamente a los pasos que harías de manera manual:

| Paso Manual (Terminal / SoapUI) | Clase / Adaptador Equivalente |
| :--- | :--- |
| Escribir el XML de la factura | [XmlBuilderAdapter]|
| `xmlsec1 --sign` con certificados PEM | [FirmaDigitalAdapter] |
| `zip` + `base64 -w 0` | [SunatSoapAdapter](interno) |
| Pegar el SOAP Request en SoapUI y enviarlo | [SunatSoapAdapter]|
| Descargar y parsear el CDR de respuesta | [SunatSoapAdapter](parseo) |

---

## 3. Requisitos
- JDK 17+
- Maven 3.8+
- Un certificado digital de PRUEBA en formato `.p12`

---

## 4. Generar el Certificado de Prueba

Si ya cuentas con los certificados PEM (`server_key.pem` y `server.pem`), conviértelos a Keystore `.p12` ejecutando:
```bash
openssl pkcs12 -export -out certificado_prueba.p12 \
  -inkey server_key.pem -in server.pem -name "prueba" \
  -passout pass:changeit
```

Si no los tienes, puedes generarlos desde cero con:
```bash
openssl req -x509 -newkey rsa:2048 -keyout server_key.pem -out server.pem \
  -days 365 -nodes -subj "/C=PE/O=PRUEBA/CN=20000000001"

openssl pkcs12 -export -out certificado_prueba.p12 \
  -inkey server_key.pem -in server.pem -name "prueba" \
  -passout pass:changeit
```
Coloca el archivo `certificado_prueba.p12` generado en la ruta:
`src/main/resources/certificados/certificado_prueba.p12`

---

## 5. Ejecutar la Aplicación
Puedes iniciar el servidor Spring Boot ejecutando:
```bash
mvn spring-boot:run
```
La aplicación levantará por defecto en el puerto `8080` (`http://localhost:8080`). Cuenta con una base de datos H2 en memoria para almacenar las entidades, cuya consola interactiva está disponible en `http://localhost:8080/h2-console`.

---

## 6. Probar la Emisión (API REST)

### Emitir una Boleta de Venta
Ejecuta el siguiente comando en tu consola para enviar una boleta de prueba:

```bash
curl -X POST http://localhost:8080/api/comprobantes/boleta \
  -H "Content-Type: application/json" \
  -d '{
    "serie": "B001",
    "clienteTipoDocumento": "1",
    "clienteNumeroDocumento": "12345678",
    "clienteNombre": "CLIENTE DE PRUEBA",
    "items": [
      {
        "descripcion": "PRODUCTO DE PRUEBA",
        "cantidad": 1,
        "precioUnitario": 100.00
      }
    ]
  }'
```

**Respuesta exitosa de SUNAT (CDR procesado):**
```json
{
  "id": 1,
  "tipoDocumento": "03",
  "serie": "B001",
  "numero": 1,
  "fechaEmision": "2026-07-10",
  "clienteTipoDocumento": "1",
  "clienteNumeroDocumento": "12345678",
  "clienteNombre": "CLIENTE DE PRUEBA",
  "totalGravada": 100.00,
  "totalIgv": 18.00,
  "totalPagar": 118.00,
  "estado": "ACEPTADO",
  "sunatResponseCode": "0",
  "sunatDescription": "La Boleta numero B001-1, ha sido aceptada"
}
```

### Emitir una Factura
Para emitir una factura, usa el mismo endpoint cambiando la ruta a `/api/comprobantes/factura`, especificando en el JSON un tipo de documento de cliente `6` (RUC) y un número de RUC válido.
