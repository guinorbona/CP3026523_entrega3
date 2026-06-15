package com.example.userservice.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CodigoCacheService {

    private static final long TEMPO_EXPIRACAO_MINUTOS = 5;

    private final Map<String, CodigoCache> cache = new ConcurrentHashMap<>();

    public void salvarCodigo(String email, String codigo) {
        String emailNormalizado = normalizarEmail(email);

        CodigoCache codigoCache = new CodigoCache(
                codigo,
                LocalDateTime.now().plusMinutes(TEMPO_EXPIRACAO_MINUTOS)
        );

        cache.put(emailNormalizado, codigoCache);
    }

    public boolean validarCodigo(String email, String codigoInformado) {
        String emailNormalizado = normalizarEmail(email);

        CodigoCache codigoCache = cache.get(emailNormalizado);

        if (codigoCache == null) {
            return false;
        }

        if (codigoCache.estaExpirado()) {
            cache.remove(emailNormalizado);
            return false;
        }

        boolean codigoValido = codigoCache.codigo().equals(codigoInformado);

        if (codigoValido) {
            cache.remove(emailNormalizado);
        }

        return codigoValido;
    }

    @Scheduled(fixedRate = 60000)
    public void removerCodigosExpirados() {
        cache.entrySet().removeIf(entry -> entry.getValue().estaExpirado());
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }

    private record CodigoCache(String codigo, LocalDateTime expiracao) {

        public boolean estaExpirado() {
            return LocalDateTime.now().isAfter(expiracao);
        }
    }
}