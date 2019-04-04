package com.codxplore.quranulkarim;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.codxplore.quranulkarim.GridviewAdapter;

@SuppressLint("NewApi")
public class HomeFragment extends Fragment {

	private GridviewAdapter mAdapter;
	private ArrayList<String> listText;
	private ArrayList<Integer> listImage;

	private GridView gridView;

	public HomeFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_home, container,
				false);

		prepareList();

		// prepared arraylist and passed it to the Adapter class
		mAdapter = new GridviewAdapter(getActivity(), listText, listImage);

		// Set custom adapter to gridview
		gridView = (GridView) rootView.findViewById(R.id.gridView1);
		gridView.setAdapter(mAdapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// Toast.makeText(getActivity(),
				// mAdapter.getItem(position),Toast.LENGTH_SHORT).show();
				// System.out.println(position);
				switch (position) {
				case 0:
					Intent i = new Intent(getActivity(), SuraListActivity.class);
					startActivity(i);
					break;
				default:
					break;
				}
			}
		});

		return rootView;
	}

	public void prepareList() {
		listText = new ArrayList<String>();

		listText.add("Sura list");
		listText.add("Bookmark");
		listText.add("Search");
		listText.add("Feedback");

		listImage = new ArrayList<Integer>();
		listImage.add(R.drawable.menu_icon);
		listImage.add(R.drawable.bookmark);
		listImage.add(R.drawable.search);
		listImage.add(R.drawable.send_message);
	}

}
