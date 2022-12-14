package com.zhen.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.zhen.backend.controller.reponse.MessageWithMontoResponse;
import com.zhen.backend.controller.reponse.SingleMessageResponse;
import com.zhen.backend.dto.MovimientoDtoOut;
import com.zhen.backend.dto.MovimientoDtoPostIn;
import com.zhen.backend.dto.MovimientoDtoPutIn;
import com.zhen.backend.exception.*;
import com.zhen.backend.model.Movimiento;
import com.zhen.backend.servicios.MovimientoService;
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
@RequestMapping("/api/movimientos")
public class MovimientosController {
    @Autowired
    MovimientoService movimientoService;

    private List<Movimiento> getAllMovimientoList(QueryToJpa<Movimiento> movimientoQueryToJpa) {
        Specification<Movimiento> specification = movimientoQueryToJpa.getSpecification();
        Pageable pageable = movimientoQueryToJpa.getPageable();

        if (specification != null) {
            if (pageable != null)
                return movimientoService.getAllMovimientos(specification, pageable);
            if (movimientoQueryToJpa.getSort() != null)
                return movimientoService.getAllMovimientos(specification, movimientoQueryToJpa.getSort());
            return movimientoService.getAllMovimientos(specification);
        }

        if (pageable != null)
            return movimientoService.getAllMovimientos(pageable);
        if (movimientoQueryToJpa.getSort() != null)
            return movimientoService.getAllMovimientos(movimientoQueryToJpa.getSort());
        return movimientoService.getAllMovimientos();
    }

    //    Get
    @GetMapping
    public List<MovimientoDtoOut> getAllMovimientos(@RequestParam Map<String, String> queryParams) {
        List<Movimiento> movimientos = getAllMovimientoList(new QueryToJpa<Movimiento>(queryParams));
//        return movimientos;
        return movimientos.stream().map(m -> new MovimientoDtoOut(m)).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public MovimientoDtoOut getMoviemiento(@PathVariable("id") Long id) {
        return new MovimientoDtoOut(movimientoService.getMovimiento(id));
    }


    //    Post
    @PostMapping
    public ResponseEntity<URI> createMoviemiento(@RequestBody @Valid MovimientoDtoPostIn movimientoDtoPostIn) {
        Movimiento movimiento = new Movimiento();
        movimiento.setCuentaId(movimientoDtoPostIn.getCuenta());
        movimiento.setTipo(movimientoDtoPostIn.getTipo());
        movimiento.setEstado(movimientoDtoPostIn.getEstado());
        movimiento.setValor(movimientoDtoPostIn.getValor());
        movimiento.setDescripcion(movimientoDtoPostIn.getDescripcion());
        var id = movimientoService.createMovimiento(movimiento);
        var movimientoLocation = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(movimientoLocation).build();
    }

    //    Put
    @PutMapping
    public ResponseEntity<URI> createOrUpdateMoviemiento(@RequestBody MovimientoDtoPutIn movimientoDtoPutIn) {

        if (movimientoDtoPutIn.getId() != null && movimientoDtoPutIn.getEstado()!=null) {
            Movimiento movimiento = movimientoService.getMovimiento(movimientoDtoPutIn.getId());
            movimiento.setEstado(movimientoDtoPutIn.getEstado());
            movimientoService.updateMovimiento(movimiento);
            return ResponseEntity.noContent().build();
        }

        return createMoviemiento(new MovimientoDtoPostIn(movimientoDtoPutIn));
    }


    //    Patch
    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateMoviemiento(@PathVariable("id") Long id, @RequestBody JsonPatch patch) throws JsonPatchException, JsonProcessingException {
        if (patch == null)
            return ResponseEntity.noContent().build();
        movimientoService.patchMovimiento(id, patch);
        return ResponseEntity.ok().build();
    }

    //    Delete
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteMoviemiento(@PathVariable("id") Long id) {
        movimientoService.deleteMovimiento(id);
    }

    @ExceptionHandler(value = JpaCreateHaveIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SingleMessageResponse captureJpaCreateHaveIdException() {
        return new SingleMessageResponse("El movimiento a crear no puede contener ID.");
    }

    @ExceptionHandler(value = JpaNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public SingleMessageResponse captureJpaNotFoundException(JpaNotFoundException e) {
        return new SingleMessageResponse("No se encontro el movimiento con ID: " + e.getId());
    }

    @ExceptionHandler(value = {JsonProcessingException.class, JsonPatchException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public SingleMessageResponse captureJsonProcessingExceptionAndJsonPatchException() {
        return new SingleMessageResponse("Error causado mientras se procesaba un JSON.");
    }

    @ExceptionHandler(value = {CuentaIsNotActiveException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SingleMessageResponse captureCuentaIsNotActiveException() {
        return new SingleMessageResponse("La cuenta a la que se quiere realizar movimientos no esta activa.");
    }

    @ExceptionHandler(value = {ClienteIsNotActiveException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SingleMessageResponse captureClienteIsNotActiveException() {
        return new SingleMessageResponse("El cliente de la cuenta a la que se quiere añadir un movimiento no esta activo.");
    }

    @ExceptionHandler(value = {MovimientoLimitPassException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public MessageWithMontoResponse captureMovimientoLimitPassException(MovimientoLimitPassException e) {
    return new MessageWithMontoResponse(e.getMessage(), e.getExceso());
    }
}
