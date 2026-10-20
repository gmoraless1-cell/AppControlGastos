# Clase 12 — Guía: conectar la librería Kotlin con un proyecto Android

**Programación 2 · Universidad Mariano Gálvez de Guatemala**
**Catedrático:** Walter Cordova
**Proyecto Kotlin:** `clase12-gastos-core` (en `eclipse-workspace-umg/Programacion2`)
**Relacionado con:** `Clase 11/Clase11_Guia_Instalacion_Proyecto_Kotlin.md` (JDK, IntelliJ, Maven)

---

## La idea en un dibujo

```
 1. Proyecto Kotlin (IntelliJ + Maven)      2. Repositorio local          3. Proyecto Android (Android Studio + Gradle)
 ┌────────────────────────────┐            ┌──────────────────────┐       ┌────────────────────────────────┐
 │ modelo/  Gasto, Categoria  │ mvn install│ ~/.m2/repository/    │ lee   │ settings.gradle.kts            │
 │ datos/   GastoRepository   │ ─────────► │  umg/edu/gt/progra2/ │ ◄──── │   repositories { mavenLocal() }│
 │ servicio/ControlGastos     │            │   gastos-core-1.0.0  │       │ app/build.gradle.kts           │
 └────────────────────────────┘            │     .jar             │       │   implementation("...:1.0.0")  │
                                           └──────────────────────┘       └────────────────────────────────┘
```

- **Librería:** un `.jar` con clases compiladas que otro proyecto usa sin copiar el código.
- **`~/.m2/repository`:** la carpeta donde Maven guarda **todas** las librerías que descarga
  (ahí ya está el driver de MySQL de la Clase 5). `mvn install` pone **nuestra** librería ahí.
- **Gradle:** la herramienta de construcción de Android. Cumple el mismo papel que Maven.
- **`mavenLocal()`:** le dice a Gradle "busca también en la carpeta `~/.m2` de esta computadora".

> **IMPORTANTE:** `mavenLocal()` solo existe en **tu** computadora. Si un compañero clona el
> proyecto Android, debe hacer también el Paso 1 en su máquina (o usar el Plan B del Paso 9).

---

## Paso 0 — Requisitos

| Necesitas | Cómo verificar |
|---|---|
| JDK 11+ | `java -version` |
| Maven (terminal) o IntelliJ | `mvn -v` (o usar el panel *Maven* de IntelliJ) |
| Android Studio reciente | Proyecto vacío corriendo en el emulador (tarea de la Clase 11) |
| Kotlin **2.0 o superior** en Android | `gradle/libs.versions.toml` → línea `kotlin = "..."` |

---

## Paso 1 — Instalar la librería en `~/.m2`

**Terminal**, desde la carpeta que tiene el `pom.xml`:

```bash
cd clase12-gastos-core
mvn compile exec:java     # opcional: probar el menú de consola
mvn clean install         # compila, empaqueta el .jar y lo copia a ~/.m2
```

**IntelliJ (sin terminal):** panel **Maven** (derecha) → `gastos-core` → `Lifecycle` → doble clic en
`clean` y luego en `install`.

Debe terminar con `BUILD SUCCESS` y existir el archivo:

| Sistema | Ruta |
|---|---|
| macOS / Linux | `~/.m2/repository/umg/edu/gt/progra2/gastos-core/1.0.0/gastos-core-1.0.0.jar` |
| Windows | `%USERPROFILE%\.m2\repository\umg\edu\gt\progra2\gastos-core\1.0.0\gastos-core-1.0.0.jar` |

> **Cuidado:** anota las **coordenadas** del `pom.xml`; en Android se escriben igual, letra por letra:
> `groupId:artifactId:version` → `umg.edu.gt.progra2:gastos-core:1.0.0`

---

## Paso 2 — Crear el proyecto Android

1. Android Studio → **New Project** → **Empty Views Activity** (no *Empty Activity*, esa es Compose).
2. Name: `ControlGastos` · Package: `edu.umg.controlgastos` · Language: **Kotlin**.
3. **Minimum SDK: API 24** · Build configuration language: **Kotlin DSL (build.gradle.kts)**.
4. **Finish** y esperar a que termine el *Gradle Sync* (barra inferior).

---

## Paso 3 — Agregar `mavenLocal()` en `settings.gradle.kts`

Archivo en la **raíz** del proyecto (no el de `app/`). Agregar `mavenLocal()` dentro de
`dependencyResolutionManagement`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()   // 👈 NUEVO: busca también en ~/.m2 (donde quedó gastos-core)
    }
}
```

> **Cuidado:** va en `dependencyResolutionManagement`, **no** en `pluginManagement` (ese bloque es
> para plugins de Gradle). Y va al **final**: así las librerías oficiales se siguen buscando primero
> en Google y Maven Central.

---

## Paso 4 — Declarar la dependencia en `app/build.gradle.kts`

```kotlin
android {
    // ... lo que ya trae la plantilla ...
    buildFeatures {
        viewBinding = true   // 👈 conecta el XML con Kotlin sin findViewById (ver diapositivas)
    }
}

