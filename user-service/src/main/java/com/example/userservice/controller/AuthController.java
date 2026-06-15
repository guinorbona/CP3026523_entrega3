package com.example.userservice.controller;

import com.example.userservice.dto.RequestCodeDto;
import com.example.userservice.dto.VerifyCodeDto;
import com.example.userservice.service.AuthCodeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthCodeService authCodeService;

    public AuthController(AuthCodeService authCodeService) {
        this.authCodeService = authCodeService;
    }

    @PostMapping("/request-code")
    public ResponseEntity<Map<String, String>> requestCode(@RequestBody @Valid RequestCodeDto requestCodeDto) {
        authCodeService.solicitarCodigo(requestCodeDto);

        return ResponseEntity.ok(Map.of(
                "message", "Código enviado para o e-mail informado."
        ));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Object>> verifyCode(@RequestBody @Valid VerifyCodeDto verifyCodeDto) {
        boolean codigoValido = authCodeService.verificarCodigo(verifyCodeDto);

        if (!codigoValido) {
            return ResponseEntity.badRequest().body(Map.of(
                    "valid", false,
                    "message", "Código inválido ou expirado."
            ));
        }

        return ResponseEntity.ok(Map.of(
                "valid", true,
                "message", "Código validado com sucesso."
        ));
    }
}