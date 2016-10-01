package com.learning.tata.notepad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import data.noteDatabaseHandler;

public class note_detail extends AppCompatActivity {

    private EditText title, content;
    private TextView date;
    private Button deleteButton;
    private Button changeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        title = (EditText) findViewById(R.id.detailNoteTitle);
        content = (EditText) findViewById(R.id.detailNoteContent);
        date = (TextView) findViewById(R.id.detailNoteDate);
        deleteButton = (Button) findViewById(R.id.detailDeleteButton);
        changeButton = (Button) findViewById(R.id.detailChangeButton);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            title.setText(extras.getString("title"));
            content.setText(extras.getString("content"));
            date.setText("Created in " + extras.getString("date"));

            final int id = extras.getInt("id");

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    noteDatabaseHandler dba = new noteDatabaseHandler(getApplicationContext());
                    dba.deleteNote(id);
                    dba.close();

                    Toast.makeText(note_detail.this, "Note deleted!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(note_detail.this, DisplayNote.class));
                    note_detail.this.finish();
                }
            });

            changeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    noteDatabaseHandler dba = new noteDatabaseHandler(getApplicationContext());

                    String newTitle = title.getText().toString();
                    String newContent = content.getText().toString();


                    dba.changeNote(id, newTitle, newContent);
                    dba.close();
                    Toast.makeText(note_detail.this, "Note changed!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(note_detail.this, DisplayNote.class));
                    note_detail.this.finish();
                }
            });

        }
    }



}
