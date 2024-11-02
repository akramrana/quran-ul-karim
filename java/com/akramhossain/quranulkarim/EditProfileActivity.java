package com.akramhossain.quranulkarim;

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
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;


public class EditProfileActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener{

    private static final String TAG = EditProfileActivity.class.getSimpleName();
    private Button btnUpdateProfile;
    ProgressBar progressBar;
    public static String URL, CountryUrl;
    private EditText inputFullName;
    private Spinner spinner;
    private ArrayList<String> countries;
    private JSONArray result;
    private SessionManager session;
    public static String host = "http://quran.codxplore.com/";
    public String user_id = "";
    public String country_id = "";
    public int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        inputFullName = (EditText) findViewById(R.id.name);
        btnUpdateProfile = (Button) findViewById(R.id.btnUpdateProfile);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        countries = new ArrayList<String>();
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(EditProfileActivity.this);

        URL = host+"api/v1/update-profile.php";
        CountryUrl = host+"api/v1/countries.php";
        getCountryData();

        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            String userJson = session.getLoginData();
            try {
                JSONObject response = new JSONObject(userJson);
                user_id = response.getString("user_id");
                JSONObject user = new JSONObject(response.getString("user"));
                Log.i(TAG,user.toString());
                String name = user.getString("name");
                inputFullName.setText(name);
                country_id = user.getString("country_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Intent intent = new Intent(EditProfileActivity.this,ChallengeDashboardActivity.class);
            startActivity(intent);
            finish();
        }

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                Integer position = spinner.getSelectedItemPosition();
                String countryId = getCountryId(position);
                if (!name.isEmpty() && !countryId.isEmpty()) {
                    updateUser(name, countryId);
                } else {
                    Toast.makeText(getApplicationContext(),"Please enter your details!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void updateUser(final String name, String countryId) {
        // Tag used to cancel the request
        String tag_string_req = "req_update_profile";
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "Update Profile URL: " + URL.toString());
        StringRequest strReq = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Profile Response: " + response.toString());
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in database
                        session.setLoginData(response.toString());
                        Toast.makeText(getApplicationContext(), "Profile successfully updated!", Toast.LENGTH_LONG).show();
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
                Log.e(TAG, "Update Profile Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("country_id",countryId);
                params.put("user_id",user_id);
                return params;
            }
        };
        strReq.setShouldCache(false);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void getCountryData(){
        String tag_string_req = "req_country";
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "Country URL: " + CountryUrl.toString());
        StringRequest strReq = new StringRequest(Request.Method.GET, CountryUrl, new Response.Listener<String>() {
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
                            String contryId = json.getString("country_id");
                            if(country_id.equals(contryId)){
                                position = i;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    spinner.setAdapter(new ArrayAdapter<String>(EditProfileActivity.this, android.R.layout.simple_spinner_dropdown_item, countries));
                    spinner.setSelection(position);

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