package es.iesjuanbosco.practicareservasaula.service;

import es.iesjuanbosco.practicareservasaula.entity.Aulas;
import es.iesjuanbosco.practicareservasaula.repository.AulasRepository;
import es.iesjuanbosco.practicareservasaula.repository.ReservasRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AulasService {

    private final AulasRepository aulasRepo; // Inyección por constructor obligatoria

    // Constructor para inyección de dependencias
    public AulasService(AulasRepository aulasRepo) {

        this.aulasRepo = aulasRepo;
    }


    // Metodo para validar los filtros de capacidad y tieneOrdenadores
    public List<Aulas> validarFiltros(List<Aulas> aulas, int capacidad, Boolean tieneOrdenadores) {

        List<Aulas> lista = new ArrayList<>();

        for (Aulas aula : aulas) {
            // Si se pasa un parámetro de consulta "capacidad" mayor que 0, devuelve las aulas cuya capacidad sea superior a ese número
            if (aula.getCapacidad() > capacidad) {
                // Si se pasa un parámetro de consulta "tieneOrdenadores" con valor true, devuelve las aulas que tienen ordenadores
                if (tieneOrdenadores == null || aula.getTieneOrdenadores() ==  tieneOrdenadores) {
                    lista.add(aula);
                }
            }
        }
        return lista;
    }
}
