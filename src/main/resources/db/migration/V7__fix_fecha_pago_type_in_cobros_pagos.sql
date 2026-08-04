ALTER TABLE cobros_pagos
    ALTER COLUMN fecha_pago TYPE DATE USING fecha_pago::date;