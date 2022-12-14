package com.zhen.backend.servicios;

import com.zhen.backend.dto.CuentaDtoEstado;
import com.zhen.backend.dto.ReporteEstadoDeCuentas;
import com.zhen.backend.exception.JpaCreateHaveIdException;
import com.zhen.backend.exception.JpaNotFoundException;
import com.zhen.backend.model.Cliente;
import com.zhen.backend.model.Cuenta;
import com.zhen.backend.model.Movimiento;
import com.zhen.backend.respository.ClienteRepository;
import com.zhen.backend.respository.CuentaRepository;
import com.zhen.backend.respository.MovimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReporteService {

    @Autowired
    MovimientoRepository movimientoRepository;

    @Autowired
    ClienteRepository clientRepository;
    @Autowired
    CuentaRepository cuentaRepository;

    @Autowired
    CuentaService cuentaService;

    private ReporteEstadoDeCuentas createReporteEstadoDeCuentas(Long clientId) {
        ReporteEstadoDeCuentas reporteEstadoDeCuentas = new ReporteEstadoDeCuentas();
        Optional<Cliente> clienteOptional = clientRepository.findById(clientId);
        if (clienteOptional.isEmpty())
            throw new JpaNotFoundException("No existe el cliente con id: " + clientId, clientId);
        reporteEstadoDeCuentas.setCliente(clienteOptional.get());
        return reporteEstadoDeCuentas;
    }

    private List<Cuenta> getCuentas(Long clientId) {
        return cuentaRepository.findAllByClienteId(clientId);
    }

    private List<Movimiento> getMovimientos(Cuenta cuenta, LocalDateTime fechaInicial, LocalDateTime fechaFinal) {
        if (fechaInicial == null) {
            if (fechaFinal == null)
                return movimientoRepository.findAllByCuentaId(cuenta.getNumeroCuenta());
            return movimientoRepository.findAllByCuentaIdAndCreationTimestampBefore(cuenta.getNumeroCuenta(), fechaFinal);
        }
        if (fechaFinal == null)
            return movimientoRepository.findAllByCuentaIdAndCreationTimestampAfter(cuenta.getNumeroCuenta(), fechaInicial);
        return movimientoRepository.findAllByCuentaIdAndCreationTimestampBetween(cuenta.getNumeroCuenta(), fechaInicial, fechaFinal);
    }

    private ReporteEstadoDeCuentas setCuentaDtoEstado(ReporteEstadoDeCuentas reporte) {
        List<CuentaDtoEstado> cuentaDtoEstadosList = new ArrayList<>();

        LocalDateTime inicio = null;
        if(reporte.getFechaInicial()!=null)
            inicio=reporte.getFechaInicial().atStartOfDay();
        LocalDateTime fin = null;
        if(reporte.getFechaFinal()!=null)
            fin = LocalTime.MAX.atDate(reporte.getFechaFinal());
        for (Cuenta cuenta :
                getCuentas(reporte.getCliente().getId())) {

            CuentaDtoEstado cuentaDtoEstado = new CuentaDtoEstado(cuenta, cuentaService.getSaldoCuenta(cuenta));
            BigDecimal totalCredito = new BigDecimal("0.00");
            BigDecimal totalDebito = new BigDecimal("0.00");
            List<Movimiento> movimientos = getMovimientos(cuenta, inicio, fin);
            for (Movimiento movimiento :
                    movimientos) {
                BigDecimal valor = movimiento.getValor();
                if (valor.signum() == 1)
                    totalCredito = totalCredito.add(valor);
                else
                    totalDebito = totalDebito.add(valor.negate());
            }
            cuentaDtoEstado.setTotalCredito(totalCredito);
            cuentaDtoEstado.setTotalDebito(totalDebito);
            cuentaDtoEstadosList.add(cuentaDtoEstado);
        }
        reporte.setCuentas(cuentaDtoEstadosList);
        return reporte;
    }

    public ReporteEstadoDeCuentas getClienteReporteEstadoDeCuentas(Long clientId, LocalDate fechaInicial, LocalDate fechaFinal) {
        ReporteEstadoDeCuentas reporte = createReporteEstadoDeCuentas(clientId);
        reporte.setFechaInicial(fechaInicial);
        reporte.setFechaFinal(fechaFinal);
        return setCuentaDtoEstado(reporte);
    }
}
