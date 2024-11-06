package com.akramhossain.quranulkarim;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.app.AppController;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.helper.SessionManager;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
    private SessionManager session;
    TextView user_name;
    LinearLayout logged_user_sec;

    public static String host = "http://quran.codxplore.com/";
    ProgressBar progressBar;
    public static String SYNC_URL;
    public String user_id = "";
    public static String DEL_ACC_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_dashboard);

        SYNC_URL = host+"api/v1/sync.php";
        DEL_ACC_URL = host+"api/v1/delete-account.php";
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

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

        user_name = (TextView) findViewById(R.id.user_name);

        logged_user_sec = (LinearLayout) findViewById(R.id.logged_user_sec);
        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            logged_user_sec.setVisibility(View.VISIBLE);
            String userJson = session.getLoginData();
            try {
                JSONObject response = new JSONObject(userJson);
                JSONObject user = new JSONObject(response.getString("user"));
                Log.i(TAG, user.toString());
                String name = user.getString("name");
                user_name.setText("Hi, "+name+"\nWelcome Back!");
                user_id = response.getString("user_id");
                //Log.i(TAG, user_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            logged_user_sec.setVisibility(View.GONE);
        }

        Button btn_change_pass = (Button) findViewById(R.id.btn_change_pass);
        btn_change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                startActivity(i);
            }
        });

        Button btn_edit_profile = (Button) findViewById(R.id.btn_edit_profile);
        btn_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(i);
            }
        });

        Button btn_sign_out = (Button) findViewById(R.id.btn_sign_out);
        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.setLogin(false);
                session.setLoginData("");

                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });

        Button btn_del_acc = (Button) findViewById(R.id.btn_del_acc);
        btn_del_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ChallengeDashboardActivity.this);
                alert.setTitle(R.string.text_delete_title);
                alert.setMessage(R.string.text_delete_desc);
                alert.setPositiveButton(R.string.text_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteAcount();
                    }
                });
                alert.setNegativeButton(R.string.text_no, null);
                alert.show();
            }
        });

        Button btn_publish_data = (Button) findViewById(R.id.btn_publish_data);
        btn_publish_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.isLoggedIn()) {
                    SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                    String sql = "SELECT * FROM word_answers WHERE is_sync = 0";
                    Cursor cursor = db.rawQuery(sql, null);
                    try {
                        if (cursor.moveToFirst()) {
                            do {
                                String word_id = cursor.getString(cursor.getColumnIndexOrThrow("word_id"));
                                String datetime = cursor.getString(cursor.getColumnIndexOrThrow("datetime"));
                                String is_right_answer = cursor.getString(cursor.getColumnIndexOrThrow("is_right_answer"));
                                pushAnswerData(word_id, datetime, is_right_answer);
                            } while (cursor.moveToNext());
                            Toast.makeText(getApplicationContext(), "User data successfully sync", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.i(TAG, e.getMessage());
                    } finally {
                        if (cursor != null && !cursor.isClosed()) {
                            cursor.close();
                        }
                        db.close();
                    }
                }
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

    public void deleteAcount(){
        String tag_string_req = "req_acc_delete";
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "Delete Account URL: " + DEL_ACC_URL.toString());
        StringRequest strReq = new StringRequest(Request.Method.POST, DEL_ACC_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Delete Account Response: " + response.toString());
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Toast.makeText(getApplicationContext(), "User account successfully deleted", Toast.LENGTH_LONG).show();
                        session.setLogin(false);
                        session.setLoginData("");
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Delete Account Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id",user_id);
                return params;
            }
        };
        strReq.setShouldCache(false);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void pushAnswerData(final String word_id, final String datetime,final String is_right_answer){
        String tag_string_req = "req_sync_data";
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "Sync URL: " + SYNC_URL.toString());
        StringRequest strReq = new StringRequest(Request.Method.POST, SYNC_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Sync Response: " + response.toString());
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in database
                        //Toast.makeText(getApplicationContext(), "User data successfully sync", Toast.LENGTH_LONG).show();
                        SQLiteDatabase db = DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase();
                        String sql = "SELECT * FROM word_answers WHERE word_id = "+word_id;
                        Cursor cursor = db.rawQuery(sql, null);
                        try {
                            if (cursor.moveToFirst()) {
                                ContentValues values = new ContentValues();
                                values.put("is_sync", 1);
                                DatabaseHelper.getInstance(getApplicationContext()).getWritableDatabase().update("word_answers", values, "word_id = ?", new String[]{word_id});
                            }
                        }catch (Exception e) {
                            Log.i("SYNC ERROR SQL", e.getMessage());
                        } finally {
                            if (cursor != null && !cursor.isClosed()) {
                                cursor.close();
                            }
                            db.close();
                        }
                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Sync Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("word_id", word_id);
                params.put("datetime", datetime);
                params.put("is_right_answer", is_right_answer);
                params.put("user_id",user_id);
                return params;
            }
        };
        strReq.setShouldCache(false);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public void onResume() {
        super.onResume();
        countTotalScore();
        countWrongTotal();
        countTotal();
        if (session.isLoggedIn()) {
            logged_user_sec.setVisibility(View.VISIBLE);
            String userJson = session.getLoginData();
            try {
                JSONObject response = new JSONObject(userJson);
                JSONObject user = new JSONObject(response.getString("user"));
                Log.i(TAG, user.toString());
                String name = user.getString("name");
                user_name.setText("Hi, "+name+"\nWelcome Back!");
                user_id = response.getString("user_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            logged_user_sec.setVisibility(View.GONE);
        }
    }
}