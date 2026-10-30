package es.iesjuanbosco.practicareservasaula.repository;

import es.iesjuanbosco.practicareservasaula.entity.Profesores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfesoresRepository extends JpaRepository<Profesores, Long> {

}
