package com.akramhossain.quranulkarim;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.akramhossain.quranulkarim.game.DhikrGameView;

public class DhikrGameActivity extends AppCompatActivity {

    private DhikrGameView dhikrGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dhikrGameView = new DhikrGameView(this);
        setContentView(dhikrGameView);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (dhikrGameView != null) {
            dhikrGameView.pauseGame();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (dhikrGameView != null) {
            dhikrGameView.resumeGame();
        }
    }
}