package com.zhen.backend.respository;

import com.zhen.backend.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long>, JpaSpecificationExecutor<Cuenta> {
    public List<Cuenta> findAllByClienteId(Long clienteId);

    @Query(value = "SELECT c.numeroCuenta FROM Cuenta c WHERE c.clienteId = ?1")
    public List<Long> findNumeroCuentasByClientId(Long clienteId);

}
