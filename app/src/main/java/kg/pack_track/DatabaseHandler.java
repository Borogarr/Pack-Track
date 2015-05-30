package kg.pack_track;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 5/27/2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    //static variables
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Tracking_Nums";

    private static final String TABLE_NAME = "Tracking_Numbers";
    private static final String KEY_ID = "id";
    private static final String KEY_TRACK = "tracking_number";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + KEY_ID
                + " INTEGER PRIMARY KEY," + KEY_TRACK + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //DROP OLDER VERSION
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

    public void addTrackingNumber (String trackingNumber) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRACK, trackingNumber);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public String getTrackingNumber(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] {KEY_ID, KEY_TRACK}, KEY_ID + "=?",
                                 new String[] {String.valueOf(id)}, null, null, null, null );
        if (cursor != null) {
            cursor.moveToFirst();
        }

        cursor.close();
        return cursor.getString(1);
    }

    public List<String> getAllTrackingNums() {
        List<String> trackNums = new ArrayList<String>();

        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                trackNums.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return trackNums;
    }

    public void deleteTrackingNumber(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + "=?", new String[] {String.valueOf(id)});
        db.close();
    }
}