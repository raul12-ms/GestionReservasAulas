# Instrucciones para agentes de IA

## Contexto del proyecto

- Es una aplicación Spring Boot para gestionar reservas de aulas.
- El backend está escrito en Java 17 y utiliza Spring MVC, Spring Data JPA, Hibernate, MySQL y Lombok.
- El frontend estático se encuentra en `src/main/resources/static/frontend/` y está compuesto por `index.html`, `styles.css` y `app.js`.
- La aplicación expone la API REST y sirve el frontend desde el mismo servidor.
- Las entidades principales son `Aulas`, `Profesores`, `FranjasHorarias` y `Reservas`.
- La lógica de disponibilidad y validación de fechas está en `service/`; los endpoints HTTP están en `controller/`.

## Antes de modificar código

1. Lee los archivos relacionados con el cambio y comprueba cómo se utiliza actualmente cada clase o endpoint.
2. Respeta la estructura existente:
   - `entity/`: entidades JPA.
   - `repository/`: repositorios Spring Data.
   - `service/`: reglas de negocio.
   - `controller/`: API REST.
   - `src/main/resources/static/frontend/`: interfaz web.
3. No des por válida una propuesta generada automáticamente sin contrastarla con el código existente.
4. No introduzcas credenciales reales ni publiques valores sensibles de la base de datos.

## Reglas funcionales que deben conservarse

- Una reserva no puede tener una fecha anterior al día actual.
- No puede existir más de una reserva para la misma combinación de fecha, aula y franja horaria.
- Los conflictos de disponibilidad se comunican mediante `409 Conflict`.
- Las fechas no válidas se comunican mediante `400 Bad Request`.
- Los filtros de aulas pueden combinar capacidad y disponibilidad de ordenadores.
- Las colecciones inversas de reservas no deben serializarse de forma que provoquen ciclos JSON.
- El frontend debe seguir usando rutas relativas de la API, como `/aulas`, `/profesores`, `/franjasHorarias` y `/reservas`.

## Cambios en el backend

- Coloca la lógica de negocio en los servicios, no en el JavaScript del navegador ni únicamente en los controladores.
- Mantén respuestas HTTP coherentes con el comportamiento actual: `201 Created` al crear, `404 Not Found` si no existe el recurso y `204 No Content` al eliminar correctamente.
- Si modificas relaciones JPA o la forma de serializar entidades, comprueba las respuestas JSON y evita recorridos circulares.
- Si cambias la configuración de persistencia, revisa `src/main/resources/application.properties`. La configuración actual usa MySQL en `localhost:3306` y `spring.jpa.hibernate.ddl-auto=update`.

## Cambios en el frontend

- Mantén la comunicación con el backend centralizada en la función `request` de `app.js`.
- Conserva la gestión visible de errores de conexión, `400` y `409`.
- Escapa cualquier dato que se inserte mediante HTML con el mecanismo existente (`escapeHtml`).
- No elimines la confirmación previa antes de borrar una reserva ni la desactivación del botón mientras se crea una reserva sin sustituirlas por un comportamiento equivalente.
- Mantén la interfaz accesible: usa los elementos y atributos existentes (`label`, `required`, `role="status"` y `aria-live`) cuando modifiques los formularios o mensajes.

## Validación

Utiliza el Maven Wrapper incluido en el repositorio. Desde la raíz del proyecto, ejecuta al menos:

```powershell
.\mvnw.cmd test
```

Para comprobar la compilación y generar el artefacto ejecutable:

```powershell
.\mvnw.cmd clean package
```

Si el cambio afecta a la interfaz, comprueba también manualmente `http://localhost:8080/frontend/index.html` con Spring Boot ejecutándose y verifica la carga de aulas, profesores, franjas y reservas.

## Documentación y alcance

- Actualiza `README.md` cuando cambien las instrucciones de ejecución, los endpoints, las reglas funcionales o las limitaciones conocidas.
- No afirmes que existe una funcionalidad, una prueba o una configuración que no esté implementada en el repositorio.
- La suite actual contiene una prueba de contexto (`contextLoads`); no presentes cobertura de reglas de negocio como existente si no se han añadido pruebas específicas.
- Evita cambios no relacionados con la tarea solicitada.
