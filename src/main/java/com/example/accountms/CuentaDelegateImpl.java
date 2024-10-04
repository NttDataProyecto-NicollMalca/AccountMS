package com.example.accountms;

import com.example.accountms.api.CuentasApiDelegate;
import com.example.accountms.business.CuentaService;
import com.example.accountms.model.CuentaRequest;
import com.example.accountms.model.CuentaResponse;
import com.example.accountms.model.InlineObject;
import com.example.accountms.model.InlineObject1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuentaDelegateImpl implements CuentasApiDelegate{


    @Autowired
    CuentaService cuentaService;

    @Override
    public ResponseEntity<CuentaResponse> agregarCuenta(CuentaRequest cuentaRequest) {
       return ResponseEntity.ok(cuentaService.agregarCuenta(cuentaRequest));
    }

    @Override
    public ResponseEntity<Void> eliminarCuenta(String id) {
        cuentaService.eliminarCuenta(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CuentaResponse> getCuentaPorId(String id) {
        return ResponseEntity.ok(cuentaService.getCuentaPorId(id));
    }

    @Override
    public ResponseEntity<List<CuentaResponse>> listarCuentas() {
        return ResponseEntity.ok(cuentaService.listarCuentas());
    }

    @Override
    public ResponseEntity<Void> realizarDepositoCuenta(String cuentaId, InlineObject inlineObject) {
        cuentaService.realizarDepositoCuenta(cuentaId,inlineObject);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> realizarRetiroCuenta(String cuentaId, InlineObject1 inlineObject1) {
        cuentaService.realizarRetiroCuenta(cuentaId,inlineObject1);
        return ResponseEntity.noContent().build();
    }
}
