package com.codxplore.quranulkarim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CustomAdapter extends SimpleAdapter {

	ConnectionDetector cd;

	Boolean isInternetPresent = false;

	Typeface font;

	MediaPlayer mp;

	DBHelper dbhelper;

	Context context;

	public CustomAdapter(Context context,
			ArrayList<HashMap<String, String>> data, int layout, String[] from,
			int[] to) {
		super(context, data, layout, from, to);
		font = Typeface.createFromAsset(context.getAssets(),
				"fonts/Siyamrupali.ttf");

		cd = new ConnectionDetector(context);

		isInternetPresent = cd.isConnectingToInternet();

		dbhelper = new DBHelper(context);

		this.context = context;

	}

	@SuppressLint("UseValueOf")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final View view = super.getView(position, convertView, parent);

		TextView bn = (TextView) view.findViewById(R.id.translation_bn);
		bn.setTypeface(font);

		final TextView audio_url = (TextView) view.findViewById(R.id.audio_url);

		final ImageView audio = (ImageView) view.findViewById(R.id.audio_icon);

		final ImageView bookmark = (ImageView) view
				.findViewById(R.id.bookmark_icon);

		final ImageView more = (ImageView) view
				.findViewById(R.id.more_info_icon);

		audio.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				String url = audio_url.getText().toString();

				if (isInternetPresent) {

					try {
						mp = new MediaPlayer();
						mp.setDataSource(url);
						mp.prepare();
						int length = mp.getCurrentPosition();
						mp.seekTo(length);
						mp.start();

					} catch (IOException e) {
						Log.e("MP3", "start() failed");
					}

				} else {
					Toast.makeText(view.getContext(),
							"You don't have internet connection.",
							Toast.LENGTH_LONG).show();
				}

				CustomAdapter.this.notifyDataSetChanged();
			}
		});

		bookmark.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				TextView suraId = (TextView) view.findViewById(R.id.sura_id);
				String sura_id = suraId.getText().toString().trim();

				TextView suraName = (TextView) view
						.findViewById(R.id.sura_name);
				String sura_name = suraName.getText().toString().trim();

				TextView ayahId = (TextView) view.findViewById(R.id.ayah_index);
				String ayah_id = ayahId.getText().toString().trim();

				TextView ayahText = (TextView) view
						.findViewById(R.id.text_tashkeel);
				String ayah_text = ayahText.getText().toString().trim();

				try {
					dbhelper.insertIntoDB(sura_id, sura_name, ayah_id,
							ayah_text);
					Toast.makeText(view.getContext(),
							"Bookmark successfully added.", Toast.LENGTH_LONG)
							.show();
				} catch (Exception e) {
					Log.e("Bookmark", "added failed");
				}

			}
		});

		more.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				TextView suraId = (TextView) view.findViewById(R.id.sura_id);
				String sura_id = suraId.getText().toString().trim();

				TextView suraName = (TextView) view
						.findViewById(R.id.sura_name);
				String sura_name = suraName.getText().toString().trim();

				TextView ayahId = (TextView) view.findViewById(R.id.ayah_index);
				String ayah_id = ayahId.getText().toString().trim();

				TextView ayahText = (TextView) view
						.findViewById(R.id.text_tashkeel);
				String ayah_text = ayahText.getText().toString().trim();

				TextView ayahNum = (TextView) view
						.findViewById(R.id.total_ayah_num);
				String ayah_num_text = ayahNum.getText().toString().trim();

				Intent in = new Intent(context, MoreActivity.class);

				in.putExtra("suraId", sura_id);
				in.putExtra("suraName", sura_name);
				in.putExtra("ayahId", ayah_id);
				in.putExtra("ayahText", ayah_text);
				in.putExtra("ayahNum", ayah_num_text);

				in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				context.startActivity(in);
				// System.out.println(ayah_num_text);

			}
		});

		return view;
	}
}
