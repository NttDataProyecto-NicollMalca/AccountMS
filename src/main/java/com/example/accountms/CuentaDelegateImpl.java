package com.example.accountms;

import com.example.accountms.api.CuentasApiDelegate;
import com.example.accountms.business.CuentaService;
import com.example.accountms.model.CuentaRequest;
import com.example.accountms.model.CuentaResponse;
import com.example.accountms.model.OperacionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    public ResponseEntity<Void> realizarDepositoCuenta(String cuentaId, OperacionRequest inlineObject) {
        cuentaService.realizarDepositoCuenta(cuentaId,inlineObject);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> realizarRetiroCuenta(String cuentaId, OperacionRequest inlineObject1) {
        try {
            cuentaService.realizarRetiroCuenta(cuentaId, inlineObject1);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
