package com.akramhossain.quranulkarim.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class AyahOverlayView extends View {
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF displayRect; // provided from PhotoView
    private List<float[]> boxes = new ArrayList<>(); // each: [x,y,w,h] normalized

    public AyahOverlayView(Context c, AttributeSet a) {
        super(c, a);
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.argb(110, 255, 241, 118));
    }

    public void setDisplayRect(RectF r) { this.displayRect = r; invalidate(); }
    public void setBoxes(List<float[]> b) { this.boxes = b; invalidate(); }
    public void clear() { this.boxes.clear(); invalidate(); }
    public RectF getDisplayRect() {
        return displayRect;
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (displayRect == null) {
            Log.d("QURAN", "Overlay: displayRect is null");
            return;
        }
        if (boxes == null || boxes.isEmpty()) {
            Log.d("QURAN", "Overlay: no boxes to draw");
            return;
        }

        Log.d("QURAN", "Overlay: drawing " + boxes.size() + " boxes");

        if (displayRect == null || boxes == null) return;

        for (float[] b : boxes) {
            float left   = displayRect.left + b[0] * displayRect.width();
            float top    = displayRect.top  + b[1] * displayRect.height();
            float right  = left + b[2] * displayRect.width();
            float bottom = top  + b[3] * displayRect.height();
            canvas.drawRoundRect(new RectF(left, top, right, bottom), 14f, 14f, p);
        }
    }
}