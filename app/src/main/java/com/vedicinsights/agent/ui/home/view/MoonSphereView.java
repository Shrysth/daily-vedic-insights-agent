package com.vedicinsights.agent.ui.home.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MoonSphereView extends View {
    private Paint paint;
    private double illumination = 0.0;
    private String paksha = "Shukla";

    public MoonSphereView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setMoonData(double illumination, String paksha) {
        this.illumination = illumination;
        this.paksha = paksha;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        float radius = Math.min(cx, cy) - 10;

        // Draw base cosmic background (dark sphere)
        paint.setColor(Color.parseColor("#13182E"));
        canvas.drawCircle(cx, cy, radius, paint);

        // Draw illuminated portion based on percentage and Paksha (waxing/waning)
        paint.setColor(Color.parseColor("#F5A623"));
        canvas.drawArc(cx - radius, cy - radius, cx + radius, cy + radius, 
                       paksha.equals("Shukla") ? 270f : 90f, 
                       (float) (180 + (180 * (illumination - 0.5) * 2)), 
                       false, paint);
    }
}