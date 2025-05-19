package com.akramhossain.quranulkarim;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Intent;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.akramhossain.quranulkarim.task.CmsJsonFromUrlTask;

import org.json.JSONException;
import org.json.JSONObject;

import static com.akramhossain.quranulkarim.BugReportActivity.host;

public class MoreMenuActivity extends AppCompatActivity {

    public static String cmsTitle;
    public static String cmsPage;
    public static String URL;
    private static final String TAG = MoreMenuActivity.class.getSimpleName();
    TextView cmsTextViewTitle;
    TextView cmsTextViewContent;
    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cmsTitle = extras.getString("cms_title");
            cmsPage = extras.getString("cms_page");
        }

        URL = host+"api/cms?page="+cmsPage;

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_more_menu);

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

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        //FETCH DATA FROM REMOTE SERVER
        getDataFromInternet();
    }

    private void getDataFromInternet() {
        if (isInternetPresent) {
            new CmsJsonFromUrlTask(this, URL);
        }
        else{
            AlertDialog.Builder alert = new AlertDialog.Builder(MoreMenuActivity.this);
            alert.setTitle(R.string.text_warning);
            alert.setMessage(R.string.text_enable_internet);
            alert.setPositiveButton(R.string.text_ok,null);
            alert.show();
        }
    }

    public void parseJsonResponse(String result) {
        Log.i(TAG, result);
        try {
            JSONObject response = new JSONObject(result);
            JSONObject json = response.getJSONObject("data");

            cmsTextViewTitle = (TextView) findViewById(R.id.cms_title);
            cmsTextViewTitle.setText(json.getString("title"));

            cmsTextViewContent = (TextView) findViewById(R.id.cms_content);
            cmsTextViewContent.setText(Html.fromHtml(json.getString("content"),Html.FROM_HTML_MODE_LEGACY));
            cmsTextViewContent.setMovementMethod(new ScrollingMovementMethod());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}