package es.iesjuanbosco.practicareservasaula.controller;

import es.iesjuanbosco.practicareservasaula.entity.Reservas;
import es.iesjuanbosco.practicareservasaula.repository.ReservasRepository;
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
        return ResponseEntity.status(HttpStatus.CREATED).body(reservasRepo.save(reserva));  // Devuelve 201 Created con el reserva creada
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
