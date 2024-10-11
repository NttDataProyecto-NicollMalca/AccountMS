package com.example.accountms.controller;

import com.example.accountms.CuentaDelegateImpl;
import com.example.accountms.api.CuentasApiController;
import com.example.accountms.business.CuentaMapper;
import com.example.accountms.model.*;
import com.example.accountms.repository.CuentaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CuentaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CuentaDelegateImpl cuentaDelegate;

    @Mock
    private CuentaRepository cuentaRepository;

    @InjectMocks
    private CuentasApiController cuentasApiController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cuentasApiController)
                .build();
    }

    @Test
    @DisplayName("Cuando agrego una cuenta correctamente, debe retornar un estado 200 OK")
    void agregarCuentaReturnOk() throws Exception {
        CuentaRequest cuentaRequest = new CuentaRequest();
        cuentaRequest.setClienteId("123");
        cuentaRequest.setSaldo(1000.0);
        cuentaRequest.setTipoCuenta(CuentaRequest.TipoCuentaEnum.AHORROS);

        CuentaMapper cuentaMapper = CuentaMapper.getInstance();
        Cuenta cuenta = cuentaMapper.getCuentaofCuentaRequest(cuentaRequest);
        CuentaResponse cuentaResponse = cuentaMapper.getCuentaResponseofCuenta(cuenta);

        Mockito.when(cuentaDelegate.agregarCuenta(Mockito.any(CuentaRequest.class)))
                .thenReturn(ResponseEntity.ok(cuentaResponse));

        mockMvc.perform(MockMvcRequestBuilders.post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(cuentaRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.numeroCuenta").isNotEmpty())
                .andExpect(jsonPath("$.clienteId").value("123"))
                .andExpect(jsonPath("$.saldo").value(1000.0))
                .andExpect(jsonPath("$.tipoCuenta").value("AHORROS"));
    }

    @Test
    @DisplayName("Cuando elimino una cuenta correctamente, debe retornar un estado 204 NO CONTENT")
    void eliminarCuentaReturnNoContent() throws Exception {
        String cuentaId = "123456";

        Mockito.when(cuentaDelegate.eliminarCuenta(Mockito.eq(cuentaId)))
                .thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(MockMvcRequestBuilders.delete("/cuentas/{id}", cuentaId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Cuando obtengo una cuenta por ID correctamente, debe retornar un estado 200 OK")
    void getCuentaPorIdReturnOk() throws Exception {
        String cuentaId = "123456";

        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta("123456789012");
        cuenta.setClienteId("123");
        cuenta.setSaldo(1000.0);
        cuenta.setTipoCuenta(TipoCuenta.AHORROS);

        CuentaMapper cuentaMapper = CuentaMapper.getInstance();
        CuentaResponse cuentaResponse = cuentaMapper.getCuentaResponseofCuenta(cuenta);

        Mockito.when(cuentaDelegate.getCuentaPorId(Mockito.eq(cuentaId)))
                .thenReturn(ResponseEntity.ok(cuentaResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/cuentas/{id}", cuentaId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.numeroCuenta").value("123456789012"))
                .andExpect(jsonPath("$.clienteId").value("123"))
                .andExpect(jsonPath("$.saldo").value(1000.0))
                .andExpect(jsonPath("$.tipoCuenta").value("AHORROS"));
    }


    @Test
    @DisplayName("Cuando listamos todas las cuentas, debe retornar un estado 200 OK")
    void listarCuentasReturnOk() throws Exception {
        Cuenta cuenta1 = new Cuenta();
        cuenta1.setId(1L);
        cuenta1.setNumeroCuenta("123456789012");
        cuenta1.setClienteId("123");
        cuenta1.setSaldo(1000.0);
        cuenta1.setTipoCuenta(TipoCuenta.AHORROS);

        Cuenta cuenta2 = new Cuenta();
        cuenta2.setId(2L);
        cuenta2.setNumeroCuenta("987654321098");
        cuenta2.setClienteId("456");
        cuenta2.setSaldo(2000.0);
        cuenta2.setTipoCuenta(TipoCuenta.CORRIENTE);

        List<CuentaResponse> cuentas = Arrays.asList(
                CuentaMapper.getInstance().getCuentaResponseofCuenta(cuenta1),
                CuentaMapper.getInstance().getCuentaResponseofCuenta(cuenta2)
        );

        Mockito.when(cuentaDelegate.listarCuentas())
                .thenReturn(ResponseEntity.ok(cuentas));

        mockMvc.perform(MockMvcRequestBuilders.get("/cuentas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].numeroCuenta").value("123456789012"))
                .andExpect(jsonPath("$[0].clienteId").value("123"))
                .andExpect(jsonPath("$[0].saldo").value(1000.0))
                .andExpect(jsonPath("$[0].tipoCuenta").value("AHORROS"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].numeroCuenta").value("987654321098"))
                .andExpect(jsonPath("$[1].clienteId").value("456"))
                .andExpect(jsonPath("$[1].saldo").value(2000.0))
                .andExpect(jsonPath("$[1].tipoCuenta").value("CORRIENTE"));
    }

    @Test
    @DisplayName("Cuando se realiza un depósito correctamente, debe devolver 204 No Content")
    void testRealizarDepositoCuenta() throws Exception {
        String cuentaId = "123";
        OperacionRequest deposito = new OperacionRequest();
        deposito.setMonto(500.0);

        Mockito.when(cuentaDelegate.realizarDepositoCuenta(Mockito.eq(cuentaId), Mockito.any(OperacionRequest.class)))
                .thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(MockMvcRequestBuilders.put("/cuentas/{cuentaId}/depositar", cuentaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(deposito)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Cuando se realiza un retiro correctamente, debe devolver 204 No Content")
    void testRealizarRetiroCuenta() throws Exception {
        String cuentaId = "123";
        OperacionRequest retiro = new OperacionRequest();
        retiro.setMonto(500.0);

        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setSaldo(1000.0);
        cuenta.setTipoCuenta(TipoCuenta.AHORROS);

        Mockito.when(cuentaDelegate.realizarRetiroCuenta(Mockito.eq(cuentaId), Mockito.any(OperacionRequest.class)))
                .thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(MockMvcRequestBuilders.put("/cuentas/{cuentaId}/retirar", cuentaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(retiro)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Cuando se agrega una cuenta de tipo AHORROS, debe retornar el tipo correctamente")
    void testAgregarCuentaTipoAhorros() throws Exception {
        CuentaRequest cuentaRequest = new CuentaRequest();
        cuentaRequest.setClienteId("123");
        cuentaRequest.setTipoCuenta(CuentaRequest.TipoCuentaEnum.AHORROS);
        cuentaRequest.setSaldo(1000.0);

        CuentaResponse cuentaResponse = new CuentaResponse();
        cuentaResponse.setId("1");
        cuentaResponse.setClienteId("123");
        cuentaResponse.setTipoCuenta(CuentaResponse.TipoCuentaEnum.AHORROS);
        cuentaResponse.setSaldo(1000.0);

        Mockito.when(cuentaDelegate.agregarCuenta(Mockito.any(CuentaRequest.class)))
                .thenReturn(ResponseEntity.ok(cuentaResponse));

        mockMvc.perform(MockMvcRequestBuilders.post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(cuentaRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoCuenta").value("AHORROS"))
                .andExpect(jsonPath("$.saldo").value(1000.0));
    }



    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}
