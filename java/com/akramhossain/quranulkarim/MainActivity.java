package com.akramhossain.quranulkarim;

import java.io.IOException;
import java.util.ArrayList;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akramhossain.quranulkarim.adapter.PopularRecyclerViewAdapter;
import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.listener.RecyclerTouchListener;
import com.akramhossain.quranulkarim.model.Sura;
import com.akramhossain.quranulkarim.notification.NotificationHelper;

import org.w3c.dom.Text;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private GridviewAdapter mAdapter;
    private ArrayList<String> listText;
    private ArrayList<Integer> listImage;

    private GridView gridView;

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    TextView start_from_last;
    TextView txtNightMode;
    View horizontal_line;
    DatabaseHelper dbhelper;
    LinearLayout sura_link,bookmark_link,search_link,quick_links_link,word_collection_link,about_link,juz_link,hizb_link,rub_link,time_link;
    Button btnPrayerTime;

    public static final String NIGHT_MODE = "APP_NIGHT_MODE";
    SharedPreferences mPrefs;
    String appTheme;

    private RecyclerView popularSearchView;
    private ArrayList<Sura> popularSearches;
    private PopularRecyclerViewAdapter rvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.activity_main);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        appTheme = mPrefs.getString(NIGHT_MODE,"-1");

        SwitchCompat switchCompat = findViewById(R.id.switchCompat);
        txtNightMode = findViewById(R.id.txtNightMode);

        Log.d("APP THEME", String.valueOf(appTheme));

        if (appTheme.equals("1")){
            switchCompat.setChecked(true);
            txtNightMode.setText("Dark Mode");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else if (appTheme.equals("0")){
            switchCompat.setChecked(false);
            txtNightMode.setText("Light Mode");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            switchCompat.setChecked(true);
            txtNightMode.setText("Dark Mode");
        }
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString(NIGHT_MODE, "1");
                    editor.apply();
                    //
                    Intent intent = getIntent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    startActivity(intent);
                } else {
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString(NIGHT_MODE, "0");
                    editor.apply();
                    //
                    Intent intent = getIntent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    startActivity(intent);
                }
            }
        });

        //prepareList();

        start_from_last = (TextView) findViewById(R.id.start_from_last);
        horizontal_line = (View) findViewById(R.id.horizontal_line);

        sura_link = (LinearLayout) findViewById(R.id.sura_link);
        sura_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),SuraListV2Activity.class);
                startActivity(i);
            }
        });

        bookmark_link = (LinearLayout) findViewById(R.id.bookmark_link);
        bookmark_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),BookmarkActivity.class);
                startActivity(i);
            }
        });

        search_link = (LinearLayout) findViewById(R.id.search_link);
        search_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),SearchActivity.class);
                startActivity(i);
            }
        });

        quick_links_link = (LinearLayout) findViewById(R.id.quick_links_link);
        quick_links_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),QuickLinksActivity.class);
                startActivity(i);
            }
        });

        word_collection_link = (LinearLayout) findViewById(R.id.word_collection_link);
        word_collection_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),DictionaryActivity.class);
                startActivity(i);
            }
        });

        about_link = (LinearLayout) findViewById(R.id.about_link);
        about_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),AboutActivity.class);
                startActivity(i);
            }
        });

        time_link = (LinearLayout) findViewById(R.id.time_link);
        time_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),PrayerTimesActivity.class);
                startActivity(i);
            }
        });

        juz_link = (LinearLayout) findViewById(R.id.juz_link);
        juz_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),JuzActivity.class);
                startActivity(i);
            }
        });

        hizb_link = (LinearLayout) findViewById(R.id.hizb_link);
        hizb_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),HizbActivity.class);
                startActivity(i);
            }
        });

        rub_link = (LinearLayout) findViewById(R.id.rub_link);
        rub_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),RubActivity.class);
                startActivity(i);
            }
        });


        // prepared arraylist and passed it to the Adapter class
        //mAdapter = new GridviewAdapter(this, listText, listImage);

        // Set custom adapter to gridview
        //gridView = (GridView) findViewById(R.id.gridView1);
        //gridView.setAdapter(mAdapter);

