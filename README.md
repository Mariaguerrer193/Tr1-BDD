Tr1-BDD

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





EJERCICIO 1: Sistema de Biblioteca UTN

Sistema en Java para el control de inventario, préstamo de libros y revistas, cálculo de multas por retraso y reportes para el administrador con persistencia en archivos de texto.

Código en: src/main/java/com/mycompany/tr1/bdd/ejercicio1/
Clase principal: ejercicio1/Main.java

1. Enunciado resumido

La biblioteca de la UTN presta libros y revistas. Cada título tiene varios ejemplares.

Hay que controlar cuántos están disponibles y cobrar multas por retraso.
Los libros se prestan por 7 días y la multa es de $0,25 por día de retraso.
Las revistas se prestan por 3 días y la multa es de $0,50 por día de retraso.
Un usuario puede tener como máximo 3 préstamos activos simultáneos.
No se presta si no hay ejemplares disponibles.
No se devuelve el mismo préstamo dos veces.
Un usuario con multas pendientes no puede solicitar nuevos préstamos.
El administrador requiere reportes de préstamos vencidos, historial de usuario, materiales más prestados, total de multas en un período y auditoría de ejemplares disponibles.

2. Reglas de negocio

#	Regla
1	Los libros tienen un plazo de préstamo de 7 días y multa de $0,25 por día de retraso.
2	Las revistas tienen un plazo de préstamo de 3 días y multa de $0,50 por día de retraso.
3	Solo se presta si ejemplaresDisponibles > 0; al prestar se descuenta 1 unidad.
4	Un usuario puede tener como máximo 3 préstamos con estado ACTIVO.
5	Un usuario con multas pendientes no puede solicitar nuevos préstamos.
6	No se devuelve el mismo préstamo dos veces; al devolver se restaura 1 unidad al stock disponible.
7	Si la devolución supera la fecha límite esperada, se calcula multa = díasRetraso × tarifaDiaria y se asigna al usuario.
8	El usuario puede liquidar la totalidad de sus multas acumuladas para desbloquear nuevos préstamos.
9	Toda la información se persiste en archivos planos (materiales.txt, usuarios.txt, prestamos.txt).

3. Estructura de carpetas

ejercicio1/
├── Main.java                 (menú interactivo y validaciones automáticas [OK]/[FALLO])
├── modelo/
│   ├── Material.java         (clase abstracta base)
│   ├── Libro.java            (hereda de Material)
│   ├── Revista.java          (hereda de Material)
│   ├── Usuario.java
│   ├── Prestamo.java
│   └── EstadoPrestamo.java   (enum: ACTIVO, DEVUELTO, CON_RETRASO)
└── servicio/
    ├── ArchivoServicio.java   (lectura y escritura de archivos .txt)
    └── BibliotecaServicio.java(lógica del negocio, validaciones y reportes)

4. Diagrama de clases

«abstract»
Material
#String id
#String titulo
#int ejemplaresTotales
#int ejemplaresDisponibles
+prestar() : boolean
+devolver() : void
+getDiasPrestamo()* : int
+getMultaPorDia()* : double

Libro
+getDiasPrestamo() : int
+getMultaPorDia() : double

Revista
+getDiasPrestamo() : int
+getMultaPorDia() : double

Usuario
-String cedula
-String nombre
-int prestamosActivos
-double multasPendientes
+puedePrestar() : boolean
+incrementarPrestamos() : void
+decrementarPrestamos() : void
+acumularMulta(double monto) : void
+liquidarMultas() : void

EstadoPrestamo
«enum»
ACTIVO
DEVUELTO
CON_RETRASO

Prestamo
-String idPrestamo
-String idMaterial
-String cedulaUsuario
-LocalDate fechaPrestamo
-LocalDate fechaDevolucionEsperada
-LocalDate fechaDevolucionReal
-EstadoPrestamo estado
-double multaGenerada
+estaVencido(LocalDate hoy) : boolean
+calcularMulta(LocalDate hoy, double tarifa) : double

