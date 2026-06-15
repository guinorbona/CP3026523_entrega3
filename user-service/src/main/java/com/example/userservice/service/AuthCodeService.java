package com.example.userservice.service;

import com.example.userservice.dto.EmailDto;
import com.example.userservice.dto.RequestCodeDto;
import com.example.userservice.dto.VerifyCodeDto;
import com.example.userservice.entity.User;
import com.example.userservice.producer.UserProducer;
import com.example.userservice.repository.RoleRepository;
import com.example.userservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.userservice.entity.Role;
import com.example.userservice.entity.RoleName;

import java.util.Set;
import java.security.SecureRandom;
import java.util.UUID;

@Service
public class AuthCodeService {

    private final UserRepository userRepository;
    private final CodigoCacheService codigoCacheService;
    private final UserProducer userProducer;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    public AuthCodeService(
            UserRepository userRepository,
            CodigoCacheService codigoCacheService,
            UserProducer userProducer,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.codigoCacheService = codigoCacheService;
        this.userProducer = userProducer;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public void solicitarCodigo(RequestCodeDto requestCodeDto) {
        String email = requestCodeDto.getEmail().trim().toLowerCase();

        User usuario = userRepository.findByEmail(email)
                .orElseGet(() -> criarUsuarioTemporario(email));

        String codigo = gerarCodigo();

        codigoCacheService.salvarCodigo(email, codigo);

        EmailDto emailDto = new EmailDto(
                email,
                "Seu código de acesso",
                "Olá! Seu código de acesso é: " + codigo + ". Ele expira em 5 minutos.",
                usuario.getId()
        );

        userProducer.publicarEmail(emailDto);
    }

    public boolean verificarCodigo(VerifyCodeDto verifyCodeDto) {
        return codigoCacheService.validarCodigo(
                verifyCodeDto.getEmail(),
                verifyCodeDto.getCode()
        );
    }

    private String gerarCodigo() {
        int numero = secureRandom.nextInt(1_000_000);
        return String.format("%06d", numero);
    }

    private User criarUsuarioTemporario(String email) {
        User usuario = new User();

        Role roleCustomer = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseGet(() -> roleRepository.save(new Role(null, RoleName.ROLE_CUSTOMER)));

        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        usuario.setRoles(Set.of(roleCustomer));

        return userRepository.save(usuario);
    }
}