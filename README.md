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
- 📋 **Routine Management & Calendar Abbreviations**: Create, edit, duplicate, and delete workout routines (Tren Superior, Tren Inferior, Custom). Each routine includes custom calendar abbreviations (e.g., **SUP** for Upper Body, **INF** for Lower Body).
- 🏋️ **Exercises & Extra Tags**: Add exercises to any routine, featuring a dedicated **EXTRA** badge for accessory exercises.
- ⚖️ **Set Weights in Kilograms (kg) or Pounds (lbs)**: Dynamically configure sets, repetitions, and individual weights per set in kg or lbs with unit toggling in settings.
- 📅 **Main Screen Workout Calendar & Counters**: Interactive Monday-starting monthly calendar directly on the main dashboard, displaying workout abbreviations inside orange circles for completed days. Includes dedicated counters for weekly and course workout days.
- 😈 **Coach Diablillo & Notifications**: Interactive mascot assistant providing humorous gym roasts and motivational system push notifications.
- ⚙️ **Settings & JSON Backup**: Settings dialog featuring weight unit toggle, routine JSON export to Downloads, routine JSON file import, and developer credits.
- 💾 **Real-time SQLite Persistence**: Automatic local persistence using SQLite (`kobasgym.db`) alongside continuous JSON exports (`gymRoutine.json`).

## 🛠️ Architecture & Tech Stack
- **Language**: Java 17+
- **Min SDK**: API 24 (Android 7.0) | **Target SDK**: API 34/35
- **UI Framework**: Android ViewBinding, Material Components 3
- **Database**: SQLite (`SQLiteOpenHelper`)
- **Data Interchange**: JSON

---

# 🇪🇸 Versión en Español

## 📌 Descripción General
**Koba's GYM** es una aplicación nativa para Android diseñada para ayudarte a registrar, gestionar y dominar tus rutinas de entrenamiento en el gimnasio de forma rápida, intuitiva y persistente. Desarrollada en Java, Material Design 3 y base de datos SQLite.

## ✨ Características Principales
- 📋 **Gestión de Rutinas y Abreviaturas**: Crea, edita, duplica y elimina rutinas de entrenamiento (*Tren Superior* -> **SUP**, *Tren Inferior* -> **INF**, etc.).
- 🏋️ **Ejercicios y Etiqueta EXTRA**: Añade ejercicios y marca elementos accesorios con la etiqueta **EXTRA**.
- ⚖️ **Series y Pesos (kg / lbs)**: Configura número de series, repeticiones y pesos individuales por serie, con opción de alternar entre kilos y libras en los ajustes.
- 📅 **Calendario y Contadores**: Calendario mensual interactivo (iniciado en lunes) en la pantalla principal con abreviaturas en círculos naranjas y contadores de días de entrenamiento.
- 😈 **Coach Diablillo y Notificaciones**: Asistente con ilustraciones de diablillos que ofrece frases motivacionales e irónicas y notificaciones push del sistema.
- ⚙️ **Ajustes y Respaldo JSON**: Menú de ajustes con exportación de rutinas a la carpeta Descargas, importación de archivos JSON actualizados y ventana de créditos.
- 💾 **Persistencia Real en SQLite**: Almacenamiento local mediante SQLite (`kobasgym.db`) y sincronización automática con `gymRoutine.json`.

---

## 📄 Licensing & Attribution / Licencias y Atribución
- **Desarrollado por**: Mario García Rodríguez.
- **Personajes e Ilustraciones**: Basados en recursos de video de Vecteezy: [Monster Stock Videos by Vecteezy](https://www.vecteezy.com/free-videos/monster), con versiones personalizadas para la app.

---
*(Desarrollado para la gestión integral de entrenamientos en Koba's GYM)*
