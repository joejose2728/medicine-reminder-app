package edu.sjsu.medscan.dao;

import java.util.ArrayList;
import java.util.List;

import edu.sjsu.medscan.core.model.Medicine;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MedicinesDataSource {

  private SQLiteDatabase database;
  private SQLiteHelper dbHelper;
  private String[] allColumns = { SQLiteHelper.COLUMN_ID, SQLiteHelper.COLUMN_MEDICINE, SQLiteHelper.COLUMN_CONSUMPTION };

  public MedicinesDataSource(Context context) {
    dbHelper = new SQLiteHelper(context);
  }

  public void open() throws SQLException {
    database = dbHelper.getWritableDatabase();
  }

  public void close() {
    dbHelper.close();
  }

  public long createMedicine(String medicineName, String consumptionDetails) {
    ContentValues values = new ContentValues();
    values.put(SQLiteHelper.COLUMN_MEDICINE, medicineName);
    values.put(SQLiteHelper.COLUMN_CONSUMPTION, consumptionDetails);
    return database.insert(SQLiteHelper.TABLE_MEDICINE, null,values);
  }


  public List<Medicine> getAllMedicines() {
    List<Medicine> medicines = new ArrayList<Medicine>();

    Cursor cursor = database.query(SQLiteHelper.TABLE_MEDICINE,allColumns, null, null, null, null, null);
    cursor.moveToFirst();

    while (!cursor.isAfterLast()) {
      Medicine m = new Medicine();
      m.setMedicine(cursor.getString(1));
      m.setConsumption(cursor.getString(2));
      medicines.add(m);
      cursor.moveToNext();
    }
    cursor.close();
    System.out.println("Size:" + medicines.size());
    return medicines;
  }

} 