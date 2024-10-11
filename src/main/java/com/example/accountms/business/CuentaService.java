package com.example.accountms.business;

import com.example.accountms.api.CuentasApiDelegate;
import com.example.accountms.model.CuentaRequest;
import com.example.accountms.model.CuentaResponse;

import com.example.accountms.model.OperacionRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CuentaService {

    public CuentaResponse agregarCuenta(CuentaRequest cuentaRequest);

    public ResponseEntity<Void> eliminarCuenta(String id);

    public CuentaResponse getCuentaPorId(String id);

    public List<CuentaResponse> listarCuentas();

    public void realizarDepositoCuenta(String cuentaId, OperacionRequest inlineObject);

    public void realizarRetiroCuenta(String cuentaId, OperacionRequest inlineObject1);

}
