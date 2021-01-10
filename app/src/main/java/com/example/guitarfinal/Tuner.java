package com.example.guitarfinal;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Tuner extends AppCompatActivity implements PitchFragment.OnNoteDetectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuner);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, PitchFragment.class, null)
                    .commit();
        }
    }

    @Override
    public void onNoteDetected(Note note) {
        if(note.getMidi() == getResources().getInteger(R.integer.Sv1)){
            TextView textView = this.findViewById(R.id.S1);
            textView.setBackgroundResource(R.drawable.text_round_bg_green);
        }
        if(note.getMidi() == getResources().getInteger(R.integer.Sv2)){
            TextView textView = this.findViewById(R.id.S2);
            textView.setBackgroundResource(R.drawable.text_round_bg_green);
        }
        if(note.getMidi() == getResources().getInteger(R.integer.Sv3)){
            TextView textView = this.findViewById(R.id.S3);
            textView.setBackgroundResource(R.drawable.text_round_bg_green);
        }
        if(note.getMidi() == getResources().getInteger(R.integer.Sv4)){
            TextView textView = this.findViewById(R.id.S4);
            textView.setBackgroundResource(R.drawable.text_round_bg_green);
        }
        if(note.getMidi() == getResources().getInteger(R.integer.Sv5)){
            TextView textView = this.findViewById(R.id.S5);
            textView.setBackgroundResource(R.drawable.text_round_bg_green);
        }
        if(note.getMidi() == getResources().getInteger(R.integer.Sv6)){
            TextView textView = this.findViewById(R.id.S6);
            textView.setBackgroundResource(R.drawable.text_round_bg_green);
        }
    }
}