-- Creación de la tabla empresa
CREATE TABLE empresa (
    id BIGSERIAL PRIMARY KEY,
    ruc VARCHAR(11) NOT NULL UNIQUE,
    razon_social VARCHAR(255) NOT NULL,
    nombre_comercial VARCHAR(255),
    ubigeo VARCHAR(6) NOT NULL,
    departamento VARCHAR(255) NOT NULL,
    provincia VARCHAR(255) NOT NULL,
    distrito VARCHAR(255) NOT NULL,
    direccion_fiscal VARCHAR(255) NOT NULL
);

-- Creación de la tabla comprobantes
CREATE TABLE comprobantes (
    id BIGSERIAL PRIMARY KEY,
    tipo_documento VARCHAR(2) NOT NULL,
    serie VARCHAR(4) NOT NULL,
    numero INTEGER NOT NULL,
    fecha_emision DATE NOT NULL,
    cliente_tipo_documento VARCHAR(1) NOT NULL,
    cliente_numero_documento VARCHAR(15) NOT NULL,
    cliente_nombre VARCHAR(255) NOT NULL,
    total_gravada NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    total_igv NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    total_pagar NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    estado VARCHAR(50) NOT NULL,
    sunat_response_code VARCHAR(255),
    sunat_description VARCHAR(1000),
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    enviado_en TIMESTAMP
);

-- Creación de la tabla comprobante_detalles
CREATE TABLE comprobante_detalles (
    id BIGSERIAL PRIMARY KEY,
    comprobante_id BIGINT NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    cantidad NUMERIC(12,4) NOT NULL,
    precio_unitario NUMERIC(12,4) NOT NULL,
    codigo_producto_sunat VARCHAR(255),
    CONSTRAINT fk_comprobante FOREIGN KEY (comprobante_id) REFERENCES comprobantes(id) ON DELETE CASCADE
);