ArchivoServicio
+cargarMateriales() : List<Material>
+guardarMateriales(List<Material>) : void
+cargarUsuarios() : List<Usuario>
+guardarUsuarios(List<Usuario>) : void
+cargarPrestamos() : List<Prestamo>
+guardarPrestamos(List<Prestamo>) : void

BibliotecaServicio
-List<Material> materiales
-List<Usuario> usuarios
-List<Prestamo> prestamos
+registrarLibro(id, titulo, ejemplares) : boolean
+registrarRevista(id, titulo, ejemplares) : boolean
+registrarUsuario(cedula, nombre) : boolean
+prestarMaterial(idPrestamo, idMat, cedula, fecha) : String
+devolverMaterial(idPrestamo, fechaDevolucion) : String
+pagarMultasUsuario(cedula) : boolean

Libro y Revista heredan de Material. BibliotecaServicio gestiona las listas en memoria y ArchivoServicio las persiste en los archivos planos de texto.

5. Operaciones de la clase BibliotecaServicio

Método                     	       Qué hace
registrarLibro, registrarRevista	Valida ID único, crea el ejemplar y actualiza materiales.txt.
registrarUsuario	                Valida cédula única, crea el usuario y actualiza usuarios.txt.
prestarMaterial	                        Verifica disponibilidad, límite de 3 préstamos y ausencia de multas antes de generar el préstamo.
devolverMaterial	                Valida que no esté devuelto, restaura el stock, calcula multas por retraso y actualiza el saldo del usuario.
pagarMultasUsuario	                Pone a cero la deuda del socio en memoria y en el archivo usuarios.txt.

6. Reportes para el administrador

Reporte                                     Método
Préstamos vencidos a la fecha               reportePrestamosVencidos(hoy)
Historial de préstamos de un usuario        reporteHistorialUsuario(cedula)
Materiales más prestados                    reporteMaterialesMasPrestados()
Total de multas generadas en un período     reporteTotalMultasPeriodo(desde, hasta)
Auditoría de ejemplares disponibles         reporteAuditoriaDisponibles()

7. Ejemplo de uso

BibliotecaServicio servicio = new BibliotecaServicio();
LocalDate hoy = LocalDate.now();

// Registrar materiales y usuarios
servicio.registrarLibro("L001", "Estructuras de Datos", 2);
servicio.registrarRevista("R001", "IEEE Software", 1);
servicio.registrarUsuario("1004567890", "Maria Guerrero");

// Realizar préstamo de libro
servicio.prestarMaterial("P001", "L001", "1004567890", hoy);

// Realizar préstamo de revista con fecha anterior (5 días atrás)
servicio.prestarMaterial("P002", "R001", "1004567890", hoy.minusDays(5));

// Devolución con retraso de la revista (plazo 3 días, retraso 2 días)
servicio.devolverMaterial("P002", hoy);

Cálculo del ejemplo:
Plazo de revista: 3 días.
Días transcurridos: 5 días. Días de retraso: 2 días.
Multa generada: 2 días × $0,50 = $1,00.
Stock final: L001 tiene 1 disponible; R001 recupera su ejemplar y vuelve a tener 1 disponible.

8. Ejemplo de salida de los reportes

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

9. Decisiones de diseño

Validación antes de persistir. prestarMaterial valida stock, límite de préstamos y ausencia de multas antes de modificar colecciones o escribir en disco.
Cálculo polimórfico de multas. Cada subclase implementa getDiasPrestamo() y getMultaPorDia(), permitiendo que Prestamo calcule la sanción sin acoplarse al tipo concreto de material.
Persistencia en archivos planos. Toda modificación se sincroniza inmediatamente con archivos delimitados por barra vertical (|) para evitar inconsistencias de datos ante cierres inesperados.
Consola unificada con batería de pruebas. Main ofrece un menú interactivo con Scanner (opciones 1 a 7) y una opción 8 que autoejecuta la verificación completa sin entrada de usuario.

10. Pruebas

