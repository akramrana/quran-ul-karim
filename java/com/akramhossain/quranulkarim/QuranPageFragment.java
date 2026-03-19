package com.akramhossain.quranulkarim;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.task.BackgroundTask;
import com.akramhossain.quranulkarim.util.AyahAnchors;
import com.akramhossain.quranulkarim.util.AyahOverlayView;
import com.akramhossain.quranulkarim.util.Utils;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import io.sentry.Sentry;

public class QuranPageFragment extends Fragment {
    private static final String ARG_PAGE = "page";

    private View ayahMenuView;
    private FrameLayout pageRoot;
    private AyahOverlayView overlay;

    private LinearLayout infoPanel;
    private TextView infoTitle;
    private TextView infoContent, infoTransliterationBn, infoTranslationEn, infoTranslationBn, infoAyahKey;

    private View btnDone;
    private View panelScrim;
    private boolean isInfoPanelVisible = false;

    DatabaseHelper dbhelper;
    SharedPreferences mPrefs;

    Typeface font;

    String audioUrl, audioDuration, ayahIndex, textTashkeel, textUthmani, indoPak, contentEn, contentBn, ayahNum, surahId, ayahKey, trans, textUthmaniTajweed;

    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    public static QuranPageFragment newInstance(int page) {
        Bundle b = new Bundle();
        b.putInt(ARG_PAGE, page);
        QuranPageFragment f = new QuranPageFragment();
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_quran_page, container, false);
        PhotoView img = v.findViewById(R.id.img);

        overlay = v.findViewById(R.id.overlay);

        pageRoot = v.findViewById(R.id.pageRoot);

        int page = requireArguments().getInt(ARG_PAGE);

        // asset path example: assets/quran_pages/1.png ... 604.png
        String assetPath = "file:///android_asset/quran_pages/" + page + ".png";

        Glide.with(this)
                .load(assetPath)
                .dontAnimate()
                .into(img);

        img.setOnMatrixChangeListener(rect -> overlay.setDisplayRect(rect));

        img.post(() -> {
            RectF rect = img.getDisplayRect();
            if (rect != null) {
                overlay.setDisplayRect(rect);
            }
        });

        List<AyahAnchors.AyahBand> bands = new ArrayList<>();
        try {
            bands = AyahAnchors.loadPageBands(requireContext(), page);
        } catch (Exception e) {
            Log.e("QURAN", "Failed to load map for page " + page, e);
        }

        List<AyahAnchors.AyahBand> finalBands = bands;

        img.setOnViewTapListener((view, x, y) -> {
            RectF rect = img.getDisplayRect();
            if (rect == null) return;

            float nx = (x - rect.left) / rect.width();
            float ny = (y - rect.top) / rect.height();

            if (nx < 0f || nx > 1f || ny < 0f || ny > 1f) {
                overlay.clear();
                return;
            }

            AyahAnchors.AyahBand hit = AyahAnchors.hitTest(finalBands, nx, ny);

            if (hit != null) {
                List<float[]> ayahBoxes = AyahAnchors.getAllBoxesForAyah(
                        finalBands,
                        hit.surah,
                        hit.ayah
                );
                overlay.setBoxes(ayahBoxes);
                Log.d("QURAN", "Selected ayah: " + hit.surah + ":" + hit.ayah);

                getDataFromLocalDb(hit.surah, hit.ayah);

                showAyahFloatingMenu(ayahBoxes, hit.surah, hit.ayah);
            } else {
                overlay.clear();
                hideAyahFloatingMenu();
            }
        });

        mPrefs = requireContext().getSharedPreferences(Utils.PREF_NAME, 0);

        cd = new ConnectionDetector(requireContext());
        isInternetPresent = cd.isConnectingToInternet();

        font = Typeface.createFromAsset(requireContext().getAssets(),"fonts/Siyamrupali.ttf");

        infoPanel = v.findViewById(R.id.infoPanel);
        infoTitle = v.findViewById(R.id.infoTitle);
        infoContent = v.findViewById(R.id.infoContent);

