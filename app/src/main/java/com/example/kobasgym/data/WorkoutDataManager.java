package com.example.kobasgym.data;

import android.content.Context;
import android.text.TextUtils;
import com.example.kobasgym.R;
import com.example.kobasgym.model.Exercise;
import com.example.kobasgym.model.ExerciseVariation;
import com.example.kobasgym.model.Routine;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WorkoutDataManager {

    private static WorkoutDataManager instance;
    private final List<Routine> routines;
    private final GymDatabaseHelper dbHelper;
    private final Context appContext;

    private WorkoutDataManager(Context context) {
        this.appContext = context != null ? context.getApplicationContext() : null;
        this.routines = new ArrayList<>();
        this.dbHelper = appContext != null ? new GymDatabaseHelper(appContext) : null;
        loadData(appContext);
    }

    public static synchronized WorkoutDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new WorkoutDataManager(context != null ? context.getApplicationContext() : null);
        }
        return instance;
    }

    public static synchronized WorkoutDataManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("WorkoutDataManager must be initialized with context first.");
        }
        return instance;
    }

    public synchronized void resetRoutinesFromAssetJson() {
        if (appContext == null) return;
        if (dbHelper != null) {
            dbHelper.saveAllRoutines(new ArrayList<>());
        }
        routines.clear();
        loadDataFromJson(appContext);
        if (dbHelper != null && !routines.isEmpty()) {
            dbHelper.saveAllRoutines(routines);
            exportJsonFile();
        }
    }

    private void loadData(Context context) {
        if (context == null) return;

        if (dbHelper != null && !dbHelper.isDatabaseEmpty()) {
            routines.clear();
            routines.addAll(dbHelper.getAllRoutines());
            return;
        }

        // Initialize from assets if database is empty
        loadDataFromJson(context);
        if (dbHelper != null && !routines.isEmpty()) {
            dbHelper.saveAllRoutines(routines);
            exportJsonFile();
        }
    }

    private void loadDataFromJson(Context context) {
        if (context == null) return;
        try {
            InputStream is = context.getAssets().open("gymRoutine.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            int bytesRead = is.read(buffer);
            is.close();
            if (bytesRead <= 0) return;
            String jsonString = new String(buffer, StandardCharsets.UTF_8);

            JSONObject root = new JSONObject(jsonString);
            JSONArray rutinasArray = root.getJSONArray("rutinas");

            for (int i = 0; i < rutinasArray.length(); i++) {
                JSONObject rutObj = rutinasArray.getJSONObject(i);
                String id = rutObj.optString("id", UUID.randomUUID().toString());
                String nombre = rutObj.optString("nombre", "");
                String descripcion = rutObj.optString("descripcion", "");

                List<Exercise> exercises = new ArrayList<>();

                // Parse regular exercises
                JSONArray exArray = rutObj.optJSONArray("ejercicios");
                if (exArray != null) {
                    for (int j = 0; j < exArray.length(); j++) {
                        Exercise ex = parseExercise(exArray.optJSONObject(j), false);
                        if (ex != null) exercises.add(ex);
                    }
                }

                // Parse extra exercises
                JSONArray extraExArray = rutObj.optJSONArray("ejercicios_extra");
                if (extraExArray != null) {
                    for (int j = 0; j < extraExArray.length(); j++) {
                        Exercise ex = parseExercise(extraExArray.optJSONObject(j), true);
                        if (ex != null) exercises.add(ex);
                    }
                }

                String abbreviation = rutObj.optString("abreviatura", "");

                int nameResId = 0;
                int descResId = 0;
                if ("rutina_tren_superior".equals(id)) {
                    nameResId = R.string.routine_upper_body;
                    descResId = R.string.routine_desc_upper_body;
                } else if ("rutina_tren_inferior".equals(id)) {
                    nameResId = R.string.routine_lower_body;
                    descResId = R.string.routine_desc_lower_body;
                }

                Routine routine = new Routine(
                        id,
                        nombre, nameResId,
                        descripcion, descResId,
                        nombre, 0,
                        R.drawable.red_devil_mad,
                        abbreviation,
                        exercises
                );
                routines.add(routine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Exercise parseExercise(JSONObject exObj, boolean isExtra) {
        if (exObj == null) return null;
        String id = exObj.optString("id", UUID.randomUUID().toString());
        String nombre = exObj.optString("nombre", "");

        List<ExerciseVariation> variations = new ArrayList<>();
        JSONArray varArray = exObj.optJSONArray("variaciones");
        if (varArray != null) {
            for (int i = 0; i < varArray.length(); i++) {
                ExerciseVariation var = parseVariation(varArray.optJSONObject(i));
                if (var != null) variations.add(var);
            }
        }

        return new Exercise(
                id,
                nombre, 0,
                "", 0,
                R.drawable.red_devil_mad,
                variations,
                isExtra
        );
    }

    private ExerciseVariation parseVariation(JSONObject varObj) {
        if (varObj == null) return null;
        String id = varObj.optString("id", UUID.randomUUID().toString());
        String nombre = varObj.optString("nombre", "");
        String notas = varObj.optString("notas", "");

        List<Double> setWeights = new ArrayList<>();
        int repetitions = 10;
        int restSeconds = 90;

        JSONArray seriesArray = varObj.optJSONArray("series");
        if (seriesArray != null && seriesArray.length() > 0) {
            for (int i = 0; i < seriesArray.length(); i++) {
                JSONObject setObj = seriesArray.optJSONObject(i);
                if (setObj != null) {
                    double pesoKg = setObj.optDouble("peso_kg", 0.0);
                    int reps = setObj.optInt("repeticiones", 10);
                    if (i == 0) {
                        repetitions = reps;
                    }
                    setWeights.add(pesoKg);
                }
            }
        } else {
            setWeights.add(0.0);
            setWeights.add(0.0);
            setWeights.add(0.0);
        }

        int sets = setWeights.size();
        double primaryWeight = !setWeights.isEmpty() ? setWeights.get(0) : 0.0;

        ExerciseVariation variation = new ExerciseVariation(
                id,
                nombre, 0,
                sets,
                repetitions,
                primaryWeight,
                "kg",
                restSeconds,
                notas,
                0,
                R.drawable.red_devil_mad
        );
        variation.setSetWeights(setWeights);
        return variation;
    }

    public synchronized List<Routine> getRoutines() {
        return new ArrayList<>(routines);
    }

    public synchronized Routine getRoutineById(String id) {
        if (id == null) return null;
        for (Routine routine : routines) {
            if (id.equals(routine.getId())) {
                return routine;
            }
        }
        return null;
    }

    public synchronized void addRoutine(Routine routine) {
        if (routine != null) {
            if (routine.getId() == null) {
                routine.setId(UUID.randomUUID().toString());
            }
            routines.add(routine);
            if (dbHelper != null) {
                dbHelper.insertRoutine(routine, routines.size() - 1);
            }
            exportJsonFile();
        }
    }

    public synchronized void updateRoutine(Routine updated) {
        if (updated == null || updated.getId() == null) return;
        for (int i = 0; i < routines.size(); i++) {
            if (updated.getId().equals(routines.get(i).getId())) {
                routines.set(i, updated);
                if (dbHelper != null) {
                    dbHelper.updateRoutine(updated);
                }
                exportJsonFile();
                return;
            }
        }
    }

    public synchronized void deleteRoutine(String routineId) {
        if (routineId == null) return;
        for (int i = 0; i < routines.size(); i++) {
            if (routineId.equals(routines.get(i).getId())) {
                routines.remove(i);
                if (dbHelper != null) {
                    dbHelper.deleteRoutine(routineId);
                }
                exportJsonFile();
                return;
            }
        }
    }

    public synchronized Routine duplicateRoutine(Routine source) {
        if (source == null) return null;
        List<Exercise> clonedExercises = new ArrayList<>();
        if (source.getExercises() != null) {
            for (Exercise ex : source.getExercises()) {
                List<ExerciseVariation> clonedVars = new ArrayList<>();
                if (ex.getVariations() != null) {
                    for (ExerciseVariation var : ex.getVariations()) {
                        List<Double> clonedWeights = new ArrayList<>(var.getSetWeights());
                        ExerciseVariation clonedVar = new ExerciseVariation(
                                UUID.randomUUID().toString(),
                                var.getName(), var.getNameResId(),
                                var.getSets(), var.getRepetitions(),
                                var.getWeight(), var.getWeightUnit(),
                                var.getRestSeconds(), var.getNotes(),
                                var.getNotesResId(), var.getImageResource()
                        );
                        clonedVar.setSetWeights(clonedWeights);
                        clonedVars.add(clonedVar);
                    }
                }
                Exercise clonedEx = new Exercise(
                        UUID.randomUUID().toString(),
                        ex.getName(), ex.getNameResId(),
                        ex.getMuscleGroup(), ex.getMuscleGroupResId(),
                        ex.getImageResource(), clonedVars, ex.isExtra()
                );
                clonedExercises.add(clonedEx);
            }
        }
        Routine duplicated = new Routine(
                UUID.randomUUID().toString(),
                source.getName() + " (Copia)", 0,
                source.getDescription(), 0,
                source.getMuscleGroup(), 0,
                source.getImageResource(),
                clonedExercises
        );
        addRoutine(duplicated);
        return duplicated;
    }

    // Exercise operations
    public synchronized Exercise getExerciseById(String routineId, String exerciseId) {
        Routine routine = getRoutineById(routineId);
        if (routine == null || exerciseId == null) return null;
        for (Exercise exercise : routine.getExercises()) {
            if (exerciseId.equals(exercise.getId())) {
                return exercise;
            }
        }
        return null;
    }

    public synchronized void addExercise(String routineId, Exercise exercise) {
        Routine routine = getRoutineById(routineId);
        if (routine != null && exercise != null) {
            if (exercise.getId() == null) {
                exercise.setId(UUID.randomUUID().toString());
            }
            routine.getExercises().add(exercise);
            if (dbHelper != null) {
                dbHelper.insertExercise(routineId, exercise, routine.getExercises().size() - 1);
            }
            exportJsonFile();
        }
    }

    public synchronized void updateExercise(String routineId, Exercise updatedExercise) {
        Routine routine = getRoutineById(routineId);
        if (routine != null && updatedExercise != null && updatedExercise.getId() != null) {
            List<Exercise> exercises = routine.getExercises();
            for (int i = 0; i < exercises.size(); i++) {
                if (updatedExercise.getId().equals(exercises.get(i).getId())) {
                    exercises.set(i, updatedExercise);
                    if (dbHelper != null) {
                        dbHelper.updateExercise(updatedExercise);
                    }
                    exportJsonFile();
                    return;
                }
            }
        }
    }

    public synchronized void deleteExercise(String routineId, String exerciseId) {
        Routine routine = getRoutineById(routineId);
        if (routine != null && exerciseId != null) {
            List<Exercise> exercises = routine.getExercises();
            for (int i = 0; i < exercises.size(); i++) {
                if (exerciseId.equals(exercises.get(i).getId())) {
                    exercises.remove(i);
                    if (dbHelper != null) {
                        dbHelper.deleteExercise(exerciseId);
                    }
                    exportJsonFile();
                    return;
                }
            }
        }
    }

    // Variation operations
    public synchronized ExerciseVariation getVariationById(String routineId, String exerciseId, String variationId) {
        Exercise exercise = getExerciseById(routineId, exerciseId);
        if (exercise == null || variationId == null) return null;
        for (ExerciseVariation var : exercise.getVariations()) {
            if (variationId.equals(var.getId())) {
                return var;
            }
        }
        return null;
    }

    public synchronized void addVariation(String routineId, String exerciseId, ExerciseVariation variation) {
        Exercise exercise = getExerciseById(routineId, exerciseId);
        if (exercise != null && variation != null) {
            if (variation.getId() == null) {
                variation.setId(UUID.randomUUID().toString());
            }
            exercise.getVariations().add(variation);
            if (dbHelper != null) {
                dbHelper.insertVariation(exerciseId, variation, exercise.getVariations().size() - 1);
            }
            exportJsonFile();
        }
    }

    public synchronized void updateVariation(String routineId, String exerciseId, ExerciseVariation updatedVariation) {
        Exercise exercise = getExerciseById(routineId, exerciseId);
        if (exercise != null && updatedVariation != null && updatedVariation.getId() != null) {
            List<ExerciseVariation> variations = exercise.getVariations();
            for (int i = 0; i < variations.size(); i++) {
                if (updatedVariation.getId().equals(variations.get(i).getId())) {
                    variations.set(i, updatedVariation);
                    if (dbHelper != null) {
                        dbHelper.updateVariation(updatedVariation);
                    }
                    exportJsonFile();
                    return;
                }
            }
        }
    }

    public synchronized void deleteVariation(String routineId, String exerciseId, String variationId) {
        Exercise exercise = getExerciseById(routineId, exerciseId);
        if (exercise != null && variationId != null) {
            List<ExerciseVariation> variations = exercise.getVariations();
            for (int i = 0; i < variations.size(); i++) {
                if (variationId.equals(variations.get(i).getId())) {
                    variations.remove(i);
                    if (dbHelper != null) {
                        dbHelper.deleteVariation(variationId);
                    }
                    exportJsonFile();
                    return;
                }
            }
        }
    }

    public synchronized void reorderRoutines(List<Routine> reorderedList) {
        if (reorderedList == null) return;
        routines.clear();
        routines.addAll(reorderedList);
        if (dbHelper != null) {
            dbHelper.updateRoutinePositions(routines);
        }
        exportJsonFile();
    }

    public synchronized void reorderExercises(String routineId, List<Exercise> reorderedList) {
        Routine routine = getRoutineById(routineId);
        if (routine != null && reorderedList != null) {
            routine.setExercises(reorderedList);
            if (dbHelper != null) {
                dbHelper.updateExercisePositions(routineId, reorderedList);
            }
            exportJsonFile();
        }
    }

    public synchronized void reorderVariations(String routineId, String exerciseId, List<ExerciseVariation> reorderedList) {
        Exercise exercise = getExerciseById(routineId, exerciseId);
        if (exercise != null && reorderedList != null) {
            exercise.setVariations(reorderedList);
            if (dbHelper != null) {
                dbHelper.updateVariationPositions(exerciseId, reorderedList);
            }
            exportJsonFile();
        }
    }

    public synchronized Routine getUpperBodyRoutine() {
        for (Routine routine : routines) {
            if (routine.getNameResId() == R.string.routine_upper_body) {
                return routine;
            }
        }
        for (Routine routine : routines) {
            if ("Upper Body".equalsIgnoreCase(routine.getName()) || "Tren superior".equalsIgnoreCase(routine.getName())) {
                return routine;
            }
        }
        return !routines.isEmpty() ? routines.get(0) : null;
    }

    public synchronized void saveWorkoutLog(com.example.kobasgym.model.WorkoutLog log) {
        if (dbHelper != null) {
            dbHelper.saveWorkoutLog(log);
        }
    }

    public synchronized void deleteWorkoutLog(String date) {
        if (dbHelper != null) {
            dbHelper.deleteWorkoutLog(date);
        }
    }

    public synchronized com.example.kobasgym.model.WorkoutLog getWorkoutLog(String date) {
        if (dbHelper != null) {
            return dbHelper.getWorkoutLog(date);
        }
        return null;
    }

    public synchronized java.util.Map<String, com.example.kobasgym.model.WorkoutLog> getAllWorkoutLogsMap() {
        if (dbHelper != null) {
            return dbHelper.getAllWorkoutLogsMap();
        }
        return new java.util.HashMap<>();
    }

    public synchronized boolean importJsonData(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) return false;
        try {
            JSONObject rootObj = new JSONObject(jsonString);
            JSONArray rutinasArray = rootObj.optJSONArray("rutinas");
            if (rutinasArray == null) return false;

            if (dbHelper != null) {
                dbHelper.saveAllRoutines(new ArrayList<>());
            }
            routines.clear();

            for (int i = 0; i < rutinasArray.length(); i++) {
                JSONObject rutObj = rutinasArray.getJSONObject(i);
                String id = rutObj.optString("id", UUID.randomUUID().toString());
                String nombre = rutObj.optString("nombre", "");
                String descripcion = rutObj.optString("descripcion", "");
                String abbrev = rutObj.optString("abreviatura", "");

                List<Exercise> exercises = new ArrayList<>();
                JSONArray exArray = rutObj.optJSONArray("ejercicios");
                if (exArray != null) {
                    for (int j = 0; j < exArray.length(); j++) {
                        Exercise ex = parseExercise(exArray.optJSONObject(j), false);
                        if (ex != null) exercises.add(ex);
                    }
                }
                JSONArray extraExArray = rutObj.optJSONArray("ejercicios_extra");
                if (extraExArray != null) {
                    for (int j = 0; j < extraExArray.length(); j++) {
                        Exercise ex = parseExercise(extraExArray.optJSONObject(j), true);
                        if (ex != null) exercises.add(ex);
                    }
                }

                Routine routine = new Routine(
                        id,
                        nombre, 0,
                        descripcion, 0,
                        nombre, 0,
                        R.drawable.red_devil_mad,
                        abbrev,
                        exercises
                );
                routines.add(routine);
                if (dbHelper != null) {
                    dbHelper.insertRoutine(routine, i);
                }
            }
            exportJsonFile();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized String generateJsonString() {
        try {
            JSONObject root = new JSONObject();
            JSONArray rutinasArray = new JSONArray();

            for (Routine r : routines) {
                JSONObject rutObj = new JSONObject();
                rutObj.put("id", r.getId());
                rutObj.put("nombre", r.getName() != null ? r.getName() : "");
                rutObj.put("descripcion", r.getDescription() != null ? r.getDescription() : "");
                rutObj.put("abreviatura", r.getAbbreviation() != null ? r.getAbbreviation() : "");

                JSONArray ejerciciosArray = new JSONArray();
                JSONArray ejerciciosExtraArray = new JSONArray();

                if (r.getExercises() != null) {
                    for (Exercise ex : r.getExercises()) {
                        JSONObject exObj = new JSONObject();
                        exObj.put("id", ex.getId());
                        exObj.put("nombre", ex.getName() != null ? ex.getName() : "");

                        JSONArray variacionesArray = new JSONArray();
                        if (ex.getVariations() != null) {
                            for (ExerciseVariation var : ex.getVariations()) {
                                JSONObject varObj = new JSONObject();
                                varObj.put("id", var.getId());
                                varObj.put("nombre", var.getName() != null ? var.getName() : "");
                                varObj.put("notas", var.getNotes() != null ? var.getNotes() : "");

                                JSONArray seriesArray = new JSONArray();
                                List<Double> weights = var.getSetWeights();
                                if (weights != null) {
                                    for (int sIdx = 0; sIdx < weights.size(); sIdx++) {
                                        JSONObject setObj = new JSONObject();
                                        setObj.put("numero", sIdx + 1);
                                        setObj.put("peso_kg", weights.get(sIdx));
                                        setObj.put("repeticiones", var.getRepetitions());
                                        seriesArray.put(setObj);
                                    }
                                }
                                varObj.put("series", seriesArray);
                                variacionesArray.put(varObj);
                            }
                        }
                        exObj.put("variaciones", variacionesArray);

                        if (ex.isExtra()) {
                            ejerciciosExtraArray.put(exObj);
                        } else {
                            ejerciciosArray.put(exObj);
                        }
                    }
                }
                rutObj.put("ejercicios", ejerciciosArray);
                if (ejerciciosExtraArray.length() > 0) {
                    rutObj.put("ejercicios_extra", ejerciciosExtraArray);
                }
                rutinasArray.put(rutObj);
            }
            root.put("rutinas", rutinasArray);
            return root.toString(2);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void exportJsonFile() {
        if (appContext == null) return;
        new Thread(() -> {
            try {
                String jsonStr = generateJsonString();
                File outFile = new File(appContext.getFilesDir(), "gymRoutine.json");
                FileOutputStream fos = new FileOutputStream(outFile);
                fos.write(jsonStr.getBytes(StandardCharsets.UTF_8));
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}