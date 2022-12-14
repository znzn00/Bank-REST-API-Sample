package com.zhen.backend.controller;

import com.zhen.backend.dto.ReporteEstadoDeCuentas;
import com.zhen.backend.servicios.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/reportes")
public class ReportesController {

    @Autowired
    ReporteService reporteService;

    @GetMapping("/cliente/{clientId}/estado/cuentas")
    public ReporteEstadoDeCuentas getEstadoDeCuentasDeCliente(@PathVariable("clientId") Long clientId, @RequestParam(required = false) String fechaInicial, @RequestParam(required = false) String fechaFinal) {
        LocalDate start = null;
        if (fechaInicial != null)
            start = LocalDate.parse(fechaInicial);
        LocalDate end = null;
        if (fechaFinal != null)
            end = LocalDate.parse(fechaFinal);
        return reporteService.getClienteReporteEstadoDeCuentas(clientId, start, end);
    }
}
