package com.learning.tata.notepad;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import data.noteDatabaseHandler;
import model.Notepad;

public class MainActivity extends AppCompatActivity {
    private EditText title;
    private EditText content;
    private Button saveButton;
    private noteDatabaseHandler Notedb;
    private SlidingPaneLayout slidePanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Notedb = new noteDatabaseHandler(MainActivity.this);

        title = (EditText) findViewById(R.id.titleEdit);
        content = (EditText) findViewById(R.id.contentEdit);
        saveButton = (Button) findViewById(R.id.saveButton);
        slidePanel = (SlidingPaneLayout) findViewById(R.id.slidingPanelLeft);
        slidePanel.setSliderFadeColor(Color.TRANSPARENT);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToDB();

            }
        });
        SlidingPaneLayout.PanelSlideListener panelSlideListener = new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Toast.makeText(MainActivity.this, "Slide On", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPanelOpened(View panel) {
                Toast.makeText(MainActivity.this, "Slide Open", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPanelClosed(View panel) {

                Toast.makeText(MainActivity.this, "Slide closed", Toast.LENGTH_SHORT).show();
            }
        };

    }





    private void saveToDB() {

        Notepad Note = new Notepad();
        Note.setNoteTitle(title.getText().toString().trim());
        Note.setNoteContent(content.getText().toString().trim());

        Notedb.addNote(Note);
        Notedb.close();

        //Clear title and context
        title.setText("");
        content.setText("");

        //go to list screen
        Intent displayNote = new Intent(MainActivity.this, DisplayNote.class);
        startActivity(displayNote);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public void startDisplayNote(MenuItem item) {
        //go to list screen
        Intent displayNote = new Intent(MainActivity.this, DisplayNote.class);
        startActivity(displayNote);
    }

    public void startAbout(MenuItem item) {
        final CharSequence[] about = {"Author: Vũ Thành Tâm", "Email: vuthanhtam1506@gmail.com"};


        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("About");
        builder.setItems(about, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
}
