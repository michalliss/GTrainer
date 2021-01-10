package com.example.guitarfinal;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity implements PitchFragment.OnNoteDetectedListener {
    List<String> noteNames = new ArrayList<String>();
    ListView noteList;
    ArrayAdapter<String> noteAdapter;
    Note currentNote;
    TextView currentNoteText;

    boolean finished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, PitchFragment.class, null)
                    .commit();
        }

        // Get note list from bundle
        Bundle b = getIntent().getExtras();
        if (b != null)
            noteNames = b.getStringArrayList("notes");

        // Disable scrolling
        noteList = findViewById(R.id.noteList);
        noteList.setEnabled(false);

        // Fetch first note
        currentNote = new Note(noteNames.get(0));
        noteNames.remove(0);
        currentNoteText = findViewById(R.id.currentNote);
        currentNoteText.setText(currentNote.getFullName());

        // Update adapter
        noteAdapter = new ArrayAdapter<String>(this, R.layout.note_list, R.id.Row, noteNames);
        noteList.setAdapter(noteAdapter);
    }


    @Override
    public void onNoteDetected(Note note) {
        if (note.getFullName().equals(currentNote.getFullName())) {
            if (finished) return;

            // Update note statistics
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            int noteVal = sharedPref.getInt("stat_" + note.getName(), 0);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("stat_" + note.getName(), noteVal + 1);
            editor.apply();

            // Finish condition
            if (noteNames.size() == 0) {
                finished = true;
                onWin();
                return;
            }

            // Fetch next note
            currentNote = new Note(noteNames.get(0));
            currentNoteText.setText(currentNote.getFullName());
            noteNames.remove(0);
            noteAdapter.notifyDataSetChanged();
        }
    }

    private void onWin() {

        // Update win statistics
        Toast.makeText(getApplicationContext(), "Well done!", Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int winVal = sharedPref.getInt("stat_win", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("stat_win", winVal + 1);
        editor.apply();
        finish();
    }
}