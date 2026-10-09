# 🏋️‍♂️ Koba's GYM - Android Workout Manager

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Gradle-8.x-02303A?style=for-the-badge&logo=gradle&logoColor=white"/>
  <img src="https://img.shields.io/badge/SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white"/>
  <img src="https://img.shields.io/badge/Material%20Design-3-757575?style=for-the-badge&logo=materialdesign&logoColor=white"/>
  <img src="https://img.shields.io/badge/License-MIT-2ea44f?style=for-the-badge"/>
</p>

---

# 🇬🇧 English Version

## 📌 Overview
**Koba's GYM** is a native Android application designed to log, manage, and master your gym workout routines easily, intuitively, and persistently. Built with Java, Material Design 3, and SQLite database storage, it ensures your training logs and routine customisations are permanently preserved.

## ✨ Key Features
- 📋 **Routine Management & Calendar Abbreviations**: Create, edit, duplicate, and delete workout routines (Tren Superior, Tren Inferior, Custom). Each routine includes custom calendar abbreviations (e.g., **TS** for Upper Body, **TI** for Lower Body).
- 🏋️ **Exercises & Extra Tags**: Add exercises to any routine, featuring a dedicated **EXTRA** badge for accessory exercises.
- ⚖️ **Set Weights in Kilograms (kg)**: Dynamically configure sets, repetitions, and individual weights per set in kg (initialized to 0.0 kg for clean tracking).
- 📅 **Main Screen Workout Calendar & Counters**: Interactive Monday-starting monthly calendar directly on the main dashboard, displaying workout abbreviations inside orange circles for completed days. Includes two separate counter boxes for **This Week** and **This Academic Course** (since Sept 1st).
- 💾 **Real-time SQLite Persistence**: Automatic local persistence using SQLite (`kobasgym.db`) alongside continuous JSON exports (`gymRoutine.json`), ensuring zero data loss upon closing the app.
- 🎨 **Sleek Spanish UI**: Built with Material Design 3, neon blue accent theme, and custom full-screen background image.

## 🛠️ Architecture & Tech Stack
- **Language**: Java 17+
- **Min SDK**: API 24 (Android 7.0) | **Target SDK**: API 34/35
- **UI Framework**: Android ViewBinding, Material Components 3
- **Database**: SQLite (`SQLiteOpenHelper`)
- **Data Interchange**: JSON

## 🚀 How to Build & Run
1. Clone or open the repository in **Android Studio**.
2. Ensure JDK 17+ and Android SDK (API 24+) are configured.
3. Sync Gradle dependencies.
4. Build the project using Gradle:
   ```bash
   ./gradlew assembleDebug
   ```
5. Deploy to an Android device or emulator.

---

# 🇪🇸 Versión en Español

## 📌 Descripción General
**Koba's GYM** es una aplicación nativa para Android diseñada para ayudarte a registrar, gestionar y dominar tus rutinas de entrenamiento en el gimnasio de forma rápida, intuitiva y persistente. Desarrollada en Java, Material Design 3 y base de datos SQLite, garantiza que tus rutinas y registros nunca se pierdan.

## ✨ Características Principales
- 📋 **Gestión de Rutinas y Abreviaturas**: Crea, edita, duplica y elimina rutinas de entrenamiento (*Tren Superior*, *Tren Inferior*, etc.). Cada rutina cuenta con su abreviatura para el calendario (**TS**, **TI**, etc.).
- 🏋️ **Ejercicios y Etiqueta EXTRA**: Añade ejercicios a cualquier rutina y marca ejercicios especiales con la etiqueta **EXTRA**.
- ⚖️ **Series y Pesos en Kilogramos (kg)**: Configura número de series, repeticiones y pesos individuales por serie en kg (inicializados en 0.0 kg para un registro limpio).
- 📅 **Calendario y Contadores en Pantalla Principal**: Calendario mensual interactivo (iniciado en lunes) ubicado directamente en la pantalla principal. Muestra las abreviaturas de los entrenamientos dentro de círculos naranjas en los días completados y cuenta los entrenamientos mediante dos cajas separadas para **Esta semana** y **Este curso** (desde el 1 de septiembre).
- 💾 **Persistencia Real en SQLite**: Almacenamiento local automático mediante base de datos SQLite (`kobasgym.db`) y exportación continua en JSON (`gymRoutine.json`), asegurando cero pérdida de datos al cerrar la app.
- 🎨 **Interfaz en Español y Estilo Neón**: Diseñada con Material Design 3, fondo neón personalizado y acentos en azul.

## 🛠️ Arquitectura y Tecnologías
- **Lenguaje**: Java 17+
- **Min SDK**: API 24 (Android 7.0) | **Target SDK**: API 34/35
- **UI & Binding**: Android ViewBinding, Material Components 3
- **Base de Datos**: SQLite (`SQLiteOpenHelper`)
- **Formato de Datos**: JSON

## 🚀 Cómo Compilar y Ejecutar
1. Abre el proyecto en **Android Studio**.
2. Verifica disponer de JDK 17+ y Android SDK (API 24+).
3. Sincroniza Gradle.
4. Compila la aplicación con el comando:
   ```bash
   ./gradlew assembleDebug
   ```
5. Ejecuta la aplicación en un dispositivo físico o emulador Android.

---

## 📁 Project Structure / Estructura del Proyecto

```
app/src/main/java/com/example/kobasgym/
├── adapter/
│   ├── CalendarGridAdapter.java    # Custom month calendar grid adapter
│   ├── ExerciseAdapter.java        # Exercise list adapter
│   ├── RoutineAdapter.java         # Routine list adapter
│   ├── VariationAdapter.java       # Variation list adapter
│   └── WorkoutLogAdapter.java      # Workout calendar log adapter
├── data/
│   ├── GymDatabaseHelper.java      # SQLite database helper
│   └── WorkoutDataManager.java     # Singleton data manager
├── model/
│   ├── CalendarDay.java            # Calendar day model
│   ├── Exercise.java               # Exercise model
│   ├── ExerciseVariation.java      # Variation model
│   ├── Routine.java                # Routine model
│   └── WorkoutLog.java             # Calendar log model
└── ui/
    ├── BaseActivity.java           # Base activity
    ├── CalendarActivity.java       # Calendar & Workout logs screen
    ├── MainOptionsActivity.java    # Main dashboard with Calendar & Counters
    ├── RoutineDetailActivity.java  # Routine detail & exercises
    ├── RoutinesListActivity.java   # Routines list
    ├── VariationDetailActivity.java# Variation & set weights editor
    ├── VariationsListActivity.java # Exercise variations list
    └── WelcomeActivity.java        # Welcome screen
```

---

## 📄 License / Licencia
Licensed under the [MIT License](LICENSE). Developed for **Koba's GYM** workout management.
