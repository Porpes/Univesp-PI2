package com.api.reserva.condominio.domain.service;

import com.api.reserva.condominio.domain.model.Reserva;
import com.api.reserva.condominio.domain.model.UserRole;
import com.api.reserva.condominio.domain.model.Usuario;
import com.api.reserva.condominio.domain.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservaService {
    @Autowired private ReservaRepository repository;

    public Reserva salvar(Reserva reserva, Usuario logado) {
        reserva.setUsuario(logado); // Atrela ao dono
        if (repository.existeConflito(reserva.getEspaco(), reserva.getDataHoraInicio(), reserva.getDataHoraFim())) {
            throw new RuntimeException("Espaço ocupado!");
        }
        return repository.save(reserva);
    }

    public void excluir(Long id, Usuario logado) {
        // 1. Busca a reserva
        var reserva = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));
        
        // 2. A REGRA DE OURO: 
        // Só deleta se for ADMIN -OU- se o ID do dono da reserva for igual ao ID de quem está logado
        if (logado.getRole() == UserRole.ADMIN || reserva.getUsuario().getId().equals(logado.getId())) {
            repository.delete(reserva);
        } else {
            // Se cair aqui, é porque o Morador B tentou apagar a do Morador A
            throw new RuntimeException("Você não tem permissão para excluir a reserva de outro morador!");
        }
    }
}
