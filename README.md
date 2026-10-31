# Gestión de reservas de aulas

Aplicación web para gestionar la reserva de aulas de un centro educativo. Permite consultar aulas y profesores, crear reservas para una fecha y una franja horaria, filtrar aulas por capacidad y disponibilidad de ordenadores y eliminar reservas existentes.

El proyecto está realizado con **Spring Boot**, **Spring Data JPA**, **MySQL** y un frontend estático en **HTML, CSS y JavaScript**. El backend expone una API REST y sirve el frontend desde la misma aplicación, por lo que no es necesario configurar CORS para el uso normal.

## Funcionalidades principales

- Listado de aulas con nombre, ubicación, capacidad y disponibilidad de ordenadores.
- Filtros de aulas:
  - aulas con o sin ordenadores;
  - capacidad superior a la indicada.
- Listado de profesores y franjas horarias para rellenar el formulario de reserva.
- Creación de reservas con:
  - fecha;
  - aula;
  - profesor;
  - franja horaria;
  - motivo.
- Rechazo de fechas anteriores al día actual.
- Rechazo de reservas duplicadas para la misma combinación de **fecha, aula y franja horaria**.
- Consulta y eliminación de reservas desde la interfaz.
- Mensajes de estado para operaciones correctas, errores de conexión y conflictos de disponibilidad.

## Tecnologías

| Tecnología | Uso |
|---|---|
| Java 17 | Lenguaje principal |
| Spring Boot 4.1.1 | Arranque y configuración de la aplicación |
| Spring MVC | Controladores REST |
| Spring Data JPA / Hibernate | Persistencia y mapeo objeto-relacional |
| MySQL | Base de datos |
| Lombok | Getters, setters, constructores y builders |
| Maven Wrapper | Compilación y ejecución sin instalar Maven globalmente |
| HTML, CSS y JavaScript | Interfaz de usuario |

## Estructura del proyecto

```text
src/
├── main/
│   ├── java/es/iesjuanbosco/practicareservasaula/
│   │   ├── controller/       # Endpoints REST
│   │   ├── entity/           # Entidades JPA
│   │   ├── repository/       # Repositorios Spring Data
│   │   ├── service/          # Reglas de negocio
│   │   └── GestionReservasAulaApplication.java
│   └── resources/
│       ├── application.properties
│       └── static/frontend/  # index.html, styles.css y app.js
└── test/
    └── java/                 # Pruebas de contexto
```

Las entidades principales son `Aulas`, `Profesores`, `FranjasHorarias` y `Reservas`. Una reserva se relaciona con un aula, un profesor y una franja horaria mediante relaciones `ManyToOne`. Las listas inversas se ignoran al serializar JSON para evitar recorridos circulares.

## Requisitos previos

- JDK 17.
- MySQL en ejecución en `localhost:3306`.
- Una base de datos con permisos para el usuario configurado en `application.properties`.

La configuración actual utiliza la base de datos `reservas-aulas` y `spring.jpa.hibernate.ddl-auto=update`, por lo que Hibernate actualiza el esquema al arrancar. Para otro entorno se deben cambiar la URL, el usuario y la contraseña en `src\main\resources\application.properties`. No se deben subir credenciales reales al repositorio.

## Puesta en marcha

Desde PowerShell, situado en la raíz del proyecto:

```powershell
.\mvnw.cmd spring-boot:run
```

También se puede crear el JAR ejecutable:

```powershell
.\mvnw.cmd clean package
java -jar target\GestionReservasAulas-0.0.1-SNAPSHOT.jar
```

Con la aplicación iniciada, abrir:

```text
http://localhost:8080/frontend/index.html
```

La interfaz carga automáticamente los datos de referencia y las reservas existentes. Si la base de datos no contiene aulas, profesores o franjas horarias, los selectores aparecerán vacíos y será necesario crearlos mediante la API.

## API REST

Todos los endpoints devuelven y reciben JSON salvo las respuestas `204 No Content`.

