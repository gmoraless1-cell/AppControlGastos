# AppControlGastos — Mi UMG

App de **control de gastos personales** (Proyecto Final — Programación 2, Universidad Mariano Gálvez).
Demuestra cómo compartir código entre un proyecto Kotlin y una app Android publicando
una **librería propia** en el repositorio local de Maven (`~/.m2`) y consumiéndola desde
Android con Gradle (`mavenLocal()`).

## Funcionalidades

- **Login y registro** de usuarios (local, contraseñas hasheadas con SHA-256). Multiusuario:
  cada usuario ve solo sus datos.
- **Ingresos y egresos** con categorías, monto, fecha y descripción.
- **Persistencia con SQLite** mediante Room (los datos sobreviven al cerrar la app).
- **Pantallas** con navegación inferior: Inicio (capital, ingresos, egresos, gráfica de dona),
  Transacciones (lista con filtros Todas/Ingresos/Egresos), Categorías (con porcentajes) y
  Resumen. Más la pantalla de Registrar transacción y la de bienvenida.
- **Tema oscuro dorado** e icono propio.

> La **lógica de negocio** (modelos, validaciones, balance, cálculos) vive en la librería Kotlin
> y se reutiliza. La **persistencia (Room/SQLite)** vive en la app Android, que implementa la
> interfaz de repositorio que define la librería.

El repositorio contiene **dos proyectos**:

| Carpeta | Qué es | Herramienta |
|---|---|---|
| [`clase12-gastos-core/`](clase12-gastos-core) | Librería Kotlin con la lógica de negocio (modelo, repositorio, servicio). No sabe nada de Android. | Maven |
| [`ControlGastos/`](ControlGastos) | App Android que usa la librería para la interfaz de usuario. | Gradle (Kotlin DSL) |

```
 clase12-gastos-core        ~/.m2/repository              ControlGastos (Android)
 ┌──────────────────┐  mvn  ┌──────────────────┐   lee   ┌──────────────────┐
 │ Gasto, Categoria │install│ gastos-core-     │ ◄────── │ MainActivity usa │
 │ ControlGastos    │ ────► │ 1.0.0.jar        │mavenLocal│ esas clases      │
 └──────────────────┘       └──────────────────┘         └──────────────────┘
```

> **Idea clave:** la validación y los cálculos se escriben **una sola vez** en la librería.
> La app Android (y el menú de consola) los reutilizan sin copiar ningún archivo `.kt`.

---

## Requisitos en la otra computadora

| Necesitas | Para qué | Verificar |
|---|---|---|
| **JDK 17** | Compilar la librería y la app | `java -version` |
| **IntelliJ IDEA** | Abrir/compilar la librería Kotlin | — |
| **Android Studio** (con un emulador o un celular) | Ejecutar la app Android | — |
| **Git** | Clonar el repositorio | `git --version` |

> JDK 17 es ideal: sirve para la librería **y** cumple el mínimo del Android Gradle Plugin 8.x.
> Evita JDK 25: rompe el compilador de Kotlin 2.0. Android Studio además trae su propio JDK 17.

---

## Paso 0 — Clonar el repositorio

```bash
git clone https://github.com/gmoraless1-cell/AppControlGastos.git
cd AppControlGastos
```

---

## Paso 1 — Compilar e instalar la librería en `~/.m2`  (OBLIGATORIO y PRIMERO)

La app Android **no compilará** si este paso no se hace antes, porque busca el `.jar`
de la librería en el repositorio local de Maven.

### Opción A — Con IntelliJ IDEA (sin terminal)
1. **File → Open** y selecciona la carpeta `clase12-gastos-core`.
2. Espera a que IntelliJ importe el proyecto Maven.
3. Panel **Maven** (lado derecho) → `gastos-core` → `Lifecycle` → doble clic en **`clean`** y luego en **`install`**.
4. Debe terminar con `BUILD SUCCESS`.

### Opción B — Con terminal (Maven)
Desde la raíz del repositorio clonado:

```bash
cd clase12-gastos-core
mvn clean install
```

> Si `mvn` no está instalado en la terminal, usa la Opción A (IntelliJ ya trae Maven).

### Verificar que quedó instalado
El archivo debe existir en:

| Sistema | Ruta |
|---|---|
| Windows | `%USERPROFILE%\.m2\repository\umg\edu\gt\progra2\gastos-core\1.0.0\gastos-core-1.0.0.jar` |
| macOS / Linux | `~/.m2/repository/umg/edu/gt/progra2/gastos-core/1.0.0/gastos-core-1.0.0.jar` |

