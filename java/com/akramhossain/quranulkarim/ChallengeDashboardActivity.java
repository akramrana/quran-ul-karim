package com.akramhossain.quranulkarim;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akramhossain.quranulkarim.helper.DatabaseHelper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChallengeDashboardActivity extends AppCompatActivity {

    TextView current_score_count,challenge_attempted_count,right_answer_count,wrong_answer_score_count;
    private static final String TAG = ChallengeDashboardActivity.class.getSimpleName();
    TextView txtPer;
    ProgressBar prog;
    Integer rightAnsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_dashboard);

        Button enter_challenge_button = (Button) findViewById(R.id.start_button);
        enter_challenge_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), WordAnswerActivity.class);
                startActivity(i);
            }
        });

        current_score_count = (TextView) findViewById(R.id.current_score_count);
        challenge_attempted_count = (TextView) findViewById(R.id.challenge_attempted_count);
        right_answer_count = (TextView) findViewById(R.id.right_answer_count);
        wrong_answer_score_count = (TextView) findViewById(R.id.wrong_answer_count);

        countTotalScore();
        countWrongTotal();
        countTotal();

        txtPer = (TextView) findViewById(R.id.txtPer);
        prog = (ProgressBar) findViewById(R.id.prog);

        Button leader_button = (Button) findViewById(R.id.leader_button);
        leader_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LeaderboardActivity.class);
                startActivity(i);
            }
        });

    }

    private void countTotalScore(){
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "SELECT count(*) as total FROM word_answers WHERE is_right_answer = 1";
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                String total = cursor.getString(cursor.getColumnIndexOrThrow("total"));
                Integer pointsTotal = Integer.parseInt(total)*1;
                current_score_count.setText(String.valueOf(pointsTotal)+" Point(s)");
                right_answer_count.setText(String.valueOf(pointsTotal));
                rightAnsCount = pointsTotal;
            }
        }catch (Exception e) {
            Log.i(TAG, e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        }
    }

    private void countWrongTotal(){
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "SELECT count(*) as total FROM word_answers WHERE is_right_answer = 0";
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                String total = cursor.getString(cursor.getColumnIndexOrThrow("total"));
                Integer pointsTotal = Integer.parseInt(total)*1;
                wrong_answer_score_count.setText(String.valueOf(pointsTotal));
            }
        }catch (Exception e) {
            Log.i(TAG, e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        }
    }

    private void countTotal(){
        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
        String sql = "SELECT count(*) as total FROM word_answers";
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                String total = cursor.getString(cursor.getColumnIndexOrThrow("total"));
                Integer pointsTotal = Integer.parseInt(total)*1;
                challenge_attempted_count.setText(String.valueOf(pointsTotal));

                double pointsTotalD = pointsTotal;
                double percentile = (rightAnsCount/pointsTotalD*100);
                prog.setProgress((int) percentile,true);

                Log.d("pointsTotal",String.valueOf(pointsTotalD));
                Log.d("rightAnsCount",String.valueOf(rightAnsCount));
                Log.d("percentile",String.valueOf(percentile));

                txtPer.setText(Math.round(percentile)+"%");
            }
        }catch (Exception e) {
            Log.i(TAG, e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        countTotalScore();
        countWrongTotal();
        countTotal();
    }
}