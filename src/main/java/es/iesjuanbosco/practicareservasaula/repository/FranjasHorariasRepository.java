package es.iesjuanbosco.practicareservasaula.repository;

import es.iesjuanbosco.practicareservasaula.entity.FranjasHorarias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FranjasHorariasRepository extends JpaRepository<FranjasHorarias, Long> {

}
