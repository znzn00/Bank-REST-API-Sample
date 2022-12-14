package com.zhen.backend;

import com.zhen.backend.controller.ReportesController;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import javax.swing.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = BackendApplication.class)
@AutoConfigureMockMvc
@WebMvcTest(ReportesController.class)
public class BackendIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void verificarReporteCuentaDeEstados() throws Exception {
        mvc.perform(get("/api/reportes/cliente/1/estado/cuentas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
