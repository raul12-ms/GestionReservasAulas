package es.iesjuanbosco.practicareservasaula.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class Aulas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String nombre;

    @Column(length = 50, nullable = false)
    private String ubicacion;

    @Column(nullable = false)
    private Integer capacidad;

    @Column(nullable = false)
    private Boolean tieneOrdenadores;

    @JsonIgnore // Para evitar la serialización de la lista de reservas al convertir Aulas a JSON
    @OneToMany(mappedBy = "aula", cascade = CascadeType.REMOVE)     // CascadeType.PERSIST
    private List<Reservas> reservas;

}
