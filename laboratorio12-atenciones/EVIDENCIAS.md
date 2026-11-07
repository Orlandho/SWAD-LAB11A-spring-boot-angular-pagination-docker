# Evidencias — Laboratorio 12 (guía para el estudiante)

## I. Datos experimentales o registros de ejecución

| Evidencia | Resultado esperado | Observación |
| --- | --- | --- |
| \`java -version\` y \`./mvnw -v\` | JDK 17 o superior; Maven Wrapper responde | Adjuntar salida real |
| \`./mvnw clean test\` | 3 pruebas correctas | Adjuntar salida real del entorno |
| POST \`/api/atenciones\`, \`simularError=false\` | HTTP 201; 1 atención y 1 historial | Captura de API y listados |
| POST \`/api/atenciones\`, \`simularError=true\` | HTTP 500; conteos antes/después iguales | Captura de API y listados |
| GET \`/api/atenciones/buscar\` | Página de atenciones filtrada con JPQL | Captura de JSON y parámetros |

**Estado de verificación:** preparar y ejecutar estos pasos en Codespaces o VS Code.
No se atribuyen resultados ni capturas a una ejecución inexistente.

## II. Análisis de resultados

**1. ¿Qué dato no debería guardarse si ocurre rollback?**

Ni la atención nueva ni su historial clínico. Ambos INSERT deben revertirse
aunque se haya realizado \`saveAndFlush\`. El paciente registrado antes de la
transacción debe conservarse.

**2. ¿Por qué @Transactional se ubica en servicio?**

Porque el Service coordina una operación de negocio completa que utiliza
dos repositorios. El Controller solo atiende HTTP; si abriera la transacción,
mezclaría responsabilidades y dificultaría la reutilización del negocio.

**3. ¿Qué ventaja tienen los named parameters?**

Permiten expresar filtros claros usando \`:nombre\`, \`:hemoglobinaMin\` y
\`:hemoglobinaMax\` enlazados con \`@Param\`, sin concatenar valores de usuario
dentro del texto JPQL.

## III. Conclusiones

1. La anotación \`@Transactional(rollbackFor = Exception.class)\` permite tratar
   la atención y su historial como una única operación atómica.
2. La prueba de excepción controlada demuestra qué se debe verificar al
   comprobar que una transacción revierte cambios en más de una tabla.
3. JPQL con parámetros nombrados y \`Pageable\` facilita consultar atenciones
   aplicando filtros legibles sin depender del SQL concreto de H2.
