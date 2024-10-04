package com.example.accountms.business;

import com.example.accountms.api.CuentasApiDelegate;
import com.example.accountms.model.CuentaRequest;
import com.example.accountms.model.CuentaResponse;
import com.example.accountms.model.InlineObject;
import com.example.accountms.model.InlineObject1;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CuentaService {

    public CuentaResponse agregarCuenta(CuentaRequest cuentaRequest);

    public ResponseEntity<Void> eliminarCuenta(String id);

    public CuentaResponse getCuentaPorId(String id);

    public List<CuentaResponse> listarCuentas();

    public ResponseEntity<Void> realizarDepositoCuenta(String cuentaId, InlineObject inlineObject);

    public ResponseEntity<Void> realizarRetiroCuenta(String cuentaId, InlineObject1 inlineObject1);

}
