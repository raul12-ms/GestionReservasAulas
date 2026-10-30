package es.iesjuanbosco.practicareservasaula.repository;

import es.iesjuanbosco.practicareservasaula.entity.Reservas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservasRepository extends JpaRepository<Reservas, Long> {

}
