# Enterprise Fullstack Pagination Architecture - Spring Boot 3, Angular 22, MySQL & Docker (UPN SWAD - Lab 11A)

[![Java 17](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.13-brightgreen?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-22.2-dd0031?style=for-the-badge&logo=angular)](https://angular.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-6.0-blue?style=for-the-badge&logo=typescript)](https://www.typescriptlang.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.4-blue?style=for-the-badge&logo=mysql)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-29.6-2496ed?style=for-the-badge&logo=docker)](https://www.docker.com/)
[![GitHub Codespaces](https://img.shields.io/badge/GitHub%20Codespaces-Cloud%20Ready-black?style=for-the-badge&logo=github)](https://github.com/features/codespaces)
[![Vitest](https://img.shields.io/badge/Vitest-5.0-6e9f18?style=for-the-badge&logo=vitest)](https://vitest.dev/)
[![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-2088FF?style=for-the-badge&logo=githubactions)](https://github.com/features/actions)

> **Repositorio de Portafolio Profesional** desarrollado para la asignatura **Soluciones Web y Aplicaciones Distribuidas (Semana 11 - Laboratorio 11A)** en la **Universidad Privada del Norte (UPN)**.  
> Diseñado e implementado con estándares de ingeniería de software corporativa: arquitectura desacoplada en tres capas (Controller-Service-Repository), paginación real de alto rendimiento en base de datos con Spring Data JPA (`Page`, `Pageable`), persistencia contenerizada reproducible mediante Docker y Docker Compose, frontend reactivo SPA en Angular 22 con soporte de proxy reverso, y pruebas unitarias/integración automatizadas en backend y frontend.

---

## 1. Resumen Ejecutivo para Evaluadores Técnicos y Reclutadores

En aplicaciones web de escala empresarial, transferir conjuntos de datos masivos en una sola petición HTTP degrada severamente el rendimiento: satura el ancho de banda, sobrecarga el recolector de basura en el backend, penaliza el árbol DOM del navegador y arruina los Core Web Vitals (especialmente INP e LCP).

Este proyecto implementa una solución integral al problema de escalabilidad del catálogo de productos:
1. **Paginación Inteligente a Nivel de Base de Datos:** A diferencia de las soluciones ingenuas que descargan todo el catálogo y lo filtran en memoria, este sistema delega la segmentación a MySQL mediante cláusulas `LIMIT` y `OFFSET` optimizadas por Spring Data JPA, combinadas con proyecciones de conteo (`COUNT(*)`) para metadatos.
2. **Contrato de Datos Pagina<T> Robusto:** El backend expone metadatos estructurales (`totalElements`, `totalPages`, `number`, `size`, `first`, `last`) permitiendo que el cliente Angular gobierne la interfaz sin recalcular estados de manera redundante.
3. **Cálculos Transitorios de Negocio (@Transient):** Reglas de negocio dinámicas (como la evaluación de stock crítico: `stock < 5 ? 'INSUFICIENTE' : 'OK'`) son encapsuladas en la capa de dominio sin contaminar el esquema relacional.
4. **Resiliencia de Red y Proxy de Desarrollo:** Integración limpia mediante el proxy reverso de Angular CLI (`proxy.conf.json`), desacoplando el código cliente de las IPs efímeras de la infraestructura en nube.
5. **Entorno 100% Reproducible en la Nube:** Configurado con `.devcontainer/devcontainer.json` para despliegue instantáneo en GitHub Codespaces con Docker-in-Docker habilitado.

---

## 2. Arquitectura de la Solución y Diagramas Draw.io

La solución respeta los principios de Clean Architecture y separación de responsabilidades:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                       GITHUB CODESPACES / DEVCONTAINER                      │
│                                                                             │
│  ┌──────────────────────┐    HTTP (Proxy)     ┌─────────────────────────┐  │
│  │   Angular 22 SPA     │ ──────────────────> │   Spring Boot 3 API     │  │
│  │   (Puerto 4200)      │                     │   (Puerto 8080)         │  │
│  │                      │                     │                         │  │
│  │ • ProductosComponent │                     │ • ProductoController    │  │
│  │ • ProductoService    │                     │ • ProductoService       │  │
│  │ • Pagina<Producto>   │                     │ • ProductoRepository    │  │
│  └──────────────────────┘                     └────────────┬────────────┘  │
│                                                            │ JDBC           │
│                                                            │ SQL LIMIT/?    │
│                                                            v                │
│                                               ┌─────────────────────────┐  │
│                                               │  Docker MySQL 8.4       │  │
│                                               │  (Puerto 3306)          │  │
│                                               │                         │  │
│                                               │ • bd_laboratorio11      │  │
│                                               │ • mysql_lab11_data (Vol)│  │
│                                               └─────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Diagrama Fuente Draw.io
El diagrama arquitectónico formal, detallado con especificación de componentes, flujos de red y estructuras de datos, se encuentra disponible en formato `.drawio` editable:
- **Ruta del Diagrama:** [`diagramas/arquitectura_paginacion.drawio`](diagramas/arquitectura_paginacion.drawio)

---

## 3. Stack Tecnológico

| Capa | Tecnología | Versión | Justificación Técnica |
|---|---|---|---|
| **Backend Runtime** | Java OpenJDK | 17 LTS / 25 | Rendimiento moderno, soporte a largo plazo y compatibilidad con Spring Boot 3. |
| **Framework Backend** | Spring Boot | 3.4.13 | Productividad empresarial, autoconfiguración e integración nativa con Spring Data. |
| **ORM / Acceso a Datos**| Spring Data JPA / Hibernate | 6.6 / 3.4 | Abstracción de persistencia, generación óptima de consultas paginadas y soporte de `Pageable`. |
| **Frontend Framework** | Angular | 22.2 | Componentes Standalone, tipado estricto con TypeScript, control de flujo declarativo. |
| **Diseño / Estilos** | Bootstrap | 5.3 | Sistema de grillas responsivo, tarjetas de catálogo y componentes accesibles. |
| **Base de Datos** | MySQL Server | 8.4 LTS | Motor relacional estándar con soporte para índices b-tree y paginación rápida. |
| **Contenedores** | Docker & Docker Compose | 29.6 / Compose v2 | Entorno de persistencia aislado y reproducible en cualquier host o Codespace. |
| **Pruebas Backend** | JUnit 5 + MockMvc + AssertJ + H2 | 3.4.13 | Validación integral de endpoints REST, repositorios y reglas transitorias de negocio. |
| **Pruebas Frontend** | Vitest + jsdom + HttpTestingController | 5.0 | Ejecución ultra-rápida de pruebas unitarias en componentes y servicios Angular. |
| **Integración Continua** | GitHub Actions | v4 | Pipeline automatizado que valida la compilación y pruebas en cada `git push`. |

---

## 4. Estructura del Proyecto

El repositorio adopta una organización multi-módulo desacoplada:

```
SWAD-LAB11A-spring-boot-angular-pagination-docker/
├── .devcontainer/
│   └── devcontainer.json               # Configuración oficial de GitHub Codespaces con Docker-in-Docker
├── .github/
│   └── workflows/
│       └── ci.yml                      # Pipeline automatizado de GitHub Actions (Backend + Frontend)
├── diagramas/
│   └── arquitectura_paginacion.drawio  # Diagrama arquitectónico en formato Draw.io (Regla R1)
├── docker/
│   └── init.sql                        # Script de inicialización con 15 registros de prueba del laboratorio
├── docker-compose.yml                  # Orquestación del contenedor MySQL 8.4 con volumen persistente
├── backend-productos/                  # Módulo Backend Spring Boot 3
│   ├── pom.xml                         # Dependencias Maven (Web, JPA, MySQL, Validation, H2 test)
│   ├── mvnw / mvnw.cmd                 # Maven Wrapper reproducible
│   └── src/
│       ├── main/
│       │   ├── java/com/ejemplo/laboratorio11/
│       │   │   ├── BackendProductosApplication.java
│       │   │   ├── controller/ProductoController.java
│       │   │   ├── model/Producto.java
│       │   │   ├── repository/ProductoRepository.java
│       │   │   └── service/ProductoService.java
│       │   └── resources/application.properties
│       └── test/
│           ├── java/com/ejemplo/laboratorio11/
│           │   ├── BackendProductosApplicationTests.java
│           │   ├── ProductoControllerTest.java
│           │   └── ProductoRepositoryTest.java
│           └── resources/application-test.properties
└── frontend-productos/                 # Módulo Frontend Angular 22
    ├── package.json                    # Dependencias npm y scripts
    ├── angular.json                    # Configuración de compilación y builder de testing
    └── src/
        ├── index.html
        ├── main.ts
        ├── styles.css                  # Importación de Bootstrap 5
        ├── proxy.conf.json             # Proxy reverso para /api/** -> http://127.0.0.1:8080
        └── app/
            ├── app.ts / app.html       # Componente raíz con <router-outlet>
            ├── app.config.ts           # Configuración con provideRouter y provideHttpClient
            ├── app.routes.ts           # Enrutamiento hacia ProductosComponent
            ├── models/
            │   ├── producto.ts         # Modelo Producto con estadoStock opcional
            │   └── pagina.ts           # Modelo genérico Pagina<T>
            ├── services/
            │   ├── producto.service.ts # Servicio HTTP (listarPaginado, buscarPorNombre)
            │   └── producto.service.spec.ts
            └── productos/
                ├── productos.component.ts   # Componente con selector de tamaño y buscador
                ├── productos.component.html # Tabla responsiva y paginador
                ├── productos.component.css
                └── productos.component.spec.ts
```

---

## 5. Especificación de la API REST y Contratos de Datos

El backend expone endpoints RESTful con parámetros de paginación (`page`, `size`, `sort`) procesados por `@PageableDefault(size = 5, sort = "id")`.

### Matriz de Endpoints

| Método | Endpoint | Parámetros Query | Descripción | Ejemplo de Uso con `curl` |
|---|---|---|---|---|
| `GET` | `/api/productos/pagina` | `page` (int, default: 0)<br>`size` (int, default: 5)<br>`sort` (string, default: `id,asc`) | Retorna una página de productos con metadatos completos. | `curl "http://localhost:8080/api/productos/pagina?page=0&size=5"` |
| `GET` | `/api/productos/pagina` | `page=1&size=5` | Retorna el segundo bloque de 5 productos. | `curl "http://localhost:8080/api/productos/pagina?page=1&size=5"` |
| `GET` | `/api/productos/pagina` | `sort=precio,desc` | Retorna productos ordenados por precio descendente. | `curl "http://localhost:8080/api/productos/pagina?page=0&size=5&sort=precio,desc"` |
| `GET` | `/api/productos/pagina/buscar` | `nombre` (string)<br>`page` (int)<br>`size` (int) | Filtra productos por coincidencia insensible a mayúsculas con paginación. | `curl "http://localhost:8080/api/productos/pagina/buscar?nombre=Laptop&page=0&size=5"` |

### Estructura de Respuesta JSON (`Page<Producto>`)

```json
{
  "content": [
    {
      "id": 1,
      "nombre": "Laptop Lenovo",
      "precio": 2500.0,
      "stock": 10,
      "estadoStock": "OK"
    },
    {
      "id": 2,
      "nombre": "Mouse Logitech",
      "precio": 80.0,
      "stock": 25,
      "estadoStock": "OK"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 5,
    "sort": { "sorted": true, "unsorted": false }
  },
  "totalElements": 15,
  "totalPages": 3,
  "number": 0,
  "size": 5,
  "numberOfElements": 5,
  "first": true,
  "last": false,
  "empty": false
}
```

---

## 6. Funcionalidades Destacadas (Lab 11A + Actividad Guiada + Reto Adicional)

### A. Paginación Básica del Laboratorio 11A
- Tabla responsiva estilizada con Bootstrap 5.
- Formateo de moneda peruana (`S/. PEN`).
- Botones de navegación **« Anterior** y **Siguiente »** que se deshabilitan reactivamente según las propiedades booleanas `first` y `last` devueltas por el servidor.
- Indicador dinámico de avance: `Página X de Y (Z registros)`.

### B. Actividad Guiada (Sección 12): Selector Dinámico de Tamaño de Página
- Selector `<select>` con opciones configuradas en `[5, 10, 20]` productos por página.
- Al modificar la opción, el componente reinicia automáticamente a `page = 0` y efectúa una nueva consulta paginada al backend.
- **Cumplimiento estricto de la regla del laboratorio:** No se descargan todos los registros para paginarlos en el navegador; la consulta `LIMIT ? OFFSET ?` se ejecuta de forma delegada en Spring Boot y MySQL.

### C. Reto Adicional (Sección 16): Búsqueda con Paginación Persistente
- Campo de búsqueda interactivo con botón "Buscar" (también activable con la tecla `Enter`) y botón "Limpiar".
- Al ingresar un término (ej. *"Monitor"* o *"USB"*), invoca `/api/productos/pagina/buscar?nombre=...&page=0&size=5`.
- La paginación continúa funcionando sobre el conjunto de resultados filtrado, respetando `totalElements` y `totalPages` del subconjunto.

---

## 7. Guía de Ejecución y Puesta en Marcha

### Opción 1: En GitHub Codespaces (Recomendada)
1. Abrir el repositorio en GitHub y seleccionar **Code > Codespaces > Create codespace on main**.
2. Una vez cargado el Codespace, abrir la terminal integrada e iniciar MySQL con Docker:
   ```bash
   docker compose up -d
   ```
3. Ejecutar el backend Spring Boot:
   ```bash
   cd backend-productos
   ./mvnw spring-boot:run
   ```
4. En una nueva terminal, ejecutar el frontend Angular con proxy activo:
   ```bash
   cd frontend-productos
   npm install
   npx ng serve --host 0.0.0.0 --proxy-config src/proxy.conf.json
   ```
5. En la pestaña **Ports** de VS Code, abrir el puerto `4200` en el navegador.

### Opción 2: Ejecución Local en Windows / Linux / macOS
1. **Iniciar MySQL en Docker:**
   ```bash
   docker compose up -d
   ```
2. **Ejecutar Pruebas Automatizadas del Backend:**
   ```bash
   cd backend-productos
   ./mvnw test
   ```
3. **Ejecutar Backend:**
   ```bash
   ./mvnw spring-boot:run
   ```
4. **Ejecutar Pruebas y Servidor Frontend:**
   ```bash
   cd ../frontend-productos
   npm install
   npm test
   npm start
   ```
5. Ingresar a `http://localhost:4200` en su navegador web.

---

## 8. Verificación y Calidad de Pruebas Automatizadas

El proyecto cuenta con suites de pruebas unitarias y de integración en ambas capas:

### Backend Test Suite (Spring Boot + JUnit 5 + MockMvc)
```
[INFO] Running com.ejemplo.laboratorio11.BackendProductosApplicationTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.583 s
[INFO] Running com.ejemplo.laboratorio11.ProductoControllerTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.812 s
[INFO] Running com.ejemplo.laboratorio11.ProductoRepositoryTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.033 s
[INFO] Results:
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Casos cubiertos:
- Carga de contexto de Spring Boot (`contextLoads`).
- Paginación predeterminada (5 elementos, `first=true`, `last=false`).
- Segunda página con validación de bandera `last=true`.
- Ordenamiento por precio descendente (`sort=precio,desc`).
- Búsqueda por coincidencia de nombre insensible a mayúsculas.
- Búsqueda con término vacío retornando el catálogo paginado.
- Cálculo de la propiedad `@Transient` `getEstadoStock()` (`< 5` -> "INSUFICIENTE", `>= 5` -> "OK").

### Frontend Test Suite (Angular 22 + Vitest)
```
 ✓  frontend-productos  src/app/services/producto.service.spec.ts (3 tests) 48ms
 ✓  frontend-productos  src/app/productos/productos.component.spec.ts (7 tests) 253ms

 Test Files  2 passed (2)
      Tests  10 passed (10)
   Duration  3.12s
```

Casos cubiertos:
- Inicialización del servicio `ProductoService` y construcción correcta de `HttpParams`.
- Comportamiento del proxy ante `/api/productos/pagina` y `/api/productos/pagina/buscar`.
- Renderizado inicial y asignación de propiedades de `Pagina<Producto>`.
- Selector de tamaño de página reiniciando el cursor a `page = 0`.
- Búsqueda por término y restablecimiento con `limpiarBusqueda()`.
- Guardas en los métodos `anterior()` y `siguiente()` para no desbordar índices.
- Manejo elegante de fallas de conexión o errores HTTP en la interfaz.

---

## 9. Cuestionario de Análisis Técnico (10 Preguntas Oficiales del Laboratorio)

### 1. ¿Qué ventaja ofrece ejecutar MySQL como contenedor Docker dentro de Codespaces?
Ofrece **aislamiento completo**, **portabilidad** y **reproducibilidad**. Evita instalar dependencias pesadas en el sistema operativo anfitrión, elimina discrepancias entre entornos de desarrollo ("en mi máquina sí funciona") y permite crear o destruir la base de datos de manera determinista mediante scripts de infraestructura como código (`docker-compose.yml` e `init.sql`).

### 2. ¿Qué diferencia existe entre `List<Producto>` y `Page<Producto>`?
`List<Producto>` retorna únicamente la colección plana de entidades recuperadas de memoria. En contraste, `Page<Producto>` es una abstracción enriquecida de Spring Data que encapsula tanto el subconjunto de datos (`content`) como los **metadatos de paginación** calculados en base de datos: cantidad total de registros (`totalElements`), número total de páginas (`totalPages`), índice actual (`number`), tamaño de página (`size`) y banderas booleanas de frontera (`first`, `last`).

### 3. ¿Por qué Spring utiliza `page=0` para representar la primera página?
Por convención idiomática de indexación basada en cero (*zero-based indexing*) en la plataforma Java. El framework alinea de manera natural el cálculo matemático de desplazamiento en base de datos:
$$\text{OFFSET} = \text{page} \times \text{size}$$
Cuando $\text{page} = 0$, $\text{OFFSET} = 0$, simplificando la aritmética SQL sin requerir conversiones intermedias en el runtime.

### 4. ¿Qué función cumple `Pageable`?
`Pageable` es la interfaz de Spring Data que define el contrato abstracto de la petición de paginación: número de página solicitada, cantidad máxima de filas a retornar y especificación de ordenamiento (`Sort`). Permite desacoplar los parámetros de la solicitud HTTP de las consultas SQL/JPQL que genera Hibernate.

### 5. ¿Por qué Angular utiliza `/api/productos` en lugar de una URL pública del puerto 8080?
Porque en entornos en la nube como GitHub Codespaces, `http://localhost:8080` en el navegador del usuario apuntaría a la máquina local del cliente y no al contenedor cloud donde corre Spring Boot. Al invocar `/api/productos`, Angular utiliza una ruta relativa al mismo origen (*same-origin*), evitando problemas de URLs públicas variables, puertos bloqueados y complejidades de políticas CORS.

### 6. ¿Qué función cumple `proxy.conf.json` en este laboratorio?
Actúa como un **proxy inverso de desarrollo** dentro del servidor local de Angular CLI (`@angular/build:dev-server`). Intercepta cualquier llamada HTTP dirigida a `/api/**` y la retransmite internamente a `http://127.0.0.1:8080`, permitiendo que el navegador se comunique con el backend sin restricciones de Cross-Origin Resource Sharing (CORS).

### 7. ¿Qué información necesita Angular además del arreglo `content`?
Requiere imperativamente los metadatos de control:
- `totalElements`: Para informar al usuario el universo total de productos disponibles.
- `totalPages`: Para calcular los límites de navegación y mostrar "Página X de Y".
- `number`: Para sincronizar el índice de la página activa.
- `first` y `last`: Para deshabilitar los botones de navegación « Anterior y Siguiente » en los extremos del catálogo.

### 8. ¿Qué ventaja tiene paginar en el backend en lugar de hacerlo únicamente en Angular?
Paginar en el frontend obliga a transferir miles de registros por la red, saturando el ancho de banda y consumiendo gigabytes de memoria en dispositivos móviles. Paginar en el backend mediante `LIMIT` y `OFFSET` garantiza que **únicamente viajen los registros visualizables** (ej. 5 o 10 filas), optimizando el tiempo de respuesta, el uso de CPU y la escalabilidad global del sistema.

### 9. ¿Qué ocurriría si se elimina el contenedor `mysql-lab11` pero se conserva el volumen `mysql_lab11_data`?
Los datos **no se pierden**. Los volúmenes administrados por Docker residen en el sistema de archivos del host de forma desacoplada del ciclo de vida del contenedor. Al crear un nuevo contenedor montando el mismo volumen (`-v mysql_lab11_data:/var/lib/mysql`), MySQL montará inmediatamente las tablas y registros intactos.

### 10. ¿Cómo se combinan `page`, `size` y `sort` en una misma petición?
Se transmiten como parámetros de consulta (*Query Parameters*) en la URL HTTP:
```http
GET /api/productos/pagina?page=1&size=5&sort=precio,desc
```
Spring Boot interpreta automáticamente estos parámetros mediante el argumento `@PageableDefault Pageable pageable`, generando la consulta SQL equivalente:
```sql
SELECT * FROM producto ORDER BY precio DESC LIMIT 5 OFFSET 5;
```

---

## 10. Perfil del Autor y Contacto

**Orlando Dorival**  
Estudiante de Ingeniería de Sistemas de Información | Universidad Privada del Norte (UPN) & Universidad Nacional de Ingeniería (UNI)  
- **GitHub:** [@Orlandho](https://github.com/Orlandho)  
- **Portafolio:** [orlandodorival.dev](https://orlandodorival.dev/)  
- **Especialidad:** Soluciones Web Distribuidas, Arquitecturas Cloud-Native, Spring Boot & Angular Ecosystems.
