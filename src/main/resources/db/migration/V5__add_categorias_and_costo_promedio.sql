-- Creación de la tabla categorias
CREATE TABLE categorias (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

-- Modificar productos para agregar precio_costo_promedio y categoria_id
ALTER TABLE productos ADD COLUMN precio_costo_promedio NUMERIC(12,4) NOT NULL DEFAULT 0.0000;
ALTER TABLE productos ADD COLUMN categoria_id BIGINT;

-- Añadir restricción de llave foránea
ALTER TABLE productos ADD CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE SET NULL;
