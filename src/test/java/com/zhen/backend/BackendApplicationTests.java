package com.zhen.backend;

import com.zhen.backend.model.Cliente;
import com.zhen.backend.model.Cuenta;
import com.zhen.backend.model.enumerables.EstadoCuenta;
import com.zhen.backend.model.enumerables.TipoCuenta;
import com.zhen.backend.respository.CuentaRepository;
import com.zhen.backend.servicios.ClienteService;
import com.zhen.backend.servicios.CuentaService;
import com.zhen.backend.servicios.MovimientoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.util.Assert;

import java.math.BigDecimal;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BackendApplicationTests {

	@Autowired
	CuentaService cuentaService;

	@Autowired
	ClienteService clienteService;

	@Autowired
	CuentaRepository cuentaRepository;

	@Autowired
	MovimientoService movimientoService;

//	UnitTest 1
	@Test
	void verificacionDeCreacionDeCuenta() {
		Cliente cliente = clienteService.getCliente(1L);
		Cuenta cuenta = new Cuenta();
		cuenta.setCliente(cliente);
		cuenta.setEstado(EstadoCuenta.ACTIVO);
		cuenta.setSaldoInicial(new BigDecimal("1600.00"));
		cuenta.setTipo(TipoCuenta.CORRIENTE);
		Long numeroCuenta = cuentaService.createCuenta(cuenta);
		Assert.isTrue(cuentaRepository.existsById(numeroCuenta), "Se debio crear una cuenta con id.");
	}
// UnitTest 2
	@Test
	void verificacionDeCancelacionDeCuenta() {
		Cuenta cuenta = cuentaRepository.findById(2L).get();
		cuenta.setEstado(EstadoCuenta.CANCELADO);
		cuentaService.updateCuenta(cuenta);

		Cuenta cuentaRecargada =  cuentaRepository.findById(2L).get();
		Assert.isTrue(cuentaRecargada.getEstado()==EstadoCuenta.CANCELADO, "El estado debería de estar cancelado.");
	}

	@Autowired
	MockMvc mvc;

//	Integration
//	Verifica que se genere el reporte correctamente.
	@Test
	public void verificarReporteCuentaDeEstados() throws Exception {
		mvc.perform(get("/api/reportes/cliente/1/estado/cuentas")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect( jsonPath("$.cliente.id").value("1"))
				.andExpect(jsonPath("$.cuentas.size()").value("0"));
	}

}
