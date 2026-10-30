package es.iesjuanbosco.practicareservasaula.controller;


import es.iesjuanbosco.practicareservasaula.entity.FranjasHorarias;
import es.iesjuanbosco.practicareservasaula.repository.FranjasHorariasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FranjasHorariasController {

    // Inyección de dependencias de los repositorios
    @Autowired
    private FranjasHorariasRepository franjasHorariasRepo;


    // GET: http://localhost:8080/franjasHorarias -> devuelve todas las franjasHorarias
    @GetMapping("/franjasHorarias")
    public List<FranjasHorarias> findAll() {
        return franjasHorariasRepo.findAll();
    }


    // GET: http://localhost:8080/franjasHorarias/{id} -> devuelve una reserva por id
    @GetMapping("/franjasHorarias/{id}")
    public ResponseEntity<FranjasHorarias> findById(@PathVariable Long id) {
        return franjasHorariasRepo.findById(id)
                .map(ResponseEntity::ok)    // Devuelve 200 OK con el reserva encontrada
                .orElseGet(() -> ResponseEntity.notFound().build());    // Devuelve 404 si no se encuentra el reserva
    }


    // POST: http://localhost:8080/franjasHorarias -> crea una nueva reserva
    @PostMapping("/franjasHorarias")
    public ResponseEntity<FranjasHorarias> create(@RequestBody FranjasHorarias reserva) {
        return ResponseEntity.status(HttpStatus.CREATED).body(franjasHorariasRepo.save(reserva));  // Devuelve 201 Created con el reserva creada
    }


    // PUT: http://localhost:8080/franjasHorarias/{id} -> actualiza una reserva por id y cuerpo de la petición
    @PutMapping("/franjasHorarias/{id}")
    public ResponseEntity<FranjasHorarias> update(@PathVariable Long id, @RequestBody FranjasHorarias reserva) {
        if (!franjasHorariasRepo.existsById(id)) {
            return ResponseEntity.notFound().build();   // Devuelve 404 si no se encuentra el reserva
        }

        reserva.setId(id);
        return ResponseEntity.ok(franjasHorariasRepo.save(reserva)); // Devuelve 200 OK con el reserva actualizada
    }


    // DELETE: http://localhost:8080/franjasHorarias/{id} -> borra una reserva por id
    @DeleteMapping("/franjasHorarias/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (!franjasHorariasRepo.existsById(id)) {
            return ResponseEntity.notFound().build();   // Devuelve 404 si no se encuentra el reserva
        }

        franjasHorariasRepo.deleteById(id);
        return ResponseEntity.noContent().build();  // Devuelve 204 No Content si se ha borrado correctamente
    }

    // DELETE: http://localhost:8080/franjasHorarias -> borra todas las franjasHorarias
    @DeleteMapping("/franjasHorarias")
    public ResponseEntity<Void> deleteAll() {
        franjasHorariasRepo.deleteAll();
        return ResponseEntity.noContent().build();  // Devuelve 204 No Content si se han borrado todas las franjasHorarias
    }
}
