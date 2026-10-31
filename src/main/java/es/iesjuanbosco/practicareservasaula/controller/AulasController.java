package es.iesjuanbosco.practicareservasaula.controller;

import es.iesjuanbosco.practicareservasaula.entity.Aulas;
import es.iesjuanbosco.practicareservasaula.repository.AulasRepository;
import es.iesjuanbosco.practicareservasaula.service.AulasService;
import es.iesjuanbosco.practicareservasaula.service.ReservasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AulasController {

    // Inyección de dependencias de los repositorios
    @Autowired
    private AulasRepository aulasRepo;

    // Inyección del servicio
    private final AulasService aulasService;

    // Constructor para inyección de dependencias del servicio
    public AulasController(AulasService aulasService) {
        this.aulasService = aulasService;
    }


    // GET: http://localhost:8080/aulas -> devuelve todas las aulas
    // GET: http://localhost:8080/aulas?capacidad=20 -> devuelve las aulas cuya capacidad sea superior a 20
    // GET: http://localhost:8080/aulas?ordenadores=true -> devuelve las aulas que tienen ordenadores
    // GET: http://localhost:8080/aulas?capacidad=20&ordenadores=true -> devuelve las aulas cuya capacidad sea superior a 20 y que tienen ordenadores
    @GetMapping("/aulas")
    public List<Aulas> findAll(
            @RequestParam(defaultValue = "-1") int capacidad,
            @RequestParam(required = false) Boolean tieneOrdenadores   // required = false indica que el parámetro de consulta es opcional, si no se pasa, se devuelve null
    ) {
        // Recogemos todas las aulas de la base de datos y las filtramos según los parámetros de consulta
        List<Aulas> aulas = aulasRepo.findAll();
        
        return aulasService.validarFiltros(aulas, capacidad, tieneOrdenadores);
    }


    // GET: http://localhost:8080/aulas/{id} -> devuelve una aula por id
    @GetMapping("/aulas/{id}")
    public ResponseEntity<Aulas> findById(@PathVariable Long id) {
        return aulasRepo.findById(id)
                .map(ResponseEntity::ok)    // Devuelve 200 OK con el aula encontrada
                .orElseGet(() -> ResponseEntity.notFound().build());    // Devuelve 404 si no se encuentra el aula
    }


    // POST: http://localhost:8080/aulas -> crea una nueva aula
    @PostMapping("/aulas")
    public ResponseEntity<Aulas> create(@RequestBody Aulas aula) {
        return ResponseEntity.status(HttpStatus.CREATED).body(aulasRepo.save(aula));  // Devuelve 201 Created con el aula creada
    }


    // PUT: http://localhost:8080/aulas/{id} -> actualiza una aula por id y cuerpo de la petición
    @PutMapping("/aulas/{id}")
    public ResponseEntity<Aulas> update(@PathVariable Long id, @RequestBody Aulas aula) {
        if (!aulasRepo.existsById(id)) {
            return ResponseEntity.notFound().build();   // Devuelve 404 si no se encuentra el aula
        }

        aula.setId(id);
        return ResponseEntity.ok(aulasRepo.save(aula)); // Devuelve 200 OK con el aula actualizada
    }


    // DELETE: http://localhost:8080/aulas/{id} -> borra una aula por id
    @DeleteMapping("/aulas/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (!aulasRepo.existsById(id)) {
            return ResponseEntity.notFound().build();   // Devuelve 404 si no se encuentra el aula
        }

        aulasRepo.deleteById(id);
        return ResponseEntity.noContent().build();  // Devuelve 204 No Content si se ha borrado correctamente
    }

    // DELETE: http://localhost:8080/aulas -> borra todas las aulas
    @DeleteMapping("/aulas")
    public ResponseEntity<Void> deleteAll() {
        aulasRepo.deleteAll();
        return ResponseEntity.noContent().build();  // Devuelve 204 No Content si se han borrado todas las aulas
    }

}
