package com.zhen.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.zhen.backend.controller.reponse.MessageWithMovimientoResponse;
import com.zhen.backend.controller.reponse.SingleMessageResponse;
import com.zhen.backend.dto.CuentaDtoOut;
import com.zhen.backend.dto.CuentaDtoPostIn;
import com.zhen.backend.dto.CuentaDtoPutIn;
import com.zhen.backend.exception.ClienteIsNotActiveException;
import com.zhen.backend.exception.CuentaHaveMovimientosException;
import com.zhen.backend.exception.JpaCreateHaveIdException;
import com.zhen.backend.exception.JpaNotFoundException;
import com.zhen.backend.model.Cuenta;
import com.zhen.backend.model.enumerables.EstadoCuenta;
import com.zhen.backend.servicios.ClienteService;
import com.zhen.backend.servicios.CuentaService;
import com.zhen.backend.util.QueryToJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cuentas")
public class CuentasController {
    @Autowired
    CuentaService cuentaService;

    @Autowired
    ClienteService clienteService;

    private List<Cuenta> getAllCuentasList(QueryToJpa<Cuenta> cuentaQueryToJpa) {
        Specification<Cuenta> specification = cuentaQueryToJpa.getSpecification();
        Pageable pageable = cuentaQueryToJpa.getPageable();

        if(specification!=null) {
            if(pageable!=null)
                return cuentaService.getAllCuentas(specification, pageable);
            if(cuentaQueryToJpa.getSort()!=null)
                return cuentaService.getAllCuentas(specification, cuentaQueryToJpa.getSort());
            return cuentaService.getAllCuentas(specification);
        }

        if(pageable!=null)
            return cuentaService.getAllCuentas(pageable);
        if(cuentaQueryToJpa.getSort()!=null)
            return cuentaService.getAllCuentas(cuentaQueryToJpa.getSort());
        return cuentaService.getAllCuentas();
    }
    //    Get
    @GetMapping
    public List<CuentaDtoOut> getAllCuentas(@RequestParam Map<String, String> queryParams) {
        QueryToJpa<Cuenta> cuentaQueryToJpa = new QueryToJpa<>(queryParams);
        List<Cuenta> cuentas = getAllCuentasList(cuentaQueryToJpa);
        return cuentas.stream().map(c -> new CuentaDtoOut(c, cuentaService.getSaldoCuenta(c))).collect(Collectors.toList());
    }

    @GetMapping("/{numeroCuenta}")
    public CuentaDtoOut getCuenta(@PathVariable("numeroCuenta") Long numeroCuenta) {
        Cuenta cuenta = cuentaService.getCuenta(numeroCuenta);
        CuentaDtoOut cuentaDtoOut = new CuentaDtoOut(cuenta, cuentaService.getSaldoCuenta(cuenta));
        return cuentaDtoOut;
    }


    //    Post
    @PostMapping
    public ResponseEntity<URI> createCuenta(@RequestBody @Valid CuentaDtoPostIn cuentaDtoPostIn) {
        Cuenta cuenta = new Cuenta();
        cuenta.setCliente(clienteService.getCliente(cuentaDtoPostIn.getCliente()));
        cuenta.setSaldoInicial(cuentaDtoPostIn.getSaldoInicial());
        cuenta.setTipo(cuentaDtoPostIn.getTipo());
        cuenta.setEstado(EstadoCuenta.ACTIVO);
        var numeroCuenta = cuentaService.createCuenta(cuenta);
        var cuentaLocation = ServletUriComponentsBuilder.fromCurrentRequest().path("/{numeroCuenta}").buildAndExpand(numeroCuenta).toUri();
        return ResponseEntity.created(cuentaLocation).build();
    }

    //    Put
    @PutMapping
    public ResponseEntity<URI> createOrUpdateCuenta(@RequestBody @Valid CuentaDtoPutIn cuentaDtoPutIn) {

        if (cuentaDtoPutIn.getNumeroCuenta() != null && cuentaDtoPutIn.getEstado()!=null) {
            Cuenta cuenta = cuentaService.getCuenta(cuentaDtoPutIn.getNumeroCuenta());
            cuenta.setEstado(cuentaDtoPutIn.getEstado());
            cuentaService.updateCuenta(cuenta);

            return ResponseEntity.noContent().build();
        }
        CuentaDtoPostIn cuentaDtoPostIn = new CuentaDtoPostIn();
        cuentaDtoPostIn.setTipo(cuentaDtoPutIn.getTipo());
        cuentaDtoPostIn.setCliente(cuentaDtoPutIn.getCliente());
        cuentaDtoPostIn.setSaldoInicial(cuentaDtoPutIn.getSaldoInicial());
        return createCuenta(cuentaDtoPostIn);
    }


    //    Patch
    @PatchMapping("/{numeroCuenta}")
    public ResponseEntity<Object> updateCuenta(@PathVariable("numeroCuenta") Long numeroCuenta, @RequestBody JsonPatch patch) throws JsonPatchException, JsonProcessingException {
        if (patch == null)
            return ResponseEntity.noContent().build();
        cuentaService.patchCuenta(numeroCuenta, patch);
        return ResponseEntity.ok().build();
    }

    //    Delete
    @DeleteMapping("/{numeroCuenta}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCuenta(@PathVariable("numeroCuenta") Long numeroCuenta) {
        cuentaService.deleteCuenta(numeroCuenta);
    }

    @ExceptionHandler(value = JpaCreateHaveIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SingleMessageResponse captureJpaCreateHaveIdException() {
        return new SingleMessageResponse("La cuenta a crear no puede contener ID.");
    }

    @ExceptionHandler(value = JpaNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public SingleMessageResponse captureJpaNotFoundException(JpaNotFoundException e) {
        return new SingleMessageResponse("No se encontro la cuenta con el numero: " + e.getId());
    }

    @ExceptionHandler(value = {JsonProcessingException.class, JsonPatchException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public SingleMessageResponse captureJsonProcessingExceptionAndJsonPatchException() {
        return new SingleMessageResponse("Error causado mientras se procesaba un JSON.");
    }

    @ExceptionHandler(value = {ClienteIsNotActiveException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public SingleMessageResponse captureClienteIsNotActiveException() {
        return new SingleMessageResponse("El cliente que se le creara una cuenta no esta activo.");
    }

    @ExceptionHandler(value = {CuentaHaveMovimientosException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public MessageWithMovimientoResponse captureCuentaHaveMovimientosException(CuentaHaveMovimientosException e) {
        return new MessageWithMovimientoResponse("La cuenta a eliminar no puede ser eliminada debido a que tiene movimientos.", e.getMovimientos());
    }


}
