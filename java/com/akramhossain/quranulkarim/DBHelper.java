package com.akramhossain.quranulkarim;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static String DB_NAME = "quran.db";
	private SQLiteDatabase db;

	public DBHelper(Context context) {
		super(context, DB_NAME, null, 1);
	}

	public void insertIntoDB(String sura_id, String sura_name, String ayah_id,
			String ayah_text) {

		ContentValues values = new ContentValues();
		values.put("sura_id", sura_id);
		values.put("sura_name", sura_name);
		values.put("ayah_id", ayah_id);
		values.put("ayah_text", ayah_text);

		this.getWritableDatabase().insertOrThrow("bookmark", "", values);
	}

	private static final String TABLE_BOOKMARK_CREATE = "CREATE TABLE 'bookmark' ('bookmark_id' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , 'sura_id' INTEGER, 'sura_name' CHAR, 'ayah_id' INTEGER, 'ayah_text' TEXT)";

	private static final String TABLE_COLLECTION_CREATE = "CREATE TABLE 'collection' ('collection_id' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , 'sura_id' INTEGER NOT NULL , 'name_en' TEXT NOT NULL , 'name_ar' TEXT NOT NULL , 'translation_en' TEXT NOT NULL , 'translation_bn' TEXT, 'ayat_num' INTEGER NOT NULL , 'revelation_place' TEXT NOT NULL )";
	
	private static final String TABLE_WORD_CREATE = "CREATE TABLE 'word' ('word_id' INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , 'word' TEXT NOT NULL , 'transliteration_en' TEXT NOT NULL , 'transliteration_bn' TEXT, 'meaing_en' TEXT NOT NULL , 'meaing_bn' TEXT)";
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(TABLE_BOOKMARK_CREATE);
		db.execSQL(TABLE_COLLECTION_CREATE);
		db.execSQL(TABLE_WORD_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS bookmark");
		db.execSQL("DROP TABLE IF EXISTS collection");
		db.execSQL("DROP TABLE IF EXISTS word");
		onCreate(db);
	}
}
