package com.api.reserva.condominio.controller;

import com.api.reserva.condominio.domain.model.Usuario;
import com.api.reserva.condominio.domain.model.UserRole;
import com.api.reserva.condominio.domain.repository.UsuarioRepository;
import com.api.reserva.condominio.infra.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class AutenticacaoController {
    @Autowired private AuthenticationManager manager;
    @Autowired private UsuarioRepository repository;
    @Autowired private TokenService tokenService;
    @Autowired private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid DadosAutenticacao dados) {
        var token = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
        var auth = manager.authenticate(token);
        var jwt = tokenService.gerarToken((Usuario) auth.getPrincipal());
        return ResponseEntity.ok(new DadosTokenJWT(jwt));
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody @Valid DadosRegistro dados) {
        if (repository.findByLogin(dados.login()) != null) return ResponseEntity.badRequest().body("Login já existe");
        
        String senhaCripto = passwordEncoder.encode(dados.senha());
        // Se o JSON não mandar Role, vira USER por padrão
        UserRole role = (dados.role() != null) ? dados.role() : UserRole.USER;
        
        Usuario novo = new Usuario(null, dados.login(), senhaCripto, role);
        repository.save(novo);
        return ResponseEntity.ok().build();
    }
}

// DTOs simples (pode criar no mesmo arquivo ou separado)
record DadosAutenticacao(String login, String senha) {}
record DadosRegistro(String login, String senha, UserRole role) {}
record DadosTokenJWT(String token) {}
