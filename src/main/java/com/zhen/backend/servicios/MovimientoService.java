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
import com.zhen.backend.model.enumerables.EstadoCuenta;
import com.zhen.backend.model.enumerables.EstadoMovimiento;
import com.zhen.backend.model.enumerables.TipoMovimiento;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MovimientoService {
    @Autowired
    MovimientoRepository movimientoRepository;

    @Autowired
    CuentaRepository cuentaRepository;

    public Long createMovimiento(Movimiento movimiento) throws RuntimeException {
        if (movimiento.getId() != null)
            throw new JpaCreateHaveIdException();
        if (movimiento.getSaldo() != null)
            throw new MovimientoCreateHaveSaldoException();
        if (movimiento.getValor() == null || movimiento.getValor().stripTrailingZeros().equals(BigDecimal.ZERO.stripTrailingZeros()))
            throw new MovimientoCreateMissingValorException();

        EstadoMovimiento estado = movimiento.getEstado();
        if (estado == EstadoMovimiento.CANCELADO || estado == EstadoMovimiento.REVERSADO) {
            throw new MovimientoCreateInvalidEstadoException(estado);
        }

//        verificar que la orientacion del tipo este correcto
        TipoMovimiento.Orientacion orientacion = movimiento.getTipo().getOrientacion();
        if (orientacion == TipoMovimiento.Orientacion.CREDITO) {
            if (movimiento.getValor().signum() != 1)
                throw new MovimientoCreateInvalidValorException();
            if (estado == EstadoMovimiento.PENDIENTE)
                throw new MovimientoCreateInvalidEstadoException(estado);
        }
        if (orientacion == TipoMovimiento.Orientacion.DEBITO) {
            if (movimiento.getValor().signum() != -1)
                throw new MovimientoCreateInvalidValorException();
        }


        Cuenta cuenta = cuentaRepository.findById(movimiento.getCuentaId()).orElseThrow(() -> new JpaNotFoundException(movimiento.getCuentaId()));

//        Verificando que esten activa.
        if(cuenta.getEstado()!= EstadoCuenta.ACTIVO)
            throw new CuentaIsNotActiveException();
        if(cuenta.getCliente().getEstado()!= EstadoCliente.ACTIVO)
            throw new ClienteIsNotActiveException();

        Optional<Movimiento> optionalMovimiento = movimientoRepository.findFirstByCuentaIdOrderByCreationTimestampDesc(movimiento.getCuentaId());
        if (optionalMovimiento.isPresent()) {
            movimiento.setSaldo(optionalMovimiento.get().getSaldo().add(movimiento.getValor()));
        } else {
            movimiento.setSaldo(cuenta.getSaldoInicial().add(movimiento.getValor()));
        }
        if (movimiento.getSaldo().signum() == -1)
            throw new MovimientoLimitPassException("El saldo de la cuenta no se suficiente.", movimiento.getSaldo().abs());

        if (movimiento.getTipo() == TipoMovimiento.RETIRO) {
            LocalDate localDate = LocalDate.now();
            BigDecimal acumulated = movimientoRepository.getSumOfAllByCuentaIdAndTipoAndCreationTimestampBetween(movimiento.getCuentaId(), TipoMovimiento.RETIRO, localDate.atStartOfDay(), LocalTime.MAX.atDate(localDate));
            if(acumulated!=null) {
                acumulated = acumulated.add(movimiento.getValor()).abs();
                BigDecimal dailyLimit = new BigDecimal("1000.00");
                if (acumulated.compareTo(dailyLimit) > 0)
                    throw new MovimientoLimitPassException("Se ha pasado del limite diario.", acumulated.add(dailyLimit.negate()));
            }

        }

        movimiento.setCuenta(cuenta);

        Movimiento guardado = movimientoRepository.save(movimiento);
        return guardado.getId();
    }

    /**
     * Verica que el cambio al movimiento es posible.
     *
     * @param movimiento
     * @throws RuntimeException
     */
    private void verifyOld(Movimiento movimiento) throws RuntimeException {
        EstadoMovimiento pasado = movimiento.getEstadoPasado();

        if (pasado == EstadoMovimiento.REVERSADO || pasado == EstadoMovimiento.CANCELADO)
            throw new MovimientoUpdateInvalidException("No se puede cambiar el estado de un movimiento con estado: " + pasado.toString());


        Cuenta cuenta = movimiento.getCuenta();
        if(cuenta.getEstado()!= EstadoCuenta.ACTIVO)
            throw new CuentaIsNotActiveException();
        if(cuenta.getCliente().getEstado()!= EstadoCliente.ACTIVO)
            throw new ClienteIsNotActiveException();

//        Verificar
        if ((movimiento.getEstado() == EstadoMovimiento.REVERSADO
                && pasado != EstadoMovimiento.COMPLETADO)
                || ((movimiento.getEstado() == EstadoMovimiento.CANCELADO || movimiento.getEstado() == EstadoMovimiento.COMPLETADO)
                && pasado != EstadoMovimiento.PENDIENTE))
            throw new MovimientoUpdateInvalidException("No se puede cambiar el estado desde \"" + pasado.toString() + "\" a \"" + movimiento.getEstado().toString() + "\".");

        if (movimiento.getEstado() != EstadoMovimiento.COMPLETADO) {
            Movimiento ajuste = new Movimiento();
            ajuste.setCuenta(movimiento.getCuenta());
            ajuste.setCuentaId(movimiento.getCuentaId());
            ajuste.setTipo(TipoMovimiento.AJUSTE);
            ajuste.setEstado(EstadoMovimiento.COMPLETADO);
            ajuste.setValor(movimiento.getValor().negate());
            ajuste.setDescripcion("Ajuste del movimiento con ID \"" + movimiento.getId()+"\" debido a que fue "+movimiento.getEstado().toString()+".");
            createMovimiento(ajuste);
        }

    }

    public Long updateMovimiento(Movimiento movimiento) throws RuntimeException {
        if (movimiento.getId() == null)
            throw new JpaUpdateMissingIdException();
        if (!movimientoRepository.existsById(movimiento.getId()))
            throw new JpaNotFoundException(movimiento.getId());

        verifyOld(movimiento);
        Movimiento guardado = movimientoRepository.save(movimiento);
        return guardado.getId();
    }

    ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

    public void patchMovimiento(Long id, JsonPatch patch) throws JpaNotFoundException, JsonPatchException, JsonProcessingException {
        Optional<Movimiento> optional = movimientoRepository.findById(id);
        if (optional.isEmpty())
            throw new JpaNotFoundException(id);
        Movimiento movimiento = optional.get();
        Movimiento movimientoPatched = objectMapper.treeToValue(patch.apply(objectMapper.convertValue(movimiento, JsonNode.class)), Movimiento.class);
        if (movimientoPatched.getId() != id)
            throw new JsonPatchException("No se puede actualizar el ID.");

        movimientoPatched.setCuenta(movimiento.getCuenta());
        movimientoPatched.setCuentaId(movimiento.getCuentaId());
        verifyOld(movimientoPatched);

        movimientoRepository.save(movimientoPatched);
    }

    public void deleteMovimiento(Long id) throws JpaNotFoundException {
        if (!movimientoRepository.existsById(id))
            throw new JpaNotFoundException(id);
        movimientoRepository.deleteById(id);
    }

    public Movimiento getMovimiento(Long id) {
        Optional<Movimiento> optional = movimientoRepository.findById(id);
        return optional.orElseThrow(() -> new JpaNotFoundException(id));
    }

    public List<Movimiento> getAllMovimientos() {
        return movimientoRepository.findAll();
    }

    public List<Movimiento> getAllMovimientos(Pageable pageable) {
        return movimientoRepository.findAll(pageable).getContent();
    }

    public List<Movimiento> getAllMovimientos(Specification<Movimiento> movimientoSpecification, Pageable pageable) {
        return movimientoRepository.findAll(movimientoSpecification, pageable).getContent();
    }

    public List<Movimiento> getAllMovimientos(Specification<Movimiento> movimientoSpecification, Sort sort) {
        return movimientoRepository.findAll(movimientoSpecification, sort);
    }

    public List<Movimiento> getAllMovimientos(Specification<Movimiento> movimientoSpecification) {
        return movimientoRepository.findAll(movimientoSpecification);
    }

    public List<Movimiento> getAllMovimientos(Sort sort) {
        return movimientoRepository.findAll(sort);
    }
}