dependencies {
    implementation("umg.edu.gt.progra2:gastos-core:1.0.0")        // 👈 NUESTRA librería
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7") // 👈 para lifecycleScope
    // ... las dependencias que ya trae la plantilla ...
}
```

<details>
<summary>Variante con <code>libs.versions.toml</code> (catálogo de versiones)</summary>

La plantilla nueva usa `gradle/libs.versions.toml`. Si prefieres seguir ese estilo:

```toml
[versions]
gastosCore = "1.0.0"
lifecycleRuntime = "2.8.7"

[libraries]
gastos-core = { group = "umg.edu.gt.progra2", name = "gastos-core", version.ref = "gastosCore" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntime" }
```

```kotlin
implementation(libs.gastos.core)                    // el guion se convierte en punto
implementation(libs.androidx.lifecycle.runtime.ktx)
```
</details>

Luego: **File → Sync Project with Gradle Files** (o el botón del elefante 🐘).

---

## Paso 5 — Verificar que Android "ve" la librería

1. Vista **Project** (no *Android*) → **External Libraries** → debe aparecer
   `Gradle: umg.edu.gt.progra2:gastos-core:1.0.0`.
2. En `MainActivity.kt` escribe `Gasto` y presiona **Alt+Enter** (Windows) / **Option+Enter** (Mac):
   debe ofrecer `import edu.umg.programacion2.gastos.modelo.Gasto`.
3. **Ctrl/Cmd + clic** sobre `Gasto` abre el código **decompilado**: confirma que viene del `.jar`.

---

## Paso 6 — Diseñar la pantalla (`res/layout/activity_main.xml`)

Formulario mínimo del Incremento 2. Los `id` son los que usará View Binding
(`etDescripcion` → `binding.etDescripcion`).

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="24dp">

    <EditText
        android:id="@+id/etDescripcion"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Descripción"
        android:inputType="text" />

    <EditText
        android:id="@+id/etMonto"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Monto (Q)"
        android:inputType="numberDecimal" />

    <Spinner
        android:id="@+id/spCategoria"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp" />

    <Button
        android:id="@+id/btnGuardar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="Guardar" />

    <TextView
        android:id="@+id/tvResumen"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:textSize="18sp" />
</LinearLayout>
```

> **Cuidado:** conserva `android:id="@+id/main"` en la raíz; el código de la plantilla
> (`enableEdgeToEdge` / `ViewCompat.setOnApplyWindowInsetsListener`) lo busca por ese id.

---

## Paso 7 — Usar la librería en `MainActivity.kt`

```kotlin
package edu.umg.controlgastos

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import edu.umg.controlgastos.databinding.ActivityMainBinding   // generado por View Binding
import edu.umg.programacion2.gastos.datos.GastoRepositoryEnMemoria  // 👇 todo esto viene del .jar
import edu.umg.programacion2.gastos.modelo.Categoria
import edu.umg.programacion2.gastos.modelo.Gasto
import edu.umg.programacion2.gastos.servicio.ControlGastos
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // lateinit = "la inicializo después, en onCreate". Sin esto Kotlin exige un valor ya.
    private lateinit var binding: ActivityMainBinding

    // IMPORTANTE: la MISMA línea que en el Main.kt de consola. La lógica no cambió.
    private val control = ControlGastos(GastoRepositoryEnMemoria())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Inflar el XML con View Binding y mostrarlo.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. El enum de la librería alimenta el Spinner (muestra el toString(): "Alimentación").
        binding.spCategoria.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, Categoria.entries
        )

        // 3. Evento clic: el bloque { } se ejecuta DESPUÉS, cuando el usuario toca el botón.
        binding.btnGuardar.setOnClickListener { guardar() }

        // 4. resumen() es suspend -> se llama dentro de lifecycleScope.launch (receta).
        lifecycleScope.launch { actualizarResumen() }
    }

    private fun guardar() {
        // ❌ Error común: .toDouble() cierra la app si el campo está vacío.
        // ✅ toDoubleOrNull() + early return (null safety de la Clase 11).
        val monto = binding.etMonto.text.toString().toDoubleOrNull()
        if (monto == null) {
            binding.etMonto.error = "Ingresa un monto válido"
            return
        }

        val gasto = Gasto(
            descripcion = binding.etDescripcion.text.toString().trim(),
            monto = monto,
            categoria = binding.spCategoria.selectedItem as Categoria,
            fecha = fechaDeHoy()
        )

        lifecycleScope.launch {
            // 5. La validación vive en la librería, no en la pantalla.
            val error = control.registrar(gasto)
            if (error != null) {
                Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                return@launch   // return dentro de launch: sale solo del bloque
            }
            binding.etDescripcion.text.clear()
            binding.etMonto.text.clear()
            actualizarResumen()
        }
    }

    private suspend fun actualizarResumen() {
        binding.tvResumen.text = control.resumen()   // "2 gastos · Total: Q 55.50"
    }

    // Cuidado: java.time.LocalDate necesita API 26; con minSdk 24 usamos SimpleDateFormat.
    private fun fechaDeHoy(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}
```

> Si la plantilla trae el bloque `enableEdgeToEdge()` + `ViewCompat.setOnApplyWindowInsetsListener(...)`,
> déjalo después de `setContentView(binding.root)`.

---

## Paso 8 — Ejecutar y probar

| Prueba | Resultado esperado |
|---|---|
| Guardar con monto vacío | Error en el campo monto, la app **no** se cierra |
| Guardar sin descripción | Toast "La descripción no puede estar vacía" (viene de la librería) |
| Guardar "Almuerzo", 45.50, Alimentación | Formulario limpio y `1 gasto · Total: Q 45.50` |
| Cerrar la app y abrirla | Los gastos desaparecen (memoria). Se resuelve con Room en la semana 4 |

---

## Paso 9 — Cambiar la librería y trabajar en equipo

### Si modifico el proyecto Kotlin

1. Cambiar el código en `clase12-gastos-core`.
2. **Recomendado:** subir la versión en el `pom.xml` (`1.0.0` → `1.1.0`) y en `app/build.gradle.kts`.
3. `mvn clean install` → en Android Studio: **Sync** + **Build → Rebuild Project**.

> **Cuidado:** si reinstalas **la misma** versión y Android sigue viendo el código viejo, haz
> **Build → Clean Project** y luego **Rebuild**. Subir la versión evita esta confusión.

### Plan B para el equipo: el `.jar` dentro del proyecto Android

Útil si un compañero no tiene Maven o no quiere clonar el proyecto Kotlin:

1. Copiar `clase12-gastos-core/target/gastos-core-1.0.0.jar` a `app/libs/`.
2. En `app/build.gradle.kts` cambiar la línea de la dependencia por:
   ```kotlin
   implementation(files("libs/gastos-core-1.0.0.jar"))
   ```
3. Hacer commit del `.jar` (aquí sí es intencional: es una dependencia, no un build output del proyecto Android).

| Opción | Ventaja | Desventaja |
|---|---|---|
| `mavenLocal()` | Igual que una librería "de verdad"; versiones claras | Cada integrante debe hacer `mvn install` |
| `.jar` en `app/libs` | Clonar y ejecutar, sin Maven | Hay que copiar el `.jar` a mano en cada cambio |
| Publicar (GitHub Packages / JitPack) | Nadie instala nada localmente | Requiere configuración extra (fuera del alcance) |

---

## Problemas comunes

| Mensaje / síntoma | Causa | Solución |
|---|---|---|
| `Could not find umg.edu.gt.progra2:gastos-core:1.0.0` | No se hizo `mvn install`, falta `mavenLocal()` o hay un error de dedo en las coordenadas | Revisar el Paso 1 (el `.jar` existe) y el Paso 3; comparar con el `pom.xml` |
| `Build was configured to prefer settings repositories over project repositories...` | Se puso `mavenLocal()` en `app/build.gradle.kts` | Moverlo a `settings.gradle.kts` (Paso 3) |
| `Module was compiled with an incompatible version of Kotlin...` | Kotlin del proyecto Android es 1.9 o menor | Subir `kotlin` en `libs.versions.toml` a 2.0+ (o bajar `kotlin.version` del `pom.xml`) |
| `Suspend function 'registrar' should be called only from a coroutine...` | Se llamó una función `suspend` fuera de `lifecycleScope.launch` | Envolver la llamada en `lifecycleScope.launch { ... }` |
| `Unresolved reference: lifecycleScope` | Falta la dependencia de lifecycle | Agregar `lifecycle-runtime-ktx` (Paso 4) |
| `Unresolved reference: ActivityMainBinding` | View Binding no está activado | `buildFeatures { viewBinding = true }` + Sync |
| El Spinner muestra `ALIMENTACION` | Librería vieja sin el `toString()` | Reinstalar la librería (Paso 9) |
| Android ve el código viejo de la librería | Se reinstaló la misma versión | Clean + Rebuild, o subir la versión |
| La app se abre y se cierra · Logcat: `You need to use a Theme.AppCompat theme (or descendant) with this activity` | Se creó el proyecto con *Empty Activity* (Compose): su tema no es AppCompat y `MainActivity` hereda de `AppCompatActivity` | En `res/values/themes.xml` usar `parent="Theme.AppCompat.DayNight.NoActionBar"`, o recrear el proyecto con *Empty Views Activity* (Paso 2) |

---

## Lista de verificación

- [ ] `mvn clean install` termina en `BUILD SUCCESS` y el `.jar` está en `~/.m2`.
- [ ] `mavenLocal()` está en `settings.gradle.kts`.
- [ ] `gastos-core` aparece en *External Libraries*.
- [ ] `MainActivity` importa `ControlGastos` **sin** haber copiado ningún archivo `.kt`.
- [ ] Guardar con datos inválidos muestra el error y la app no se cierra.
- [ ] El resumen muestra `N gastos · Total: Q ...`.