        infoTransliterationBn = v.findViewById(R.id.infoTransliterationBn);
        infoTransliterationBn.setTypeface(font);

        infoTranslationEn = v.findViewById(R.id.infoTranslationEn);

        infoTranslationBn = v.findViewById(R.id.infoTranslationBn);
        infoTranslationBn.setTypeface(font);

        infoAyahKey = v.findViewById(R.id.infoAyahKey);

        btnDone = v.findViewById(R.id.btnDone);

        panelScrim = v.findViewById(R.id.panelScrim);
        panelScrim.setOnClickListener(v1 -> hideInfoPanel());

        btnDone.setOnClickListener(v1 -> hideInfoPanel());

        pageRoot.post(() -> {
            int topGap = dp(72);

            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) infoPanel.getLayoutParams();
            lp.height = pageRoot.getHeight() - topGap;
            lp.gravity = Gravity.BOTTOM;
            infoPanel.setLayoutParams(lp);

            infoPanel.setTranslationY(pageRoot.getHeight());
            infoPanel.setVisibility(View.GONE);
        });

        dbhelper = DatabaseHelper.getInstance(requireContext().getApplicationContext());

        String show_bn_pron = mPrefs.getString("show_bn_pron", "2");
        if (show_bn_pron.equals("-1")) {
            infoTransliterationBn.setVisibility(View.GONE);
        }
        String show_en_trans = mPrefs.getString("show_en_trans", "2");
        if (show_en_trans.equals("-1")) {
            infoTranslationEn.setVisibility(View.GONE);
        }
        String show_bn_trans = mPrefs.getString("show_bn_trans", "2");
        if (show_bn_trans.equals("-1")) {
            infoTranslationBn.setVisibility(View.GONE);
        }

        String mp_enFz = mPrefs.getString("enFontSize", "15");
        if(!mp_enFz.equals("") && mp_enFz !=null){
            try {
                infoTranslationEn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_enFz));
            }catch (NumberFormatException e) {
                Log.e("WRONG_FONT_SIZE", "Error parsing number: ", e);
            }
        }
        String mp_bnFz = mPrefs.getString("bnFontSize", "15");
        if(!mp_bnFz.equals("") && mp_bnFz !=null){
            try {
                infoTranslationBn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
                infoTransliterationBn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(mp_bnFz));
            }catch (NumberFormatException e) {
                Log.e("WRONG_FONT_SIZE", "Error parsing number: ", e);
            }
        }

        return v;
    }

    private void showAyahFloatingMenu(List<float[]> boxes, int surah, int ayah) {
        if (boxes == null || boxes.isEmpty() || overlay.getDisplayRect() == null || pageRoot == null) {
            return;
        }

        hideAyahFloatingMenu();
        hideAyahFloatingMenuImmediate();

        RectF displayRect = overlay.getDisplayRect();

        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        float maxX = 0, maxY = 0;

        for (float[] b : boxes) {
            float x = b[0];
            float y = b[1];
            float w = b[2];
            float h = b[3];

            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x + w);
            maxY = Math.max(maxY, y + h);
        }

        float left = displayRect.left + minX * displayRect.width();
        float top = displayRect.top + minY * displayRect.height();
        float right = displayRect.left + maxX * displayRect.width();
        float bottom = displayRect.top + maxY * displayRect.height();
        float centerX = (left + right) / 2f;

        ayahMenuView = LayoutInflater.from(requireContext())
                .inflate(R.layout.view_ayah_floating_menu, pageRoot, false);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        ayahMenuView.setLayoutParams(params);
        ayahMenuView.setAlpha(0f);
        ayahMenuView.setVisibility(View.INVISIBLE);

        pageRoot.addView(ayahMenuView);

        TextView btnPlay = ayahMenuView.findViewById(R.id.btnPlay);
        TextView btnTranslation = ayahMenuView.findViewById(R.id.btnTranslation);
        TextView btnTafsir = ayahMenuView.findViewById(R.id.btnTafsir);
        TextView btnCopy = ayahMenuView.findViewById(R.id.btnCopy);
        TextView btnBookmark = ayahMenuView.findViewById(R.id.btnBookmark);
        TextView btnDefinition = ayahMenuView.findViewById(R.id.btnDefinition);
        TextView btnShare = ayahMenuView.findViewById(R.id.btnShare);

        btnPlay.setOnClickListener(v -> playAyah(surah, ayah));
        btnTranslation.setOnClickListener(v -> openTranslation(surah, ayah));
        btnTafsir.setOnClickListener(v -> openTafsir(surah, ayah));
        btnCopy.setOnClickListener(v -> copyAyah(surah, ayah));
        btnBookmark.setOnClickListener(v -> bookmarkAyah(surah, ayah));
        btnDefinition.setOnClickListener(v -> openDefinition(surah, ayah));
        btnShare.setOnClickListener(v -> openShare(surah, ayah));

        ayahMenuView.post(() -> {

            int menuWidth = ayahMenuView.getWidth();
            int menuHeight = ayahMenuView.getHeight();
            int parentWidth = pageRoot.getWidth();
            int parentHeight = pageRoot.getHeight();

            float margin = dp(8);
            float bottomSafeInset = dp(60);
            float usableBottom = parentHeight - bottomSafeInset;

            // CENTER HORIZONTALLY
            float desiredX = (parentWidth - menuWidth) / 2f;

            // SMART VERTICAL POSITION
            float selectionCenterY = (top + bottom) / 2f;
            float desiredY;

            if (top < dp(80)) {
                desiredY = bottom + dp(10);
            } else {
                desiredY = selectionCenterY - (menuHeight / 2f);
            }

            // CLAMP INSIDE SCREEN
            if (desiredY + menuHeight > usableBottom - margin) {
                desiredY = usableBottom - menuHeight - margin;
            }

            if (desiredY < margin) {
                desiredY = margin;
            }

            ayahMenuView.setX(desiredX);
            ayahMenuView.setY(desiredY);

            ayahMenuView.setVisibility(View.VISIBLE);

            ayahMenuView.setScaleX(0.96f);
            ayahMenuView.setScaleY(0.96f);

            ayahMenuView.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(140)
                    .start();
        });
    }

    private void hideAyahFloatingMenuImmediate() {
        if (ayahMenuView != null) {
            if (pageRoot != null) {
                pageRoot.removeView(ayahMenuView);
            }
            ayahMenuView = null;
        }
    }

    private void hideAyahFloatingMenu() {
        if (ayahMenuView != null) {
            View viewToRemove = ayahMenuView;
            ayahMenuView = null;

            viewToRemove.animate()
                    .alpha(0f)
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(120)
                    .withEndAction(() -> {
                        if (pageRoot != null) {
                            pageRoot.removeView(viewToRemove);
                        }
                    })
                    .start();
        }
    }

    private int dp(int value) {
        return Math.round(value * requireContext().getResources().getDisplayMetrics().density);
    }

    private void playAyah(int surah, int ayah) {
        Log.d("QURAN", "Play " + surah + ":" + ayah);
        if (isInternetPresent) {
            if (checkPermission()) {
                new BackgroundTask(requireActivity()) { // safer than requireActivity()
                    @Override
                    public void onPreExecute() {}

                    @Override
                    public void doInBackground() {
                        try {
                            URL url = new URL(audioUrl);
                            String fileName = url.getFile().replaceAll("/", "_").toLowerCase();
                            Log.d("File Name:", fileName);
                            //
                            String mPath = getActivity().getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/";
                            String fullPath = mPath + fileName;
                            Log.d("File Path:", mPath);
                            Log.d("Full File Path:", fullPath);
                            File file = new File(fullPath);
                            if (file.exists()) {
                                Log.d("File Path:", "Exist!");
                                AudioPlay.stopAudio();
                                AudioPlay.playAudio(getActivity(), fullPath);
                            } else {
                                Log.d("File Path:", "Not Exist Downloading!");
                                downloadFile(audioUrl, fileName, mPath);
                                AudioPlay.stopAudio();
                                AudioPlay.playAudio(getActivity(), fullPath);
                            }
                            //
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onPostExecute() {
                        if (!isAdded()) return; // prevent crash
                    }
                }.execute();
            }else {
                requestPermission(); // Code for permission
            }
        }
    }

    private void openTranslation(int surah, int ayah) {
        Log.d("QURAN", "Translation " + surah + ":" + ayah);
        hideAyahFloatingMenu();
        showInfoPanel(
                "Translation",
                "Here you can show the translation text for " + surah + ":" + ayah
        );
    }

    private void openTafsir(int surah, int ayah) {
        Log.d("QURAN", "Tafsir " + surah + ":" + ayah);
        try {
            String mushaf = mPrefs.getString("mushaf", "IndoPak");
            Intent in = new Intent(requireActivity(), TafsirActivity.class);
            in.putExtra("ayah_index", ayahIndex);
            if(mushaf.equals("ImlaeiSimple")) {
                in.putExtra("text_tashkeel", textTashkeel);
            }
            else if(mushaf.equals("Uthmanic")) {
                in.putExtra("text_tashkeel", textUthmani);
            }
            else{
                in.putExtra("text_tashkeel", indoPak);
            }
            in.putExtra("content_en", contentEn);
            in.putExtra("content_bn", contentBn);
            in.putExtra("ayah_num", ayahNum);
            in.putExtra("surah_id", surahId);
            in.putExtra("ayah_key", ayahKey);
            in.putExtra("trans", trans);
            in.putExtra("text_tajweed", textUthmaniTajweed);
            requireActivity().startActivity(in);
        } catch (Exception e) {
            Log.e("Tafsirs", e.getMessage());
        }
    }

    private void copyAyah(int surah, int ayah) {
        Log.d("QURAN", "Copy " + surah + ":" + ayah);
    }

    private void bookmarkAyah(int surah, int ayah) {
        Log.d("QURAN", "Bookmark " + surah + ":" + ayah);
    }

    private void openDefinition(int surah, int ayah) {
        Log.d("QURAN", "Definition " + surah + ":" + ayah);
    }

    private void openShare(int surah, int ayah) {
        Log.d("QURAN", "Share " + surah + ":" + ayah);
    }

    private void showInfoPanel(String title, String content) {
        infoTitle.setText(title);
        infoContent.setText(content);

        if (isInfoPanelVisible) return;

        isInfoPanelVisible = true;

        infoPanel.setVisibility(View.VISIBLE);
        panelScrim.setVisibility(View.VISIBLE);

        panelScrim.setAlpha(0f);
        panelScrim.animate().alpha(1f).setDuration(200).start();

        infoPanel.animate()
                .translationY(0)
                .setDuration(260)
                .start();
    }

    private void hideInfoPanel() {
        if (!isInfoPanelVisible) return;

        isInfoPanelVisible = false;

        panelScrim.animate()
                .alpha(0f)
                .setDuration(180)
                .withEndAction(() -> panelScrim.setVisibility(View.GONE))
                .start();

        infoPanel.animate()
                .translationY(pageRoot.getHeight())
                .setDuration(220)
                .withEndAction(() -> infoPanel.setVisibility(View.GONE))
                .start();
    }

    public void getDataFromLocalDb(int surah_id, int ayah_num){
        SQLiteDatabase db = DatabaseHelper.getInstance(requireContext().getApplicationContext()).getWritableDatabase();
        String sql = "SELECT ayah.*,sura.name_arabic,sura.name_complex,sura.name_english,sura.name_simple,transliteration.trans,ayah_indo.text as indo_pak,ut.text_uthmani_tajweed,u.text_uthmani " +
                "FROM ayah " +
                "LEFT join sura ON ayah.surah_id = sura.surah_id " +
                "LEFT join transliteration ON ayah.ayah_num = transliteration.ayat_id and transliteration.sura_id = ayah.surah_id " +
                "LEFT join ayah_indo ON ayah.ayah_num = ayah_indo.ayah and ayah_indo.sura = ayah.surah_id "+
                "LEFT JOIN uthmani_tajweed ut ON ayah.ayah_key = ut.verse_key " +
                "LEFT JOIN uthmani u ON ayah.ayah_key = u.verse_key "+
                "WHERE ayah.surah_id = "+surah_id+" and ayah.ayah_num = "+ayah_num+" ";

        Log.d("SQL",sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                infoTransliterationBn.setText(cursor.getString(19));
                infoTranslationEn.setText(cursor.getString(11));
                infoTranslationBn.setText(cursor.getString(12));
                infoAyahKey.setText(cursor.getString(16)+" "+cursor.getString(8));
                //
                audioUrl = cursor.getString(cursor.getColumnIndexOrThrow("audio_url"));
                audioDuration = cursor.getString(cursor.getColumnIndexOrThrow("audio_duration"));
                ayahIndex = cursor.getString(cursor.getColumnIndexOrThrow("ayah_index"));
                textTashkeel = cursor.getString(cursor.getColumnIndexOrThrow("text_tashkeel"));
                textUthmani = cursor.getString(cursor.getColumnIndexOrThrow("text_uthmani"));
                indoPak = cursor.getString(cursor.getColumnIndexOrThrow("indo_pak"));
                contentEn = cursor.getString(cursor.getColumnIndexOrThrow("content_en"));
                contentBn = cursor.getString(cursor.getColumnIndexOrThrow("content_bn"));
                ayahNum = cursor.getString(cursor.getColumnIndexOrThrow("ayah_num"));
                surahId = cursor.getString(cursor.getColumnIndexOrThrow("surah_id"));
                ayahKey = cursor.getString(cursor.getColumnIndexOrThrow("ayah_key"));
                trans = cursor.getString(cursor.getColumnIndexOrThrow("trans"));
                textUthmaniTajweed = cursor.getString(cursor.getColumnIndexOrThrow("text_uthmani_tajweed"));
            }
        }catch (Exception e){
            Log.e("QuranPageFragment", e.getMessage());
            //throw new RuntimeException("SQL Query: " + sql, e);
            Sentry.captureException(new RuntimeException("SQL Query: " + sql, e));
        }
        finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
        return ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return;
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(
                    requireContext(),
                    "Write External Storage permission allows us to save files. Please allow this permission.",
                    Toast.LENGTH_LONG
            ).show();
        } else {
            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO
    };

    public static String[] storage_permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storage_permissions_33;
        } else {
            p = storage_permissions;
        }
        return p;
    }

    static void downloadFile(String dwnload_file_path, String fileName, String pathToSave) {
        int downloadedSize = 0;
        int totalSize = 0;
        try {
            URL url = new URL(dwnload_file_path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            //urlConnection.setDoOutput(true);
            // connect
            urlConnection.connect();
            File myDir;
            myDir = new File(pathToSave);
            myDir.mkdirs();
            // create a new file, to save the downloaded file
            String mFileName = fileName;
            File file = new File(myDir, mFileName);
            FileOutputStream fileOutput = new FileOutputStream(file);
            // Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();
            // this is the total size of the file which we are downloading
            totalSize = urlConnection.getContentLength();
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
            }
            // close the output stream when complete //
            fileOutput.close();

        } catch (final MalformedURLException e) {
            // showError("Error : MalformedURLException " + e);
            e.printStackTrace();
        } catch (final IOException e) {
            // showError("Error : IOException " + e);
            e.printStackTrace();
        } catch (final Exception e) {
            // showError("Error : Please check your internet connection " + e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        hideAyahFloatingMenu();
    }
}
