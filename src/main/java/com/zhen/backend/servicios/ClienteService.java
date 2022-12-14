package com.zhen.backend.servicios;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.zhen.backend.exception.*;
import com.zhen.backend.model.Cliente;
import com.zhen.backend.respository.ClienteRepository;
import com.zhen.backend.respository.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService {

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    CuentaRepository cuentaRepository;
    public Long createCliente(Cliente cliente) throws ClienteUsernameConflictException, JpaCreateHaveIdException {
        if (cliente.getId() != null)
            throw new JpaCreateHaveIdException();
        if (clienteRepository.existsByClienteId(cliente.getClienteId()))
            throw new ClienteUsernameConflictException();
        Cliente guardado = clienteRepository.save(cliente);
        return guardado.getId();
    }

    public Long updateCliente(Cliente cliente) throws JpaUpdateMissingIdException, JpaNotFoundException {
        if (cliente.getId() == null)
            throw new JpaUpdateMissingIdException();
        if (!clienteRepository.existsById(cliente.getId()))
            throw new JpaNotFoundException(cliente.getId());
        Cliente guardado = clienteRepository.save(cliente);
        return guardado.getId();
    }

    ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

    public void patchCliente(Long id, JsonPatch patch) throws JpaNotFoundException, JsonPatchException, JsonProcessingException {
        Optional<Cliente> optional = clienteRepository.findById(id);
        if (optional.isEmpty())
            throw new JpaNotFoundException(id);
        Cliente cliente = optional.get();
        Cliente clientePatched = objectMapper.treeToValue(patch.apply(objectMapper.convertValue(cliente, JsonNode.class)), Cliente.class);
        if (clientePatched.getId() != id)
            throw new JsonPatchException("No se puede actualizar el ID.");
        clienteRepository.save(clientePatched);

    }

    public void deleteCliente(Long id) throws JpaNotFoundException {
        if (!clienteRepository.existsById(id))
            throw new JpaNotFoundException(id);
        List<Long> cuentas = cuentaRepository.findNumeroCuentasByClientId(id);
        if(cuentas.size()>0)
            throw new ClienteHaveCuentaException(id, cuentas);
        clienteRepository.deleteById(id);
    }

    public Cliente getCliente(Long id) {
        Optional<Cliente> optional = clienteRepository.findById(id);
        return optional.orElseThrow(() -> new JpaNotFoundException(id));
    }

    public List<Cliente> getAllClientes() {
        return clienteRepository.findAll();
    }

    public List<Cliente> getAllClientes(Pageable pageable) {
        return clienteRepository.findAll(pageable).getContent();
    }

    public List<Cliente> getAllClientes(Specification<Cliente> clienteSpecification, Pageable pageable) {
        return clienteRepository.findAll(clienteSpecification, pageable).getContent();
    }

    public List<Cliente> getAllClientes(Specification<Cliente> clienteSpecification, Sort sort) {
        return clienteRepository.findAll(clienteSpecification, sort);
    }

    public List<Cliente> getAllClientes(Specification<Cliente> clienteSpecification) {
        return clienteRepository.findAll(clienteSpecification);
    }
    public List<Cliente> getAllClientes(Sort sort) {
        return clienteRepository.findAll(sort);
    }

}
