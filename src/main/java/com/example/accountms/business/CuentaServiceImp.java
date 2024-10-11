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

    private final  CuentaRepository cuentaRepository;
    private final  CuentaMapper cuentaMapper;
    private final  RestTemplate restTemplate;
    private final  CuentaValidator cuentaValidator;

    private static final String Customer_WS = "http://localhost:8080/clientes/{clienteId}";

    @Autowired
    public CuentaServiceImp(CuentaRepository cuentaRepository, CuentaMapper cuentaMapper,
                            CuentaValidator cuentaValidator, RestTemplate restTemplate) {
        this.cuentaRepository = cuentaRepository;
        this.cuentaMapper = cuentaMapper;
        this.cuentaValidator = cuentaValidator;
        this.restTemplate = restTemplate;
    }




    @Override
    public CuentaResponse agregarCuenta(CuentaRequest cuentaRequest) {
        validarClienteExiste(cuentaRequest.getClienteId());

        cuentaValidator.validarSaldoInicial(cuentaRequest.getSaldo());

        return cuentaMapper.getCuentaResponseofCuenta(
                cuentaRepository.save(cuentaMapper.getCuentaofCuentaRequest(cuentaRequest)));
    }

    @Override
    public ResponseEntity<Void> eliminarCuenta(String id) {

        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada con el id: " + id));

        cuentaValidator.validarEliminacionCuenta(cuenta.getSaldo());

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
    public void realizarDepositoCuenta(String cuentaId, OperacionRequest inlineObject) {
        cuentaRepository.findById(cuentaId).ifPresentOrElse(cuenta -> {
            cuenta.setSaldo(cuenta.getSaldo() + inlineObject.getMonto());
            cuentaRepository.save(cuenta);
        }, () -> {

            throw new IllegalArgumentException("Cuenta no encontrada con el id: " + cuentaId);
        });
    }

    @Override
    public void realizarRetiroCuenta(String cuentaId, OperacionRequest inlineObject1) {
        cuentaRepository.findById(cuentaId).ifPresentOrElse(cuenta -> {
            cuentaValidator.validarRetiroCuenta(cuenta, inlineObject1.getMonto());

            double nuevoSaldo = cuenta.getSaldo() - inlineObject1.getMonto();
            cuenta.setSaldo(nuevoSaldo);
            cuentaRepository.save(cuenta);
        }, () -> {
            throw new IllegalArgumentException("Cuenta no encontrada con el id: " + cuentaId);
        });
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
