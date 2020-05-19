package com.akramhossain.quranulkarim;

import java.io.IOException;
import java.util.ArrayList;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akramhossain.quranulkarim.helper.DatabaseHelper;
import com.akramhossain.quranulkarim.notification.NotificationHelper;

public class MainActivity extends Activity {

    private GridviewAdapter mAdapter;
    private ArrayList<String> listText;
    private ArrayList<Integer> listImage;

    private GridView gridView;

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    TextView start_from_last;
    View horizontal_line;
    DatabaseHelper dbhelper;
    LinearLayout sura_link,bookmark_link,search_link,quick_links_link,word_collection_link,about_link,juz_link,hizb_link,rub_link,time_link;
    Button btnPrayerTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.activity_main);

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
