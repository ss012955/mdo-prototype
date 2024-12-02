package HelperClasses;

import static android.app.DownloadManager.COLUMN_ID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class NotesDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    // Database table and column names
    private static final String TABLE_NOTES = "notes";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DATE_TIME = "date_time";
    private static final String COLUMN_SYMPTOMS = "symptoms";
    private static final String COLUMN_MOOD = "mood";
    private static final String COLUMN_MEDICINE = "medicine";

    public NotesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NOTES + " ("
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_DATE_TIME + " TEXT, "
                + COLUMN_SYMPTOMS + " TEXT, "
                + COLUMN_MOOD + " TEXT, "
                + COLUMN_MEDICINE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }
    public List<Note> getLatestNotes(int limit) {
        List<Note> notesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM notes ORDER BY date_time DESC LIMIT ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(limit)});

        while (cursor.moveToNext()) {
            notesList.add(new Note(
                    cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    cursor.getString(cursor.getColumnIndexOrThrow("date_time")),
                    cursor.getString(cursor.getColumnIndexOrThrow("symptoms")),
                    cursor.getString(cursor.getColumnIndexOrThrow("mood")),
                    cursor.getString(cursor.getColumnIndexOrThrow("medicine"))
            ));
        }
        cursor.close();
        return notesList;
    }

    public void addNote(String title, String dateTime, String symptoms, String mood, String medicine) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DATE_TIME, dateTime);
        values.put(COLUMN_SYMPTOMS, symptoms);
        values.put(COLUMN_MOOD, mood);
        values.put(COLUMN_MEDICINE, medicine);
        db.insert(TABLE_NOTES, null, values);
        db.close();
    }

    public Cursor getAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NOTES, null);
    }
}
