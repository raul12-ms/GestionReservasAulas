package es.iesjuanbosco.practicareservasaula.controller;

import es.iesjuanbosco.practicareservasaula.entity.Profesores;
import es.iesjuanbosco.practicareservasaula.repository.ProfesoresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProfesoresController {

    // Inyección de dependencias de los repositorios
    @Autowired
    private ProfesoresRepository profesoresRepo;


    // GET: http://localhost:8080/profesores -> devuelve todas las profesores
    @GetMapping("/profesores")
    public List<Profesores> findAll() {
        return profesoresRepo.findAll();
    }


    // GET: http://localhost:8080/profesores/{id} -> devuelve una reserva por id
    @GetMapping("/profesores/{id}")
    public ResponseEntity<Profesores> findById(@PathVariable Long id) {
        return profesoresRepo.findById(id)
                .map(ResponseEntity::ok)    // Devuelve 200 OK con el reserva encontrada
                .orElseGet(() -> ResponseEntity.notFound().build());    // Devuelve 404 si no se encuentra el reserva
    }


    // POST: http://localhost:8080/profesores -> crea una nueva reserva
    @PostMapping("/profesores")
    public ResponseEntity<Profesores> create(@RequestBody Profesores reserva) {
        return ResponseEntity.status(HttpStatus.CREATED).body(profesoresRepo.save(reserva));  // Devuelve 201 Created con el reserva creada
    }


    // PUT: http://localhost:8080/profesores/{id} -> actualiza una reserva por id y cuerpo de la petición
    @PutMapping("/profesores/{id}")
    public ResponseEntity<Profesores> update(@PathVariable Long id, @RequestBody Profesores reserva) {
        if (!profesoresRepo.existsById(id)) {
            return ResponseEntity.notFound().build();   // Devuelve 404 si no se encuentra el reserva
        }

        reserva.setId(id);
        return ResponseEntity.ok(profesoresRepo.save(reserva)); // Devuelve 200 OK con el reserva actualizada
    }


    // DELETE: http://localhost:8080/profesores/{id} -> borra una reserva por id
    @DeleteMapping("/profesores/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (!profesoresRepo.existsById(id)) {
            return ResponseEntity.notFound().build();   // Devuelve 404 si no se encuentra el reserva
        }

        profesoresRepo.deleteById(id);
        return ResponseEntity.noContent().build();  // Devuelve 204 No Content si se ha borrado correctamente
    }

    // DELETE: http://localhost:8080/profesores -> borra todas las profesores
    @DeleteMapping("/profesores")
    public ResponseEntity<Void> deleteAll() {
        profesoresRepo.deleteAll();
        return ResponseEntity.noContent().build();  // Devuelve 204 No Content si se han borrado todas las profesores
    }
}
