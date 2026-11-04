

Proyecto de Java (Maven) de la Universidad Técnica del Norte (UTN) que reúne varios ejercicios, cada uno en su propio paquete dentro de com.mycompany.tr1.bdd.

Contenido
Estructura del repositorio
Cómo ejecutarlo
Ejercicio 4: Sistema de Cafetería UTN
Estructura del repositorio
Tr1-BDD/
├── pom.xml
└── src/main/java/com/mycompany/tr1/bdd/
    ├── ejercicio4/        (Sistema de Cafetería UTN)
    └── ...                (demás ejercicios del proyecto)
Cómo ejecutarlo
Clona el repositorio:
bash
   git clone https://github.com/Mariaguerrer193/Tr1-BDD
Abre la carpeta en VS Code (con el Extension Pack for Java instalado) o en NetBeans.
Ejecuta la clase Main del ejercicio que quieras probar.





## EJERCICIO 1: Sistema de Biblioteca UTN

Sistema desarrollado en Java para controlar el inventario de una biblioteca, registrar préstamos de libros y revistas, calcular multas por retraso y generar reportes para el administrador, con persistencia en archivos de texto.

**Código:** `src/main/java/com/mycompany/tr1/bdd/ejercicio1/`  
**Clase principal:** `ejercicio1/Main.java`  
**Tecnología:** Java, Maven y persistencia en archivos planos `.txt` delimitados por `|`.

### 1. Enunciado resumido

La biblioteca de la UTN presta libros y revistas, y cada título tiene varios ejemplares. Los libros se prestan por 7 días con una multa de $0,25 por día de retraso; las revistas, por 3 días con una multa de $0,50 por día de retraso. Un usuario puede tener como máximo 3 préstamos activos simultáneos. No se presta si no hay ejemplares disponibles, no se devuelve dos veces el mismo préstamo y un usuario con multas pendientes no puede solicitar nuevos préstamos. El sistema proporciona cinco reportes para el administrador.

### 2. Reglas de negocio

| N.º | Regla |
|---|---|
| 1 | Los libros tienen un plazo de préstamo de 7 días y una multa de $0,25 por día de retraso. |
| 2 | Las revistas tienen un plazo de préstamo de 3 días y una multa de $0,50 por día de retraso. |
| 3 | Solo se presta si `ejemplaresDisponibles > 0`; al prestar se descuenta 1 unidad. |
| 4 | Un usuario puede tener como máximo 3 préstamos con estado `ACTIVO`. |
| 5 | Un usuario con multas pendientes no puede solicitar nuevos préstamos. |
| 6 | No se devuelve el mismo préstamo dos veces; al devolver se restaura 1 unidad al stock. |
| 7 | Si la devolución supera la fecha esperada, `multa = díasRetraso × tarifaDiaria` y se asigna al usuario. |
| 8 | El usuario puede liquidar todas sus multas acumuladas para desbloquear nuevos préstamos. |
| 9 | Toda la información se guarda en archivos planos (`materiales.txt`, `usuarios.txt`, `prestamos.txt`). |

### 3. Estructura de carpetas

```text
ejercicio1/
├── Main.java                  (menú interactivo y validaciones automáticas [OK]/[FALLO])
├── modelo/
│   ├── Material.java          (clase abstracta base)
│   ├── Libro.java             (hereda de Material)
│   ├── Revista.java           (hereda de Material)
│   ├── Usuario.java
│   ├── Prestamo.java
│   └── EstadoPrestamo.java    (enum: ACTIVO, DEVUELTO, CON_RETRASO)
└── servicio/
    ├── ArchivoServicio.java   (lectura y escritura de archivos .txt)
    └── BibliotecaServicio.java (lógica del negocio, validaciones y reportes)
```

