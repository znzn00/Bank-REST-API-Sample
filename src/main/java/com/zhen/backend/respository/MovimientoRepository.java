package com.zhen.backend.respository;

import com.zhen.backend.model.Movimiento;
import com.zhen.backend.model.enumerables.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long>, JpaSpecificationExecutor<Movimiento> {

    public boolean existsByCuentaId(Long cuentaId);

    public List<Movimiento> findAllByCuentaId(Long cuentaId);

    public Optional<Movimiento> findFirstByCuentaIdOrderByCreationTimestampDesc(Long cuentaId);

    @Query("SELECT SUM(m.valor) FROM Movimiento m WHERE m.cuentaId=?1 AND m.tipo=?2 AND m.creationTimestamp BETWEEN ?3 and ?4")
    public BigDecimal getSumOfAllByCuentaIdAndTipoAndCreationTimestampBetween(Long cuentaId, TipoMovimiento tipo, LocalDateTime before, LocalDateTime after);

    @Query(value = "SELECT m.id FROM Movimiento m WHERE m.cuentaId = ?1")
    public List<Long> findIdsByNumeroCuenta(Long numeroCuenta);

    public List<Movimiento> findAllByCuentaIdAndCreationTimestampAfter(Long numeroCuenta, LocalDateTime fecha);

    public List<Movimiento> findAllByCuentaIdAndCreationTimestampBefore(Long numeroCuenta, LocalDateTime fecha);

    public List<Movimiento> findAllByCuentaIdAndCreationTimestampBetween(Long numeroCuenta, LocalDateTime before, LocalDateTime after);
}
