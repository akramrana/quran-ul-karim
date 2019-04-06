package com.codxplore.quranulkarim;

import java.io.IOException;
import java.util.ArrayList;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

import com.codxplore.quranulkarim.helper.DatabaseHelper;

public class MainActivity extends Activity {

	private GridviewAdapter mAdapter;
	private ArrayList<String> listText;
	private ArrayList<Integer> listImage;

	private GridView gridView;

	private DatabaseHelper mDBHelper;
	private SQLiteDatabase mDb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		prepareList();

		// prepared arraylist and passed it to the Adapter class
		mAdapter = new GridviewAdapter(this, listText, listImage);

		// Set custom adapter to gridview
		gridView = (GridView) findViewById(R.id.gridView1);
		gridView.setAdapter(mAdapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// Toast.makeText(getActivity(),
				// mAdapter.getItem(position),Toast.LENGTH_SHORT).show();
				// System.out.println(position);
				switch (position) {
					case 0:
						Intent i = new Intent(getApplicationContext(),
								SuraListV2Activity.class);
						startActivity(i);
						break;
					case 1:
						Intent b = new Intent(getApplicationContext(),
								BookmarkActivity.class);
						startActivity(b);
						break;
					case 3:
						Intent q = new Intent(getApplicationContext(),
								QuickLinksActivity.class);
						startActivity(q);
						break;
					case 4:
						Intent d = new Intent(getApplicationContext(),
                                DictionaryActivity.class);
						startActivity(d);
						break;
					case 5:
						Intent a = new Intent(getApplicationContext(),
								AboutActivity.class);
						startActivity(a);
						break;
					default:
						break;
				}
			}
		});

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

}
