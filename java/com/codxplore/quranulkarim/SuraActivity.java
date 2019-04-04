package com.codxplore.quranulkarim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.codxplore.quranulkarim.CustomAdapter;

public class SuraActivity extends ListActivity {

	private ProgressDialog pDialog;
	ArrayList<HashMap<String, String>> suraDetails;

	private static final String tag_ayah_index = "ayah_num";
	private static final String tag_text_tashkeel = "text_tashkeel";
	private static final String tag_translation_en = "translation_en";
	private static final String tag_translation_bn = "translation_bn";
	private static final String tag_audio_url = "audio_url";
	private static final String tag_sura_id = "sura_id";
	private static final String tag_sura_name = "sura_name";
	private static final String tag_total_ayah = "total_ayah_num";

	String suraId;
	String suraName;
	String suraNameAr;
	String suraAyahNum;

	int currentId = 0;
	int nextId = 0;
	int currentSuraId = 0;
	int prevId = 0;
	int nextProcessId = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			suraId = extras.getString("suraId");
			suraName = extras.getString("suraName");
			suraNameAr = extras.getString("suraNameAr");
			suraAyahNum = extras.getString("suraAyahNum");
		}

		setTitle(suraName);

		setContentView(R.layout.activity_sura);

		TextView titleEn = (TextView) findViewById(R.id.name_title_en);
		titleEn.setText(suraName);

		TextView titleAr = (TextView) findViewById(R.id.name_title_ar);
		titleAr.setText(suraNameAr);

		suraDetails = new ArrayList<HashMap<String, String>>();

		new LoadSuraAyah().execute();

		ListView lv = getListView();

		ImageView next = (ImageView) findViewById(R.id.next_icon);
		ImageView prev = (ImageView) findViewById(R.id.prev_icon);

		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currentId = Integer.parseInt(suraId);
				nextId = currentId;
				currentSuraId = currentId - 1;
				prevId = currentSuraId - 1;

				if (currentId < 114) {
					nextProcessId = nextId;
					loadSura();
				}
			}
		});

		prev.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currentId = Integer.parseInt(suraId);
				nextId = currentId;
				currentSuraId = currentId - 1;
				prevId = currentSuraId - 1;

				if (currentId > 1) {
					nextProcessId = prevId;
					loadSura();
				}
			}
		});

	}

	public void loadSura() {
		new LoadSuraById().execute();
	}

	class LoadSuraById extends AsyncTask<String, String, String> {

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

			try {
				JSONArray json = new JSONArray(sb.toString());

				if (nextProcessId != -1) {
					JSONObject e = json.getJSONObject(nextProcessId);

					String sid = e.getString("surah_id").toString();
					String sname = e.getString("name_simple").toString();
					String sname_ar = e.getString("name_arabic");
					String sayat = e.getString("ayat");

					// Log.d("ayah: ", e.toString());

					Intent in = new Intent(getApplicationContext(),
							SuraActivity.class);
					// sending cid to next activity
					in.putExtra("suraId", sid);
					in.putExtra("suraName", sname);
					in.putExtra("suraNameAr", sname_ar);
					in.putExtra("suraAyahNum", sayat);

					// starting new activity and expecting some response back
					startActivityForResult(in, 100);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class LoadSuraAyah extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SuraActivity.this);
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
						suraId + ".json")));
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

					String ayah_index = e.getString(tag_ayah_index);
					String text_tashkeel = e.getString(tag_text_tashkeel);

					// creating new HashMap
					HashMap<String, String> map = new HashMap<String, String>();

					JSONArray translation = e.optJSONArray("content");

					JSONObject obj = new JSONObject(e.getString("audio"));

					String audio_url = obj.getString("url");

					// System.out.println(translation.getJSONObject(1).getString("text"));

					String translation_en = translation.getJSONObject(0)
							.getString("text");
					String translation_bn = translation.getJSONObject(1)
							.getString("text");

					// adding each child node to HashMap key => value
					map.put(tag_ayah_index, ayah_index);
					map.put(tag_text_tashkeel, text_tashkeel);
					map.put(tag_translation_en, translation_en);
					map.put(tag_translation_bn, translation_bn);
					map.put(tag_audio_url, audio_url);
					map.put(tag_sura_id, suraId);
					map.put(tag_sura_name, suraName);
					map.put(tag_total_ayah, suraAyahNum);

					// adding HashList to ArrayList
					suraDetails.add(map);

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
					String[] from = new String[] { tag_ayah_index,
							tag_text_tashkeel, tag_translation_en,
							tag_translation_bn, tag_audio_url, tag_sura_id,
							tag_sura_name, tag_total_ayah };
					int[] to = new int[] { R.id.ayah_index, R.id.text_tashkeel,
							R.id.translation_en, R.id.translation_bn,
							R.id.audio_url, R.id.sura_id, R.id.sura_name,
							R.id.total_ayah_num };
					// ListAdapter adapter = new SimpleAdapter(
					// SuraActivity.this, suraDetails,
					// R.layout.surah_ayah_list, new String[]
					// {tag_ayah_index,tag_text_tashkeel,tag_translation_en,tag_translation_bn},
					// new int[] { R.id.ayah_index, R.id.text_tashkeel,
					// R.id.translation_en,R.id.translation_bn});

					CustomAdapter adapter = new CustomAdapter(
							getApplicationContext(), suraDetails,
							R.layout.surah_ayah_list, from, to);

					setListAdapter(adapter);
				}
			});

		}
	}

}
