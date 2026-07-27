package com.tuempresa.facturacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cuota {
    private Long id;
    private Integer numeroCuota;
    private BigDecimal monto;
    private LocalDate fechaVencimiento;
}
