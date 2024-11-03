package com.akramhossain.quranulkarim;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.akramhossain.quranulkarim.app.AppController;
import com.akramhossain.quranulkarim.helper.SessionManager;
import com.android.volley.toolbox.StringRequest;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.provider.Settings;


public class RegisterActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener{

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLoginScreen;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private SessionManager session;
    ProgressBar progressBar;
    public static String host = "http://quran.codxplore.com/";
    public static String URL, CountryUrl;
    private Spinner spinner;
    private ArrayList<String> countries;
    private JSONArray result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        countries = new ArrayList<String>();
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(RegisterActivity.this);

        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,LeaderboardActivity.class);
            startActivity(intent);
            finish();
        }

        URL = host+"api/v1/register.php";
        CountryUrl = host+"api/v1/countries.php";

        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                Integer position = spinner.getSelectedItemPosition();
                String countryId = getCountryId(position);
                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !countryId.isEmpty()) {
                    registerUser(name, email, password, countryId);
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
            }
        });

        getCountryData();
    }

    private void getCountryData(){
        String tag_string_req = "req_country";
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "Country URL: " + CountryUrl.toString());
        StringRequest strReq = new StringRequest(Method.GET, CountryUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Country Response: " + response.toString());
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jObj = new JSONObject(response);
                    result = new JSONArray(jObj.getString("list"));
                    for(int i=0;i<result.length();i++){
                        try {
                            //Getting json object
                            JSONObject json = result.getJSONObject(i);
                            //Adding the name of the student to array list
                            countries.add(json.getString("nicename"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    spinner.setAdapter(new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item, countries));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Country Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
        strReq.setShouldCache(false);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void registerUser(final String name, final String email,final String password, String countryId) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "Register URL: " + URL.toString());
        StringRequest strReq = new StringRequest(Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in database
                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();
                        // Launch login activity
                        Intent intent = new Intent(RegisterActivity.this,SigninActivity.class);
                        startActivity(intent);
                        finish();
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
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("countryId",countryId);
                String mId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                params.put("device_id",mId);
                return params;
            }
        };
        strReq.setShouldCache(false);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Setting the values to textviews for a selected item
        Log.d("country_id",getCountryId(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private String getCountryId(int position){
        String id="";
        try {
            //Getting object of given index
            JSONObject json = result.getJSONObject(position);
            //Fetching name from that object
            id = json.getString("country_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the id
        return id;
    }
}