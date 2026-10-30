package es.iesjuanbosco.practicareservasaula.repository;

import es.iesjuanbosco.practicareservasaula.entity.Aulas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AulasRepository extends JpaRepository<Aulas, Long> {

    List<Aulas> findByCapacidadGreaterThan(int capacidad);

    List<Aulas> findByTieneOrdenadoresTrue();
}
