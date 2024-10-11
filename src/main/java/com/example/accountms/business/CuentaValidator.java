package com.example.accountms.business;

import com.example.accountms.model.Cuenta;
import com.example.accountms.model.TipoCuenta;
import org.springframework.stereotype.Service;

@Service
public class CuentaValidator {
    public void validarSaldoInicial(double saldo) {
        if (saldo == 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser 0.");
        }
    }

    public void validarEliminacionCuenta(double saldo) {
        if (saldo > 0) {
            throw new IllegalArgumentException("No se puede eliminar una cuenta con saldo mayor a 0.");
        }
    }

    public void validarRetiroCuenta(Cuenta cuenta, double monto) {
        double nuevoSaldo = cuenta.getSaldo() - monto;
        if (cuenta.getTipoCuenta() == TipoCuenta.AHORROS && nuevoSaldo < 0) {
            throw new IllegalArgumentException("No se puede realizar un retiro que deje el saldo en negativo para cuentas de ahorro.");
        } else if (cuenta.getTipoCuenta() == TipoCuenta.CORRIENTE && nuevoSaldo < -500) {
            throw new IllegalArgumentException("Las cuentas corrientes no pueden tener un sobregiro mayor a -500.");
        }
    }
}
