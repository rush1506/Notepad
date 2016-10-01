package com.learning.tata.notepad;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import data.noteDatabaseHandler;
import model.Notepad;

public class DisplayNote extends AppCompatActivity {

    private noteDatabaseHandler Notedba;
    private ArrayList<Notepad> dbNotes = new ArrayList<>();
    private NoteAdapter noteAdapter;
    private ListView noteListView;
    private static final int CAMERA_REQUEST = 1888;
    private static final int PICK_PHOTO_FROM_GALLERY = 2016;
    private int currentID = -1;
    private ImageView testIm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_note);

        noteListView = (ListView) findViewById(R.id.noteList);

        refreshData();

    }

    private void refreshData() {
        dbNotes.clear();
        Notedba = new noteDatabaseHandler(getApplicationContext());

        ArrayList<Notepad> notesFromDB = Notedba.getNotes();

        for (int i = 0; i < notesFromDB.size(); i++) {

            String title = notesFromDB.get(i).getNoteTitle();
            String content = notesFromDB.get(i).getNoteContent();
            String date = notesFromDB.get(i).getNoteDate();
            int mid = notesFromDB.get(i).getItemID();

            Notepad tmpNote = new Notepad();
            tmpNote.setNoteTitle(title);
            tmpNote.setNoteContent(content);
            tmpNote.setNoteDate(date);
            tmpNote.setItemID(mid);

            dbNotes.add(tmpNote);
        }
        Notedba.close();
        //setup adapter

        noteAdapter = new NoteAdapter(DisplayNote.this, R.layout.note_row, dbNotes);
        noteListView.setAdapter(noteAdapter);
        noteAdapter.notifyDataSetChanged();

    }

    public class NoteAdapter extends ArrayAdapter<Notepad> {
        Activity activity;
        int layoutRes;
        ArrayList<Notepad> mData = new ArrayList<>();


        public NoteAdapter(Activity act, int resource, ArrayList<Notepad> data) {
            super(act, resource, data);
            activity = act;
            layoutRes = resource;
            mData = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Notepad getItem(int position) {
            return mData.get(position);
        }

        @Override
        public int getPosition(Notepad item) {
            return super.getPosition(item);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = convertView;
            ViewHolder holder = null;

            if (row == null || (row.getTag()) == null) {

                LayoutInflater inflater = LayoutInflater.from(activity);
                row = inflater.inflate(layoutRes, null);
                holder = new ViewHolder();

                holder.mTitle = (TextView) row.findViewById(R.id.noteTitle);
                holder.mDate = (TextView) row.findViewById(R.id.noteDate);
                holder.mContent = (TextView) row.findViewById(R.id.noteContent);
                holder.mDeleteButton = (Button) row.findViewById(R.id.deleteButton);
                holder.mNotePicture = (ImageView) row.findViewById(R.id.notePicture);

                //Recycle the view
                row.setTag(holder);

            } else {
                holder = (ViewHolder) row.getTag();
            }
            holder.mNote = getItem(position);

            holder.mTitle.setText(holder.mNote.getNoteTitle());
            holder.mDate.setText(holder.mNote.getNoteDate());
            holder.mContent.setText(holder.mNote.getNoteContent());
            holder.mId = holder.mNote.getItemID();
            //get images from internal storage
            loadImageFromStorage(holder.mId, holder);


            final ViewHolder finalHolder = holder;
            holder.mTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String text = finalHolder.mNote.getNoteContent().toString();
                    String dateText = finalHolder.mNote.getNoteDate().toString();
                    String title = finalHolder.mNote.getNoteTitle().toString();

                    int mid = finalHolder.mNote.getItemID();


                    Intent i = new Intent(DisplayNote.this, note_detail.class);
                    i.putExtra("content", text);
                    i.putExtra("title", title);
                    i.putExtra("date", dateText);
                    i.putExtra("id", mid);

                    startActivity(i);

                }
            });

            holder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int mid = finalHolder.mNote.getItemID();
                    noteDatabaseHandler dba = new noteDatabaseHandler(getApplicationContext());
                    dba.deleteNote(mid);
                    dba.close();
                    Toast.makeText(DisplayNote.this, "Note deleted! ", Toast.LENGTH_LONG).show();


                    startActivity(new Intent(DisplayNote.this, DisplayNote.class));
                    //Add this to clear previous activity
                    DisplayNote.this.finish();
                }
            });

            holder.mNotePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentID = finalHolder.mNote.getItemID();
                    //Correct ID has been taken here
                    selectImage(currentID, finalHolder);
                }
            });


            return row;


        }

        class ViewHolder {
            TextView mTitle;
            TextView mContent;
            int mId;
            TextView mDate;
            Notepad mNote;
            Button mDeleteButton;
            ImageView mNotePicture;
        }

        private void loadImageFromStorage(int id, ViewHolder v) {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

            try {
                File f = new File(directory.getAbsolutePath(), String.valueOf(id) + ".jpg");
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                v.mNotePicture.setImageBitmap(b);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                //Toast.makeText(DisplayNote.this, "Can't open image", Toast.LENGTH_SHORT).show();
            }

        }



        private void selectImage(final int id, ViewHolder viewHolder) {
            final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Delete", "Cancel"};


            AlertDialog.Builder builder = new AlertDialog.Builder(DisplayNote.this);

            builder.setTitle("Add Photo!");

            builder.setItems(options, new DialogInterface.OnClickListener() {

                @Override

                public void onClick(DialogInterface dialog, int item) {

                    if (options[item].equals("Take Photo")) {

                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                    else if (options[item].equals("Choose from Gallery")) {

                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, PICK_PHOTO_FROM_GALLERY);

                    }
                    else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                    else if (options[item].equals("Delete")) {

                        ContextWrapper cw = new ContextWrapper(getApplicationContext());
                        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

                        File f = new File(directory.getAbsolutePath(), String.valueOf(id) + ".jpg");

                        boolean deleteFile = f.delete();

                        startActivity(new Intent(DisplayNote.this, DisplayNote.class));
                        //Add this to clear previous activity
                        DisplayNote.this.finish();
                    }

                }

            });
            builder.show();

        }




    }


    private String saveToInternalStorage(Bitmap bitmapImage, int id) {

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, String.valueOf(id) + ".jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == CAMERA_REQUEST) {

                if (resultCode == RESULT_OK) {

                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    saveToInternalStorage(photo, currentID);

                    startActivity(new Intent(DisplayNote.this, DisplayNote.class));
                    //Add this to clear previous activity
                    DisplayNote.this.finish();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(DisplayNote.this, "Request Cancelled", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == PICK_PHOTO_FROM_GALLERY) {

                if (resultCode == RESULT_OK) {

                    if (data == null) {
                        Toast.makeText(DisplayNote.this, "No picture was chosen!", Toast.LENGTH_SHORT).show();
                        return;
                    } else {


                        //Error below cannot use inputStream
                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        Bitmap photo = BitmapFactory.decodeStream(inputStream);
                        String path = saveToInternalStorage(photo, currentID);
                        inputStream.close();

                        startActivity(new Intent(DisplayNote.this, DisplayNote.class));
                        //Add this to clear previous activity
                        DisplayNote.this.finish();

                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(DisplayNote.this, "Request Cancelled!", Toast.LENGTH_SHORT).show();
                }

            }
        } catch (Exception e) {
            Toast.makeText(DisplayNote.this, "Can't fetch picture", Toast.LENGTH_SHORT).show();
        }
    }

}
