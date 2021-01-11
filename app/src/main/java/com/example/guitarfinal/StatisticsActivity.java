package com.example.guitarfinal;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        TextView statWinText = findViewById(R.id.stat_win);
        int statWin = sharedPref.getInt("stat_win", 0);
        statWinText.setText("" + statWin);

        TextView statLeastNote = findViewById(R.id.stat_least_note);
        TextView statMostNote = findViewById(R.id.stat_most_note);
        HashMap<String, Integer> noteStats = new HashMap<>();
        for(String note : Note.noteNames){
            int noteVal = sharedPref.getInt("stat_" + note, 0);
            noteStats.put(note, noteVal);
        }
        String keyMax = null; //= Collections.max(noteStats.entrySet(), Map.Entry.comparingByValue()).getKey();
        String keyMin = null; //= Collections.min(noteStats.entrySet(), Map.Entry.comparingByValue()).getKey();

        int valMax = Collections.max(noteStats.values());
        for (Map.Entry<String, Integer> entry : noteStats.entrySet()) {
            if (entry.getValue()==valMax) {
                keyMax = entry.getKey();
            }
        }

        int valMin = Collections.min(noteStats.values());
        for (Map.Entry<String, Integer> entry : noteStats.entrySet()) {
            if (entry.getValue()==valMin) {
                keyMin = entry.getKey();
            }
        }

        statLeastNote.setText(keyMin);
        statMostNote.setText(keyMax);

        PieChart pieChart = findViewById(R.id.stat_chart);
        pieChart.setUsePercentValues(true);

        List<PieEntry> values = new ArrayList<>();
        for(Map.Entry<String, Integer> e : noteStats.entrySet()){
            if(e.getValue() != 0){
                values.add(new PieEntry(e.getValue(), e.getKey()));
            }
        }

        PieDataSet pieDataSet = new PieDataSet(values, "Notes");

        PieData pieData = new PieData(pieDataSet);
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);


        pieChart.setDescription(null);
        pieChart.setData(pieData);
    }
}