//		gridView.setOnItemClickListener(new OnItemClickListener() {
//			public void onItemClick(AdapterView<?> arg0, View arg1,
//					int position, long arg3) {
//				// Toast.makeText(getActivity(),
//				// mAdapter.getItem(position),Toast.LENGTH_SHORT).show();
//				// System.out.println(position);
//				switch (position) {
//					case 0:
//						Intent i = new Intent(getApplicationContext(),
//								SuraListV2Activity.class);
//						startActivity(i);
//						break;
//					case 1:
//						Intent b = new Intent(getApplicationContext(),
//								BookmarkActivity.class);
//						startActivity(b);
//						break;
//					case 2:
//						Intent s = new Intent(getApplicationContext(),
//								SearchActivity.class);
//						startActivity(s);
//						break;
//					case 3:
//						Intent q = new Intent(getApplicationContext(),
//								QuickLinksActivity.class);
//						startActivity(q);
//						break;
//					case 4:
//						Intent d = new Intent(getApplicationContext(),
//                                DictionaryActivity.class);
//						startActivity(d);
//						break;
//					case 5:
//						Intent a = new Intent(getApplicationContext(),
//								AboutActivity.class);
//						startActivity(a);
//						break;
//					default:
//						break;
//				}
//			}
//		});

        mDBHelper = new DatabaseHelper(this);

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }


        //NotificationHelper.scheduleRepeatingRTCNotification(getApplicationContext(), "22", "10");
        //NotificationHelper.enableBootReceiver(getApplicationContext());

        dbhelper = new DatabaseHelper(getApplicationContext());

        start_from_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbhelper.getWritableDatabase();
                String sql = "SELECT last_position.sura_id,position,name_english,name_arabic " +
                        "FROM last_position " +
                        "LEFT JOIN sura ON last_position.sura_id = sura.surah_id " +
                        "LIMIT 1";
                Log.d("Last Position SQL", sql);
                Cursor cursor = db.rawQuery(sql, null);
                try {
                    if (cursor.moveToFirst()) {
                        String sura_id = cursor.getString(cursor.getColumnIndex("sura_id")).toString();
                        String position = cursor.getString(cursor.getColumnIndex("position")).toString();
                        String sura_name = cursor.getString(cursor.getColumnIndex("name_english")).toString();
                        String sura_name_arabic = cursor.getString(cursor.getColumnIndex("name_arabic")).toString();

                        Log.d("Last Position Sura", sura_id);
                        Log.d("Last Position Sura POS", position);
                        //
                        Intent in = new Intent(getApplicationContext(), SuraDetailsActivity.class);
                        in.putExtra("sura_id", sura_id);
                        in.putExtra("sura_name", sura_name);
                        in.putExtra("sura_name_arabic", sura_name_arabic);
                        in.putExtra("position", position);
                        startActivityForResult(in, 100);
                    }
                } catch (Exception e) {
                    Log.i("Last Position Select", e.getMessage());
                } finally {
                    db.close();
                }
            }
        });

//        btnPrayerTime = (Button) findViewById(R.id.btnPrayerTime);
//        btnPrayerTime.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(),PrayerTimesActivity.class);
//                startActivity(i);
//            }
//        });
        //popular searches
        popularSearchView = (RecyclerView) findViewById(R.id.popularSearchView);
        LinearLayoutManager rLinearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        popularSearchView.setLayoutManager(rLinearLayoutManager);
        setPopularSearchViewAdapter();
        popularSearchView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), popularSearchView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Sura vd = popularSearches.get(position);
                //Toast.makeText(getApplicationContext(), vd.getVideo_id() + " is selected!", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(getApplicationContext(),SuraDetailsActivity.class);
                in.putExtra("sura_id", vd.getSurah_id());
                in.putExtra("sura_name", vd.getName_english());
                in.putExtra("sura_name_arabic", vd.getName_arabic());
                startActivityForResult(in, 100);
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        getPopularSearchFromLocalDb();
    }

    private void getPopularSearchFromLocalDb() {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String sql = "SELECT * FROM sura WHERE surah_id IN(18,36,55,56,67,71,73,78,85,112) order by RANDOM() ASC LIMIT 10";
        Log.i(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Sura sura = new Sura();
                    sura.setSurah_id(cursor.getString(cursor.getColumnIndex("surah_id")));
                    sura.setName_arabic(cursor.getString(cursor.getColumnIndex("name_arabic")));
                    sura.setName_english(cursor.getString(cursor.getColumnIndex("name_english")));
                    sura.setName_simple(cursor.getString(cursor.getColumnIndex("name_simple")));
                    sura.setRevelation_place(cursor.getString(cursor.getColumnIndex("revelation_place")));
                    sura.setAyat(cursor.getString(cursor.getColumnIndex("ayat")));
                    sura.setRevelation_order(cursor.getString(cursor.getColumnIndex("revelation_order")));
                    sura.setId(cursor.getString(cursor.getColumnIndex("sid")));
                    popularSearches.add(sura);
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.i(TAG, e.getMessage());
        }
        finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            db.close();
        }
        rvAdapter.notifyDataSetChanged();
    }

    private void setPopularSearchViewAdapter() {
        popularSearches = new ArrayList<Sura>();
        rvAdapter = new PopularRecyclerViewAdapter(MainActivity.this, popularSearches);
        popularSearchView.setAdapter(rvAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void prepareList() {
        listText = new ArrayList<String>();

        listText.add("Sura list");
        listText.add("Bookmarks");
        listText.add("Search");
        listText.add("Quick Links");
        listText.add("Dictionary");
        listText.add("About");

        listImage = new ArrayList<Integer>();
        listImage.add(R.drawable.menu_icon);
        listImage.add(R.drawable.bookmark);
        listImage.add(R.drawable.search);
        listImage.add(R.drawable.links);
        listImage.add(R.drawable.collection);
        listImage.add(R.drawable.about);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.d("ACTIVTY","RESUME");
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String sql = "SELECT last_position.sura_id,position,name_english,name_arabic " +
                "FROM last_position " +
                "LEFT JOIN sura ON last_position.sura_id = sura.surah_id " +
                "LIMIT 1";
        Log.d("Last Position SQL", sql);
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.moveToFirst()) {
                start_from_last.setVisibility(View.VISIBLE);
                horizontal_line.setVisibility(View.VISIBLE);
            } else {
                start_from_last.setVisibility(View.GONE);
                horizontal_line.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.i("Last Position Select", e.getMessage());
        } finally {
            db.close();
        }
    }
}
