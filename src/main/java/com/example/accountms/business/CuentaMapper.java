package com.example.accountms.business;

import com.example.accountms.model.Cuenta;
import com.example.accountms.model.CuentaRequest;
import com.example.accountms.model.CuentaResponse;
import com.example.accountms.model.TipoCuenta;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.stream.IntStream;

@Component
public class CuentaMapper {

    private String generateNumeroCuenta() {
        Random random = new Random();
        StringBuilder numeroCuenta = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            int digito = random.nextInt(10); 
            numeroCuenta.append(digito);
        }

        return numeroCuenta.toString();
    }

    private int generarIdAleatorio() {
        return IntStream.generate(() -> new Random().nextInt(900000) + 100000)
                .limit(1)
                .findFirst()
                .getAsInt();
    }

    public Cuenta getCuentaofCuentaRequest(CuentaRequest request) {
        Cuenta cuenta = new Cuenta();
        cuenta.setId((long)generarIdAleatorio());
        cuenta.setNumeroCuenta(generateNumeroCuenta());
        cuenta.setClienteId(request.getClienteId());
        cuenta.setTipoCuenta(mapToTipoCuenta(request.getTipoCuenta()));
        cuenta.setSaldo(request.getSaldo());
        return cuenta;
    }

    public CuentaResponse getCuentaResponseofCuenta(Cuenta cuenta) {
        CuentaResponse response = new CuentaResponse();
        response.setId(cuenta.getId().toString());
        response.setNumeroCuenta(cuenta.getNumeroCuenta());
        response.setClienteId(cuenta.getClienteId());
        response.setSaldo(cuenta.getSaldo());
        response.setTipoCuenta(mapToTipoCuentaEnum(cuenta.getTipoCuenta())); 
        return response;
    }

    private TipoCuenta mapToTipoCuenta(CuentaRequest.TipoCuentaEnum tipoCuentaEnum) {
        switch (tipoCuentaEnum) {
            case AHORROS:
                return TipoCuenta.AHORROS;
            case CORRIENTE:
                return TipoCuenta.CORRIENTE;
            default:
                throw new IllegalArgumentException("Valor no esperado: " + tipoCuentaEnum + ". Solo se acepta Ahorro y Corriente");
        }
    }

    private CuentaResponse.TipoCuentaEnum mapToTipoCuentaEnum(TipoCuenta tipoCuenta) {
        switch (tipoCuenta) {
            case AHORROS:
                return CuentaResponse.TipoCuentaEnum.AHORROS;
            case CORRIENTE:
                return CuentaResponse.TipoCuentaEnum.CORRIENTE;
            default:
                throw new IllegalArgumentException("Valor no esperado " + tipoCuenta  + ". Solo se acepta Ahorro y Corriente");
        }
    }



}
