package es.iesjuanbosco.practicareservasaula.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class Reservas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaReserva;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimaModificacion;

    @Column(length = 50, nullable = false)
    private String motivo;

    // Relaciones con otras entidades
    @ManyToOne
    private Aulas aula;

    @ManyToOne
    private Profesores profesor;

    @ManyToOne
    private FranjasHorarias franjaHoraria;


    // Se ejecuta automáticamente antes de persistir una nueva reserva
    @PrePersist     // Prepara la fecha de creación antes de persistir
    private void onCreate() {
        fechaCreacion = LocalDateTime.now();
        this.fechaUltimaModificacion = LocalDateTime.now();
    }

    // Se ejecuta automáticamente antes de actualizar una reserva existente
    @PreUpdate
    protected void onUpdate() {
        this.fechaUltimaModificacion = LocalDateTime.now();
    }



}
