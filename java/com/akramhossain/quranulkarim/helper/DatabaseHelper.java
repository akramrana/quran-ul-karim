package com.akramhossain.quranulkarim.helper;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by akram on 3/31/2019.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "db.sqlite";
    private static String DB_PATH = "";
    private static final int DB_VERSION = 20;

    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private boolean mNeedUpdate = false;

    private static DatabaseHelper mInstance = null;
    public static DatabaseHelper getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new DatabaseHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.mContext = context;
        copyDataBase();
        this.getReadableDatabase();
        Log.d("Database Helper","DatabaseHelper Invoked");
    }

    public void updateDataBase() throws IOException {
        if (mNeedUpdate) {
            File dbFile = new File(DB_PATH + DB_NAME);
            if (dbFile.exists()) {
                dbFile.delete();
            }
            copyDataBase();
            mNeedUpdate = false;
        }
    }

    private boolean checkDataBase() {
        //File dbFile = new File(DB_PATH + DB_NAME);
        //return dbFile.exists();
        SQLiteDatabase checkDb = null;
        try {
            String mypath = DB_PATH + DB_NAME;
            File dbFile = new File(DB_PATH + DB_NAME);
            if (dbFile.exists()) {
                checkDb = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READONLY);
            }
        }
        catch (Exception e) {
            Log.d("Exception",e.getMessage());
        }
        if (checkDb != null) {
            checkDb.close();
        }
        return checkDb != null? true : false;
        /*File db = new File(DB_PATH);
        if(db.exists()) return true;
        File dir = new File(db.getParent());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return false;*/
    }

    private void copyDataBase() throws Error {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
        Log.d("Database Helper","copyDataBase() Invoked");
    }

    private void copyDBFile() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        //InputStream mInput = mContext.getResources().openRawResource(R.raw.info);
        OutputStream mOutput = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();

        Log.d("Database Helper","copyDBFile() Invoked");
    }

    public boolean openDataBase() throws SQLException {
        mDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null) {
            mDataBase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            mNeedUpdate = true;
        }
    }
}
