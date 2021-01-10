package com.example.guitarfinal;

import android.graphics.Color;

public class ColorTransition {
    private int c1;
    private int c2;

    public ColorTransition(int c1, int c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    public int getColor(float state){
        int rDiff = Color.red(c2) - Color.red(c1);
        int gDiff = Color.green(c2) - Color.green(c1);
        int bDiff = Color.blue(c2) - Color.blue(c1);

        int r = (int) (Color.red(c1) + state * rDiff);
        int g = (int) (Color.green(c1) + state * gDiff);
        int b = (int) (Color.blue(c1) + state * bDiff);
        System.out.println(r + " " + g + " " + b);
        return Color.argb(255, r, g, b);
    }
}
