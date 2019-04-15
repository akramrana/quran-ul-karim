package com.akramhossain.quranulkarim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SuraListActivity extends ListActivity {

	private ProgressDialog pDialog;
	ArrayList<HashMap<String, String>> surahList;

	private static final String tag_surah_id = "surah_id";
	private static final String tag_name_simple = "name_simple";
	private static final String tag_name_english = "name_english";
	private static final String tag_name_arabic = "name_arabic";
	private static final String tag_ayat_num = "ayat";
	private static final String tag_revelation_place = "place";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle("Sura list");

		setContentView(R.layout.activity_suralist);

		surahList = new ArrayList<HashMap<String, String>>();

		new LoadSurah().execute();

		ListView lv = getListView();

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting values from selected ListItem
				String sid = ((TextView) view.findViewById(R.id.surah_id))
						.getText().toString();

				String sname = ((TextView) view.findViewById(R.id.name_simple))
						.getText().toString();

				String sname_ar = ((TextView) view
						.findViewById(R.id.name_arabic)).getText().toString();

				String ayah_num = ((TextView) view.findViewById(R.id.ayah_num))
						.getText().toString();

				Intent in = new Intent(getApplicationContext(),
						SuraActivity.class);
				// sending cid to next activity
				in.putExtra("suraId", sid);
				in.putExtra("suraName", sname);
				in.putExtra("suraNameAr", sname_ar);
				in.putExtra("suraAyahNum", ayah_num);

				// starting new activity and expecting some response back
				startActivityForResult(in, 100);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class LoadSurah extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SuraListActivity.this);
			pDialog.setMessage("Loading. Please wait...");
			// pDialog.setMessage(allUsersUrl);
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected String doInBackground(String... args) {

			StringBuffer sb = new StringBuffer();

			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(getAssets().open(
						"surahs.json")));
				String temp;
				while ((temp = br.readLine()) != null)
					sb.append(temp);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					br.close(); // stop reading
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// Try to parse JSON
			try {
				JSONArray json = new JSONArray(sb.toString());

				for (int i = 0; i < json.length(); i++) {
					JSONObject e = json.getJSONObject(i);

					String id = e.getString(tag_surah_id);
					String name_simple = e.getString(tag_name_simple);
					String name_english = e.getString(tag_name_english);
					String name_arabic = e.getString(tag_name_arabic);
					String ayat_num = e.getString(tag_ayat_num);

					JSONObject obj = new JSONObject(e.getString("revelation"));
					String revelation = obj.getString(tag_revelation_place);

					// creating new HashMap
					HashMap<String, String> map = new HashMap<String, String>();

					// adding each child node to HashMap key => value
					map.put(tag_surah_id, id);
					map.put(tag_name_simple, name_simple);
					map.put(tag_name_english, name_english);
					map.put(tag_name_arabic, name_arabic);
					map.put(tag_ayat_num, ayat_num);
					map.put(tag_revelation_place, revelation);

					// adding HashList to ArrayList
					surahList.add(map);

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {

			pDialog.dismiss();

			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					ListAdapter adapter = new SimpleAdapter(
							SuraListActivity.this, surahList,
							R.layout.surah_list, new String[] { tag_surah_id,
									tag_name_simple, tag_name_english,
									tag_name_arabic, tag_ayat_num,
									tag_revelation_place }, new int[] {
									R.id.surah_id, R.id.name_simple,
									R.id.name_english, R.id.name_arabic,
									R.id.ayah_num, R.id.revelation_place });

					setListAdapter(adapter);
				}
			});

		}

	}

}
