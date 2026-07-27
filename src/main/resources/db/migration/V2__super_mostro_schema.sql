-- 1. Seguridad
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL
);

-- 2. Maestros y Catálogos SUNAT
CREATE TABLE catalogos_sunat (
    id BIGSERIAL PRIMARY KEY,
    catalogo_codigo VARCHAR(3) NOT NULL,
    elemento_codigo VARCHAR(10) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    abreviatura VARCHAR(50),
    CONSTRAINT uq_catalogo_elemento UNIQUE (catalogo_codigo, elemento_codigo)
);

-- 3. Entidades (Clientes y Proveedores)
CREATE TABLE entidades (
    id BIGSERIAL PRIMARY KEY,
    tipo_entidad_id VARCHAR(1) NOT NULL, -- Catálogo 06 (1=DNI, 6=RUC)
    numero_documento VARCHAR(20) NOT NULL UNIQUE,
    nombre_razon_social VARCHAR(255) NOT NULL,
    direccion VARCHAR(255),
    correo VARCHAR(255)
);

-- 4. Catálogo de Productos
CREATE TABLE productos (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NOT NULL,
    precio_unitario NUMERIC(12,4) NOT NULL,
    tipo_afectacion_igv_id VARCHAR(10) NOT NULL, -- Código SUNAT Catálogo 07 (ej. '10', '20')
    unidad_medida_id VARCHAR(10) NOT NULL, -- Código SUNAT Catálogo 03 (ej. 'NIU', 'ZZ')
    stock_actual NUMERIC(12,4) NOT NULL DEFAULT 0.0000
);

-- 5. Control de Movimientos (Kardex)
CREATE TABLE kardex (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    tipo_movimiento VARCHAR(20) NOT NULL, -- COMPRA, VENTA, AJUSTE
    cantidad NUMERIC(12,4) NOT NULL,
    precio_unitario NUMERIC(12,4) NOT NULL,
    stock_resultante NUMERIC(12,4) NOT NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_kardex_producto FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE
);

-- 6. Compras y Detalles
CREATE TABLE compras (
    id BIGSERIAL PRIMARY KEY,
    tipo_documento VARCHAR(2) NOT NULL,
    serie VARCHAR(4) NOT NULL,
    numero INTEGER NOT NULL,
    proveedor_id BIGINT NOT NULL,
    fecha_emision DATE NOT NULL,
    total_gravada NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    total_igv NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    total_pagar NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_compra_proveedor FOREIGN KEY (proveedor_id) REFERENCES entidades(id)
);

CREATE TABLE compras_detalles (
    id BIGSERIAL PRIMARY KEY,
    compra_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad NUMERIC(12,4) NOT NULL,
    precio_unitario NUMERIC(12,4) NOT NULL,
    CONSTRAINT fk_compra_detalle_compra FOREIGN KEY (compra_id) REFERENCES compras(id) ON DELETE CASCADE,
    CONSTRAINT fk_compra_detalle_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
);

-- 7. Modificaciones a Empresa (Credenciales de Producción y Certificados)
ALTER TABLE empresa ADD COLUMN usuario_sol_produccion VARCHAR(50);
ALTER TABLE empresa ADD COLUMN password_sol_produccion VARCHAR(255);
ALTER TABLE empresa ADD COLUMN modo_produccion BOOLEAN DEFAULT FALSE;
ALTER TABLE empresa ADD COLUMN certificado_base64 TEXT;

-- 8. Modificaciones a Comprobantes (Ventas Avanzadas)
ALTER TABLE comprobantes ADD COLUMN forma_pago VARCHAR(20) NOT NULL DEFAULT 'CONTADO';
ALTER TABLE comprobantes ADD COLUMN detraccion_codigo VARCHAR(10);
ALTER TABLE comprobantes ADD COLUMN detraccion_porcentaje NUMERIC(5,2);
ALTER TABLE comprobantes ADD COLUMN detraccion_monto NUMERIC(12,2);
ALTER TABLE comprobantes ADD COLUMN descuento_global NUMERIC(12,2) NOT NULL DEFAULT 0.00;
ALTER TABLE comprobantes ADD COLUMN total_impuesto_bolsa NUMERIC(12,2) NOT NULL DEFAULT 0.00;
ALTER TABLE comprobantes ADD COLUMN anticipo_referencia VARCHAR(255);
ALTER TABLE comprobantes ADD COLUMN saldo_pendiente NUMERIC(12,2) NOT NULL DEFAULT 0.00;