### (Opcional) Probar la librería en consola
```bash
cd clase12-gastos-core
mvn compile exec:java
```
Aparece un menú de texto para registrar gastos, ver el resumen, etc.

### (Opcional) Correr las pruebas unitarias
```bash
mvn test
```
Deben pasar las 9 pruebas.

---

## Paso 2 — Ejecutar la app Android

1. Abre **Android Studio** → **Open** → selecciona la carpeta `ControlGastos`.
2. Espera a que termine el **Gradle Sync** (barra inferior). Descargará el Android SDK y
   las dependencias automáticamente la primera vez. Encontrará la librería `gastos-core`
   en `~/.m2` gracias a `mavenLocal()`.
3. Si Gradle pregunta por el JDK, elige **JDK 17** (o el JBR que trae Android Studio).
4. Selecciona un emulador (o conecta un dispositivo físico con depuración USB).
5. Pulsa **Run ▶** (Shift+F10).

### La app debe permitir:
- Escribir descripción y monto, elegir categoría y guardar.
- Mostrar el resumen: `N gastos · Total: Q ...` y la lista de gastos.
- Validar: con monto vacío marca error y **no** se cierra; sin descripción muestra un aviso
  (ese mensaje viene de la librería, no de la pantalla).

> **Nota:** los gastos se guardan en memoria. Al cerrar la app desaparecen (se resuelve con
> Room más adelante en el curso).

---

## Orden correcto (resumen)

```
1. git clone ...
2. clase12-gastos-core  ->  mvn clean install   (deja el .jar en ~/.m2)
3. ControlGastos        ->  abrir en Android Studio -> Sync -> Run
```

Si inviertes el orden (Android primero), el Sync fallará con
`Could not find umg.edu.gt.progra2:gastos-core:1.0.0`. Solución: haz el Paso 1 y vuelve a sincronizar.

---

## Estructura del repositorio

```
AppControlGastos/
├── README.md
├── clase12-gastos-core/            # Librería Kotlin (Maven)
│   ├── pom.xml
│   └── src/
│       ├── main/kotlin/edu/umg/programacion2/gastos/
│       │   ├── modelo/     (Gasto, Categoria)
│       │   ├── datos/      (GastoRepository, GastoRepositoryEnMemoria)
│       │   ├── servicio/   (ControlGastos)
│       │   └── Main.kt     (menú de consola)
│       └── test/kotlin/...  (pruebas)
└── ControlGastos/                  # App Android (Gradle)
    ├── settings.gradle.kts         # incluye mavenLocal()
    ├── app/build.gradle.kts        # dependencia a la librería + viewBinding
    └── app/src/main/
        ├── java/edu/umg/controlgastos/MainActivity.kt
        └── res/...                 (layout, strings, themes, iconos)
```

---

## Coordenadas de la librería

```
umg.edu.gt.progra2 : gastos-core : 1.0.0
```

En Android se declara así (ya está configurado en `app/build.gradle.kts`):

```kotlin
implementation("umg.edu.gt.progra2:gastos-core:1.0.0")
```

---

## Problemas comunes

| Mensaje / síntoma | Causa | Solución |
|---|---|---|
| `Could not find umg.edu.gt.progra2:gastos-core:1.0.0` | No se hizo `mvn install`, o se abrió Android antes | Hacer el **Paso 1** y volver a sincronizar |
| `Module was compiled with an incompatible version of Kotlin` | JDK demasiado nuevo (25) o Kotlin < 2.0 | Usar **JDK 17**; no cambiar la versión de Kotlin |
| La app se abre y se cierra (`Theme.AppCompat`) | Tema incorrecto | Ya está resuelto: el tema hereda de `Theme.MaterialComponents` (AppCompat) |
| Android ve código viejo de la librería | Se reinstaló la misma versión | En la librería: `mvn clean install`; en Android: **Build → Clean Project** y **Rebuild** |
| El Gradle Sync no encuentra el JDK | No configurado | En Android Studio: *Settings → Build Tools → Gradle → Gradle JDK* = 17 |

---

## Si vas a modificar la librería

1. Cambia el código en `clase12-gastos-core`.
2. (Recomendado) sube la versión en `pom.xml` (`1.0.0` → `1.1.0`) y en `app/build.gradle.kts`.
3. `mvn clean install`.
4. En Android Studio: **Sync** + **Build → Rebuild Project**.
