package es.iesjuanbosco.practicareservasaula.controller;

import es.iesjuanbosco.practicareservasaula.entity.Reservas;
import es.iesjuanbosco.practicareservasaula.repository.ReservasRepository;
import es.iesjuanbosco.practicareservasaula.service.ReservasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReservasController {

    // Inyección de dependencias de los repositorios
    @Autowired
    private ReservasRepository reservasRepo;

    // Inyección del servicio
    private final ReservasService reservaService;

    // Constructor para inyección de dependencias del servicio
    public ReservasController(ReservasService reservaService) {

        this.reservaService = reservaService;
    }


    // GET: http://localhost:8080/reservas -> devuelve todas las reservas
    @GetMapping("/reservas")
    public List<Reservas> findAll() {
        return reservasRepo.findAll();
    }


    // GET: http://localhost:8080/reservas/{id} -> devuelve una reserva por id
    @GetMapping("/reservas/{id}")
    public ResponseEntity<Reservas> findById(@PathVariable Long id) {
        return reservasRepo.findById(id)
                .map(ResponseEntity::ok)    // Devuelve 200 OK con el reserva encontrada
                .orElseGet(() -> ResponseEntity.notFound().build());    // Devuelve 404 si no se encuentra el reserva
    }


    // POST: http://localhost:8080/reservas -> crea una nueva reserva
    @PostMapping("/reservas")
    public ResponseEntity<Reservas> create(@RequestBody Reservas reserva) {

        // Recogemos todas las reservas de la base de datos y validamos la nueva reserva
        List<Reservas> listaReservas = reservasRepo.findAll();

        if (!reservaService.esFechaValida(reserva.getFechaReserva())) {
            // Devuelve 400 Bad Request si la fecha de la reserva es anterior a la fecha actual
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if(!reservaService.estaFranjaLibre(reserva, listaReservas)) {
            // Devuelve 409 Conflict si la franja horaria ya está reservada para la fecha indicada o si la fecha es anterior a la actual
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        // Si tódo es correcto, guardamos la reserva y devolvemos 201 Created con la reserva creada
        Reservas nuevaReserva = reservasRepo.save(reserva);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReserva);
    }


    // PUT: http://localhost:8080/reservas/{id} -> actualiza una reserva por id y cuerpo de la petición
    @PutMapping("/reservas/{id}")
    public ResponseEntity<Reservas> update(@PathVariable Long id, @RequestBody Reservas reserva) {
        if (!reservasRepo.existsById(id)) {
            return ResponseEntity.notFound().build();   // Devuelve 404 si no se encuentra el reserva
        }

        reserva.setId(id);
        return ResponseEntity.ok(reservasRepo.save(reserva)); // Devuelve 200 OK con el reserva actualizada
    }


    // DELETE: http://localhost:8080/reservas/{id} -> borra una reserva por id
    @DeleteMapping("/reservas/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (!reservasRepo.existsById(id)) {
            return ResponseEntity.notFound().build();   // Devuelve 404 si no se encuentra el reserva
        }

        reservasRepo.deleteById(id);
        return ResponseEntity.noContent().build();  // Devuelve 204 No Content si se ha borrado correctamente
    }

    // DELETE: http://localhost:8080/reservas -> borra todas las reservas
    @DeleteMapping("/reservas")
    public ResponseEntity<Void> deleteAll() {
        reservasRepo.deleteAll();
        return ResponseEntity.noContent().build();  // Devuelve 204 No Content si se han borrado todas las reservas
    }

}
