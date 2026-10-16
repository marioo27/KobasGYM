package com.example.monstergym.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.example.monstergym.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DevilImagePickerHelper {

    public interface OnImageSelectedListener {
        void onImageSelected(int drawableResId);
    }

    public static List<Integer> getDevilImageResources() {
        List<Integer> devilImages = new ArrayList<>();
        try {
            Field[] fields = R.drawable.class.getDeclaredFields();
            for (Field field : fields) {
                String name = field.getName().toLowerCase();
                if (name.contains("devil") || name.contains("diablillo")) {
                    int resId = field.getInt(null);
                    if (resId != 0 && !devilImages.contains(resId)) {
                        devilImages.add(resId);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Fallback in case reflection yields no results
        if (devilImages.isEmpty()) {
            devilImages.add(R.drawable.red_devil);
            devilImages.add(R.drawable.red_devil_mad);
            devilImages.add(R.drawable.blue_devil);
            devilImages.add(R.drawable.blue_devil_water);
            devilImages.add(R.drawable.purple_devil);
            devilImages.add(R.drawable.purple_devil_see_routines);
            devilImages.add(R.drawable.purple_devil_create_routine);
            devilImages.add(R.drawable.yellow_devil);
            devilImages.add(R.drawable.yellow_devil_bar);
            devilImages.add(R.drawable.yellow_devil_muscle);
            devilImages.add(R.drawable.yellow_devil_dumbell);
            devilImages.add(R.drawable.yellow_devil_dumbell_jumping);
        }

        return devilImages;
    }

    public static void showPicker(Context context, OnImageSelectedListener listener) {
        if (context == null || listener == null) return;

        int dp8 = dpToPx(context, 8);
        int dp16 = dpToPx(context, 16);
        int dp64 = dpToPx(context, 64);

        ScrollView scrollView = new ScrollView(context);
        scrollView.setPadding(dp16, dp16, dp16, dp16);

        GridLayout gridLayout = new GridLayout(context);
        gridLayout.setColumnCount(3);
        gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);

        AlertDialog[] dialogHolder = new AlertDialog[1];
        List<Integer> devilImages = getDevilImageResources();

        for (int resId : devilImages) {
            CardView card = new CardView(context);
            card.setRadius(dpToPx(context, 16));
            card.setCardElevation(dpToPx(context, 4));
            card.setCardBackgroundColor(Color.parseColor("#1E293B"));
            card.setUseCompatPadding(true);

            ImageView iv = new ImageView(context);
            iv.setLayoutParams(new ViewGroup.LayoutParams(dp64, dp64));
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iv.setImageResource(resId);
            iv.setPadding(dp8, dp8, dp8, dp8);

            card.addView(iv);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
            params.setMargins(dp8, dp8, dp8, dp8);
            card.setLayoutParams(params);

            card.setOnClickListener(v -> {
                listener.onImageSelected(resId);
                if (dialogHolder[0] != null) {
                    dialogHolder[0].dismiss();
                }
            });

            gridLayout.addView(card);
        }

        scrollView.addView(gridLayout);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.select_devil_image_title)
                .setView(scrollView)
                .setNegativeButton(R.string.btn_cancel, (d, which) -> d.dismiss())
                .create();

        dialogHolder[0] = dialog;
        dialog.show();
    }

    private static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
