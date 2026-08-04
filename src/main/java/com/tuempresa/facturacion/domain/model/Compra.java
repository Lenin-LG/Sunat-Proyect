package com.tuempresa.facturacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Compra {
    private Long id;
    private String tipoDocumento;
    private String serie;
    private Integer numero;
    private Long proveedorId;
    private LocalDate fechaEmision;
    private BigDecimal totalGravada;
    private BigDecimal totalIgv;
    private BigDecimal totalPagar;
    private LocalDateTime creadoEn;

    @Builder.Default
    private List<CompraDetalle> detalles = new ArrayList<>();
}
