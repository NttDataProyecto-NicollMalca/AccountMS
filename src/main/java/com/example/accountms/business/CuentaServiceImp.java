package com.example.accountms.business;

import com.example.accountms.model.*;
import com.example.accountms.repository.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CuentaServiceImp implements CuentaService {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private CuentaMapper cuentaMapper;

    @Autowired
    private RestTemplate restTemplate;

    private static final String Customer_WS = "http://localhost:8080/clientes/{clienteId}";



    @Override
    public CuentaResponse agregarCuenta(CuentaRequest cuentaRequest) {
        // Validar que el cliente exista
        validarClienteExiste(cuentaRequest.getClienteId());

        // Validar que el saldo inicial no sea 0
        if (cuentaRequest.getSaldo() == 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser 0.");
        }

        // Guardar la cuenta si todas las validaciones son correctas
        return cuentaMapper.getCuentaResponseofCuenta(
                cuentaRepository.save(cuentaMapper.getCuentaofCuentaRequest(cuentaRequest)));
    }

    @Override
    public ResponseEntity<Void> eliminarCuenta(String id) {

        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada con el id: " + id));

        // Verificar si la cuenta tiene saldo mayor a 0
        if (cuenta.getSaldo() > 0) {
            throw new IllegalArgumentException("No se puede eliminar una cuenta con saldo mayor a 0.");
        }

        // Eliminar la cuenta si no tiene saldo
        cuentaRepository.delete(cuenta);
        return ResponseEntity.noContent().build();
    }

    @Override
    public CuentaResponse getCuentaPorId(String id) {
        Cuenta cuenta= cuentaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrado con id: " + id));
        return cuentaMapper.getCuentaResponseofCuenta(cuenta);
    }

    @Override
    public List<CuentaResponse> listarCuentas() {
        return cuentaRepository.findAll().stream()
                .map(cuentaMapper::getCuentaResponseofCuenta)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<Void> realizarDepositoCuenta(String cuentaId, InlineObject inlineObject) {
        cuentaRepository.findById(cuentaId).ifPresentOrElse(cuenta -> {
            cuenta.setSaldo(cuenta.getSaldo() + inlineObject.getMonto());
            cuentaRepository.save(cuenta);
        }, () -> {
            throw new IllegalArgumentException("Cuenta no encontrada con el id: " + cuentaId);
        });

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> realizarRetiroCuenta(String cuentaId, InlineObject1 inlineObject1) {
        cuentaRepository.findById(cuentaId).ifPresentOrElse(cuenta -> {
            double nuevoSaldo = cuenta.getSaldo() - inlineObject1.getMonto();

            if (cuenta.getTipoCuenta() == TipoCuenta.AHORROS && nuevoSaldo < 0) {
                throw new IllegalArgumentException("No se puede realizar un retiro que deje el saldo en negativo para cuentas de ahorro.");
            } else if (cuenta.getTipoCuenta() == TipoCuenta.CORRIENTE && nuevoSaldo < -500) {
                throw new IllegalArgumentException("Las cuentas corrientes no pueden tener un sobregiro mayor a -500.");
            }

            cuenta.setSaldo(nuevoSaldo);
            cuentaRepository.save(cuenta);
        }, () -> {
            throw new IllegalArgumentException("Cuenta no encontrada con el id: " + cuentaId);
        });

        return ResponseEntity.ok().build();
    }

    private void validarClienteExiste(String clienteId) {
        try {
            Long id = Long.valueOf(clienteId);
            restTemplate.getForObject(Customer_WS, Void.class, id);
        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException("Cliente no encontrado con el id: " + clienteId);
        }
    }
}
