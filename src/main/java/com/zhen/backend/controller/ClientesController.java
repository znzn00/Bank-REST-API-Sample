package com.zhen.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.zhen.backend.controller.reponse.MessageWithCuentasResponse;
import com.zhen.backend.controller.reponse.SingleMessageResponse;
import com.zhen.backend.exception.*;
import com.zhen.backend.model.Cliente;
import com.zhen.backend.servicios.ClienteService;
import com.zhen.backend.util.QueryToJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class ClientesController {


    @Autowired
    ClienteService clienteService;

    //    Get
    @GetMapping
    public List<Cliente> getAllClients(@RequestParam Map<String, String> queryParams) {
        QueryToJpa<Cliente> clienteQueryToJpa = new QueryToJpa<>(queryParams);
        Specification<Cliente> specification = clienteQueryToJpa.getSpecification();
        Pageable pageable = clienteQueryToJpa.getPageable();

        if(specification!=null) {
            if(pageable!=null)
                return clienteService.getAllClientes(specification, pageable);
            if(clienteQueryToJpa.getSort()!=null)
                return clienteService.getAllClientes(specification, clienteQueryToJpa.getSort());
            return clienteService.getAllClientes(specification);
        }

        if(pageable!=null)
            return clienteService.getAllClientes(pageable);
        if(clienteQueryToJpa.getSort()!=null)
            return clienteService.getAllClientes(clienteQueryToJpa.getSort());
        return clienteService.getAllClientes();
    }

    @GetMapping("/{id}")
    public Cliente getCliente(@PathVariable("id") Long id) {
        return clienteService.getCliente(id);
    }


    //    Post
    @PostMapping
    public ResponseEntity<URI> createCliente(@RequestBody Cliente cliente) {
        var clientId = clienteService.createCliente(cliente);
        var clientLocation = ServletUriComponentsBuilder.fromCurrentRequest().path("/{clientId}").buildAndExpand(clientId).toUri();
        return ResponseEntity.created(clientLocation).build();
    }

    //    Put
    @PutMapping
    public ResponseEntity<URI> createOrUpdateCliente(@RequestBody Cliente cliente) {
        if (cliente.getId() != null) {
            clienteService.updateCliente(cliente);
            return ResponseEntity.noContent().build();
        }
        return createCliente(cliente);
    }


    //    Patch
    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateCliente(@PathVariable("id") Long id, @RequestBody JsonPatch patch) throws JsonPatchException, JsonProcessingException {
        if (patch == null)
            return ResponseEntity.noContent().build();
        clienteService.patchCliente(id, patch);
        return ResponseEntity.ok().build();
    }

    //    Delete
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCliente(@PathVariable("id") Long id) {
        clienteService.deleteCliente(id);
    }

    @ExceptionHandler(value = JpaCreateHaveIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SingleMessageResponse captureJpaCreateHaveIdException() {
        return new SingleMessageResponse("El cliente a crear no puede contener ID.");
    }

    @ExceptionHandler(value = ClienteUsernameConflictException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SingleMessageResponse captureClientUsernameConflictException() {
        return new SingleMessageResponse("El clientID a asignar ya esta siendo usado por otro cliente.");
    }

    @ExceptionHandler(value = JpaNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public SingleMessageResponse captureClienteNotFoundException(JpaNotFoundException e) {
        return new SingleMessageResponse("No se encontro el cliente con ID: " + e.getId());
    }

    @ExceptionHandler(value = {JsonProcessingException.class, JsonPatchException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public SingleMessageResponse captureJsonProcessingExceptionAndJsonPatchException() {
        return new SingleMessageResponse("Error causado mientras se procesaba un JSON.");
    }

    @ExceptionHandler(value = {ClienteHaveCuentaException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public MessageWithCuentasResponse captureClienteHaveCuentaException(ClienteHaveCuentaException e) {
        return new MessageWithCuentasResponse("El cliente a eliminar no puede ser eliminado debido a que tiene cuentas.", e.getCuentas());
    }
}
