package com.api.reserva.condominio.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api.reserva.condominio.domain.model.Espaco;
import com.api.reserva.condominio.domain.model.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    // Aqui já tem: save(), findAll(), findById(), deleteById(), etc.
    List<Reserva> findByUsuarioLoginContainingIgnoreCase(String login);

    @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE r.espaco = :espaco AND (:inicio < r.dataHoraFim AND :fim > r.dataHoraInicio)")
    boolean existeConflito(
        @Param("espaco") Espaco espaco, 
        @Param("inicio") LocalDateTime inicio, 
        @Param("fim") LocalDateTime fim
    );
}
