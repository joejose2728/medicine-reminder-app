package edu.sjsu.medscan.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

  public static final String TABLE_MEDICINE = "medicines";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_MEDICINE = "medicine";
  public static final String COLUMN_CONSUMPTION = "consumption";

  private static final String DATABASE_NAME = "medicines.db";
  private static final int DATABASE_VERSION = 1;

  private static final String DATABASE_CREATE = "create table "
      + TABLE_MEDICINE + "(" + COLUMN_ID
      + " integer primary key autoincrement, " + COLUMN_MEDICINE
      + " text not null, "+ COLUMN_CONSUMPTION +" text not null );";

  public SQLiteHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  public void onCreate(SQLiteDatabase database) {
    database.execSQL(DATABASE_CREATE);
  }

  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(SQLiteHelper.class.getName(),
        "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICINE);
    onCreate(db);
  }

} 