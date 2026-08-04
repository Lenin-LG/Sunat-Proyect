-- Catalogo 01: Tipo de Comprobante
INSERT INTO catalogos_sunat (catalogo_codigo, elemento_codigo, descripcion, abreviatura) VALUES
('01', '01', 'FACTURA ELECTRONICA', 'FACTURA'),
('01', '03', 'BOLETA DE VENTA ELECTRONICA', 'BOLETA'),
('01', '07', 'NOTA DE CREDITO ELECTRONICA', 'NOTA CREDITO'),
('01', '08', 'NOTA DE DEBITO ELECTRONICA', 'NOTA DEBITO'),
('01', '09', 'GUIA DE REMISION REMITENTE', 'GUIA REMITENTE'),
('01', '31', 'GUIA DE REMISION TRANSPORTISTA', 'GUIA TRANSPORTISTA')
ON CONFLICT (catalogo_codigo, elemento_codigo) DO NOTHING;

-- Catalogo 03: Unidad de Medida
INSERT INTO catalogos_sunat (catalogo_codigo, elemento_codigo, descripcion, abreviatura) VALUES
('03', 'NIU', 'UNIDAD (BIENES)', 'UNIDAD'),
('03', 'ZZ', 'UNIDAD (SERVICIOS)', 'SERVICIO'),
('03', 'KGM', 'KILOGRAMO', 'KILO'),
('03', 'LTR', 'LITRO', 'LITRO')
ON CONFLICT (catalogo_codigo, elemento_codigo) DO NOTHING;

-- Catalogo 05: Codigo de Tributos
INSERT INTO catalogos_sunat (catalogo_codigo, elemento_codigo, descripcion, abreviatura) VALUES
('05', '1000', 'IGV - IMPUESTO GENERAL A LAS VENTAS', 'VAT'),
('05', '7152', 'ICBPER - IMPUESTO AL CONSUMO DE BOLSAS PLASTICAS', 'OTH'),
('05', '9997', 'EXONERADO', 'VAT'),
('05', '9998', 'INAFECTO', 'FRE')
ON CONFLICT (catalogo_codigo, elemento_codigo) DO NOTHING;

-- Catalogo 06: Tipo de Documento de Identidad
INSERT INTO catalogos_sunat (catalogo_codigo, elemento_codigo, descripcion, abreviatura) VALUES
('06', '0', 'DOC.TRIB.NO.DOM.SIN.RUC', 'SIN RUC'),
('06', '1', 'DOCUMENTO NACIONAL DE IDENTIDAD', 'DNI'),
('06', '4', 'CARNET DE EXTRANJERIA', 'C.EXT'),
('06', '6', 'REGISTRO UNICO DE CONTRIBUYENTES', 'RUC'),
('06', '7', 'PASAPORTE', 'PASAPORTE')
ON CONFLICT (catalogo_codigo, elemento_codigo) DO NOTHING;

-- Catalogo 07: Tipo de Afectacion del IGV
INSERT INTO catalogos_sunat (catalogo_codigo, elemento_codigo, descripcion, abreviatura) VALUES
('07', '10', 'GRAVADO - OPERACION ONEROSA', 'GRAVADO'),
('07', '20', 'EXONERADO - OPERACION ONEROSA', 'EXONERADO'),
('07', '30', 'INAFECTO - OPERACION ONEROSA', 'INAFECTO')
ON CONFLICT (catalogo_codigo, elemento_codigo) DO NOTHING;
