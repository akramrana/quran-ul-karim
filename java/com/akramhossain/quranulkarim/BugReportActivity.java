package com.akramhossain.quranulkarim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class BugReportActivity extends AppCompatActivity {

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;
    TextView getImageFromGallery;
    private int GALLERY = 1;

    ImageView showSelectedImage;
    public Bitmap FixBitmap;
    ByteArrayOutputStream byteArrayOutputStream;
    byte[] byteArray;
    String ConvertImage;
    HttpURLConnection httpURLConnection;
    URL url;
    OutputStream outputStream;
    BufferedWriter bufferedWriter;
    int RC;
    BufferedReader bufferedReader;
    StringBuilder stringBuilder;
    boolean check = true;

    String name, email, feedback, feedbackType;
    int bRequiresResponse;

    ProgressDialog progressDialog;

    Button ButtonSendFeedback;

    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    public static String host = "http://websites.codxplore.com/islamicvideo/";
    Spinner feedbackSpinner;
    public static int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_bug_report);

        View rootView = findViewById(R.id.topBugReportBar);
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

        View bottomBar = findViewById(R.id.ScrollView01);

        ViewCompat.setOnApplyWindowInsetsListener(bottomBar, (v, insets) -> {
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(0, 0, 0, bottomInset);
            return insets;
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            position = extras.getInt("position");
        }

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        byteArrayOutputStream = new ByteArrayOutputStream();

        getImageFromGallery = (TextView) findViewById(R.id.imageSelect);

        getImageFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });

        showSelectedImage = (ImageView) findViewById(R.id.imageView);

        ButtonSendFeedback = (Button) findViewById(R.id.ButtonSendFeedback);

        feedbackSpinner = (Spinner) findViewById(R.id.SpinnerFeedbackType);
        Log.d("Bug Type",String.valueOf(position));
        if(position==3) {
            feedbackSpinner.setSelection(3);
        }
        ButtonSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Do click handling here
                Integer sizeInKb=0;
                if (FixBitmap != null) {
                    FixBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                    byteArray = byteArrayOutputStream.toByteArray();
                    ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    long lengthbmp = byteArray.length;
                    sizeInKb = (Math.round((lengthbmp / (1024)) * 10) / 10);
                    //Log.i("image size",Integer.toString(sizeInKb));
                }
                else{
                    ConvertImage="";
                }

                EditText nameField = (EditText) findViewById(R.id.EditTextName);
                EditText emailField = (EditText) findViewById(R.id.EditTextEmail);
                EditText feedbackField = (EditText) findViewById(R.id.EditTextFeedbackBody);
                CheckBox responseCheckbox = (CheckBox) findViewById(R.id.CheckBoxResponse);


                name = nameField.getText().toString();
                int error = 0;
                if( TextUtils.isEmpty(name)){
                    nameField.setError("Name can't be blank");
                    error = 1;
                }
                email = emailField.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if( TextUtils.isEmpty(email)){
                    emailField.setError("Email can't be blank");
                    error = 1;
                }
                if(email.matches(emailPattern)==false)
                {
                    emailField.setError("Enter valid email address");
                    error = 1;
                }
                feedback = feedbackField.getText().toString();
                if( TextUtils.isEmpty(feedback)){
                    feedbackField.setError("Comments can't be blank");
                    error = 1;
                }
                feedbackType = feedbackSpinner.getSelectedItem().toString();
                boolean feedbackresponse = responseCheckbox.isChecked();
                bRequiresResponse = feedbackresponse ? 1 : 0;

                if(error==0) {
                    if (isInternetPresent) {
                        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
                        AsyncTaskUploadClassOBJ.execute();
                    }
                    else{
                        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(BugReportActivity.this);
                        alert.setTitle(R.string.text_warning);
                        alert.setMessage(R.string.text_enable_internet);
                        alert.setPositiveButton(R.string.text_ok,null);
                        alert.show();
                    }
                }
            }
        });

    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Photo Gallery",
                //"Camera"
        };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    FixBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    showSelectedImage.setImageBitmap(FixBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(BugReportActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(BugReportActivity.this, "Sending feedback...", "Please Wait", false, false);
        }

        @Override
        protected void onPostExecute(String string1) {
            super.onPostExecute(string1);
            progressDialog.dismiss();
            try{
                JSONObject obj = new JSONObject(string1);
                String msg = obj.getString("message");

                EditText nameField = (EditText) findViewById(R.id.EditTextName);
                EditText emailField = (EditText) findViewById(R.id.EditTextEmail);
                EditText feedbackField = (EditText) findViewById(R.id.EditTextFeedbackBody);
                CheckBox responseCheckbox = (CheckBox) findViewById(R.id.CheckBoxResponse);

                nameField.setText("");
                emailField.setText("");
                feedbackField.setText("");
                responseCheckbox.setChecked(false);
                showSelectedImage.setImageDrawable(null);

                Toast.makeText(BugReportActivity.this, msg, Toast.LENGTH_LONG).show();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            ProcessClass processClass = new ProcessClass();
            HashMap<String, String> HashMapParams = new HashMap<String, String>();
            HashMapParams.put("image", ConvertImage);
            HashMapParams.put("name", name);
            HashMapParams.put("email", email);
            HashMapParams.put("message", feedback);
            HashMapParams.put("type", feedbackType);
            HashMapParams.put("response_required", Integer.toString(bRequiresResponse));
            String FinalData = processClass.postHttpRequest(host + "api/send-feedback", HashMapParams);
            return FinalData;
        }
    }

    public class ProcessClass {
        public String postHttpRequest(String requestURL, HashMap<String, String> PData) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                url = new URL(requestURL);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(20000);
                httpURLConnection.setConnectTimeout(20000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                outputStream = httpURLConnection.getOutputStream();

                JSONObject obj=new JSONObject(PData);

                Log.i(BugReportActivity.class.toString(), obj.toString());

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(getPostDataString(obj));

                writer.flush();
                writer.close();
                outputStream.close();
                int responseCode=httpURLConnection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    BufferedReader in=new BufferedReader(
                            new InputStreamReader(
                                    httpURLConnection.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";
                    while((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    return sb.toString();
                }
                else {
                    return new String("false : "+responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        public String getPostDataString(JSONObject params) throws Exception {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            Iterator<String> itr = params.keys();
            while(itr.hasNext()){
                String key= itr.next();
                Object value = params.get(key);
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));
            }
            return result.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {
            stringBuilder = new StringBuilder();
            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilder.append("&");

                stringBuilder.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
                stringBuilder.append("=");
                stringBuilder.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }
            return stringBuilder.toString();
        }

    }

}