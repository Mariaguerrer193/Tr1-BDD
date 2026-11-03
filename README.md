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
Ejercicio 4: Sistema de Cafetería UTN

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
