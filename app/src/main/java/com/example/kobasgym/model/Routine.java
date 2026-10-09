package com.example.kobasgym.model;

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
    private List<Exercise> exercises;

    public Routine() {
        this.exercises = new ArrayList<>();
    }

    public Routine(String id, String name, int nameResId, String description, int descriptionResId,
                   String muscleGroup, int muscleGroupResId, int imageResource, List<Exercise> exercises) {
        this.id = id;
        this.name = name;
        this.nameResId = nameResId;
        this.description = description;
        this.descriptionResId = descriptionResId;
        this.muscleGroup = muscleGroup;
        this.muscleGroupResId = muscleGroupResId;
        this.imageResource = imageResource;
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
        if (nameResId != 0 && context != null) {
            try {
                return context.getString(nameResId);
            } catch (Exception e) {
                // fallback
            }
        }
        return name != null ? name : "";
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
        if (descriptionResId != 0 && context != null) {
            try {
                return context.getString(descriptionResId);
            } catch (Exception e) {
                // fallback
            }
        }
        return description != null ? description : "";
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

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }
}