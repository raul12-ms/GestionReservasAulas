package es.iesjuanbosco.practicareservasaula.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class FranjasHorarias {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalTime horaInicio;
    private LocalTime horaFin;

    @Column(length = 50, nullable = false)
    private String descripcion;

    @JsonIgnore // Para evitar la serialización de la lista de reservas al convertir franjasHorarias a JSON
    @OneToMany(mappedBy = "franjaHoraria", cascade = CascadeType.REMOVE)     // CascadeType.PERSIST
    private List<Reservas> reservas;

}
