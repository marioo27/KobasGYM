# 🏋️‍♂️ Koba's GYM - Aplicación de Rutinas de Gimnasio

**Koba's GYM** es una aplicación nativa para Android diseñada para ayudarte a registrar, gestionar y dominar tus rutinas de entrenamiento en el gimnasio de forma rápida, intuitiva y persistente.

---

## ✨ Características Principales

- 📋 **Gestión Completa de Rutinas**:
  - Visualiza, crea, edita, duplica y elimina rutinas personalizadas (Tren Superior, Tren Inferior, etc.).
  - Las opciones de edición y eliminación se gestionan dentro de las pantallas de edición para evitar eliminaciones accidentales.

- 🏋️ **Ejercicios y Variaciones**:
  - Agrega ejercicios a cualquier rutina y marca ejercicios especiales con la etiqueta **EXTRA**.
  - Configura múltiples variaciones por ejercicio (máquina, polea, mancuernas, etc.).

- ⚖️ **Gestión de Series y Pesos en Kilogramos (kg)**:
  - Edita el número de series, repeticiones y asigna pesos individuales en **kg** por cada serie.
  - Los pesos nuevos se inicializan por defecto en `0.0 kg` para facilitar tu registro diario.

- 📅 **Calendario de Entrenamientos**:
  - Selecciona cualquier día del mes para registrar si entrenaste y qué rutina realizaste.
  - Añade notas opcionales por día y consulta las estadísticas mensuales de días entrenados.

- 💾 **Persistencia de Datos en SQLite y JSON**:
  - Almacenamiento persistente automático mediante una base de datos local **SQLite** (`kobasgym.db`).
  - Todos los cambios realizados (creación, edición, pesos o calendario) se conservan permanentemente aunque cierres la aplicación.
  - Exportación automática en tiempo real de tus datos a un archivo `gymRoutine.json` en el almacenamiento interno del dispositivo.

- 🎨 **Interfaz 100% en Español y Diseño Moderno**:
  - Estética basada en Material Design 3 con fondo personalizado y acentos en neón azul.

---

## 🛠️ Tecnologías y Arquitectura

- **Lenguaje**: Java 17+
- **Min SDK**: API 24 (Android 7.0) | **Target SDK**: API 34/35
- **UI & Binding**: Android ViewBinding, Material Components
- **Base de Datos**: SQLite (`SQLiteOpenHelper`)
- **Estructura de Datos**: JSON (`assets/gymRoutine.json`)

---

## 📁 Estructura del Código Source

```
app/src/main/java/com/example/kobasgym/
├── adapter/
│   ├── ExerciseAdapter.java        # Adaptador para lista de ejercicios
│   ├── RoutineAdapter.java         # Adaptador para lista de rutinas
│   └── VariationAdapter.java       # Adaptador para lista de variaciones
├── data/
│   ├── GymDatabaseHelper.java      # Gestor de base de datos SQLite
│   └── WorkoutDataManager.java     # Singleton de gestión y persistencia de datos
├── model/
│   ├── Exercise.java               # Modelo de Ejercicio
│   ├── ExerciseVariation.java      # Modelo de Variación (Series y Pesos)
│   ├── Routine.java                # Modelo de Rutina
│   └── WorkoutLog.java             # Modelo de Registro de Calendario
└── ui/
    ├── BaseActivity.java           # Actividad base
    ├── CalendarActivity.java       # Pantalla de Calendario de Entrenamientos
    ├── MainOptionsActivity.java    # Opciones Principales
    ├── RoutineDetailActivity.java  # Detalle y Edición de Rutina / Ejercicios
    ├── RoutinesListActivity.java   # Lista General de Rutinas
    ├── VariationDetailActivity.java# Edición de Variación, Series y Pesos
    ├── VariationsListActivity.java # Lista de Variaciones de un Ejercicio
    └── WelcomeActivity.java        # Pantalla de Bienvenida
```

---

## 🚀 Cómo Compilar y Ejecutar

1. Clona o abre el proyecto en **Android Studio**.
2. Asegúrate de tener instalado el SDK de Android (API 24 o superior) y JDK 17+.
3. Sincroniza Gradle (`Gradle Sync`).
4. Compila el proyecto ejecutando la tarea Gradle:
   ```bash
   ./gradlew assembleDebug
   ```
5. Ejecuta la aplicación en un emulador o dispositivo físico Android conectado mediante USB/ADB.

---

## 📄 Licencia

Desarrollado para la gestión personal de entrenamientos con **Koba's GYM**.
