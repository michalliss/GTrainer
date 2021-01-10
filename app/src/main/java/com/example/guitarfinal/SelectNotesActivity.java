package com.example.guitarfinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SelectNotesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_notes);

        final TextView textStart = findViewById(R.id.startOctaveText);
        SeekBar seekbarStart = findViewById(R.id.startOctaveSeek);
        final TextView textLast = findViewById(R.id.lastOctaveText);
        SeekBar seekbarLast = findViewById(R.id.lastOctaveSeek);

        seekbarStart.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textStart.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekbarLast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textLast.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    public void onStartGame(View view) {
        Spinner spinner = findViewById(R.id.noteSpinner);

        String val = spinner.getSelectedItem().toString();
        String[] scaleInfo = val.split(" ");

        String note = scaleInfo[0];
        String scaletype = scaleInfo[1];
        TextView textStart = findViewById(R.id.startOctaveText);
        int sOct = Integer.valueOf(textStart.getText().toString());
        TextView textLast = findViewById(R.id.lastOctaveText);
        int lOct = Integer.valueOf(textLast.getText().toString());

        if(lOct <= sOct) return;

        int startingNote = Note.noteNames.indexOf(note);

        ArrayList<String> scaleNotes = new ArrayList<>();
        List<Integer> steps = null;

        if (scaletype.equals("major")) {
            steps = Arrays.asList(0, 2, 4, 5, 7, 9, 11);
        } else if (scaletype.equals("minor")) {
            steps = Arrays.asList(0, 2, 4, 5, 7, 9, 11);
        }

        for (int i = sOct; i < lOct; i++) {
            for (Integer step : steps) {
                int nextOctave = (startingNote + step) / 12;
                scaleNotes.add(Note.noteNames.get((startingNote + step) % 12) + (i + nextOctave));
            }
        }

        CheckBox randomize = findViewById(R.id.randomizeNotes);
        if(randomize.isChecked()){
            Collections.shuffle(scaleNotes);
        }


        Intent intent = new Intent(this, GameActivity.class);
        Bundle b = new Bundle();
        b.putStringArrayList("notes", scaleNotes);
        intent.putExtras(b);
        startActivity(intent);
    }
}