Main ejecuta 15 verificaciones automáticas y marca cada una con [OK] o [FALLO]:

[OK] Préstamo exitoso de libro (P001)
[OK] Disponibles de L001 disminuyen a 1
[OK] Usuario María tiene 1 préstamo activo
[OK] Préstamo exitoso de revista (P002)
[OK] Disponibles de R001 quedan en 0
[OK] Rechazo de préstamo sin ejemplares disponibles
[OK] Usuario María alcanza el límite de 3 préstamos activos
[OK] Rechazo de préstamo por exceder el máximo de 3 préstamos
[OK] Devolución con retraso genera multa
[OK] Usuario acumula $1.00 de multa pendiente
[OK] Stock de R001 se restaura tras devolución
[OK] Rechazo al intentar devolver dos veces el mismo préstamo
[OK] Rechazo de préstamo a usuario con multas pendientes
[OK] Pago total de multas exitoso
[OK] Devolución a tiempo no genera multas

Pruebas OK: 15 | Fallidas: 0

11. Autor

María José Guerrero - Ingeniería de Software, Universidad Técnica del Norte





//***************************************************************************************************************************************************************





EJERCICIO 2: 





//***************************************************************************************************************************************************************




EJERCICIO 3: 




//***************************************************************************************************************************************************************




EJERCICIO 4: Sistema de Cafetería UTN

Sistema en Java para controlar el stock de ingredientes de la cafetería de la UTN, registrar abastecimientos y ventas, y generar reportes para el administrador.

Código en: src/main/java/com/mycompany/tr1/bdd/ejercicio4/ Clase principal: ejercicio4/Main.java

1. Enunciado resumido

La cafetería vende platos preparados y bebidas embotelladas.

Cada plato se prepara según una receta (ingredientes y cantidad de cada uno).
Los ingredientes se miden en kilogramos o litros y tienen un stock mínimo.
Se registra cada abastecimiento y cada venta.
El administrador necesita reportes de costos, compras, producción, ventas y auditoría de stock.
2. Reglas de negocio
#	Regla
1	El costo de un plato es la suma de costoPorUnidad × cantidad de cada ingrediente de su receta.
2	El precio de venta de un plato es el doble de su costo.
3	El margen de un plato es precio menos costo (por la regla 2, siempre es igual al costo).
4	Las bebidas se venden a su precio base más el IVA del 15 %. El stock de bebidas se cuenta en unidades.
5	Al vender platos se descuentan todos los ingredientes de la receta, multiplicados por la cantidad vendida.
6	Si falta cualquier ingrediente o stock de bebida, la venta completa se rechaza y no se modifica el stock.
7	No se puede eliminar un ingrediente que forma parte de alguna receta.
8	Cada abastecimiento y cada venta generan movimientos de stock para la auditoría.
3. Estructura de carpetas
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
4. Diagrama de clases
«abstract»
Producto
-int id
-String nombre
Plato
-Receta receta
+getCosto() : double
+getPrecioVenta() : double
+getMargen() : double
Bebida
-double precioBase
-int stockUnidades
+getPrecioConIva() : double
Receta
-int id
-List<ItemReceta> items
ItemReceta
-Ingrediente ingrediente
-double cantidad
Ingrediente
-int id
-String nombre
-UnidadMedida unidadMedida
-double stockActual
-double stockMinimo
-double costoPorUnidad
Abastecimiento
-int id
-LocalDateTime fecha
-double cantidad
-double costoUnitario
Venta
-int id
-LocalDateTime fecha
-List<DetalleVenta> detalles
+getTotal() : double
DetalleVenta
-int cantidad
-double precioUnitario
MovimientoStock
-int id
-LocalDateTime fecha
-TipoMovimiento tipo
-double cantidad
-double stockAnterior
-double stockResultante
-int referencia
Cafeteria
-List<Ingrediente> ingredientes
-List<Plato> platos
-List<Bebida> bebidas
-List<Venta> ventas
-List<Abastecimiento> abastecimientos
-List<MovimientoStock> movimientos

