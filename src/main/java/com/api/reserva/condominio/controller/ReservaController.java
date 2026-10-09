package com.api.reserva.condominio.controller;

import com.api.reserva.condominio.domain.model.Reserva;
import com.api.reserva.condominio.domain.model.Usuario;
import com.api.reserva.condominio.domain.repository.ReservaRepository;
import com.api.reserva.condominio.domain.service.ReservaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaRepository repository;

    @Autowired
    private ReservaService service;

    // CREATE - Criar uma nova reserva
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Reserva reserva, @AuthenticationPrincipal Usuario logado) {
        try {
            // O logado vem direto do Token validado pelo seu Filtro
            var novaReserva = service.salvar(reserva, logado);
            return ResponseEntity.ok(novaReserva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // READ - Listar todas as reservas
    @GetMapping
    public List<Reserva> listarTodos() {
        return repository.findAll();
    }

    // READ - Buscar reserva por ID
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }

    // READ - Buscar reservas por nome do morador (ou parte do nome)
    @GetMapping("/buscar")
    public ResponseEntity<List<Reserva>> buscarPorMorador(@RequestParam String nome) {
        List<Reserva> reservas = repository.findByUsuarioLoginContainingIgnoreCase(nome);
        return ResponseEntity.ok(reservas);
    }

    // UPDATE - Atualizar uma reserva existente
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Reserva dadosAtualizados) {
        return repository.findById(id)
                .map(reserva -> {
                    reserva.setEspaco(dadosAtualizados.getEspaco());
                    reserva.setDataHoraInicio(dadosAtualizados.getDataHoraInicio());
                    reserva.setDataHoraFim(dadosAtualizados.getDataHoraFim());
                    var atualizada = repository.save(reserva);
                    return ResponseEntity.ok(atualizada);
                }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE - Remover uma reserva
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id, @AuthenticationPrincipal Usuario logado) {
        try {
            service.excluir(id, logado);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
