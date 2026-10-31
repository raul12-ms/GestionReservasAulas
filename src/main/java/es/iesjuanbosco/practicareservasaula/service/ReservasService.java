package es.iesjuanbosco.practicareservasaula.service;

import es.iesjuanbosco.practicareservasaula.entity.Reservas;
import es.iesjuanbosco.practicareservasaula.repository.ReservasRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

// Capa Service: Aislamiento de lógica de negocio (Mejor practica)
@Service
public class ReservasService {

    private final ReservasRepository reservasRepo; // Inyección por constructor obligatoria

    // Constructor para inyección de dependencias
    public ReservasService(ReservasRepository reservasRepo) {
        this.reservasRepo = reservasRepo;
    }

    // Metodo para validar si la fecha de una reserva es igual o posterior a la fecha actual
    public boolean esFechaValida(LocalDate fechaReserva) {

        // Devuelve true si la fecha de reserva es igual o posterior a la fecha actual, false en caso contrario
        return fechaReserva != null && !fechaReserva.isBefore(LocalDate.now());
    }

    // Metodo para validar si la franja horaria de una reserva está libre
    public boolean estaFranjaLibre(Reservas reserva, List<Reservas> reservas) {

        // Validamos que la reserva tenga aula y franja horaria asignadas
        if (reserva.getAula() == null || reserva.getAula().getId() == null ||
            reserva.getFranjaHoraria() == null || reserva.getFranjaHoraria().getId() == null) {
            return false;
        }

        // La franja horaria no debe estar reservada para la fecha indicada.
        for (Reservas r : reservas) {
            boolean mismaFecha = r.getFechaReserva().equals(reserva.getFechaReserva());
            boolean mismaFranja = r.getFranjaHoraria().getId().equals(reserva.getFranjaHoraria().getId());
            boolean mismaAula = r.getAula().getId().equals(reserva.getAula().getId());

            // El conflicto solo existe si coinciden FECHA, FRANJA y AULA a la vez
            if (mismaFecha && mismaFranja && mismaAula) {
                return false;
            }
        }

        return true; // La franja horaria está libre para la fecha indicada
    }



}
