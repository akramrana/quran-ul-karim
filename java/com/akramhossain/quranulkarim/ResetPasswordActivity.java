package com.akramhossain.quranulkarim;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.akramhossain.quranulkarim.app.AppController;
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
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class ResetPasswordActivity extends AppCompatActivity {

    private static final String TAG = ResetPasswordActivity.class.getSimpleName();
    ProgressBar progressBar;
    private SessionManager session;
    public static String host = "http://quran.codxplore.com/";
    public static String URL;
    private Button btnUpdatePass;
    private EditText reset_otp_code,new_password,confirm_new_password;
    private Button btnLinkToLoginScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_reset_password);

        View rootView = findViewById(R.id.topAboutBar);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply padding to avoid overlap with status/navigation bars
            view.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    view.getPaddingBottom()
            );
            return insets;
        });

        View bottomBar = findViewById(R.id.bottomBar);
        ViewCompat.setOnApplyWindowInsetsListener(bottomBar, (v, insets) -> {
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(
                    v.getPaddingLeft(),
                    v.getPaddingTop(),
                    v.getPaddingRight(),
                    bottomInset
            );
            return insets;
        });

        reset_otp_code = (EditText) findViewById(R.id.reset_otp_code);
        new_password = (EditText) findViewById(R.id.new_password);
        confirm_new_password = (EditText) findViewById(R.id.confirm_new_password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(ResetPasswordActivity.this,LeaderboardActivity.class);
            startActivity(intent);
            finish();
        }

        URL = host+"api/v1/reset-password.php";

        btnUpdatePass = (Button) findViewById(R.id.btnUpdatePass);
        btnUpdatePass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String reset_otp_code_str = reset_otp_code.getText().toString().trim();
                String new_password_str = new_password.getText().toString().trim();
                String confirm_new_password_str = confirm_new_password.getText().toString().trim();

                if (!reset_otp_code_str.isEmpty() && !new_password_str.isEmpty() && !confirm_new_password_str.isEmpty()) {
                    if(confirm_new_password_str.equals(new_password_str)) {
                        resetPassword(reset_otp_code_str, new_password_str, confirm_new_password_str);
                    }else{
                        Toast.makeText(getApplicationContext(),"Confirm password did not match!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Please enter your details!", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnLinkToLoginScreen = (Button) findViewById(R.id.btnLinkToLoginScreen);
        btnLinkToLoginScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SigninActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void resetPassword(final String otp_code, String new_pass, String confirm_pass) {
        // Tag used to cancel the request
        String tag_string_req = "req_reset_forgot_pass";
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "Reset Forgot Pass URL: " + URL.toString());
        StringRequest strReq = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Reset Forgot Pass Response: " + response.toString());
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in database
                        Toast.makeText(getApplicationContext(), "Password successfully updated!", Toast.LENGTH_LONG).show();
                        new_password.setText("");
                        reset_otp_code.setText("");
                        confirm_new_password.setText("");

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
                Log.e(TAG, "Reset Forgot Pass Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("reset_otp_code", otp_code);
                params.put("new_pass",new_pass);
                params.put("confirm_pass",confirm_pass);
                return params;
            }
        };
        strReq.setShouldCache(false);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}