![Estructura del proyecto en NetBeans](https://i.postimg.cc/DwnL33mP/image.png)

### 4. Diagrama de clases

![Diagrama de clases del Ejercicio 1](https://i.postimg.cc/yNTR8Q30/image.png)

`Libro` y `Revista` heredan de `Material`. `BibliotecaServicio` gestiona las listas en memoria y `ArchivoServicio` las guarda en los archivos planos de texto.

### 5. Operaciones de la clase `BibliotecaServicio`

| Método | Qué hace |
|---|---|
| `registrarLibro` / `registrarRevista` | Valida ID único, crea el material y actualiza `materiales.txt`. |
| `registrarUsuario(cedula, nombre)` | Valida cédula única, crea el usuario y actualiza `usuarios.txt`. |
| `prestarMaterial(idPrestamo, idMat, cedula, fecha)` | Verifica disponibilidad, límite de 3 préstamos y ausencia de multas antes de generar el préstamo. |
| `devolverMaterial(idPrestamo, fechaDevolucion)` | Valida que no esté devuelto, restaura el stock, calcula la multa y actualiza el saldo del usuario. |
| `pagarMultasUsuario(cedula)` | Pone a cero la deuda del usuario en memoria y en `usuarios.txt`. |

### 6. Reportes para el administrador

| Reporte | Método |
|---|---|
| Préstamos vencidos a la fecha | `reportePrestamosVencidos(hoy)` |
| Historial de préstamos de un usuario | `reporteHistorialUsuario(cedula)` |
| Materiales más prestados | `reporteMaterialesMasPrestados()` |
| Total de multas generadas en un período | `reporteTotalMultasPeriodo(desde, hasta)` |
| Auditoría de ejemplares disponibles | `reporteAuditoriaDisponibles()` |

### 7. Ejemplo de uso

```java
BibliotecaServicio servicio = new BibliotecaServicio();
LocalDate hoy = LocalDate.now();

// Registrar materiales y usuarios
servicio.registrarLibro("L001", "Estructuras de Datos", 2);
servicio.registrarRevista("R001", "IEEE Software", 1);
servicio.registrarUsuario("1004567890", "Maria Guerrero");

// Préstamo de libro
servicio.prestarMaterial("P001", "L001", "1004567890", hoy);

// Préstamo de revista con fecha anterior (5 días atrás)
servicio.prestarMaterial("P002", "R001", "1004567890", hoy.minusDays(5));

// Devolución con retraso de la revista (plazo 3 días, retraso 2 días)
servicio.devolverMaterial("P002", hoy);
```

**Cálculo del ejemplo:**

| Concepto | Valor |
|---|---|
| Plazo de la revista | 3 días |
| Días transcurridos | 5 días |
| Días de retraso | 2 días |
| Multa generada | 2 × $0,50 = **$1,00** |
| Stock final de `L001` | 1 disponible |
| Stock final de `R001` | 1 disponible (recupera su ejemplar) |

### 8. Ejemplo de salida de los reportes

```text
=== PRÉSTAMOS VENCIDOS A LA FECHA ===
Prestamo{id='P002', material='R001', usuario='1004567890', fechaEsperada=2026-10-31, estado=ACTIVO}

=== HISTORIAL DE PRÉSTAMOS (1004567890) ===
Prestamo{id='P001', material='L001', estado=ACTIVO}
Prestamo{id='P002', material='R001', estado=DEVUELTO, multa=$1.00}

=== MATERIALES MÁS PRESTADOS ===
1. Estructuras de Datos (L001) - 1 préstamo
2. IEEE Software (R001) - 1 préstamo

=== TOTAL DE MULTAS DEL PERÍODO ===
Total de multas cobradas: $1.00

=== AUDITORÍA DE EJEMPLARES DISPONIBLES ===
L001 | Totales: 2 | Prestados: 1 | Disponibles: 1 -> [AUDITORÍA OK]
R001 | Totales: 1 | Prestados: 0 | Disponibles: 1 -> [AUDITORÍA OK]
```

![Salida de los reportes en la consola de NetBeans](https://i.postimg.cc/DZLwzmM5/image.png)

### 9. Decisiones de diseño

- **Validación antes de persistir:** `prestarMaterial` valida stock, límite de préstamos y ausencia de multas antes de modificar colecciones o escribir en disco.
- **Cálculo polimórfico de multas:** cada subclase implementa `getDiasPrestamo()` y `getMultaPorDia()`, así `Prestamo` calcula la sanción sin acoplarse al tipo concreto de material.
- **Persistencia en archivos planos:** cada modificación se sincroniza de inmediato con archivos delimitados por barra vertical (`|`), evitando inconsistencias ante cierres inesperados.
- **Consola unificada con pruebas:** `Main` ofrece un menú interactivo con `Scanner` (opciones 1 a 7) y una opción 8 que autoejecuta la verificación completa sin entrada de usuario.

**Archivos generados** (`materiales.txt`, `usuarios.txt` y `prestamos.txt`):

![Archivos planos .txt generados](https://i.postimg.cc/g06YMnm2/image.png)

### 10. Pruebas y ejecución

**Para ejecutar en NetBeans:** abre el proyecto Maven `Tr1-BDD`, ubica `com.mycompany.tr1.bdd.ejercicio1.Main`, haz clic derecho sobre `Main.java` y selecciona **Run File** (o presiona **Shift + F6**).

`Main` ejecuta **15 verificaciones automáticas** y marca cada una con `[OK]` o `[FALLO]`:

| N.º | Verificación |
|---|---|
| 1 | Préstamo exitoso de libro (P001) |
| 2 | Disponibles de L001 disminuyen a 1 |
| 3 | Usuario María tiene 1 préstamo activo |
| 4 | Préstamo exitoso de revista (P002) |
| 5 | Disponibles de R001 quedan en 0 |
| 6 | Rechazo de préstamo sin ejemplares disponibles |
| 7 | Usuario María alcanza el límite de 3 préstamos activos |
| 8 | Rechazo de préstamo por exceder el máximo de 3 préstamos |
| 9 | Devolución con retraso genera multa |
| 10 | Usuario acumula $1.00 de multa pendiente |
| 11 | Stock de R001 se restaura tras la devolución |
| 12 | Rechazo al devolver dos veces el mismo préstamo |
| 13 | Rechazo de préstamo a usuario con multas pendientes |
| 14 | Pago total de multas exitoso |
| 15 | Devolución a tiempo no genera multas |

**Resultado obtenido:**

```text
Pruebas OK: 15 | Fallidas: 0

BUILD SUCCESS
```

![Resultado de las 15 pruebas automáticas](https://i.postimg.cc/3N97yGCj/image.png)

### 11. Autor

María José Guerrero — Ingeniería de Software, Universidad Técnica del Norte (UTN).




//***************************************************************************************************************************************************************

## EJERCICIO 2: Sistema de Cooperativa de Ahorro y Crédito

Sistema desarrollado en Java para administrar socios y cuentas bancarias de una cooperativa de Imbabura. Permite registrar depósitos, retiros y transferencias; calcular intereses; cerrar cuentas según las reglas establecidas; generar reportes y guardar la información en archivos.

**Código:** `src/main/java/com/mycompany/tr1/bdd/ejercicio2/`  
**Clase principal:** `ejercicio2/Main.java`  
**Tecnología:** Java, Maven y persistencia mediante serialización de objetos en un archivo `.dat`.

### 1. Enunciado resumido

Cada socio puede tener una o varias cuentas de ahorro o corrientes. Las cuentas de ahorro generan un interés anual del 4 %, calculado mensualmente, y deben mantener un saldo mínimo de $5. Las cuentas corrientes no generan intereses y admiten un sobregiro de hasta $200. Los depósitos y retiros deben ser mayores que cero. Las transferencias se registran en ambas cuentas y deben rechazarse sin cambios parciales cuando alguna validación falla. No se puede cerrar una cuenta con saldo distinto de cero; si contiene movimientos, se conserva como inactiva. El sistema proporciona cinco reportes y permite recuperar los datos entre ejecuciones.

### 2. Reglas de negocio

| N.º | Regla |
|---|---|
| 1 | Cada socio se identifica por una cédula única y puede tener varias cuentas. |
| 2 | Cada cuenta posee un número único y pertenece a un socio previamente registrado. |
| 3 | Las cuentas de ahorro requieren un depósito inicial mínimo de $5 y no pueden quedar por debajo de ese saldo. |
| 4 | El interés mensual de ahorros se calcula como `saldo × 0.04 / 12`, redondeado a dos decimales. |
| 5 | No se acredita el interés más de una vez en el mismo mes. Las cuentas corrientes no reciben intereses. |
| 6 | Las cuentas corrientes permiten un saldo negativo de hasta -$200. |
| 7 | Todo depósito o retiro debe ser mayor que cero. |
| 8 | No se puede transferir a la misma cuenta, ni transferir un monto inválido. |
| 9 | Antes de transferir se validan origen y destino; si alguna validación falla, no cambian los saldos ni los movimientos. |
| 10 | Una cuenta con saldo distinto de cero no se puede cerrar. |
| 11 | Una cuenta con movimientos y saldo cero se marca inactiva, conservando su historial. Una cuenta sin movimientos y saldo cero se puede eliminar. |
| 12 | Las cuentas inactivas no admiten depósitos, retiros ni transferencias. |
| 13 | Cada movimiento registra fecha, tipo, monto, referencia, saldo anterior y saldo posterior. |
| 14 | Los datos se guardan y recuperan desde un archivo `.dat` mediante serialización de objetos Java. |

### 3. Estructura de carpetas

```text
ejercicio2/
├── Main.java                    (31 pruebas automáticas)
├── modelo/
│   ├── Socio.java              (datos del socio)
│   ├── Cuenta.java             (clase abstracta)
│   ├── CuentaAhorro.java       (interés y saldo mínimo)
│   ├── CuentaCorriente.java    (límite de sobregiro)
│   ├── Movimiento.java         (historial y saldos)
│   └── TipoMovimiento.java     (enum: DEPOSITO, RETIRO, INTERES)
├── servicio/
│   ├── Cooperativa.java        (gestión y operaciones bancarias)
│   └── Reportes.java           (cinco reportes)
└── persistencia/
    └── RepositorioArchivo.java  (guardar y cargar archivos .dat)
```

>  <img width="275" height="195" alt="image" src="https://github.com/user-attachments/assets/c2bb907c-d06c-419f-85a5-ab773e4cb8a1" />



### 4. Diagrama de clases

> <img width="1009" height="545" alt="image" src="https://github.com/user-attachments/assets/c052eaba-b261-4f39-a1a3-241e042d5c5a" />

**Relación socio-cuenta:** se realiza mediante `cedulaSocio`; no se almacena un objeto `Socio` dentro de cada `Cuenta`.


### 5. Operaciones de la clase `Cooperativa`

| Método | Qué hace |
|---|---|
| `registrarSocio(cedula, nombre)` | Registra un socio nuevo y rechaza cédulas duplicadas. |
| `crearAhorro(numero, cedula, inicial)` | Abre una cuenta de ahorro con saldo inicial de al menos $5. |
| `crearCorriente(numero, cedula, inicial)` | Abre una cuenta corriente con saldo inicial no negativo. |
| `buscarSocio(cedula)` / `buscarCuenta(numero)` | Obtiene el registro solicitado o indica que no existe. |
| `depositar(numero, monto)` | Valida el monto, aumenta el saldo y registra el movimiento. |
| `retirar(numero, monto)` | Valida el monto, aplica el límite correspondiente y registra el movimiento. |
| `transferir(origen, destino, monto)` | Valida las dos cuentas antes de efectuar el retiro y el depósito. |
| `aplicarInteresesDelMes()` | Acredita intereses a cuentas de ahorro activas, sin duplicar el mes. |
| `cerrarCuenta(numero)` | Rechaza saldos no nulos; elimina cuentas sin movimientos o desactiva las que tienen historial. |

### 6. Reportes para el gerente

| Reporte | Método |
|---|---|
| Estado de cuenta y saldo después de cada movimiento | `estadoCuenta(numero)` |
| Intereses mensuales de todas las cuentas de ahorro | `interesesDelMes(mes)` |
| Cuentas corrientes en sobregiro | `cuentasEnSobregiro()` |
| Total depositado y retirado en un período | `movimientosEnPeriodo(desde, hasta)` |
| Auditoría de saldos registrados | `auditoriaSaldos()` |

El reporte del período contabiliza los movimientos `DEPOSITO` y `RETIRO`, incluidas aperturas y transferencias. Los intereses se presentan por separado. En el reporte de intereses, el mes actual puede mostrar una **estimación** basada en el saldo vigente cuando todavía no se ha acreditado.

### 7. Ejemplo de uso

```java
Cooperativa coop = new Cooperativa();
coop.registrarSocio("1002003004", "Ana Lopez");

coop.crearAhorro("A001", "1002003004", new BigDecimal("1200.00"));
coop.crearCorriente("C001", "1002003004", new BigDecimal("50.00"));

coop.depositar("A001", new BigDecimal("100.00"));
coop.retirar("A001", new BigDecimal("50.00"));
coop.transferir("A001", "C001", new BigDecimal("100.00"));
coop.aplicarInteresesDelMes();

Reportes reportes = new Reportes(coop);
System.out.println(reportes.estadoCuenta("A001"));
System.out.println(reportes.auditoriaSaldos());

Path archivo = Path.of("datos", "cooperativa_pruebas.dat");
RepositorioArchivo.guardar(coop, archivo);
Cooperativa recuperada = RepositorioArchivo.cargar(archivo);
```

*Para este ejemplo se requieren las importaciones de `BigDecimal`, `Path` y de las clases de los paquetes `modelo`, `servicio` y `persistencia`.*

### 8. Ejemplo de salida de los reportes

La clase `Main.java` ejecuta pruebas automáticas y, una vez finalizadas correctamente, imprime los saldos y los reportes de la cooperativa. Los siguientes resultados corresponden a la ejecución real realizada en NetBeans el **4 de noviembre de 2026**.

**Saldos finales de las cuentas**

```text
=== SALDOS DE LAS CUENTAS ===
Cuenta: A001 | Tipo: AHORROS | Saldo: $1153.83 | Estado: ACTIVA
Cuenta: C001 | Tipo: CORRIENTE | Saldo: $120.00 | Estado: ACTIVA
Cuenta: C002 | Tipo: CORRIENTE | Saldo: $0.00 | Estado: INACTIVA
```

**Reporte 1. Estado de cuenta**

Muestra los depósitos, retiros e intereses aplicados a la cuenta de ahorros `A001`, junto con el saldo resultante después de cada movimiento.

```text
=== ESTADO DE CUENTA ===
Cuenta: A001 | Tipo: AHORROS | Estado: ACTIVA
2026-11-04T00:00:41.879272100 | DEPOSITO | Monto: $1200.00 | Saldo despues: $1200.00 | APERTURA
2026-11-04T00:00:41.884278300 | DEPOSITO | Monto: $100.00 | Saldo despues: $1300.00 | VENTANILLA
2026-11-04T00:00:41.891283600 | RETIRO | Monto: $50.00 | Saldo despues: $1250.00 | VENTANILLA
2026-11-04T00:00:41.899791700 | RETIRO | Monto: $100.00 | Saldo despues: $1150.00 | TRANSFERENCIA-1
2026-11-04T00:00:41.909790900 | INTERES | Monto: $3.83 | Saldo despues: $1153.83 | INTERES MENSUAL 2026-11
SALDO ACTUAL: $1153.83
```

> <img width="686" height="185" alt="image" src="https://github.com/user-attachments/assets/fc312ac9-b646-4fa0-a8eb-83891c8c45e3" />
  
> *Figura 1. Saldos finales e historial de movimientos de la cuenta de ahorros A001.*

**Reporte 2. Intereses mensuales**

Para noviembre de 2026, el sistema acreditó **$3.83** de interés mensual a la cuenta de ahorros `A001`. La cuenta corriente no genera intereses.

```text
=== INTERESES DE AHORROS: 2026-11 ===
Cuenta A001: $3.83 | ACREDITADO
TOTAL: $3.83
```

> <img width="246" height="92" alt="image" src="https://github.com/user-attachments/assets/95ab1f40-ba72-4cc9-b61f-347e0e32f7ab" />

> *Figura 2. Interés mensual acreditado en la cuenta de ahorros A001.*

**Reporte 3. Cuentas en sobregiro**

Durante la prueba del límite de sobregiro, la cuenta corriente `C001` alcanzó un saldo negativo de **-$200.00**, por lo que apareció en el reporte:

```text
=== CUENTAS EN SOBREGIRO ===
Cuenta C001 | Saldo: $-200.00
TOTAL: 1
```

Más adelante se depositaron $220.00 y se transfirieron $100.00 a esa cuenta; por eso, al emitir los reportes finales, ya no había cuentas en sobregiro:

```text
=== CUENTAS EN SOBREGIRO ===
TOTAL: 0
```
  
> <img width="363" height="134" alt="image" src="https://github.com/user-attachments/assets/87c1a985-4046-4691-867e-ca3dd2315094" />

> Figura 3. Identificación de una cuenta corriente con el sobregiro máximo permitido.*

**Reporte 4. Total depositado y retirado en un período**

El sistema contabilizó los movimientos registrados entre el 3 y el 5 de noviembre de 2026.

```text
=== MOVIMIENTOS DEL 2026-11-03 AL 2026-11-05 ===
TOTAL DEPOSITADO: $1680.00
TOTAL RETIRADO: $410.00
Nota: aperturas y transferencias cuentan como movimientos.
```

Estos totales incluyen los depósitos de apertura y los movimientos de las transferencias internas. Los intereses acreditados se registran como movimientos de tipo `INTERES` y no se suman al total depositado.

**Reporte 5. Auditoría de saldos**

La auditoría reconstruye el saldo de cada cuenta utilizando los movimientos registrados y lo compara con el saldo actual.

```text
=== AUDITORIA DE SALDOS ===
Cuenta A001 | OK | Calculado: $1153.83 | Registrado: $1153.83
Cuenta C001 | OK | Calculado: $120.00 | Registrado: $120.00
Cuenta C002 | OK | Calculado: $0.00 | Registrado: $0.00
CUENTAS CON DIFERENCIAS: 0
```

> <img width="363" height="134" alt="image" src="https://github.com/user-attachments/assets/c08a0daf-e451-4a1d-a07c-6d5f1257de66" />

> *Figura 4. Resumen de movimientos financieros y verificación de consistencia de los saldos.*

### 9. Decisiones de diseño

- **Programación orientada a objetos:** la clase abstracta `Cuenta` concentra las propiedades y operaciones comunes. `CuentaAhorro` y `CuentaCorriente` heredan de ella y aplican sus respectivas restricciones de saldo.
- **Manejo de dinero:** se utiliza `BigDecimal` para representar montos y saldos, evitando los errores típicos de precisión de `double`. Los intereses se redondean a dos decimales.
- **Interés mensual:** se calcula el 4 % anual dividido entre 12 sobre el saldo de la cuenta de ahorros al momento de aplicarlo. El historial impide acreditar dos veces el interés correspondiente a un mismo mes.
- **Validación previa de transferencias:** antes de efectuar un retiro y un depósito vinculados, se comprueban ambas cuentas y el monto. Si alguna regla de negocio no se cumple, no se registran movimientos ni se alteran los saldos.
- **Conservación del historial:** las cuentas con movimientos y saldo cero se marcan como inactivas en lugar de eliminarse. Esto permite consultar las operaciones anteriores.
- **Auditoría:** cada movimiento conserva el saldo anterior y posterior. El reporte vuelve a calcular el saldo a partir del historial y detecta diferencias.
- **Persistencia en archivos:** `RepositorioArchivo` utiliza serialización de objetos Java para guardar y recuperar socios, cuentas y movimientos en el archivo binario `datos/cooperativa_pruebas.dat`. El guardado ocurre al ejecutar el método `guardar()`.
- **Pruebas automatizadas sin menú:** `Main.java` ejecuta verificaciones con mensajes `[OK]` y `[ERROR]`, y después imprime los cinco reportes para facilitar la revisión de resultados.

### 10. Pruebas y ejecución

**Cómo ejecutar el ejercicio:**

1. Abrir el proyecto Maven `Tr1-BDD` en Apache NetBeans.
2. Ubicar `src/main/java/com/mycompany/tr1/bdd/ejercicio2/Main.java`.
3. Hacer clic derecho sobre `Main.java` y seleccionar **Run File**, o presionar **Shift + F6**.
4. Revisar los mensajes de las pruebas, los cinco reportes y el resultado final de la consola.

**Verificaciones realizadas:**

| N.º | Caso de prueba | Resultado real |
|---|---|---|
| 1 | Registrar dos socios | OK |
| 2 | Rechazar cédula repetida | OK |
| 3 | Crear varias cuentas para un socio | OK |
| 4 | Rechazar número de cuenta repetido | OK |
| 5 | Rechazar apertura de ahorros con menos de $5 | OK |
| 6 | Depositar $100 en ahorros | OK |
| 7 | Rechazar depósito de $0 | OK |
| 8 | Rechazar depósito negativo | OK |
| 9 | Retirar $50 de ahorros | OK |
| 10 | Rechazar retiro de $0 | OK |
| 11 | Mantener saldo mínimo de $5 en ahorros | OK |
| 12 | Permitir sobregiro de hasta $200 | OK |
| 13 | Rechazar sobregiro superior al límite | OK |
| 14 | Identificar cuenta en sobregiro en el reporte | OK |
| 15 | Recuperar saldo corriente mediante depósito | OK |
| 16 | Transferencia válida de $100 | OK |
| 17 | Rechazar transferencia a la misma cuenta | OK |
| 18 | Rechazar transferencia de $0 | OK |
| 19 | Evitar cambios de saldos y movimientos ante transferencia fallida | OK |
| 20 | Rechazar cierre de cuenta con saldo diferente de cero | OK |
| 21 | Eliminar cuenta sin movimientos | OK |
| 22 | Desactivar cuenta con historial y saldo cero | OK |
| 23 | Rechazar depósito en cuenta inactiva | OK |
| 24 | Rechazar transferencia hacia una cuenta inactiva sin alterar el origen | OK |
| 25 | Calcular y acreditar interés mensual | OK |
| 26 | Evitar acreditación duplicada de intereses | OK |
| 27 | Generar estado de cuenta con saldo posterior | OK |
| 28 | Generar reporte de intereses del mes | OK |
| 29 | Calcular depósitos y retiros por período | OK |
| 30 | Auditar los saldos sin diferencias | OK |
| 31 | Guardar y recuperar los datos desde un archivo | OK |

**Resumen de la ejecución real en NetBeans:**

```text
=== RESULTADO FINAL ===
Pruebas aprobadas: 31
Pruebas fallidas: 0
TODAS LAS PRUEBAS FUERON CORRECTAS
Datos de prueba: datos/cooperativa_pruebas.dat

BUILD SUCCESS
Total time:  3.239 s
Finished at: 2026-11-04T00:00:42-05:00
```

> <img width="407" height="155" alt="image" src="https://github.com/user-attachments/assets/1e021960-4046-453e-a5e0-89ecc123eb1b" />

> *Figura 5. Resultado de las 31 verificaciones automáticas del sistema bancario.*

**Archivo de datos de prueba:** `datos/cooperativa_pruebas.dat`. Se genera desde el programa cuando se ejecuta la prueba de persistencia; su contenido es binario, por lo que no se modifica directamente como un archivo de texto.
 
> <img width="620" height="161" alt="image" src="https://github.com/user-attachments/assets/4510e55d-5954-40a5-9e7d-0eaaf4953b1d" />

> *Figura 6. Archivo generado para conservar la información de la cooperativa.*

### 11. Autor

**Ester Madeley Carrión Castro** — Ingeniería de Software, Universidad Técnica del Norte (UTN).

---



//***************************************************************************************************************************************************************




EJERCICIO 3: 




//***************************************************************************************************************************************************************


# EJERCICIO 4: Sistema de Cafetería UTN

Sistema en Java para controlar el stock de ingredientes de la cafetería de la Universidad Técnica del Norte (UTN), registrar abastecimientos y ventas, y generar reportes para el administrador.

**Código en:** `src/main/java/com/mycompany/tr1/bdd/ejercicio4/`  
**Clase principal:** `ejercicio4/Main.java`

## 1. Enunciado resumido

La cafetería de la UTN vende platos preparados y bebidas embotelladas.

- Cada plato se prepara según una receta que especifica los ingredientes y las cantidades necesarias.
- Los ingredientes se miden en kilogramos o litros y tienen un stock mínimo.
- Se registra cada abastecimiento y cada venta.
- El administrador necesita reportes de costos, precios, márgenes, ingredientes por comprar, platos preparables, platos más vendidos y auditoría del stock.

## 2. Reglas de negocio

| N.º | Regla |
|---|---|
| 1 | El costo de un plato es la suma de `costoPorUnidad × cantidad` de cada ingrediente de su receta. |
| 2 | El precio de venta de un plato es el doble de su costo. |
| 3 | El margen de un plato es el precio menos el costo; por la regla anterior, es igual al costo. |
| 4 | Las bebidas se venden a su precio base más el IVA del 15 %. Su stock se cuenta en unidades. |
| 5 | Al vender platos se descuentan los ingredientes de la receta multiplicados por la cantidad vendida. |
| 6 | Si falta algún ingrediente o stock de bebida, se rechaza la venta completa sin modificar el inventario. |
| 7 | No se puede eliminar un ingrediente que forma parte de alguna receta. |
| 8 | Cada abastecimiento y venta generan movimientos de stock para su auditoría. |

## 3. Estructura de carpetas

```text
ejercicio4/
├── Main.java                 (pruebas automáticas del sistema)
├── modelo/
│   ├── Producto.java         (clase abstracta)
│   ├── Plato.java            (hereda de Producto)
│   ├── Bebida.java           (hereda de Producto)
│   ├── Ingrediente.java
│   ├── ItemReceta.java
│   ├── Receta.java
│   ├── Abastecimiento.java
│   ├── Venta.java
│   ├── DetalleVenta.java
│   ├── MovimientoStock.java
│   ├── UnidadMedida.java     (enum: KG, L)
│   └── TipoMovimiento.java   (enum: ABASTECIMIENTO, VENTA)
└── servicio/
    └── Cafeteria.java        (lógica de negocio y reportes)
```

> <img width="417" height="568" alt="image" src="https://github.com/user-attachments/assets/3d17c478-58bd-43a6-bdf4-6ab7de219f5c" />
  
> *Figura 1. Organización de paquetes y clases del Sistema de Cafetería UTN.*

## 4. Diagrama de clases

El diseño utiliza la clase abstracta `Producto`, de la que heredan `Plato` y `Bebida`. `Plato` se relaciona con `Receta`, que a su vez contiene elementos `ItemReceta` asociados a `Ingrediente`. La clase `Cafeteria` administra los ingredientes, productos, ventas, abastecimientos y movimientos de stock.

Las clases descritas en el README original son:

| Clase | Atributos y métodos destacados |
|---|---|
| `Producto` (abstracta) | `id`, `nombre` |
| `Plato` | `receta`; `getCosto()`, `getPrecioVenta()`, `getMargen()` |
| `Bebida` | `precioBase`, `stockUnidades`; `getPrecioConIva()` |
| `Receta` | `id`, `List<ItemReceta> items` |
| `ItemReceta` | `ingrediente`, `cantidad` |
| `Ingrediente` | `id`, `nombre`, `unidadMedida`, `stockActual`, `stockMinimo`, `costoPorUnidad` |
| `Abastecimiento` | `id`, `fecha`, `cantidad`, `costoUnitario` |
| `Venta` | `id`, `fecha`, `List<DetalleVenta> detalles`; `getTotal()` |
| `DetalleVenta` | `cantidad`, `precioUnitario` |
| `MovimientoStock` | `id`, `fecha`, `tipo`, `cantidad`, `stockAnterior`, `stockResultante`, `referencia` |
| `Cafeteria` | Listas de ingredientes, platos, bebidas, ventas, abastecimientos y movimientos |

**Relación principal:** `Plato` y `Bebida` heredan de `Producto`. Gracias a ello, un detalle de venta puede referirse a un producto y una venta puede incluir platos y bebidas.

>  <img width="842" height="522" alt="image" src="https://github.com/user-attachments/assets/d9937cdf-dc36-40e7-8c99-f10c50ae69f4" />
 
> *Figura 2. Diagrama de clases del Sistema de Cafetería UTN.*

## 5. Operaciones de la clase `Cafeteria`

| Método | ¿Qué hace? |
|---|---|
| `agregarIngrediente`, `agregarPlato`, `agregarBebida` | Registra elementos en el sistema. |
| `abastecer(ingrediente, cantidad, costoUnitario)` | Suma stock, actualiza el costo unitario y registra el abastecimiento y su movimiento. |
| `vender(Map<Producto, Integer> pedido)` | Valida el pedido completo; si se puede realizar, descuenta el stock y registra la venta. |
| `eliminarIngrediente(ingrediente)` | Elimina un ingrediente solo si no forma parte de una receta. |
| `ingredienteEnUso(ingrediente)` | Comprueba si un ingrediente aparece en una receta. |
| `unidadesPreparables(plato)` | Calcula cuántas unidades de un plato permite preparar el stock actual. |
| `listaDeCompras()` | Identifica ingredientes por debajo de su stock mínimo. |

## 6. Reportes para el administrador

| Reporte | Método |
|---|---|
| Costo, precio y margen de cada plato | `reporteCostosPlatos()` |
| Ingredientes por debajo del stock mínimo (lista de compras) | `reporteListaDeCompras()` |
| Platos que pueden prepararse y cuántas unidades | `reportePlatosPreparables()` |
| Platos más vendidos en un período | `reportePlatosMasVendidos(desde, hasta, limite)` |
| Auditoría del stock de ingredientes | `auditoriaStock()` |

## 7. Ejemplo de uso

```java
Cafeteria cafeteria = new Cafeteria();

// Ingredientes y abastecimiento
Ingrediente arroz = new Ingrediente(1, "Arroz", UnidadMedida.KG, 0, 2, 0);
Ingrediente pollo = new Ingrediente(2, "Pollo", UnidadMedida.KG, 0, 1, 0);
cafeteria.agregarIngrediente(arroz);
cafeteria.agregarIngrediente(pollo);
cafeteria.abastecer(arroz, 10, 1.50);   // 10 kg a 1.50 por kg
cafeteria.abastecer(pollo, 5, 4.00);    // 5 kg a 4.00 por kg

// Receta y plato: 0.2 kg de arroz + 0.3 kg de pollo
Receta receta = new Receta(1);
receta.agregarItem(new ItemReceta(arroz, 0.2));
receta.agregarItem(new ItemReceta(pollo, 0.3));
Plato arrozConPollo = new Plato(1, "Arroz con pollo", receta);
cafeteria.agregarPlato(arrozConPollo);

// Bebida
Bebida agua = new Bebida(2, "Agua", 1.00, 20);
cafeteria.agregarBebida(agua);

// Venta de 2 platos y 3 aguas
Map<Producto, Integer> pedido = new LinkedHashMap<>();
pedido.put(arrozConPollo, 2);
pedido.put(agua, 3);
Venta venta = cafeteria.vender(pedido);   // total: 9.45
```

**Cálculo del ejemplo:**

- Costo del plato: `0.2 × 1.50 + 0.3 × 4.00 = $1.50`.
- Precio de venta del plato: `$3.00`.
- Precio del agua con IVA: `1.00 × 1.15 = $1.15`.
- Total de la venta: `2 × 3.00 + 3 × 1.15 = $9.45`.
- Stock final: arroz **9.6 kg**, pollo **4.4 kg** y agua **17 unidades**.


> <img width="840" height="186" alt="image" src="https://github.com/user-attachments/assets/6b853d01-bfb7-4f62-a068-5bdf53718e0a" />
  
> *Figura 3. Ejemplo de registro y venta de productos en la cafetería.*

## 8. Ejemplo de salida de los reportes

### 8.1. Costo, precio y margen de los platos

```text
=== COSTO, PRECIO Y MARGEN POR PLATO ===
Arroz con pollo      Costo: 1.50 | Precio: 3.00 | Margen: 1.50
Pollo frito          Costo: 1.90 | Precio: 3.80 | Margen: 1.90
```
 
> <img width="544" height="98" alt="image" src="https://github.com/user-attachments/assets/3ccfafc0-2476-4363-be01-8794aa5182ed" />

> * *Figura 4. Reporte de costos, precios de venta y márgenes.*

### 8.2. Lista de compras y platos preparables

```text
=== LISTA DE COMPRAS ===
Aceite (L) | Stock: 0.50 | Mínimo: 2.00 | Falta: 1.50

=== PLATOS QUE SE PUEDEN PREPARAR ===
Arroz con pollo: 14 unidades
Pollo frito: 5 unidades
```

> <img width="426" height="85" alt="image" src="https://github.com/user-attachments/assets/8e388b98-4bb9-45ac-bf84-19f1e9d4a309" />
  
> *Figura 5. Disponibilidad de ingredientes y capacidad de preparación.*

### 8.3. Platos más vendidos

```text
=== PLATOS MÁS VENDIDOS ===
1. Arroz con pollo - 2 unidades
```

>   <img width="286" height="41" alt="image" src="https://github.com/user-attachments/assets/5c103047-35c7-485b-82d0-5b833ac7f1a7" />


> * *Figura 6. Reporte de platos más vendidos.*

### 8.4. Auditoría de stock

```text
=== AUDITORÍA DE STOCK ===
#1 2026-11-02 | Arroz | ABASTECIMIENTO +10.00 | 0.00 -> 10.00 | ref: 1
#2 2026-11-02 | Pollo | ABASTECIMIENTO +5.00 | 0.00 -> 5.00 | ref: 2
#3 2026-11-02 | Aceite | ABASTECIMIENTO +0.50 | 0.00 -> 0.50 | ref: 3
#4 2026-11-02 | Arroz | VENTA -0.40 | 10.00 -> 9.60 | ref: 1
#5 2026-11-02 | Pollo | VENTA -0.60 | 5.00 -> 4.40 | ref: 1
--- Verificación ---
Arroz: OK
Pollo: OK
Aceite: OK
```

> <img width="548" height="104" alt="image" src="https://github.com/user-attachments/assets/03cdbb91-26cc-4c35-ae8b-b31125908db5" />

> * *Figura 7. Auditoría de movimientos y existencias de ingredientes.*

**Nota:** Los decimales pueden mostrarse con coma en lugar de punto según la configuración regional del equipo.

## 9. Decisiones de diseño

- **Validar antes de modificar:** `vender()` calcula primero lo que necesita el pedido completo y revisa la disponibilidad. Si falta stock, no modifica el inventario.
- **Ingredientes compartidos:** cuando varios platos del mismo pedido usan un mismo ingrediente, se suma el consumo total antes de validar la venta.
- **Actualización de costos:** al abastecer un ingrediente, su `costoPorUnidad` se actualiza con el último costo unitario.
- **Auditoría verificable:** cada movimiento registra el stock anterior y el resultante; el reporte comprueba si el stock actual coincide con los movimientos.
- **Herencia:** `Plato` y `Bebida` heredan de `Producto`, permitiendo que una venta incluya diferentes tipos de productos.

## 10. Pruebas


Las pruebas contemplan:

- Cálculo del costo, precio de venta, margen e IVA.
- Venta válida y descuento de stock.
- Rechazo por ingredientes insuficientes, ingredientes compartidos y falta de stock de bebida.
- Eliminación permitida y bloqueada de ingredientes.
- Lista de compras, platos preparables y movimientos de auditoría.

**Resultado resumido indicado en el README original:**

```text
Pruebas OK: 20 | Fallidas: 0
```

> <img width="253" height="54" alt="image" src="https://github.com/user-attachments/assets/206b011a-e475-4a88-84dc-d401d0e11c7d" />

> * *Figura 8. Resultados de las pruebas automáticas del Ejercicio 4.*


## 11. Autor

**Juan Diego** — Ingeniería de Software, Universidad Técnica del Norte (UTN).

---


//***************************************************************************************************************************************************************




EJERCICIO 5: Tienda de Artesanías de Otavalo

Sistema desarrollado en Java para administrar las artesanías entregadas en consignación por artesanos de distintas comunidades de Otavalo. Permite registrar artesanos, productos, recepciones, ventas, devoluciones, comisiones y reportes, utilizando programación orientada a objetos y persistencia en archivos de texto.

**Código en:** `src/main/java/com/mycompany/tr1/bdd/ejercicio5/`

**Clase principal:** `ejercicio5/Main.java`

**Clase de pruebas:** `ejercicio5/servicio/PruebasEjercicio5.java`

## 1. Enunciado resumido

Una tienda de artesanías de Otavalo comercializa productos textiles y tallas de madera elaborados por artesanos de diferentes comunidades.

El sistema debe registrar los datos de cada artesano y sus artesanías, controlar las unidades recibidas en consignación, realizar ventas y devoluciones y calcular las comisiones correspondientes.

- Las artesanías textiles generan una comisión del 20 % para la tienda.
- Las tallas de madera generan una comisión del 25 % para la tienda.
- Las artesanías que permanecen más de 90 días sin venderse deben identificarse para su devolución.
- Las ventas y devoluciones descuentan unidades del inventario.
- No se permite vender o devolver más unidades que las disponibles.
- Las devoluciones deben realizarse al artesano propietario.
- El sistema genera cinco reportes administrativos.
- La información se guarda y recupera mediante un archivo TXT.

## 2. Reglas de negocio

| N.º | Regla |
|---|---|
| 1 | Cada artesanía pertenece a un artesano identificado mediante un ID. |
| 2 | Las artesanías pueden ser de tipo TEXTIL o TALLA_MADERA. |
| 3 | La comisión de los textiles es del 20 % del valor vendido. |
| 4 | La comisión de las tallas de madera es del 25 % del valor vendido. |
| 5 | El pago al artesano es igual al total vendido menos la comisión de la tienda. |
| 6 | Cada recepción registra fecha, cantidad recibida y cantidad disponible. |
| 7 | Una venta válida descuenta las unidades correspondientes del inventario. |
| 8 | No se permiten ventas ni devoluciones con stock insuficiente. |
| 9 | Las devoluciones solamente pueden registrarse a nombre del artesano propietario. |
| 10 | Las recepciones con más de 90 días y unidades disponibles se identifican para devolución. |
| 11 | No se permite registrar productos, recepciones o movimientos con identificadores duplicados. |
| 12 | No se puede eliminar un artesano que tenga artesanías registradas. |
| 13 | No se puede eliminar una artesanía que tenga recepciones o movimientos asociados. |
| 14 | Se rechazan cantidades no positivas y precios de venta inválidos. |
| 15 | La información se almacena y recupera desde `datos/ejercicio5.txt`. |

## 3. Estructura de carpetas

```text
ejercicio5/
├── Main.java
├── modelo/
│   ├── Artesano.java
│   ├── Artesania.java
│   ├── Recepcion.java
│   └── Movimiento.java
└── servicio/
    ├── GestionInventario.java
    ├── Reportes.java
    ├── ArchivoInventario.java
    └── PruebasEjercicio5.java
```

**Archivo de almacenamiento:**

```text
Tr1-BDD/
└── datos/
    └── ejercicio5.txt
```

## 4. Descripción de las clases

### Artesano

Representa al productor de las artesanías.

**Atributos principales:**
- `int id`
- `String nombre`
- `String comunidad`

**Métodos principales:**
- `getId()`
- `getNombre()`
- `getComunidad()`
- `setNombre(nombre)`
- `setComunidad(comunidad)`
- `toString()`

### Artesania

Representa una artesanía entregada a la tienda.

**Atributos principales:**
- `int id`
- `String nombre`
- `Tipo tipo`
- `double precioVenta`
- `Artesano artesano`

El enumerado `Tipo` permite seleccionar `TEXTIL` o `TALLA_MADERA`.

**Métodos principales:**
- `getPorcentajeComision()`
- `calcularComision()`
- `calcularPagoArtesano()`
- `getArtesano()`
- `getPrecioVenta()`

### Recepcion

Registra las unidades que llegan a la tienda en consignación.

**Atributos principales:**
- `int id`
- `Artesania artesania`
- `LocalDate fechaRecepcion`
- `int cantidadRecibida`
- `int cantidadDisponible`

**Métodos principales:**
- `requiereDevolucion(fechaActual)`
- `descontarUnidades(cantidad)`
- `getCantidadDisponible()`
- `getCantidadRecibida()`

### Movimiento

Registra las ventas y devoluciones realizadas.

**Atributos principales:**
- `int id`
- `Tipo tipo`
- `Artesania artesania`
- `int cantidad`
- `LocalDate fecha`
- `BigDecimal totalVenta`
- `BigDecimal comision`
- `BigDecimal pagoArtesano`

El enumerado `Tipo` contiene `VENTA` y `DEVOLUCION`.

Se utiliza `BigDecimal` para los resultados monetarios de las ventas y comisiones.

### GestionInventario

Es la clase principal de la lógica del negocio. Administra los artesanos, productos, recepciones y movimientos.

**Métodos principales:**

| Método | Función |
|---|---|
| `registrarArtesano()` | Registra un nuevo artesano. |
| `registrarArtesania()` | Registra un producto y su propietario. |
| `registrarRecepcion()` | Registra las unidades recibidas. |
| `consultarStock()` | Calcula las unidades disponibles. |
| `registrarVenta()` | Valida y registra una venta. |
| `registrarDevolucion()` | Valida y registra una devolución. |
| `eliminarArtesano()` | Elimina al artesano si no tiene productos asociados. |
| `eliminarArtesania()` | Elimina productos sin recepciones ni movimientos. |
| `obtenerPendientesDevolucion()` | Identifica recepciones con más de 90 días. |
| `mostrarInventario()` | Presenta las existencias. |
| `mostrarMovimientos()` | Presenta el historial de operaciones. |

### Reportes

Contiene los métodos que generan los cinco reportes solicitados.

### ArchivoInventario

Permite guardar y recuperar la información mediante archivos de texto.

### PruebasEjercicio5

Ejecuta 20 pruebas automáticas para verificar cálculos, restricciones, inventario y almacenamiento de datos.

## 5. Operaciones principales

El sistema realiza las siguientes operaciones:

**Registro de artesanías:** verifica que el producto no esté duplicado y registra su relación con el artesano propietario.

**Recepción de productos:** registra la fecha y la cantidad recibida, inicializando las unidades disponibles.

**Venta:** comprueba las existencias antes de descontar unidades. Calcula el total de la venta, la comisión y el pago correspondiente al artesano.

**Devolución:** verifica que existan suficientes unidades y que el destinatario sea el artesano propietario.

**Control de antigüedad:** identifica las recepciones que superan los 90 días y todavía tienen unidades disponibles.

**Eliminación de registros:** impide eliminar artesanos o artesanías cuando existen relaciones que deben conservarse.

**Persistencia:** guarda los datos en un archivo TXT y permite reconstruir el estado del inventario en una nueva ejecución.

## 6. Reportes para el administrador

| Reporte | Método |
|---|---|
| Liquidación por artesano | `liquidacion(inventario, desde, hasta)` |
| Artesanías pendientes de devolución | `pendientesDevolucion(inventario, hoy)` |
| Comunidades con más ventas | `comunidadesMasVentas(inventario, desde, hasta)` |
| Artesanías más vendidas | `artesaniasMasVendidas(inventario, desde, hasta)` |
| Auditoría de inventario | `auditoriaStock(inventario)` |

Los reportes se generan por consola a partir de las recepciones, ventas y devoluciones registradas.

## 7. Cómo ejecutar el ejercicio 5

**Requisitos:**
- JDK 25.
- Apache NetBeans con soporte para Maven, o un entorno Java compatible.
- Repositorio `Tr1-BDD` descargado.

**Procedimiento:**

1. Clonar el repositorio:

   ```bash
   git clone https://github.com/Mariaguerrer193/Tr1-BDD.git
   ```

2. Abrir el proyecto Maven `Tr1-BDD` en Apache NetBeans.
3. Configurar JDK 25 como plataforma del proyecto.
4. Buscar el paquete `com.mycompany.tr1.bdd.ejercicio5`.
5. Abrir el archivo `Main.java`.
6. Ejecutarlo con clic derecho → **Run File**, o mediante **Shift + F6**.
7. Revisar los resultados en la ventana **Output**.

El programa funciona por consola. Su clase `Main` ejecuta un escenario de demostración y genera automáticamente los reportes; no requiere un menú interactivo.

**Primera ejecución:** si no existe el archivo de datos, se genera un inventario de ejemplo con recepciones, una venta y una devolución, y se guarda en `datos/ejercicio5.txt`.

**Ejecuciones posteriores:** si el archivo existe, el sistema recupera la información almacenada y muestra el inventario, los movimientos y los cinco reportes.

## 8. Ejemplo de funcionamiento

Se registra al artesano Pedro, perteneciente a la comunidad de Peguche.

Su producto es un poncho artesanal de tipo TEXTIL, con precio de venta de $100.

Se registran dos recepciones:

- Primera recepción: 12 unidades, hace 95 días.
- Segunda recepción: 8 unidades, hace 5 días.

El inventario inicial contiene 20 unidades.

Posteriormente se realizan las siguientes operaciones:

**Venta de tres ponchos:**

- Total: 3 × $100 = $300.
- Comisión de la tienda: $300 × 20 % = $60.
- Pago al artesano: $300 − $60 = $240.

**Devolución de dos ponchos al propietario:**

- Cantidad devuelta: 2 unidades.
- Stock final: 20 − 3 − 2 = 15 unidades.

También se comprueba el rechazo de ventas con stock insuficiente y devoluciones a propietarios incorrectos.

## 9. Ejemplo de salida de los reportes

```text
=== LIQUIDACION DE ARTESANOS ===
Artesano: Pedro | Comunidad: Peguche
Vendido: $300.00
Comision: $60.00
Pago: $240.00

=== ARTESANIAS PARA DEVOLUCION ===
Producto: Poncho artesanal
Artesano: Pedro
Dias: 95
Unidades: 7

=== COMUNIDADES CON MAS VENTAS ===
Peguche | Ventas: $300.00

=== ARTESANIAS MAS VENDIDAS ===
Poncho artesanal | Unidades vendidas: 3

=== AUDITORIA DE INVENTARIO ===
Producto: Poncho artesanal
Recibidas: 20
Salidas: 5
Esperado: 15
Disponible: 15
Estado: OK

=== FIN DEL EJERCICIO 5 ===
BUILD SUCCESS
```

## 10. Persistencia en archivos TXT

El sistema utiliza el archivo:

`datos/ejercicio5.txt`

La clase `ArchivoInventario.java` implementa los métodos:

- `guardar(inventario, ruta)`
- `cargar(ruta)`

El archivo almacena registros correspondientes a:

- **A:** artesanos.
- **P:** productos o artesanías.
- **R:** recepciones.
- **M:** movimientos de venta o devolución.

Los campos se separan mediante el carácter `|`. Los nombres se codifican en Base64 para preservar caracteres especiales y evitar problemas con los separadores.

Al recuperar la información, el programa reconstruye los objetos y vuelve a registrar las operaciones para comprobar la consistencia de las existencias.

El guardado se realiza explícitamente mediante `ArchivoInventario.guardar()`. En la demostración de `Main`, el archivo se crea cuando todavía no existe.

Esta implementación permite que el inventario del ejemplo se conserve entre ejecuciones del programa.

## 11. Pruebas y validaciones

Se implementó la clase independiente `PruebasEjercicio5.java`, encargada de ejecutar automáticamente 20 casos de prueba.

**Cómo ejecutar las pruebas:**

1. Abrir el paquete `ejercicio5.servicio`.
2. Seleccionar `PruebasEjercicio5.java`.
3. Hacer clic derecho → **Run File** o presionar **Shift + F6**.
4. Revisar los resultados en la consola de NetBeans.

**Casos de prueba implementados:**

1. Stock inicial de 20 unidades.
2. Venta textil y comisión del 20 %.
3. Venta y devolución que dejan 15 unidades.
4. Rechazo de venta sin suficiente stock.
5. Rechazo de devolución excesiva.
6. Rechazo de devolución a otro artesano.
7. Rechazo de movimiento duplicado.
8. Rechazo de recepción duplicada.
9. Rechazo de artesanía duplicada.
10. Rechazo de cantidades iguales a cero o negativas.
11. Detección de recepciones que superan los 90 días.
12. Prohibición de eliminar artesanos con productos.
13. Prohibición de eliminar artesanías con recepciones.
14. Prohibición de eliminar artesanías con movimientos.
15. Eliminación de registros sin dependencias.
16. Cálculo de comisión del 25 % para tallas de madera.
17. Guardado y recuperación mediante archivo TXT.
18. Rechazo de recepciones con fecha futura.
19. Rechazo de precios negativos.
20. Rechazo de artesanos duplicados.

**Resultados obtenidos:**

```text
==========================================
          RESUMEN DE PRUEBAS
==========================================
Pruebas aprobadas: 20
Pruebas fallidas: 0
Total de pruebas: 20
RESULTADO: TODAS LAS PRUEBAS PASARON

BUILD SUCCESS
```

Las pruebas comprueban que las operaciones válidas producen los resultados esperados y que determinadas operaciones inválidas son rechazadas sin alterar indebidamente las existencias.

## 12. Decisiones de diseño

**Programación orientada a objetos:** se separaron las clases de datos, la lógica del negocio, los reportes y el almacenamiento en archivos.

**Relación entre artesanos y artesanías:** cada producto mantiene una referencia a su propietario, lo cual facilita las liquidaciones y validaciones de devolución.

**Validación antes de modificar inventario:** las ventas y devoluciones verifican la cantidad disponible antes de descontar unidades.

**Cálculos monetarios:** los movimientos utilizan `BigDecimal` para calcular los valores de venta, comisión y pago.

**Control de antigüedad:** se utiliza `LocalDate` y `ChronoUnit.DAYS` para identificar recepciones que superan los 90 días.

**Auditoría de inventario:** se compara el total de unidades recibidas menos las salidas registradas con las unidades disponibles en el sistema.

**Persistencia en archivos planos:** se utiliza un archivo TXT estructurado, evitando depender de una base de datos externa.

**Pruebas independientes:** la clase `PruebasEjercicio5` permite comprobar las reglas del sistema sin alterar el archivo principal de datos.

## 13. Autor

Jonathan Sánchez — Ingeniería de Software, Universidad Técnica del Norte (UTN).

Desarrollo del Ejercicio 5: Tienda de Artesanías de Otavalo.

Implementación de modelos, gestión de inventario, ventas, devoluciones, comisiones, reportes, almacenamiento en TXT y 20 pruebas automatizadas.
