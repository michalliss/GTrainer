package com.example.guitarfinal;

import java.util.Arrays;
import java.util.List;

class Note {
    public static final List<String> noteNames = Arrays.asList("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B");
    private static final Note A4 = new Note(440.0);
    private String name;
    private int octave;
    private double pitch;
    private int midi;

    public Note(String fullName) {
        this(fullName.charAt(1) == '#' ? fullName.substring(0, 2) : String.valueOf(fullName.charAt(0)),
                fullName.charAt(1) == '#' ? Integer.valueOf(fullName.substring(2)) : Integer.valueOf(fullName.substring(1)));
    }

    public Note(String name, int octave) {
        int semitonesDiff = noteNames.indexOf(name) - noteNames.indexOf(A4.getName());
        int octaveDiff = octave - A4.getOctave();
        int totalDiff = octaveDiff * 12 + semitonesDiff;

        this.name = name;
        this.octave = octave;
        this.midi = A4.getMidi() + totalDiff;
        this.pitch = midiToPitch(this.midi);
    }

    public Note(double pitch) {
        this.setPitch(pitch);
    }

    public static Note getPerfectNote(double pitch) {
        Note note = new Note(pitch);
        note.pitch = midiToPitch(note.midi);
        return note;
    }

    public static boolean isCorrect(double pitch, Note note) {
        return note.getPitch() - note.lowerCorrectDiff() < pitch && pitch < note.getPitch() + note.upperCorrectDiff();
    }

    private static int pitchToMidi(double pitch) {
        return (int) (Math.round(12 * (Math.log(pitch / 440) / Math.log(2))) + 69);
    }

    private static double midiToPitch(int midi) {
        int semitones = midi - A4.getMidi();
        return A4.getPitch() * Math.pow(2, (double) semitones / 12);
    }

    public void setPitch(double pitch) {
        int n = pitchToMidi(pitch);
        this.midi = n;
        this.name = noteNames.get(n % 12);
        this.octave = n / 12 - 1;
        this.pitch = pitch;
    }

    public double upperDiff() {
        return (midiToPitch(this.midi + 1) - this.pitch) / 2;
    }

    public double lowerDiff() {
        return (this.pitch - midiToPitch(this.midi - 1)) / 2;
    }

    public double upperCorrectDiff() {
        return (midiToPitch(this.midi + 1) - this.pitch) / 5;
    }

    public double lowerCorrectDiff() {
        System.out.println(this.midi + " | " + midiToPitch(this.midi - 1));
        return (this.pitch - midiToPitch(this.midi - 1)) / 5;
    }

    public double maxDifference() {
        return Math.max(upperDiff(), lowerDiff());
    }


    public String getName() {
        return name;
    }

    public String getFullName() {
        return "" + name + this.octave;
    }

    public int getOctave() {
        return octave;
    }

    public double getPitch() {
        return pitch;
    }

    public int getMidi() {
        return midi;
    }
}