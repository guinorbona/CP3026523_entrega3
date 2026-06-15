package com.example.userservice.controller;

import com.example.userservice.dto.CreateUserDto;
import com.example.userservice.dto.LoginUserDto;
import com.example.userservice.dto.RecoveryJwtTokenDto;
import com.example.userservice.entity.User;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserDto dto) {
        User user = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<RecoveryJwtTokenDto> login(@Valid @RequestBody LoginUserDto dto) {
        RecoveryJwtTokenDto token = userService.login(dto);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/test/customer")
    public ResponseEntity<String> testCustomer() {
        return ResponseEntity.ok("Acesso CUSTOMER autorizado!");
    }

    @GetMapping("/test/admin")
    public ResponseEntity<String> testAdmin() {
        return ResponseEntity.ok("Acesso ADMIN autorizado!");
    }
}
