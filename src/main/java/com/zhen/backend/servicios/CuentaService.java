package com.zhen.backend.servicios;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.zhen.backend.exception.*;
import com.zhen.backend.model.Cuenta;
import com.zhen.backend.model.Movimiento;
import com.zhen.backend.model.enumerables.EstadoCliente;
import com.zhen.backend.respository.ClienteRepository;
import com.zhen.backend.respository.CuentaRepository;
import com.zhen.backend.respository.MovimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CuentaService {
    @Autowired
    CuentaRepository cuentaRepository;

    @Autowired
    MovimientoRepository movimientoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    public BigDecimal getSaldoCuenta(Cuenta cuenta) {
        Optional<Movimiento> ultimoMovimiento = movimientoRepository.findFirstByCuentaIdOrderByCreationTimestampDesc(cuenta.getNumeroCuenta());
        if(ultimoMovimiento.isPresent()) {
            return ultimoMovimiento.get().getSaldo();
        }
        return cuenta.getSaldoInicial();
    }

    public Long createCuenta(Cuenta cuenta) throws JpaCreateHaveIdException, ClienteIsNotActiveException {
        if (cuenta.getNumeroCuenta() != null)
            throw new JpaCreateHaveIdException();
        if (cuenta.getCliente().getEstado()!= EstadoCliente.ACTIVO)
            throw new ClienteIsNotActiveException();
        Cuenta guardado = cuentaRepository.save(cuenta);
        return guardado.getNumeroCuenta();
    }

    public Long updateCuenta(Cuenta cuenta) throws JpaUpdateMissingIdException, JpaNotFoundException {
        if (cuenta.getNumeroCuenta() == null)
            throw new JpaUpdateMissingIdException();
        if (!cuentaRepository.existsById(cuenta.getNumeroCuenta()))
            throw new JpaNotFoundException(cuenta.getNumeroCuenta());
        Cuenta guardado = cuentaRepository.save(cuenta);
        return guardado.getNumeroCuenta();
    }


    ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

    public void patchCuenta(Long numeroCuenta, JsonPatch patch) throws JpaNotFoundException, JsonPatchException, JsonProcessingException {

        Optional<Cuenta> optional = cuentaRepository.findById(numeroCuenta);

        if (optional.isEmpty())
            throw new JpaNotFoundException(numeroCuenta);
        Cuenta cuenta = optional.get();
        Cuenta cuentaPatched = objectMapper.treeToValue(patch.apply(objectMapper.convertValue(cuenta, JsonNode.class)), Cuenta.class);
        cuentaPatched.setCliente(clienteRepository.findById(cuentaPatched.getClienteId()).orElseThrow(() -> new JpaNotFoundException(cuentaPatched.getClienteId())));
        if (cuentaPatched.getNumeroCuenta() != numeroCuenta)
            throw new JsonPatchException("No se puede actualizar el ID.");

        cuentaRepository.save(cuentaPatched);
    }

    public void deleteCuenta(Long numeroCuenta) throws JpaNotFoundException {
        if (!cuentaRepository.existsById(numeroCuenta))
            throw new JpaNotFoundException(numeroCuenta);
        List<Long> movimientos = movimientoRepository.findIdsByNumeroCuenta(numeroCuenta);
        if(movimientos.size()>0)
            throw new CuentaHaveMovimientosException(numeroCuenta, movimientos);
        cuentaRepository.deleteById(numeroCuenta);
    }

    public Cuenta getCuenta(Long numeroCuenta) {
        Optional<Cuenta> optional = cuentaRepository.findById(numeroCuenta);
        return optional.orElseThrow(() -> new JpaNotFoundException(numeroCuenta));
    }

    public List<Cuenta> getAllCuentas() {
        return cuentaRepository.findAll();
    }

    public List<Cuenta> getAllCuentas(Pageable pageable) {
        return cuentaRepository.findAll(pageable).getContent();
    }

    public List<Cuenta> getAllCuentas(Specification<Cuenta> cuentaSpecification, Pageable pageable) {
        return cuentaRepository.findAll(cuentaSpecification, pageable).getContent();
    }

    public List<Cuenta> getAllCuentas(Specification<Cuenta> cuentaSpecification, Sort sort) {
        return cuentaRepository.findAll(cuentaSpecification, sort);
    }

    public List<Cuenta> getAllCuentas(Specification<Cuenta> cuentaSpecification) {
        return cuentaRepository.findAll(cuentaSpecification);
    }
    public List<Cuenta> getAllCuentas(Sort sort) {
        return cuentaRepository.findAll(sort);
    }

    public List<Cuenta> getAllCuentasWithClientId(Long clientId) {
        return cuentaRepository.findAllByClienteId(clientId);
    }
}
