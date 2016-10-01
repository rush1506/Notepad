package data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import model.Notepad;

/**
 * Created by vutha_000 on 7/12/2016.
 */
public class noteDatabaseHandler extends SQLiteOpenHelper {
    private final ArrayList<Notepad> noteList = new ArrayList<>();

    public noteDatabaseHandler(Context context) {
        super(context, noteConstant.DATABASE_NAME, null, noteConstant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Creating table
        String CREATE_NOTE_TABLE = "CREATE TABLE " + noteConstant.TABLE_NAME + "("
                + noteConstant.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + noteConstant.TITLE_NAME
                + " TEXT, " + noteConstant.CONTENT_NAME + " TEXT, " + noteConstant.DATE_NAME + " LONG);";

        sqLiteDatabase.execSQL(CREATE_NOTE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + noteConstant.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    //add content to table
    public void addNote(Notepad note) {
        SQLiteDatabase noteData = this.getWritableDatabase();

        ContentValues dataValues = new ContentValues();
        dataValues.put(noteConstant.TITLE_NAME, note.getNoteTitle());
        dataValues.put(noteConstant.CONTENT_NAME, note.getNoteContent());
        DateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        dataValues.put(noteConstant.DATE_NAME, date);

        noteData.insert(noteConstant.TABLE_NAME, null, dataValues);
        noteData.close();
        //Give notification on success saved
        // Log.v("Note Saved", "good job!");

    }

    //Get all notes
    public ArrayList<Notepad> getNotes() {
        String SELECT_QUERY = "SELECT * FROM " + noteConstant.TABLE_NAME;

        SQLiteDatabase noteData = this.getReadableDatabase();
        Cursor noteDatabaseCursor = noteData.query(noteConstant.TABLE_NAME,
                new String[]{noteConstant.KEY_ID, noteConstant.TITLE_NAME, noteConstant.CONTENT_NAME, noteConstant.DATE_NAME},
                null, null, null, null, noteConstant.DATE_NAME + " DESC");

        if (noteDatabaseCursor.moveToFirst()) {
            do {
                Notepad note = new Notepad();
                note.setNoteTitle(noteDatabaseCursor.getString(noteDatabaseCursor.getColumnIndex(noteConstant.TITLE_NAME)));
                note.setNoteContent(noteDatabaseCursor.getString(noteDatabaseCursor.getColumnIndex(noteConstant.CONTENT_NAME)));
                note.setItemID(noteDatabaseCursor.getInt(noteDatabaseCursor.getColumnIndex(noteConstant.KEY_ID)));
                //solved errors above: wrong type of cursor therefore all id was 0

                //Log.v("Id when get notes: ", String.valueOf(note.getItemID()));


               // java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
               // DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
               // String dateData = dateFormat.format(new Date(noteDatabaseCursor.getColumnIndex(noteConstant.DATE_NAME)).getTime());

                note.setNoteDate(noteDatabaseCursor.getString(noteDatabaseCursor.getColumnIndex(noteConstant.DATE_NAME)));

                noteList.add(note);

            } while (noteDatabaseCursor.moveToNext());

        }
        noteDatabaseCursor.close();
        noteData.close();

        return noteList;
    }


    //delete medthod

    public void deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(noteConstant.TABLE_NAME, noteConstant.KEY_ID + " = ? ",
                new String[]{String.valueOf(id)});
        db.close();


    }

    //change medthod
    public void changeNote(int id, String title, String content) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues updatedValues = new ContentValues();

            updatedValues.put(noteConstant.TITLE_NAME, title);
            updatedValues.put(noteConstant.CONTENT_NAME, content);
            DateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy");
            String date = df.format(Calendar.getInstance().getTime());
            updatedValues.put(noteConstant.DATE_NAME, date);

            db.update(noteConstant.TABLE_NAME, updatedValues, noteConstant.KEY_ID + "= " + String.valueOf(id), null);

            db.close();
        } catch (Exception e) {
            Log.v("Error! ", "Can't save");
        }

    }


}
