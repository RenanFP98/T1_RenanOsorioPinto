package edu.pe.cibertec.infracciones.repository;

import edu.pe.cibertec.infracciones.model.Infractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InfractorRepository extends JpaRepository<Infractor, Long> {
    Optional<Infractor> findByDni(String dni);
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);
    @Query("SELECT COUNT(m) FROM Multa m WHERE m.infractor.id = :infractorId AND m.estado = 'VENCIDA'")
    long countMultasVencidas(@Param("infractorId") Long infractorId);
}