Plato y Bebida heredan de Producto. Gracias a eso, DetalleVenta guarda un Producto y una misma venta puede mezclar platos y bebidas.

5. Operaciones de la clase Cafeteria
Método	Qué hace
agregarIngrediente, agregarPlato, agregarBebida	Registra elementos en el sistema.
abastecer(ingrediente, cantidad, costoUnitario)	Suma stock, actualiza el costo del ingrediente y registra el abastecimiento y el movimiento.
vender(Map<Producto, Integer> pedido)	Valida todo el pedido y, solo si es posible, descuenta stock y registra la venta.
eliminarIngrediente(ingrediente)	Elimina el ingrediente, salvo que forme parte de una receta.
ingredienteEnUso(ingrediente)	Indica si un ingrediente está en alguna receta.
unidadesPreparables(plato)	Cuántas unidades de un plato permite preparar el stock actual.
listaDeCompras()	Ingredientes por debajo del stock mínimo.
6. Reportes para el administrador
Reporte	Método
Costo, precio y margen de cada plato	reporteCostosPlatos()
Ingredientes bajo el stock mínimo (lista de compras)	reporteListaDeCompras()
Platos que se pueden preparar y cuántas unidades	reportePlatosPreparables()
Platos más vendidos en un período	reportePlatosMasVendidos(desde, hasta, limite)
Auditoría del stock de ingredientes	auditoriaStock()
7. Ejemplo de uso
java
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

Cálculo del ejemplo:

Costo del plato: 0.2 × 1.50 + 0.3 × 4.00 = 1.50, precio de venta 3.00.
Agua con IVA: 1.00 × 1.15 = 1.15.
Total: 2 × 3.00 + 3 × 1.15 = 9.45.
Stock final: arroz 9.6 kg, pollo 4.4 kg, agua 17 unidades.
8. Ejemplo de salida de los reportes
=== COSTO, PRECIO Y MARGEN POR PLATO ===
Arroz con pollo      Costo: 1.50 | Precio: 3.00 | Margen: 1.50
Pollo frito          Costo: 1.90 | Precio: 3.80 | Margen: 1.90
=== LISTA DE COMPRAS ===
Aceite (L) | Stock: 0.50 | Mínimo: 2.00 | Falta: 1.50
=== PLATOS QUE SE PUEDEN PREPARAR ===
Arroz con pollo: 14 unidades
Pollo frito: 5 unidades
=== PLATOS MÁS VENDIDOS ===
1. Arroz con pollo - 2 unidades
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

Los decimales pueden salir con coma según la configuración regional del equipo.

9. Decisiones de diseño
La venta valida antes de modificar. vender primero suma lo que requiere todo el pedido y revisa el stock; solo si alcanza, descuenta. Así una venta rechazada nunca deja el stock a medias.
Ingredientes compartidos entre platos. Si dos platos del mismo pedido usan el mismo ingrediente, se suma el consumo total antes de validar. Cada plato podría alcanzar por separado y aun así el pedido completo no.
Costo del ingrediente. Al abastecer, el costoPorUnidad del ingrediente se actualiza con el último costo unitario.
Auditoría verificable. Cada movimiento guarda el stock anterior y el resultante; el reporte compara el último movimiento de cada ingrediente con su stock actual para detectar cambios no registrados.
Herencia de Producto. Permite que una venta mezcle platos y bebidas con un solo tipo de detalle.
10. Pruebas

Main ejecuta 20 verificaciones automáticas y marca cada una con [OK] o [FALLO]:

Cálculo de costo, precio, margen e IVA.
Venta válida con descuento de stock.
Rechazo por ingredientes insuficientes, por ingrediente compartido entre platos y por falta de stock de bebida.
Eliminación permitida y bloqueada de ingredientes.
Lista de compras, platos preparables y movimientos de auditoría.

Al final imprime los reportes y el resumen:

Pruebas OK: 20 | Fallidas: 0
11. Autor

Juan Diego - Ingeniería de Software, Universidad Técnica del Norte (UTN).




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