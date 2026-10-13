# 🏋️‍♂️ Monster GYM - Android Workout Manager

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Gradle-8.x-02303A?style=for-the-badge&logo=gradle&logoColor=white"/>
  <img src="https://img.shields.io/badge/SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white"/>
  <img src="https://img.shields.io/badge/Material%20Design-3-757575?style=for-the-badge&logo=materialdesign&logoColor=white"/>
  <img src="https://img.shields.io/badge/License-MIT-2ea44f?style=for-the-badge"/>
</p>

<p align="center">
  <a href="#-english-version">English Version</a> | <a href="#-versión-en-español">Versión en Español</a>
</p>

---

# 🇬🇧 English Version

## 📌 Overview
**Koba's GYM** is a native Android application designed to log, manage, and master your gym workout routines easily, intuitively, and persistently. Built with Java, Material Design 3, and SQLite database storage, it ensures your training logs and routine customisations are permanently preserved.

## ✨ Key Features
- 📋 **Routine Management & Calendar Abbreviations**: Create, edit, duplicate, and delete workout routines. Each routine includes custom calendar abbreviations (e.g., **SUP** for Upper Body, **INF** for Lower Body).
- 🔄 **Drag & Drop Reordering**: Seamlessly reorder routines, exercises, and exercise variations using touch drag handles powered by `ItemTouchHelper`.
- 😈 **Dynamic Devil Image Selector**: Customize routine, exercise, and variation avatars by choosing from all available mascot illustrations. New devil images added to the project are automatically discovered at runtime via reflection.
- 🏋️ **Exercises & Extra Tags**: Add exercises to any routine, featuring a dedicated **EXTRA** badge for accessory exercises.
- ⚖️ **Set Weights in Kilograms (kg) or Pounds (lbs)**: Dynamically configure sets, repetitions, and individual weights per set in kg or lbs.
- 📅 **Main Screen Workout Calendar & Counters**: Interactive Monday-starting monthly calendar directly on the dashboard, displaying workout abbreviations inside orange circles for completed days.
- ⚙️ **Settings & JSON Backup**: Settings dialog featuring weight unit toggle, routine JSON export to Downloads, routine JSON file import, and developer credits.
- 💾 **Real-time SQLite Persistence**: Automatic local persistence using SQLite (`kobasgym.db`) alongside continuous JSON exports (`gymRoutine.json`).

## 📁 App Structure
```text
com.example.kobasgym/
├── adapter/     # RecyclerView Adapters (Routine, Exercise, Variation, Calendar, WorkoutLog)
├── data/        # SQLite Database Helper & Workout Data Manager (JSON & SQLite)
├── model/       # Data classes (Routine, Exercise, ExerciseVariation, WorkoutLog, CalendarDay)
├── ui/          # Activities (MainOptions, RoutinesList, RoutineDetail, VariationsList, VariationDetail, Calendar, Welcome)
└── utils/       # Helpers (DevilImagePickerHelper, DiablilloNotificationHelper, LocaleManager)
```

## 🛠️ Prerequisites & Tech Stack
- **Prerequisites**: Android Studio (Koala/Jellyfish+), Android SDK (Min SDK 24, Target SDK 34+), Java 17 JDK, Physical Device or Emulator (API 24+).
- **Language**: Java 17+
- **UI Framework**: Android ViewBinding, Material Components 3
- **Database**: SQLite (`SQLiteOpenHelper`)
- **Data Interchange**: JSON

---

# 🇪🇸 Versión en Español

## 📌 Descripción General
**Koba's GYM** es una aplicación nativa para Android diseñada para ayudarte a registrar, gestionar y dominar tus entrenamientos en el gimnasio de forma rápida, intuitiva y persistente. Desarrollada en Java, Material Design 3 y SQLite.

## ✨ Características Principales
- 📋 **Gestión de Rutinas y Abreviaturas**: Crea, edita, duplica y elimina rutinas de entrenamiento con abreviaturas personalizadas para el calendario.
- 🔄 **Reordenamiento por Arrastre (Drag & Drop)**: Reorganiza tus rutinas, ejercicios y variaciones de forma fluida mediante tiradores de arrastre táctiles.
- 😈 **Selector Dinámico de Diablillos**: Personaliza los avatares de tus rutinas, ejercicios y variaciones eligiendo entre todas las ilustraciones de diablillos disponibles. Las nuevas imágenes añadidas al proyecto se detectan automáticamente.
- 🏋️ **Ejercicios y Etiqueta EXTRA**: Añade ejercicios y marca elementos accesorios con la etiqueta **EXTRA**.
- ⚖️ **Series y Pesos (kg / lbs)**: Configura número de series, repeticiones y pesos individuales por serie.
- 📅 **Calendario y Contadores**: Calendario mensual interactivo en la pantalla principal con abreviaturas en círculos naranjas y contadores de días de entrenamiento.
- ⚙️ **Ajustes y Respaldo JSON**: Menú de ajustes con exportación e importación de archivos JSON de rutinas.
- 💾 **Persistencia Real en SQLite**: Almacenamiento local mediante SQLite (`kobasgym.db`) y sincronización con `gymRoutine.json`.

## 📁 Estructura de la Aplicación
```text
com.example.kobasgym/
├── adapter/     # Adaptadores de RecyclerView (Rutinas, Ejercicios, Variaciones, Calendario, Logs)
├── data/        # Administrador de datos y SQLite (GymDatabaseHelper, WorkoutDataManager)
├── model/       # Modelos de datos (Routine, Exercise, ExerciseVariation, WorkoutLog, CalendarDay)
├── ui/          # Actividades (MainOptions, RoutinesList, RoutineDetail, VariationsList, VariationDetail, Calendar, Welcome)
└── utils/       # Utilidades (DevilImagePickerHelper, DiablilloNotificationHelper, LocaleManager)
```

## 🛠️ Requisitos y Tecnologías
- **Qué se necesita**: Android Studio, Android SDK (Mínimo API 24, Destino API 34+), JDK 17, Dispositivo físico o Emulador Android.
- **Lenguaje**: Java 17+
- **Interfaz de Usuario**: Android ViewBinding, Material Components 3
- **Base de Datos**: SQLite (`SQLiteOpenHelper`)
- **Intercambio de Datos**: JSON

---

## 📄 Licensing & Attribution / Licencias y Atribución
- **License / Licencia**: Este proyecto se distribuye bajo la **Licencia MIT** (*MIT License*).
- **Desarrollado por**: Mario García Rodríguez.
- **Personajes e Ilustraciones**: Basados en recursos de video de Vecteezy: [Monster Stock Videos by Vecteezy](https://www.vecteezy.com/free-videos/monster), con versiones personalizadas para la app.

---
*(Desarrollado para la gestión integral de entrenamientos en Koba's GYM)*
