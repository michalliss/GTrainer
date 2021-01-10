package com.example.guitarfinal;
import com.google.common.collect.EvictingQueue;

public class PitchSmoother {
    private EvictingQueue<Double> values;

    public PitchSmoother(int n){
        this.values = EvictingQueue.create(n);
    }

    public void put(double val){
        values.add(val);
    }

    public double getAvg(){
        double avg = 0;
        for (double val : values){
            avg += val / (values.size());
        }
        System.out.println(values);
        return avg;
    }
}
