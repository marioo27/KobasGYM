package com.example.kobasgym.model;

import android.content.Context;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Exercise implements Serializable {
    private String id;
    private String name;
    private int nameResId;
    private String muscleGroup;
    private int muscleGroupResId;
    private int imageResource;
    private List<ExerciseVariation> variations;
    private boolean isExtra;

    public Exercise() {
        this.variations = new ArrayList<>();
        this.isExtra = false;
    }

    public Exercise(String id, String name, int nameResId, String muscleGroup, int muscleGroupResId,
                    int imageResource, List<ExerciseVariation> variations) {
        this(id, name, nameResId, muscleGroup, muscleGroupResId, imageResource, variations, false);
    }

    public Exercise(String id, String name, int nameResId, String muscleGroup, int muscleGroupResId,
                    int imageResource, List<ExerciseVariation> variations, boolean isExtra) {
        this.id = id;
        this.name = name;
        this.nameResId = nameResId;
        this.muscleGroup = muscleGroup;
        this.muscleGroupResId = muscleGroupResId;
        this.imageResource = imageResource;
        this.variations = variations != null ? variations : new ArrayList<>();
        this.isExtra = isExtra;
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

    public List<ExerciseVariation> getVariations() {
        return variations;
    }

    public void setVariations(List<ExerciseVariation> variations) {
        this.variations = variations;
    }

    public boolean isExtra() {
        return isExtra;
    }

    public void setExtra(boolean extra) {
        isExtra = extra;
    }
}