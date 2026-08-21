package com.akramhossain.quranulkarim.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.akramhossain.quranulkarim.R;

public class PrayerBackgroundGuideDialog extends Dialog {

    public PrayerBackgroundGuideDialog(@NonNull Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_prayer_background_guide);

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
        }

        setCancelable(true);

        ImageView imgGuide = findViewById(R.id.imgGuide);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnGotIt = findViewById(R.id.btnGotIt);

        imgGuide.setImageResource(R.drawable.prayer_background_guide);

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
            context.startActivity(intent);
            dismiss();
        });

        btnGotIt.setOnClickListener(v -> dismiss());
    }
}