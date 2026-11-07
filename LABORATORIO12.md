# Laboratorio 12 — JPQL, transacciones y SOA (UPN, 2026-II)

Continuacion del Laboratorio 11A/B. Se mantiene la estructura `com.ejemplo.laboratorio11` y el frontend Angular paginado. Se agregaron las funciones enseñadas en clase, sin desplegar microservicios ni usar infraestructura cloud ajena al laboratorio.

## Desarrollo realizado

- `GET /api/productos/pagina/avanzada`: consulta JPQL con `@Query`, cuatro `@Param`, filtros `nombre`, `precioMin`, `precioMax`, `stockMin` y `Pageable`.
- `POST /api/ventas`: descuenta stock y registra una venta en una sola transaccion con `@Transactional(rollbackFor = Exception.class)`.
- `simularError=true`: provoca `RuntimeException` despues de ambos `saveAndFlush()`, para verificar el rollback sin dejar registros.
- `GET /api/ventas`: consulta todas las ventas registradas.
- **Reto adicional:** `GET /api/ventas/buscar?inicio=2020-01-01T00:00:00&fin=2099-01-01T00:00:00&nombre=laptop`, con JPQL `JOIN` y named parameters.
- Swagger UI (semana 11B): `/swagger-ui/index.html`. La configuracion de servidor usa ruta relativa `/` para GitHub Codespaces.

**Compatibilidad:** el proyecto usa Spring Boot **3.4.13** y Java 17. El laboratorio 11B muestra springdoc **2.10.0** para Spring Boot 3.x, pero la matriz oficial de springdoc indica **2.7.x–2.8.x** para Boot 3.4.x; se usa **2.8.16** por compatibilidad, sin incorporar una tecnologia nueva.

## Iniciar en Codespaces

Desde la raiz del repositorio:

```bash
docker compose up -d
cd backend-productos
./mvnw clean test
./mvnw spring-boot:run
```

En una segunda terminal, comprobar:

```bash
curl "http://localhost:8080/api/productos/pagina/avanzada?nombre=a&precioMin=50&precioMax=3000&stockMin=5&page=0&size=5&sort=precio,asc"
curl "http://localhost:8080/api/ventas"
```

En Swagger, abrir el puerto 8080 del Codespace y anadir `/swagger-ui/index.html`. El frontend Angular de la semana 11 se inicia desde `frontend-productos` con `npm ci` y `npx ng serve --host 0.0.0.0 --proxy-config proxy.conf.json` (puerto 4200). No es necesario modificar el frontend para demostrar dos consumidores: Swagger UI y curl.

## Prueba COMMIT / ROLLBACK

Se utiliza un producto existente con stock 10 (como la Laptop Lenovo de `docker/init.sql`); comprobar su ID real antes de probar.

1. Ejecutar `GET /api/ventas` y `GET /api/productos/pagina`; anotar stock y cantidad de ventas.
2. Enviar `POST /api/ventas` con `{"productoId":1,"cantidad":2,"simularError":false}`. Esperado: **201**, stock 8, una nueva venta.
3. Enviar `POST /api/ventas` con `{"productoId":1,"cantidad":3,"simularError":true}`. Esperado: **500**, stock sigue 8, numero de ventas no aumenta.
4. Ejecutar `GET` de productos y ventas de nuevo; verificar invariantes. La prueba automatizada de rollback comprueba el estado desde los repositorios usando H2.

```bash
curl -i -X POST http://localhost:8080/api/ventas -H "Content-Type: application/json" -d '{"productoId":1,"cantidad":2,"simularError":false}'
curl -i -X POST http://localhost:8080/api/ventas -H "Content-Type: application/json" -d '{"productoId":1,"cantidad":3,"simularError":true}'
```

**Verificacion MySQL:**

```sql
USE bd_laboratorio11;
SELECT id, nombre, precio, stock FROM producto;
SELECT id, producto_id, cantidad, precio_unitario, total, fecha FROM ventas ORDER BY id DESC;
```

El script `docker/init.sql` se ejecuta solo durante la primera inicializacion del volumen de MySQL. Con un volumen previo, comprobar que existan productos; no borrar datos existentes para generar evidencias.

## Diagrama del servicio (SOA)

```text
Angular (4200)  ----HTTP / JSON----+
                                   |
Swagger UI ------HTTP / JSON-------+--> Controller REST (8080)
                                   |         |
curl ------------HTTP / JSON-------+      Service
                                             | @Transactional
                                          Repository
                                             | JPA / JPQL
                                          MySQL (Docker)
```

## Evidencias solicitadas por el docente

| Evidencia | Lugar / procedimiento |
|---|---|
| Codigo JPQL con named parameters | `ProductoRepository.java` (y reto adicional en `VentaRepository.java`) |
| Request/JSON de busqueda avanzada | `GET /api/productos/pagina/avanzada` en Swagger y curl |
| Stock y ventas antes y despues del COMMIT | Consultas SQL indicadas arriba |
| Error y rollback sin cambios persistidos | `simularError=true`, repetir consultas SQL y revisar `Laboratorio12IntegrationTest` |
| Diagrama de consumidor/servicio/BD | Seccion SOA anterior |
| Repositorio ejecutable | Rama del laboratorio 12 y CI de GitHub Actions |

La evidencia visual (capturas de Swagger y MySQL) debe obtenerse de una ejecucion real; este repositorio no incluye capturas inventadas.

## Preguntas de analisis (pagina 18)

1. SQL consulta tablas/columnas; JPQL consulta clases de entidad y atributos Java.
2. `:precioMin` separa el valor del texto JPQL: mejora legibilidad, reutilizacion y reduce riesgos de inyeccion.
3. Actualizar stock y registrar venta debe ser atomico para impedir ventas sin descuento o descuentos sin ventas.
4. COMMIT confirma los cambios; ROLLBACK revierte toda la transaccion cuando se lanza la excepcion.
5. Sin `@Transactional`, una escritura confirmada podria permanecer aunque la segunda falle, causando inconsistencia.
6. Angular conoce rutas, metodos HTTP y JSON; no necesita acceder al codigo del repositorio o a MySQL.
7. El contrato REST incluye ruta, metodo, parametros, body JSON, codigos HTTP y estructura de respuesta.
8. Sigue siendo un backend Spring Boot unico con varios clientes; no son microservicios desplegados de manera independiente.

## Alcance

El campo `simularError` existe solo para la demostracion academica. No debe mantenerse habilitado en una aplicacion productiva. El diseño simplificado usa Double para importes, igual que la entidad Producto y la guia de la semana 12.
