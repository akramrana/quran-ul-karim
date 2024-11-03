package com.akramhossain.quranulkarim;

import android.content.Intent;
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
import androidx.core.view.WindowInsetsCompat;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = ChangePasswordActivity.class.getSimpleName();
    ProgressBar progressBar;
    public String user_id = "";
    private SessionManager session;
    public static String host = "http://quran.codxplore.com/";
    public static String URL;
    private Button btnUpdatePass;

    private EditText old_password,new_password,confirm_new_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        old_password = (EditText) findViewById(R.id.old_password);
        new_password = (EditText) findViewById(R.id.new_password);
        confirm_new_password = (EditText) findViewById(R.id.confirm_new_password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        URL = host+"api/v1/update-password.php";

        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            String userJson = session.getLoginData();
            try {
                JSONObject response = new JSONObject(userJson);
                user_id = response.getString("user_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Intent intent = new Intent(ChangePasswordActivity.this,ChallengeDashboardActivity.class);
            startActivity(intent);
            finish();
        }

        btnUpdatePass = (Button) findViewById(R.id.btnUpdatePass);
        btnUpdatePass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String old_password_str = old_password.getText().toString().trim();
                String new_password_str = new_password.getText().toString().trim();
                String confirm_new_password_str = confirm_new_password.getText().toString().trim();

                if (!old_password_str.isEmpty() && !new_password_str.isEmpty() && !confirm_new_password_str.isEmpty()) {
                    if(confirm_new_password_str.equals(new_password_str)) {
                        updatePassword(old_password_str, new_password_str, confirm_new_password_str);
                    }else{
                        Toast.makeText(getApplicationContext(),"Confirm password did not match!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Please enter your details!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updatePassword(final String old_pass, String new_pass, String confirm_pass) {
        // Tag used to cancel the request
        String tag_string_req = "req_update_pass";
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "Update Pass URL: " + URL.toString());
        StringRequest strReq = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Pass Response: " + response.toString());
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in database
                        Toast.makeText(getApplicationContext(), "Password successfully updated!", Toast.LENGTH_LONG).show();
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
                Log.e(TAG, "Update Pass Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("old_pass", old_pass);
                params.put("new_pass",new_pass);
                params.put("confirm_pass",confirm_pass);
                params.put("user_id",user_id);
                return params;
            }
        };
        strReq.setShouldCache(false);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}