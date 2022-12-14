package com.zhen.backend.controller.reponse;

import java.util.List;

public record MessageWithMovimientoResponse(String mensaje, List<Long> movimientos) {
}
