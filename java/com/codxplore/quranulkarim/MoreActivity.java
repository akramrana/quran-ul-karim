package com.codxplore.quranulkarim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MoreActivity extends ListActivity {

	String suraId;
	String suraName;
	String ayahId;
	String ayahText;
	String ayahNum;

	String tag_ayah_text;
	String tag_translation_en;
	String tag_translation_bn;

	private static final String tag_word_id = "word_id";
	private static final String tag_word_translation = "translation";
	private static final String tag_word_transliteration = "transliteration";
	private static final String tag_word_arabic = "arabic";

	ArrayList<HashMap<String, String>> words;

	Typeface font;

	int currentId = 0;
	int nextId = 0;
	int currentAyaId = 0;
	int prevId = 0;
	int nextProcessId = 0;
	int total = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			suraId = extras.getString("suraId");
			suraName = extras.getString("suraName");
			ayahId = extras.getString("ayahId");
			ayahText = extras.getString("ayahText");
			ayahNum = extras.getString("ayahNum");
		}

		total = Integer.parseInt(ayahNum);

		// System.out.println(total);

		setTitle(suraName + ":" + ayahId);

		setContentView(R.layout.activity_more);

		words = new ArrayList<HashMap<String, String>>();

		new LoadAyah().execute();

		ListView lv = getListView();

		font = Typeface.createFromAsset(getApplicationContext().getAssets(),
				"fonts/Siyamrupali.ttf");

		ImageView next = (ImageView) findViewById(R.id.next_icon);
		ImageView prev = (ImageView) findViewById(R.id.prev_icon);

		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currentId = Integer.parseInt(ayahId);
				nextId = currentId;
				currentAyaId = currentId - 1;
				prevId = currentAyaId - 1;

				if (currentId < total) {
					nextProcessId = nextId;
					loadAyahById();
				}
			}
		});

		prev.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currentId = Integer.parseInt(ayahId);
				nextId = currentId;
				currentAyaId = currentId - 1;
				prevId = currentAyaId - 1;

				if (currentId > 0) {
					nextProcessId = prevId;
					loadAyahById();
				}
			}
		});

	}

	public void loadAyahById() {
		new LoadAyahById().execute();
	}

	class LoadAyahById extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
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

			try {

				JSONArray json = new JSONArray(sb.toString());

				if (nextProcessId != -1) {
					JSONObject e = json.getJSONObject(nextProcessId);

					String ayahId = e.getString("ayah_num");
					String ayahText = e.getString("text_tashkeel");
					// Log.d("ayah: ", e.toString());
					Intent in = new Intent(getApplicationContext(),
							MoreActivity.class);
					in.putExtra("suraId", suraId);
					in.putExtra("suraName", suraName);
					in.putExtra("ayahId", ayahId);
					in.putExtra("ayahText", ayahText);
					in.putExtra("ayahNum", ayahNum);
					startActivityForResult(in, 100);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}
	}

	class LoadAyah extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
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

			try {

				JSONArray json = new JSONArray(sb.toString());

				int i = Integer.parseInt(ayahId) - 1;

				JSONObject e = json.getJSONObject(i);

				tag_ayah_text = e.getString("text").toString();

				JSONArray wordList = e.getJSONArray("words");

				JSONArray translation = e.optJSONArray("content");

				String translation_en = translation.getJSONObject(0).getString(
						"text");
				String translation_bn = translation.getJSONObject(1).getString(
						"text");

				tag_translation_en = translation_en;
				tag_translation_bn = translation_bn;

				int n = wordList.length() - 1;

				for (int j = 0; j < n; j++) {

					JSONObject c = wordList.getJSONObject(j);

					// creating new HashMap
					HashMap<String, String> map = new HashMap<String, String>();

					String word_id = c.getString(tag_word_id);
					String word_translation = c.getString(tag_word_translation);
					String word_transliteration = c
							.getString(tag_word_transliteration);
					String word_arabic = c.getString(tag_word_arabic);

					// Log.d("text: ", word_arabic);

					map.put(tag_word_id, word_id);
					map.put(tag_word_translation, word_translation);
					map.put(tag_word_transliteration, word_transliteration);
					map.put(tag_word_arabic, word_arabic);

					words.add(map);
				}

				// Log.d("ayah: ", e.toString());

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			runOnUiThread(new Runnable() {
				public void run() {
					TextView ayah = (TextView) findViewById(R.id.ayah_text);
					ayah.setText(ayahText);

					TextView translation_en = (TextView) findViewById(R.id.translation_en);
					translation_en.setText(tag_translation_en);

					TextView translation_bn = (TextView) findViewById(R.id.translation_bn);
					translation_bn.setText(tag_translation_bn);
					translation_bn.setTypeface(font);

					ListAdapter adapter = new SimpleAdapter(
							MoreActivity.this,
							words,
							R.layout.word_list,
							new String[] { tag_word_id, tag_word_translation,
									tag_word_transliteration, tag_word_arabic },
							new int[] { R.id.word_id, R.id.word_translation,
									R.id.word_transliteration, R.id.word_arabic });

					setListAdapter(adapter);
				}
			});
		}

	}
}