### Aulas

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/aulas` | Lista todas las aulas |
| `GET` | `/aulas/{id}` | Consulta un aula |
| `GET` | `/aulas?capacidad=20` | Aulas con capacidad superior a 20 |
| `GET` | `/aulas?tieneOrdenadores=true` | Aulas con ordenadores |
| `POST` | `/aulas` | Crea un aula |
| `PUT` | `/aulas/{id}` | Actualiza un aula |
| `DELETE` | `/aulas/{id}` | Elimina un aula |
| `DELETE` | `/aulas` | Elimina todas las aulas |

### Profesores y franjas horarias

`/profesores` y `/franjasHorarias` ofrecen las operaciones CRUD equivalentes: `GET` de colección, `GET` por identificador, `POST`, `PUT` y `DELETE`.

Ejemplos de alta:

```json
// POST /profesores
{
  "nombre": "Ana",
  "apellidos": "García López",
  "email": "ana.garcia@centro.es",
  "departamento": "Informática"
}
```

```json
// POST /franjasHorarias
{
  "horaInicio": "09:00:00",
  "horaFin": "10:00:00",
  "descripcion": "Primera hora"
}
```

### Reservas

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/reservas` | Lista las reservas |
| `GET` | `/reservas/{id}` | Consulta una reserva |
| `POST` | `/reservas` | Crea una reserva validando fecha y conflicto |
| `PUT` | `/reservas/{id}` | Actualiza una reserva |
| `DELETE` | `/reservas/{id}` | Elimina una reserva |
| `DELETE` | `/reservas` | Elimina todas las reservas |

Ejemplo de creación:

```json
{
  "fechaReserva": "2026-11-03",
  "motivo": "Examen de matemáticas",
  "aula": { "id": 1 },
  "profesor": { "id": 1 },
  "franjaHoraria": { "id": 1 }
}
```

Las respuestas relevantes de `POST /reservas` son:

- `201 Created`: reserva guardada.
- `400 Bad Request`: la fecha es nula o anterior a hoy.
- `409 Conflict`: ya existe una reserva para la misma fecha, aula y franja.

## Principales problemas encontrados y soluciones

### Conflictos de reservas

El problema funcional más importante era evitar que dos reservas ocuparan el mismo aula en la misma franja y fecha. Se trasladó esta regla a `ReservasService.estaFranjaLibre` para no dejarla únicamente en el frontend. La comprobación compara los tres valores y devuelve `409 Conflict` cuando existe coincidencia.

### Fechas no válidas

El formulario limita la fecha mínima en el navegador, pero esa restricción puede saltarse llamando directamente a la API. Por ello, el backend vuelve a validar la fecha con `LocalDate.now()` y devuelve `400 Bad Request`.

### Filtros opcionales

Los filtros de aulas son opcionales y deben funcionar de forma independiente o combinada. La capa de servicio aplica la capacidad y el valor nullable de `tieneOrdenadores`; cuando no se envía un filtro de ordenadores, no se descarta ninguna aula por ese criterio.

### Respuestas JSON y relaciones JPA

Las relaciones inversas podían producir serialización circular y respuestas demasiado grandes. Las colecciones de reservas de las entidades relacionadas se marcan con `@JsonIgnore`, de modo que las respuestas siguen siendo útiles para el frontend sin entrar en ciclos.

### Errores de comunicación y UX

El frontend centraliza las peticiones `fetch`, traduce los estados HTTP más importantes a mensajes comprensibles y desactiva el botón de envío mientras se crea una reserva. También escapa el contenido que se inserta en HTML mediante `escapeHtml` y solicita confirmación antes de eliminar una reserva.

## Decisiones tomadas a partir de propuestas de IA

- **Mantener una arquitectura por capas:** se aceptó separar controladores, repositorios, entidades y servicios. La validación de fechas, filtros y disponibilidad pertenece a servicios, mientras que los controladores se ocupan de HTTP.
- **Usar un frontend sin framework:** se eligió JavaScript nativo porque la aplicación es pequeña, el frontend se sirve como contenido estático y no era necesario añadir una cadena de compilación ni dependencias de Node.
- **Servir frontend y API desde Spring Boot:** se optó por una única aplicación y rutas relativas (`/aulas`, `/reservas`, etc.). Esto simplifica la ejecución local y evita problemas de CORS.
- **Usar códigos HTTP diferenciados:** `400` representa datos inválidos y `409` representa un conflicto de disponibilidad. Esta decisión permite que el frontend muestre mensajes específicos.
- **Conservar CRUD completo en la API:** aunque la interfaz se centra en crear y eliminar reservas, se mantuvieron operaciones CRUD para aulas, profesores y franjas, lo que facilita cargar y preparar los datos iniciales.
- **No confiar únicamente en la validación del navegador:** las reglas importantes se repiten en el backend, ya que la API también puede consumirse con Postman, `curl` u otro cliente.