-- Notas de Crédito / Débito
ALTER TABLE comprobantes ADD COLUMN documento_modificado_id VARCHAR(50);
ALTER TABLE comprobantes ADD COLUMN documento_modificado_tipo VARCHAR(2);
ALTER TABLE comprobantes ADD COLUMN nota_motivo_codigo VARCHAR(10);
ALTER TABLE comprobantes ADD COLUMN nota_motivo_descripcion VARCHAR(255);

ALTER TABLE comprobante_detalles ADD COLUMN tipo_unidad VARCHAR(10) NOT NULL DEFAULT 'NIU';
ALTER TABLE comprobante_detalles ADD COLUMN tipo_afectacion_igv VARCHAR(10) NOT NULL DEFAULT '10';
ALTER TABLE comprobante_detalles ADD COLUMN impuesto_bolsa NUMERIC(12,2) NOT NULL DEFAULT 0.00;
ALTER TABLE comprobante_detalles ADD COLUMN codigo_interno VARCHAR(50);

-- 9. Cuotas de Pago al Crédito
CREATE TABLE cuotas_pago (
    id BIGSERIAL PRIMARY KEY,
    comprobante_id BIGINT NOT NULL,
    numero_cuota INTEGER NOT NULL,
    monto NUMERIC(12,2) NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    CONSTRAINT fk_cuota_comprobante FOREIGN KEY (comprobante_id) REFERENCES comprobantes(id) ON DELETE CASCADE
);

-- 10. Cobros y Pagos (Caja)
CREATE TABLE cobros_pagos (
    id BIGSERIAL PRIMARY KEY,
    comprobante_id BIGINT,
    compra_id BIGINT,
    monto NUMERIC(12,2) NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL, -- EFECTIVO, TRANSFERENCIA, YAPE_PLIN
    fecha_pago TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cobro_comprobante FOREIGN KEY (comprobante_id) REFERENCES comprobantes(id) ON DELETE CASCADE,
    CONSTRAINT fk_pago_compra FOREIGN KEY (compra_id) REFERENCES compras(id) ON DELETE CASCADE
);

-- 11. Vehículos, Choferes y Guías de Remisión
CREATE TABLE vehiculos (
    id BIGSERIAL PRIMARY KEY,
    placa VARCHAR(10) NOT NULL UNIQUE,
    marca VARCHAR(50),
    modelo VARCHAR(50),
    nro_autorizacion VARCHAR(100)
);

CREATE TABLE choferes (
    id BIGSERIAL PRIMARY KEY,
    tipo_documento VARCHAR(1) NOT NULL,
    numero_documento VARCHAR(15) NOT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL,
    licencia_conducir VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE guias_remision (
    id BIGSERIAL PRIMARY KEY,
    tipo_guia VARCHAR(2) NOT NULL, -- 09 = Remitente, 31 = Transportista
    serie VARCHAR(4) NOT NULL,
    numero INTEGER NOT NULL,
    fecha_emision DATE NOT NULL,
    comprobante_id BIGINT,
    cliente_id BIGINT NOT NULL,
    chofer_id BIGINT NOT NULL,
    vehiculo_id BIGINT NOT NULL,
    motivo_traslado VARCHAR(2) NOT NULL, -- Catálogo 20 de la SUNAT
    peso_total NUMERIC(12,4) NOT NULL,
    sunat_response_code VARCHAR(255),
    sunat_description VARCHAR(1000),
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_guia_comprobante FOREIGN KEY (comprobante_id) REFERENCES comprobantes(id) ON DELETE SET NULL,
    CONSTRAINT fk_guia_cliente FOREIGN KEY (cliente_id) REFERENCES entidades(id),
    CONSTRAINT fk_guia_chofer FOREIGN KEY (chofer_id) REFERENCES choferes(id),
    CONSTRAINT fk_guia_vehiculo FOREIGN KEY (vehiculo_id) REFERENCES vehiculos(id)
);
