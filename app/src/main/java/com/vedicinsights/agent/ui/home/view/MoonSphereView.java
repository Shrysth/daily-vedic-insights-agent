package com.vedicinsights.agent.ui.home.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.vedicinsights.agent.R;

public class MoonSphereView extends View {
    private final Paint spherePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint lightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private double illumination;
    private String paksha = "Shukla";

    public MoonSphereView(Context context, AttributeSet attrs) {
        super(context, attrs);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(1.5f);
        outlinePaint.setColor(ContextCompat.getColor(context, R.color.celestial_gold_light));
        outlinePaint.setAlpha(110);
    }

    public void setMoonData(double illumination, String paksha) {
        if (!Double.isFinite(illumination)) {
            throw new IllegalArgumentException("Moon illumination must be a finite value.");
        }
        this.illumination = Math.max(0.0, Math.min(1.0, illumination));
        this.paksha = paksha;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float centerX = getWidth() / 2.0f;
        float centerY = getHeight() / 2.0f;
        float radius = Math.min(centerX, centerY) - 8.0f;
        if (radius <= 0.0f) {
            return;
        }

        int shadow = ContextCompat.getColor(getContext(), R.color.cosmic_surface_variant);
        spherePaint.setShader(new RadialGradient(
            centerX - radius * 0.28f,
            centerY - radius * 0.32f,
            radius * 1.45f,
            new int[] {shadow, ContextCompat.getColor(getContext(), R.color.cosmic_background)},
            null,
            Shader.TileMode.CLAMP
        ));
        canvas.drawCircle(centerX, centerY, radius, spherePaint);
        spherePaint.setShader(null);
        canvas.save();
        Path disc = new Path();
        disc.addCircle(centerX, centerY, radius, Path.Direction.CW);
        canvas.clipPath(disc);

        double waxingPhase = Math.acos(1.0 - (2.0 * illumination));
        double phase = "Krishna".equalsIgnoreCase(paksha) ? (2.0 * Math.PI) - waxingPhase : waxingPhase;
        boolean waxing = !"Krishna".equalsIgnoreCase(paksha);
        float terminatorDirection = (float) Math.cos(phase) * (waxing ? 1.0f : -1.0f);
        lightPaint.setColor(ContextCompat.getColor(getContext(), R.color.celestial_gold_light));
        lightPaint.setShader(new RadialGradient(
            centerX - radius * 0.22f,
            centerY - radius * 0.25f,
            radius * 1.6f,
            new int[] {
                ContextCompat.getColor(getContext(), R.color.text_primary),
                ContextCompat.getColor(getContext(), R.color.celestial_gold_light)
            },
            null,
            Shader.TileMode.CLAMP
        ));

        Path illuminatedSurface = new Path();
        illuminatedSurface.moveTo(centerX, centerY - radius);
        int direction = waxing ? 1 : -1;
        for (int step = 1; step <= 48; step++) {
            double y = -radius + (2.0 * radius * step / 48.0);
            float edge = (float) Math.sqrt(Math.max(0.0, (radius * radius) - (y * y)));
            illuminatedSurface.lineTo(centerX + (direction * edge), centerY + (float) y);
        }
        for (int step = 48; step >= 0; step--) {
            double y = -radius + (2.0 * radius * step / 48.0);
            float edge = (float) Math.sqrt(Math.max(0.0, (radius * radius) - (y * y)));
            illuminatedSurface.lineTo(
                centerX + (terminatorDirection * edge),
                centerY + (float) y
            );
        }
        illuminatedSurface.close();
        canvas.drawPath(illuminatedSurface, lightPaint);
        lightPaint.setShader(null);
        canvas.restore();
        canvas.drawCircle(centerX, centerY, radius, outlinePaint);
    }
}