## Ejemplos de prompts utilizados

Estos son ejemplos representativos de las instrucciones utilizadas durante el desarrollo asistido por IA:


```text
Diseña una interfaz estática en HTML, CSS y JavaScript para consultar aulas,
filtrarlas por capacidad y ordenadores, crear reservas y eliminarlas. Debe usar
la API REST existente y mostrar mensajes claros para los estados 400 y 409.
```

```text
Analiza el código como una revisión de mantenimiento: detecta serialización
circular en las relaciones JPA, entradas no escapadas en el frontend, validaciones
que solo existan en el navegador y problemas de configuración de la base de datos.
```

La IA se utilizó como apoyo para proponer estructura, validaciones, mensajes y diseño de interfaz. Las propuestas se revisaron contra el código y se probaron antes de incorporarlas; no se consideró que una sugerencia fuera correcta automáticamente.

## Errores cometidos por la IA y correcciones aplicadas

Durante el desarrollo se detectaron varios errores típicos de generación automática:

- Se propusieron validaciones únicamente en JavaScript. Se corrigió repitiendo las reglas críticas en `ReservasService` y `ReservasController`.
- Algunas propuestas trataban cualquier reserva de la misma franja como conflicto, aunque perteneciera a otra aula. Se corrigió para comparar fecha, aula y franja conjuntamente.
- Se generaron mensajes del frontend que no distinguían entre un error de validación y un conflicto. Se mapearon explícitamente `400` y `409`.
- Se sugirió exponer directamente relaciones bidireccionales de JPA. Se corrigió usando `@JsonIgnore` en las colecciones inversas.
- Se mantuvieron restos de código y comentarios de una primera versión, como imports innecesarios y comentarios que no siempre coincidían con el comportamiento final. Es una deuda de limpieza pendiente, aunque no impide el funcionamiento principal.

## Limitaciones y mejoras pendientes

- La comprobación de disponibilidad carga todas las reservas en memoria y no está protegida por una restricción única en la base de datos. En concurrencia alta podrían producirse carreras; la solución robusta sería una restricción compuesta y/o una consulta de existencia transaccional.
- Las operaciones `PUT` no reutilizan todas las validaciones de creación, por lo que deberían validarse también las actualizaciones.
- No existe autenticación ni autorización: cualquier cliente que alcance la API puede consultar, crear, modificar o borrar datos.
- No hay datos iniciales automáticos ni migraciones versionadas. Para desplegar en otro entorno conviene usar Flyway o Liquibase y scripts de carga.
- La suite de pruebas contiene actualmente una prueba de arranque (`contextLoads`), pero faltan pruebas unitarias de servicios y pruebas de integración de los endpoints.
- `spring.jpa.hibernate.ddl-auto=update` resulta práctico durante el desarrollo, pero no es la estrategia recomendada para producción.
- La configuración de base de datos debe externalizarse mediante variables de entorno o perfiles para no depender de credenciales locales.

## Pruebas

Para ejecutar las pruebas disponibles:

```powershell
.\mvnw.cmd test
```

La prueba actual verifica que el contexto de Spring puede arrancar. Como siguiente ampliación deberían cubrirse, al menos, las fechas pasadas, los conflictos de aula/franja, los filtros de aulas, los identificadores inexistentes y las respuestas HTTP de la API.

## Estado del desarrollo

El proyecto es una práctica funcional de gestión de reservas y sirve como base para seguir incorporando validación, seguridad, pruebas y despliegue. La documentación diferencia deliberadamente entre el comportamiento implementado y las mejoras recomendadas para un entorno real.
