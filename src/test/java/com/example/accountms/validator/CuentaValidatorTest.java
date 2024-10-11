package com.example.accountms.validator;

import com.example.accountms.business.CuentaValidator;
import com.example.accountms.model.Cuenta;
import com.example.accountms.model.TipoCuenta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CuentaValidatorTest {
    private CuentaValidator cuentaValidator;

    @BeforeEach
    void setUp() {
        cuentaValidator = new CuentaValidator();
    }

    @Test
    @DisplayName("Debe lanzar excepción si el saldo inicial es 0")
    void validarSaldoInicialDebeLanzarExcepcionCuandoSaldoEsCero() {
        double saldoInicial = 0;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cuentaValidator.validarSaldoInicial(saldoInicial);
        });

        assertEquals("El saldo inicial no puede ser 0.", exception.getMessage());
    }

    @Test
    @DisplayName("No debe lanzar excepción si el saldo inicial es mayor a 0")
    void validarSaldoInicialNoDebeLanzarExcepcionCuandoSaldoEsMayorQueCero() {
        double saldoInicial = 1000;

        assertDoesNotThrow(() -> {
            cuentaValidator.validarSaldoInicial(saldoInicial);
        });
    }

    @Test
    @DisplayName("Debe lanzar excepción si se intenta eliminar una cuenta con saldo mayor a 0")
    void validarEliminacionCuentaDebeLanzarExcepcionCuandoSaldoMayorQueCero() {
        double saldo = 100;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cuentaValidator.validarEliminacionCuenta(saldo);
        });

        assertEquals("No se puede eliminar una cuenta con saldo mayor a 0.", exception.getMessage());
    }

    @Test
    @DisplayName("No debe lanzar excepción si el saldo es 0 al eliminar la cuenta")
    void validarEliminacionCuentaNoDebeLanzarExcepcionCuandoSaldoEsCero() {
        double saldo = 0;

        assertDoesNotThrow(() -> {
            cuentaValidator.validarEliminacionCuenta(saldo);
        });
    }

    @Test
    @DisplayName("Debe lanzar excepción si el retiro deja el saldo en negativo para cuentas de ahorro")
    void validarRetiroCuentaDebeLanzarExcepcionCuandoSaldoNegativoEnCuentaAhorros() {
        Cuenta cuenta = mock(Cuenta.class);
        when(cuenta.getSaldo()).thenReturn(100.0);
        when(cuenta.getTipoCuenta()).thenReturn(TipoCuenta.AHORROS);

        double montoRetiro = 150;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cuentaValidator.validarRetiroCuenta(cuenta, montoRetiro);
        });

        assertEquals("No se puede realizar un retiro que deje el saldo en negativo para cuentas de ahorro.", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el sobregiro de cuenta corriente excede -500")
    void validarRetiroCuentaDebeLanzarExcepcionCuandoSobregiroMayorQue500EnCuentaCorriente() {
        Cuenta cuenta = mock(Cuenta.class);
        when(cuenta.getSaldo()).thenReturn(-100.0);
        when(cuenta.getTipoCuenta()).thenReturn(TipoCuenta.CORRIENTE);

        double montoRetiro = 500;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cuentaValidator.validarRetiroCuenta(cuenta, montoRetiro);
        });

        assertEquals("Las cuentas corrientes no pueden tener un sobregiro mayor a -500.", exception.getMessage());
    }

    @Test
    @DisplayName("No debe lanzar excepción si el saldo resultante en cuenta de ahorro es positivo")
    void validarRetiroCuentaNoDebeLanzarExcepcionCuandoSaldoEsPositivoEnCuentaAhorros() {
        Cuenta cuenta = mock(Cuenta.class);
        when(cuenta.getSaldo()).thenReturn(500.0);
        when(cuenta.getTipoCuenta()).thenReturn(TipoCuenta.AHORROS);

        double montoRetiro = 100;

        assertDoesNotThrow(() -> {
            cuentaValidator.validarRetiroCuenta(cuenta, montoRetiro);
        });
    }

    @Test
    @DisplayName("No debe lanzar excepción si el saldo en cuenta corriente no excede el límite de sobregiro")
    void validarRetiroCuentaNoDebeLanzarExcepcionCuandoSobregiroNoExcedeLimiteEnCuentaCorriente() {
        Cuenta cuenta = mock(Cuenta.class);
        when(cuenta.getSaldo()).thenReturn(0.0);
        when(cuenta.getTipoCuenta()).thenReturn(TipoCuenta.CORRIENTE);

        double montoRetiro = 400;

        assertDoesNotThrow(() -> {
            cuentaValidator.validarRetiroCuenta(cuenta, montoRetiro);
        });
    }
}
