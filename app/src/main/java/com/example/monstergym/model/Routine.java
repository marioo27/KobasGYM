package com.example.monstergym.model;

import android.content.Context;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Routine implements Serializable {
    private String id;
    private String name;
    private int nameResId;
    private String description;
    private int descriptionResId;
    private String muscleGroup;
    private int muscleGroupResId;
    private int imageResource;
    private String abbreviation;
    private List<Exercise> exercises;

    public Routine() {
        this.exercises = new ArrayList<>();
    }

    public Routine(String id, String name, int nameResId, String description, int descriptionResId,
                   String muscleGroup, int muscleGroupResId, int imageResource, List<Exercise> exercises) {
        this(id, name, nameResId, description, descriptionResId, muscleGroup, muscleGroupResId, imageResource, null, exercises);
    }

    public Routine(String id, String name, int nameResId, String description, int descriptionResId,
                   String muscleGroup, int muscleGroupResId, int imageResource, String abbreviation, List<Exercise> exercises) {
        this.id = id;
        this.name = name;
        this.nameResId = nameResId;
        this.description = description;
        this.descriptionResId = descriptionResId;
        this.muscleGroup = muscleGroup;
        this.muscleGroupResId = muscleGroupResId;
        this.imageResource = imageResource;
        this.abbreviation = abbreviation;
        this.exercises = exercises != null ? exercises : new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNameResId() {
        return nameResId;
    }

    public void setNameResId(int nameResId) {
        this.nameResId = nameResId;
    }

    public String getDisplayName(Context context) {
        if (name != null && !name.trim().isEmpty()) {
            return name;
        }
        if (nameResId != 0 && context != null) {
            try {
                return context.getString(nameResId);
            } catch (Exception e) {
                // fallback
            }
        }
        return "";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDescriptionResId() {
        return descriptionResId;
    }

    public void setDescriptionResId(int descriptionResId) {
        this.descriptionResId = descriptionResId;
    }

    public String getDisplayDescription(Context context) {
        if (description != null && !description.trim().isEmpty()) {
            return description;
        }
        if (descriptionResId != 0 && context != null) {
            try {
                return context.getString(descriptionResId);
            } catch (Exception e) {
                // fallback
            }
        }
        return "";
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public int getMuscleGroupResId() {
        return muscleGroupResId;
    }

    public void setMuscleGroupResId(int muscleGroupResId) {
        this.muscleGroupResId = muscleGroupResId;
    }

    public String getDisplayMuscleGroup(Context context) {
        if (muscleGroupResId != 0 && context != null) {
            try {
                return context.getString(muscleGroupResId);
            } catch (Exception e) {
                // fallback
            }
        }
        return muscleGroup != null ? muscleGroup : "";
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getAbbreviation() {
        if (abbreviation != null && !abbreviation.trim().isEmpty()) {
            return abbreviation;
        }
        if ("Tren Superior".equalsIgnoreCase(name) || "rutina_tren_superior".equals(id)) return "TS";
        if ("Tren Inferior".equalsIgnoreCase(name) || "rutina_tren_inferior".equals(id)) return "TI";
        if (name == null || name.trim().isEmpty()) return "EX";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty()) {
                sb.append(Character.toUpperCase(p.charAt(0)));
                if (sb.length() >= 3) break;
            }
        }
        return sb.length() > 0 ? sb.toString() : "EX";
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }
}