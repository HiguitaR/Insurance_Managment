# 📋 Proyecto Integrador Módulo 1: "Sistema de Gestión de Seguros — JavaInsure Core"

> **Nivel:** Junior | **Tecnología:** Java 21+ | **Modalidad:** Consola

---

## 🗺️ Índice

1. [Descripción del escenario](#1-descripción-del-escenario)
2. [Objetivos de aprendizaje](#2-objetivos-de-aprendizaje)
3. [Estructura del proyecto](#3-estructura-del-proyecto)
4. [Modelo de dominio](#4-modelo-de-dominio)
5. [Formato del archivo CSV](#5-formato-del-archivo-csv)
6. [Requerimientos funcionales](#6-requerimientos-funcionales)
7. [Motor de cotización — Reglas de negocio](#7-motor-de-cotización--reglas-de-negocio)
8. [Requerimientos técnicos](#8-requerimientos-técnicos)
9. [Manejo de errores y logging](#9-manejo-de-errores-y-logging)
10. [Pruebas unitarias](#10-pruebas-unitarias)
11. [Rúbrica de evaluación](#11-rúbrica-de-evaluación)
12. [Entregables](#12-entregables)
13. [Preguntas frecuentes](#13-preguntas-frecuentes)

---

## 1. Descripción del escenario

La aseguradora **JavaInsure** está modernizando su núcleo tecnológico. Necesitan reemplazar su sistema legado por una **aplicación de consola robusta, mantenible y moderna**.

El sistema debe:

- Administrar una cartera de pólizas de seguros de tres tipos: **Vida, Automóvil y Hogar**.
- Cargar datos iniciales desde un archivo CSV.
- Calcular automáticamente el costo anual de cada póliza según reglas de negocio definidas.
- Generar reportes financieros para la gerencia.

Dado que este es un sistema financiero, la **calidad del código (Testing)** y la **trazabilidad (Logging)** son requisitos **no negociables**, no características opcionales.

---

## 2. Objetivos de aprendizaje

Este proyecto valida las siguientes competencias:

| # | Competencia               | Tecnología                                           |
|---|---------------------------|------------------------------------------------------|
| 1 | Java Moderno              | Records, Sealed Classes, Pattern Matching (Java 21+) |
| 2 | Manipulación de datos     | Streams API, Java NIO (`Files.lines()`)              |
| 3 | Calidad de software       | JUnit 5, Mockito                                     |
| 4 | Operaciones empresariales | Log4j 2, Excepciones personalizadas                  |

---

## 3. Estructura del proyecto

El proyecto debe organizarse con **Maven** usando la siguiente estructura de paquetes y archivos:

```
javainsure/
├── build.gradle          ← Configuración de Gradle (en lugar de pom.xml)
├── settings.gradle       ← Nombre del proyecto
├── README.md             ← Instrucciones de ejecución
└── src/
    ├── main/
    │   ├── java/com/javainsure/
    │   │   ├── Main.java                     ← Punto de entrada, menú de consola
    │   │   ├── model/
    │   │   │   ├── Insurance.java            ← sealed interface
    │   │   │   ├── LifeInsurance.java        ← record
    │   │   │   ├── CarInsurance.java         ← record
    │   │   │   ├── HomeInsurance.java        ← record
    │   │   │   └── Client.java              ← record
    │   │   ├── service/
    │   │   │   ├── InsuranceQuoter.java      ← Motor de cotización (switch + pattern matching)
    │   │   │   ├── PolicyManager.java        ← Gestión CRUD en memoria
    │   │   │   └── ReportService.java        ← Generación de reportes (Streams)
    │   │   ├── io/
    │   │   │   └── FileHandler.java          ← Lectura del CSV con NIO
    │   │   └── exception/
    │   │       ├── InvalidPolicyDataException.java   ← Checked
    │   │       └── BusinessRuleException.java        ← Unchecked
    │   └── resources/
    │       └── log4j2.xml                    ← Configuración de logging
    └── test/
        └── java/com/javainsure/
            ├── InsuranceQuoterTest.java
            └── FileHandlerTest.java
```

> 💡 **¿Por qué Gradle?** Gradle gestiona automáticamente las dependencias. Solo necesitas declarar las
  librerías en build.gradle y Gradle las descarga solas. Para compilar usa gradle build y para ejecutar
  los tests gradle test.
```
  // build.gradle
plugins {
  id 'java'
}

group = 'com.javainsure'
version = '1.0'
sourceCompatibility = '21'

repositories {
  mavenCentral()
}

dependencies {
  // Log4j2
  implementation 'org.apache.logging.log4j:log4j-core:2.23.1'

  // JUnit 5
  testImplementation 'org.junit.jupiter:junit-jupiter:5.10.2'

  // Mockito
  testImplementation 'org.mockito:mockito-core:5.11.0'
}

test {
  useJUnitPlatform() // Necesario para que Gradle ejecute tests de JUnit 5
}
```
```
//settings.gradle
rootProject.name = 'javainsure'
```

---

## 4. Modelo de dominio

Esta sección define **exactamente** cómo se representan los datos en el código.

### 4.1 La interfaz sellada `Insurance`

Una interfaz sellada (`sealed`) limita qué clases pueden implementarla. Esto garantiza que solo existan los tres tipos de seguro definidos:

```java
// Insurance.java
public sealed interface Insurance permits LifeInsurance, CarInsurance, HomeInsurance {
    String policyId();   // ID único de la póliza (ej: "POL-001")
    Client client();     // Cliente asociado
    double amount();     // Monto asegurado en USD
}
```

### 4.2 El record `Client`

Un `record` en Java es una clase inmutable y concisa para transportar datos:

```java
// Client.java
public record Client(String clientId, String name, int age) {}
// Ejemplo de uso: new Client("C001", "Ana García", 45)
```

| Campo      | Tipo     | Descripción                                    |
|------------|----------|------------------------------------------------|
| `clientId` | `String` | Identificador único del cliente (ej: `"C001"`) |
| `name`     | `String` | Nombre completo                                |
| `age`      | `int`    | Edad en años (debe ser > 0)                    |

### 4.3 Los records de cada tipo de seguro

```java
// LifeInsurance.java
public record LifeInsurance(String policyId, Client client, double amount)
    implements Insurance {}

// CarInsurance.java
public record CarInsurance(String policyId, Client client, double amount, int carYear)
    implements Insurance {}
// carYear: año de fabricación del automóvil (ej: 2018)

// HomeInsurance.java
public record HomeInsurance(String policyId, Client client, double amount, boolean isHighRiskZone)
    implements Insurance {}
// isHighRiskZone: true si el inmueble está en zona de riesgo (inundaciones, sismos)
```

#### Tabla resumen de campos por tipo

| Tipo            | Campo extra      | Tipo      | Descripción                  |
|-----------------|------------------|-----------|------------------------------|
| `LifeInsurance` | _(ninguno)_      | —         | Solo usa la edad del cliente |
| `CarInsurance`  | `carYear`        | `int`     | Año de fabricación del auto  |
| `HomeInsurance` | `isHighRiskZone` | `boolean` | ¿Está en zona de riesgo?     |

---

## 5. Formato del archivo CSV

### 5.1 Especificación

El archivo `policies.csv` debe ubicarse en la **raíz del proyecto** (junto al `pom.xml`).

**Formato general:**
```
TYPE,POLICY_ID,CLIENT_ID,CLIENT_NAME,CLIENT_AGE,AMOUNT,EXTRA_DATA
```

**Formato por tipo de seguro:**

| Tipo   | Columnas                                                            | Descripción de EXTRA_DATA |
|--------|---------------------------------------------------------------------|---------------------------|
| `LIFE` | `LIFE,policyId,clientId,clientName,clientAge,amount`                | Sin campo extra           |
| `CAR`  | `CAR,policyId,clientId,clientName,clientAge,amount,carYear`         | Año del auto (entero)     |
| `HOME` | `HOME,policyId,clientId,clientName,clientAge,amount,isHighRiskZone` | `true` o `false`          |

### 5.2 Archivo de ejemplo completo

Copia este contenido exactamente en tu archivo `policies.csv`:

```csv
LIFE,POL-001,C001,Ana García,45,100000
LIFE,POL-002,C002,Carlos Ruiz,65,200000
CAR,POL-003,C003,María López,38,30000,2018
CAR,POL-004,C004,Pedro Soto,52,15000,2012
HOME,POL-005,C005,Laura Mora,29,250000,false
HOME,POL-006,C006,Jorge Castro,41,180000,true
LIFE,POL-007,C007,Sofía Díaz,72,150000
CAR,POL-008,C008,Andrés Vega,33,25000,2020
HOME,POL-009,C009,Camila Ríos,55,320000,true
CAR,POL-010,C010,Felipe Núñez,47,40000,2014
```

> ⚠️ **Importante:** El archivo **no debe tener una línea de encabezado** (no escribas `TYPE,POLICY_ID,...` como primera línea). El parser leerá todas las líneas como datos.

### 5.3 Reglas de validación al leer el CSV

Al parsear cada línea, el sistema debe verificar:

- El `TYPE` debe ser exactamente `LIFE`, `CAR` o `HOME`.
- El `amount` debe ser un número decimal mayor que cero.
- El `clientAge` debe ser un entero mayor que cero.
- Para `CAR`: el `carYear` debe ser un entero de 4 dígitos (ej: `2018`).
- Para `HOME`: el `isHighRiskZone` debe ser exactamente `true` o `false`.
- Si alguna línea tiene un formato incorrecto, se debe lanzar `InvalidPolicyDataException` con un mensaje que indique el número de línea y el error.

---

## 6. Requerimientos funcionales

### 6.1 Arranque del sistema — Carga inicial de datos

Al iniciar la aplicación:

1. El sistema busca el archivo `policies.csv` en la raíz del proyecto.
2. **Si el archivo existe:** lo lee línea por línea usando `Files.lines()` y carga las pólizas en memoria.
3. **Si el archivo NO existe:** el sistema inicia con una lista vacía y muestra el mensaje: `"[INFO] No se encontró policies.csv. Iniciando con cartera vacía."` El sistema **no debe terminar ni lanzar excepción fatal** en este caso.
4. **Si una línea tiene formato incorrecto:** se registra el error con `logger.error()`, se omite esa línea, y el sistema continúa cargando las demás.

### 6.2 Menú de consola

El sistema presenta el siguiente menú en un bucle hasta que el usuario elija salir:

```
===== JavaInsure Core =====
1. Listar todas las pólizas
2. Agregar nueva póliza
3. Buscar póliza por ID de cliente
4. Ver reportes gerenciales
5. Salir
Seleccione una opción:
```

#### Opción 1 — Listar pólizas

Muestra todas las pólizas cargadas en memoria con su costo de prima calculado. Si no hay pólizas, muestra: `"No hay pólizas registradas."`.

Ejemplo de salida:

```
[POL-001] LIFE | Cliente: Ana García (C001) | Monto: $100,000 | Prima anual: $5,000.00
[POL-002] LIFE | Cliente: Carlos Ruiz (C002) | Monto: $200,000 | Prima anual: $14,000.00
[POL-003] CAR  | Cliente: María López (C003) | Monto: $30,000  | Prima anual: $3,000.00
```

#### Opción 2 — Agregar póliza

El sistema solicita los datos al usuario paso a paso:

```
Tipo de seguro (LIFE / CAR / HOME): LIFE
ID de póliza (ej: POL-011): POL-011
ID de cliente (ej: C011): C011
Nombre del cliente: Roberto Vargas
Edad del cliente: 50
Monto asegurado (USD): 80000
✅ Póliza POL-011 creada exitosamente.
```

> El ID de póliza lo ingresa el usuario. Puedes implementar autoincremento como mejora opcional.

**Validaciones obligatorias al agregar:**
- La edad no puede ser negativa ni cero → lanzar `BusinessRuleException`.
- El monto no puede ser negativo ni cero → lanzar `BusinessRuleException`.
- Si el ID de póliza ya existe → mostrar error y pedir otro.

#### Opción 3 — Buscar por ID de cliente

Solicita el `clientId` y muestra todas las pólizas asociadas a ese cliente. Un cliente puede tener múltiples pólizas. Si no se encuentra, muestra: `"No se encontraron pólizas para el cliente [ID]."`.

#### Opción 4 — Reportes gerenciales

Muestra tres reportes calculados con Streams:

```
===== Reportes Gerenciales =====

1. Prima promedio de todas las pólizas: $4,250.00

2. Pólizas por tipo:
   - LIFE: 4 pólizas
   - CAR:  3 pólizas
   - HOME: 3 pólizas

3. Top 3 pólizas más costosas:
   #1 [POL-009] HOME | Camila Ríos | Prima: $12,800.00
   #2 [POL-002] LIFE | Carlos Ruiz | Prima: $14,000.00
   #3 [POL-007] LIFE | Sofía Díaz  | Prima: $10,500.00
```

#### Opción 5 — Salir

Muestra `"Hasta luego."` y termina la aplicación.

---

## 7. Motor de cotización — Reglas de negocio

La clase `InsuranceQuoter` tiene un único método público:

```java
public double calculatePremium(Insurance insurance);
```

Este método usa un `switch` expression con **Pattern Matching** (obligatorio). No se permite usar cadenas `if-else` con `instanceof`.

### Reglas de cálculo

#### 🔵 Seguro de Vida (`LifeInsurance`)

| Concepto         | Cálculo                                                         |
|------------------|-----------------------------------------------------------------|
| Prima base       | 5% del monto asegurado                                          |
| Recargo por edad | Si `client.age() > 60`: sumar 20% adicional sobre la prima base |

**Ejemplo 1:** Ana García, 45 años, monto $100,000
- Prima base: $100,000 × 5% = **$5,000**
- Sin recargo (edad ≤ 60)
- **Prima final: $5,000**

**Ejemplo 2:** Carlos Ruiz, 65 años, monto $200,000
- Prima base: $200,000 × 5% = $10,000
- Recargo: $10,000 × 20% = $2,000
- **Prima final: $12,000**

---

#### 🟠 Seguro de Automóvil (`CarInsurance`)

| Concepto               | Cálculo                                  |
|------------------------|------------------------------------------|
| Prima base             | 10% del valor del auto                   |
| Recargo por antigüedad | Si `carYear < 2015`: sumar $50 USD fijos |

**Ejemplo 1:** María López, auto 2018, valor $30,000
- Prima base: $30,000 × 10% = **$3,000**
- Sin recargo (año ≥ 2015)
- **Prima final: $3,000**

**Ejemplo 2:** Pedro Soto, auto 2012, valor $15,000
- Prima base: $15,000 × 10% = $1,500
- Recargo antigüedad: $50
- **Prima final: $1,550**

---

#### 🟢 Seguro de Hogar (`HomeInsurance`)

| Concepto               | Cálculo                                                  |
|------------------------|----------------------------------------------------------|
| Prima base             | 2% del valor del inmueble                                |
| Recargo zona de riesgo | Si `isHighRiskZone == true`: duplicar la prima base (×2) |

**Ejemplo 1:** Laura Mora, valor $250,000, zona normal
- Prima base: $250,000 × 2% = **$5,000**
- Sin recargo
- **Prima final: $5,000**

**Ejemplo 2:** Jorge Castro, valor $180,000, zona de riesgo
- Prima base: $180,000 × 2% = $3,600
- Recargo zona riesgo: $3,600 × 2 = **$7,200**
- **Prima final: $7,200**

---

### Esqueleto del método (guía de implementación)

```java
public double calculatePremium(Insurance insurance) {
    return switch (insurance) {
        case LifeInsurance life -> {
            double base = life.amount() * 0.05;
            yield life.client().age() > 60 ? base + (base * 0.20) : base;
        }
        case CarInsurance car -> {
            double base = car.amount() * 0.10;
            yield car.carYear() < 2015 ? base + 50 : base;
        }
        case HomeInsurance home -> {
            double base = home.amount() * 0.02;
            yield home.isHighRiskZone() ? base * 2 : base;
        }
    };
}
```

> 💡 El compilador de Java verificará que el `switch` sea exhaustivo (que cubra los tres tipos). Si agregas un nuevo tipo de seguro al `sealed interface` y no lo agregas al `switch`, el código no compilará. ¡Eso es una ventaja del diseño sellado!

---

## 8. Requerimientos técnicos

### 8.1 Modelo de dominio

- ✅ Usar `sealed interface Insurance permits LifeInsurance, CarInsurance, HomeInsurance`
- ✅ Todas las implementaciones y `Client` deben ser `records`
- ✅ El cálculo de primas **DEBE** realizarse en `InsuranceQuoter` con `switch` + Pattern Matching
- ❌ **Prohibido** usar cadenas `if-else instanceof`

### 8.2 Colecciones

- ✅ Usar `ArrayList` (que implementa `SequencedCollection`) para almacenar pólizas y mantener el orden de inserción
- ✅ Todas las búsquedas y reportes deben hacerse con **Streams API**
- ❌ **Prohibido** usar bucles `for` o `while` tradicionales en los métodos de reporte y búsqueda

### 8.3 Lectura de archivos

- ✅ Usar `java.nio.file.Files.lines()` para leer el CSV línea por línea
- ✅ El parseo de cada línea debe estar en un método separado y testeable

---

## 9. Manejo de errores y logging

### 9.1 Excepciones personalizadas

```java
// InvalidPolicyDataException.java — CHECKED (extends Exception)
// Úsala cuando: una línea del CSV tiene formato incorrecto o datos inválidos
public class InvalidPolicyDataException extends Exception {
    public InvalidPolicyDataException(String message) { super(message); }
}

// BusinessRuleException.java — UNCHECKED (extends RuntimeException)
// Úsala cuando: el usuario ingresa datos que violan reglas de negocio (edad negativa, monto cero)
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) { super(message); }
}
```

### 9.2 Logging con Log4j 2

**Regla de oro:** `System.out` es solo para la interacción visible del menú. Todo lo demás usa el logger.

| Situación                       | Qué usar                       | Ejemplo                                                               |
|---------------------------------|--------------------------------|-----------------------------------------------------------------------|
| Póliza creada exitosamente      | `logger.info()`                | `logger.info("Póliza {} creada para cliente {}", policyId, clientId)` |
| Error al leer una línea del CSV | `logger.error()` con excepción | `logger.error("Error en línea {}: {}", lineNum, e.getMessage(), e)`   |
| Archivo CSV no encontrado       | `logger.warn()`                | `logger.warn("policies.csv no encontrado. Iniciando vacío.")`         |
| Mostrar el menú al usuario      | `System.out.println()`         | Solo aquí                                                             |

**Configuración mínima de `log4j2.xml`:**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
  <Appenders>
    <Console name="Console" target="SYSTEM_ERR">
      <PatternLayout pattern="%d{HH:mm:ss} [%-5level] %logger{1} - %msg%n"/>
    </Console>
    <File name="File" fileName="javainsure.log">
      <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} [%-5level] %logger{36} - %msg%n"/>
    </File>
  </Appenders>
  <Loggers>
    <Root level="info">
      <AppenderRef ref="Console"/>
      <AppenderRef ref="File"/>
    </Root>
  </Loggers>
</Configuration>
```

> 💡 Con esta configuración, los logs de INFO y ERROR se verán en consola (en `SYSTEM_ERR`, separado del menú) **y** se guardarán en el archivo `javainsure.log`.

---

## 10. Pruebas unitarias

Las pruebas valen **30 puntos** y son la parte más importante del proyecto. A continuación se detalla exactamente qué debe probarse.

### 10.1 `InsuranceQuoterTest.java`

Prueba el cálculo de primas para todos los casos posibles.

#### Casos de prueba mínimos requeridos

| Test                                        | Tipo | Condición               | Prima esperada                |
|---------------------------------------------|------|-------------------------|-------------------------------|
| `lifeInsurance_underAge_returnsBaseOnly`    | LIFE | Edad 45, monto $100,000 | $5,000.00                     |
| `lifeInsurance_overAge_appliesSurcharge`    | LIFE | Edad 65, monto $200,000 | $12,000.00                    |
| `carInsurance_recentCar_returnsBaseOnly`    | CAR  | Año 2018, monto $30,000 | $3,000.00                     |
| `carInsurance_oldCar_appliesFixedSurcharge` | CAR  | Año 2012, monto $15,000 | $1,550.00                     |
| `homeInsurance_normalZone_returnsBaseOnly`  | HOME | Normal, monto $250,000  | $5,000.00                     |
| `homeInsurance_riskZone_doublesBase`        | HOME | Riesgo, monto $180,000  | $7,200.00                     |
| `lifeInsurance_exactlyAge60_noSurcharge`    | LIFE | Edad exactamente 60     | Sin recargo                   |
| `businessRuleException_negativeAge`         | LIFE | Edad -1                 | Lanza `BusinessRuleException` |

**Estructura de un test de ejemplo:**

```java
@Test
void lifeInsurance_underAge_returnsBaseOnly() {
    // Arrange
    Client client = new Client("C001", "Ana García", 45);
    LifeInsurance policy = new LifeInsurance("POL-001", client, 100_000);
    InsuranceQuoter quoter = new InsuranceQuoter();

    // Act
    double premium = quoter.calculatePremium(policy);

    // Assert
    assertEquals(5_000.0, premium, 0.001);
}
```

### 10.2 `FileHandlerTest.java`

Prueba la lectura y parseo del CSV **sin depender de un archivo fijo en disco**.

**Estrategia recomendada: archivos temporales con `@TempDir`**

```java
@Test
void readPolicies_validFile_returnsCorrectList(@TempDir Path tempDir) throws Exception {
    // Arrange: crear un archivo temporal con contenido conocido
    Path csvFile = tempDir.resolve("policies.csv");
    Files.writeString(csvFile, "LIFE,POL-001,C001,Ana García,45,100000\n");

    FileHandler handler = new FileHandler(csvFile.toString());

    // Act
    List<Insurance> policies = handler.loadPolicies();

    // Assert
    assertEquals(1, policies.size());
    assertInstanceOf(LifeInsurance.class, policies.getFirst());
}

@Test
void readPolicies_invalidLine_throwsInvalidPolicyDataException(@TempDir Path tempDir) {
    Path csvFile = tempDir.resolve("policies.csv");
    Files.writeString(csvFile, "INVALID,datos,incorrectos\n");

    FileHandler handler = new FileHandler(csvFile.toString());

    assertThrows(InvalidPolicyDataException.class, handler::loadPolicies);
}
```

#### Casos mínimos requeridos para `FileHandlerTest`

- ✅ Archivo válido con una línea LIFE → devuelve lista con 1 póliza
- ✅ Archivo válido con múltiples tipos → devuelve lista con todos los tipos correctos
- ✅ Línea con tipo inválido → lanza `InvalidPolicyDataException`
- ✅ Línea con edad negativa → lanza excepción
- ✅ Archivo vacío → devuelve lista vacía (sin excepción)

---

## 11. Rúbrica de evaluación

**Total: 100 puntos**

| Categoría                     | Criterio detallado                                                                                                                                                      | Puntos |
|-------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------|
| **Modelado Moderno**          | Uso correcto de `sealed interface`, `permits` y `records`. Código conciso e inmutable por defecto.                                                                      | 15     |
| **Lógica & Pattern Matching** | `switch` expression con pattern matching en `InsuranceQuoter`. Resultados matemáticamente correctos para todos los casos.                                               | 20     |
| **Streams & NIO**             | Lectura del CSV con `Files.lines()`. Reportes implementados con `filter`, `map`, `collect`, `groupingBy`, `sorted`. Sin bucles `for` en reportes.                       | 20     |
| **Calidad & Logging**         | Log4j 2 con niveles correctos (info/warn/error). Excepciones custom usadas apropiadamente. `System.out` solo en el menú.                                                | 15     |
| **Testing (JUnit 5)**         | Mínimo 8 tests en `InsuranceQuoterTest` + 5 tests en `FileHandlerTest`. Uso de `@TempDir`. Aserciones correctas con `assertEquals`, `assertThrows`, `assertInstanceOf`. | 30     |

### Niveles de desempeño

| Nivel              | Rango  | Descripción                                                                                      |
|--------------------|--------|--------------------------------------------------------------------------------------------------|
| 🏆 **Senior**      | 90–100 | Tests exhaustivos con Mockito, Streams complejos, código limpio y sin deudas técnicas.           |
| 🥈 **Semi-Senior** | 75–89  | Funciona correctamente, usa features modernas, pero faltan algunos tests o el logging es básico. |
| 🥉 **Junior**      | 60–74  | Funciona pero usa `for` en reportes, o no implementa tests, o usa `System.out` para errores.     |
| ❌ **Insuficiente** | < 60   | No compila, no lee el archivo, o no usa conceptos de Objetos.                                    |

---

## 12. Entregables

Al finalizar el proyecto, debes entregar:

- [ ] **Repositorio en GitHub** con el código fuente completo.
- [ ] **`README.md`** con: descripción del proyecto, pre-requisitos (Java 21+, Maven), instrucciones paso a paso para compilar y ejecutar.
- [ ] **`policies.csv`** de ejemplo en la raíz del proyecto (con al menos 6 pólizas de diferentes tipos).
- [ ] **Captura de pantalla** de los tests ejecutándose en verde (sin fallos).
- [ ] **`javainsure.log`** generado por una ejecución real del sistema.

---

## 13. Preguntas frecuentes

**¿Puedo agregar más funcionalidades al menú (como eliminar pólizas)?**
Sí, pero asegúrate primero de que los requerimientos básicos estén completos. Las funcionalidades extra no suman puntos si los requerimientos obligatorios fallan.

**¿El ID de póliza lo genera el sistema o lo ingresa el usuario?**
Lo ingresa el usuario al agregar una póliza. Asegúrate de validar que no se repita. Implementar autoincremento es una mejora opcional que puedes mencionar en tu README.

**¿Un cliente puede tener más de una póliza?**
Sí. La búsqueda por `clientId` debe devolver **todas** las pólizas asociadas a ese cliente.

**¿Qué pasa si el usuario ingresa letras donde se espera un número?**
Debes capturar la excepción `NumberFormatException` y mostrar un mensaje amigable pidiéndole que intente de nuevo. No debe romperse el programa.

**¿Cuántos tests mínimos necesito para aprobar?**
Al menos 8 tests en `InsuranceQuoterTest` y 5 en `FileHandlerTest`, cubriendo casos de éxito y casos de error (excepciones). Más tests = más seguridad de llegar al nivel Senior.

**¿Debo persistir los cambios al CSV cuando agrego una póliza desde el menú?**
No. El sistema trabaja en memoria. Al reiniciar, vuelve a cargar desde el CSV original. Persistir cambios es una mejora opcional para nivel Senior.

---