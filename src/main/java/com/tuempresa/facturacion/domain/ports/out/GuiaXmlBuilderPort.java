package com.tuempresa.facturacion.domain.ports.out;

import com.tuempresa.facturacion.domain.model.GuiaRemision;
import com.tuempresa.facturacion.domain.model.Empresa;
import com.tuempresa.facturacion.domain.model.Entidad;
import com.tuempresa.facturacion.domain.model.Chofer;
import com.tuempresa.facturacion.domain.model.Vehiculo;
import org.w3c.dom.Document;

import java.util.List;
import com.tuempresa.facturacion.domain.model.ComprobanteDetalle;

public interface GuiaXmlBuilderPort {
    Document construir(GuiaRemision de, Empresa empresa, Entidad cliente, Chofer chofer, Vehiculo vehiculo, List<ComprobanteDetalle> detalles);
}
