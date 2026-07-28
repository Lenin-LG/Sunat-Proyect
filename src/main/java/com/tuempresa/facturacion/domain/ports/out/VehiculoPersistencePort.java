package com.tuempresa.facturacion.domain.ports.out;

import com.tuempresa.facturacion.domain.model.Vehiculo;
import java.util.List;
import java.util.Optional;

public interface VehiculoPersistencePort {
    Vehiculo save(Vehiculo vehiculo);
    Optional<Vehiculo> findById(Long id);
    Optional<Vehiculo> findByPlaca(String placa);
    List<Vehiculo> findAll();
}
