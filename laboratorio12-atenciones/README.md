# Laboratorio 12 — Transacciones, rollback y JPQL

UPN · SIST1402A · Soluciones Web y Aplicaciones Distribuidas · 2026-2

Este proyecto responde a la **guía de Laboratorio 12 adjuntada por el estudiante**, cuyo
contrato base es registrar una \`Atencion\` y su \`HistorialClinico\` en la misma
transacción. No se confunde con la **guía alternativa de ventas y stock** existente en
la carpeta \`sem 12\` del curso.

## Tecnologías vistas en clase

- Java 17, Spring Boot 3.5.16, Maven Wrapper.
- Spring Web (sem. 1–2), Spring Data JPA + H2 (sem. 3–4).
- \`JpaRepository\`, \`@Query\`, \`@Param\` (sem. 4 y 12).
- Validaciones y respuestas HTTP (sem. 5).
- \`Pageable\`, \`Page<T>\`, Swagger/OpenAPI (sem. 11).
- \`@Transactional(rollbackFor = Exception.class)\` **solo en Service** (sem. 12).

No requiere Angular, MySQL, Docker ni servicios externos para esta práctica: la
guía adjuntada pide H2 y un backend Spring Boot ejecutable.

## Ejecución

Desde esta carpeta:

\`\`\`powershell
java -version
.\mvnw.cmd -v
.\mvnw.cmd dependency:tree
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
\`\`\`

En Linux / Codespaces, reemplace \`.\mvnw.cmd\` por \`./mvnw\`.

Swagger: \`http://localhost:8080/swagger-ui/index.html\`.

## Pruebas manuales (PowerShell)

\`\`\`powershell
curl.exe -i http://localhost:8080/api/saludo
curl.exe -i http://localhost:8080/api/pacientes

# Registrar paciente: tomar el ID devuelto por el servidor.
curl.exe -i -X POST "http://localhost:8080/api/pacientes" -H "Content-Type: application/json" -d '{"nombre":"Maria Lopez","hemoglobina":11.2}'

# Caso correcto: guarda atencion e historial (sustituir pacienteId).
curl.exe -i -X POST "http://localhost:8080/api/atenciones" -H "Content-Type: application/json" -d '{"pacienteId":1,"diagnostico":"Control de anemia","simularError":false}'

# Comprobar estado antes y despues de un fallo:
curl.exe -i http://localhost:8080/api/atenciones
curl.exe -i http://localhost:8080/api/historiales

# Caso fallido: debe retornar 500, sin agregar registros a ninguna tabla.
curl.exe -i -X POST "http://localhost:8080/api/atenciones" -H "Content-Type: application/json" -d '{"pacienteId":1,"diagnostico":"Fallo de prueba","simularError":true}'

curl.exe -i http://localhost:8080/api/atenciones
curl.exe -i http://localhost:8080/api/historiales

# JPQL con parametros nombrados + paginacion de semana 11.
curl.exe -i "http://localhost:8080/api/atenciones/buscar?nombre=mar&hemoglobinaMin=10&hemoglobinaMax=12&page=0&size=5&sort=id,asc"
\`\`\`

Si la BD H2 se reinicia, se borran sus registros: es una base **en memoria**.
Para probar de nuevo, cree nuevamente el paciente y use el ID entregado.

## Flujo transaccional

\`\`\`text
POST /api/atenciones
    -> AtencionController
    -> AtencionService.registrarAtencion() @Transactional(rollbackFor=Exception.class)
    -> repoAtencion.saveAndFlush()  [operacion A]
    -> repoHistorial.saveAndFlush() [operacion B]
    -> simularError?
         no -> COMMIT
         si -> throw Exception -> ROLLBACK de ambas tablas
\`\`\`

\`saveAndFlush\` envía operaciones a H2, pero no las confirma fuera del límite
transaccional. La excepción controlada de prueba es **checked**, por lo que
\`rollbackFor = Exception.class\` resulta significativo.

## Estructura

\`\`\`text
src/main/java/pe/upn/sist1402a/semana12/
├── controller/    # Entrada HTTP y JSON
├── dto/           # Datos del POST
├── exception/     # Respuestas HTTP estandarizadas
├── model/         # Entidades JPA
├── repository/    # JpaRepository, JPQL y @Param
├── service/       # Lógica de negocio y transacción
└── config/        # Swagger de la sem. 11
\`\`\`

## Evidencias y preguntas del docente

Consulte [EVIDENCIAS.md](EVIDENCIAS.md), que contiene respuestas a las tres
preguntas de análisis, conclusiones y una plantilla para incorporar la salida
real de las pruebas. **No se incluyen capturas ni ejecuciones inventadas.**

## Referencias de clase

- Carpeta \`SOLUCIONES WEB Y APLI. DIST.\`, \`sem 1\` hasta \`sem 12\`,
  especialmente \`requerimientos_semana_04.txt\`, \`semana_11.txt\`
  y \`semana_12.txt\`.
- Guía \`UPN_SIST1402A_S12_Laboratorio_12_Transacciones,_rollback_y_JPQL_con_parámetros_2026-2.docx\`.
