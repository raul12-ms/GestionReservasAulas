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
public class Profesores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String nombre;
    @Column(length = 50, nullable = false)
    private String apellidos;
    @Column(length = 50, nullable = false)
    private String email;
    @Column(length = 30, nullable = false)
    private String departamento;

    @JsonIgnore // Para evitar la serialización de la lista de reservas al convertir Profesores a JSON
    @OneToMany(mappedBy = "profesor", cascade = CascadeType.REMOVE)     // CascadeType.PERSIST
    private List<Reservas> reservas;

}
