package com.example.guitarfinal;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class PitchFragment extends Fragment {

    public interface OnNoteDetectedListener {
        void onNoteDetected(Note note);
    }

    private Note currentNote = new Note(1);
    private Note desiredNote = new Note("A", 3);
    private PitchSmoother smoothPitch = new PitchSmoother(10);
    PitchMeter pitchMeter;
    AudioDispatcher dispatcher;

    public PitchFragment() {
    }

    public static PitchFragment newInstance() {
        PitchFragment fragment = new PitchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializePlayerAndStartRecording();
    }

    public void onDestroy() {
        dispatcher.stop();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pitch, container, false);
        RelativeLayout relativeLayout = (RelativeLayout) rootView.findViewById(R.id.rect);
        pitchMeter = new PitchMeter(getActivity());
        relativeLayout.addView(pitchMeter);
        return rootView;
    }

    public void processPitch(float pitchInHz) {
        this.currentNote.setPitch(pitchInHz);
        if (desiredNote.getPitch() != Note.getPerfectNote(pitchInHz).getPitch()) {
            this.desiredNote = Note.getPerfectNote(pitchInHz);
        }

        if (pitchInHz != -1) smoothPitch.put(this.currentNote.getPitch());
        pitchMeter.onPitchChange(pitchInHz);
        checkForProperNote(smoothPitch.getAvg());
    }

    private void checkForProperNote(double pitch) {
        if (Note.isCorrect(pitch, desiredNote)) {
            if(getActivity() != null){
                ((OnNoteDetectedListener) getActivity()).onNoteDetected(currentNote);
            }
        }
    }


    @AfterPermissionGranted(123)
    private void initializePlayerAndStartRecording() {
        String[] perms = {Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this.getContext(), perms)) {
            dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            final float sensitivity = (float) sharedPref.getInt("sensitivity", 97) / 100;

            PitchDetectionHandler pdh = new PitchDetectionHandler() {
                @Override
                public void handlePitch(PitchDetectionResult res, AudioEvent e) {
                    if (res.getProbability() > sensitivity) {
                        final float pitchInHz = res.getPitch();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                processPitch(pitchInHz);
                            }
                        });
                    }
                }
            };
            PitchProcessor.PitchEstimationAlgorithm pea = null;
            String prefAlg = sharedPref.getString("algorithm", "FFT_YIN");
            switch (prefAlg) {
                case "FFT_YIN":
                    pea = PitchProcessor.PitchEstimationAlgorithm.FFT_YIN;
                    break;
                case "MPM":
                    pea = PitchProcessor.PitchEstimationAlgorithm.MPM;
                    break;
                case "FFT_PITCH":
                    pea = PitchProcessor.PitchEstimationAlgorithm.FFT_PITCH;
                    break;
                case "DYNAMIC_WAVELET":
                    pea = PitchProcessor.PitchEstimationAlgorithm.DYNAMIC_WAVELET;
                    break;
                case "AMDF":
                    pea = PitchProcessor.PitchEstimationAlgorithm.AMDF;
                    break;
                case "YIN":
                    pea = PitchProcessor.PitchEstimationAlgorithm.YIN;
                    break;
            }


            AudioProcessor pitchProcessor = new PitchProcessor(pea, 22050, 1024, pdh);
            dispatcher.addAudioProcessor(pitchProcessor);
            Thread audioThread = new Thread(dispatcher, "Audio Thread");
            audioThread.start();
        } else {
            EasyPermissions.requestPermissions(this, "We need permissions to record audio", 123, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private class PitchMeter extends View {
        Paint paint = new Paint();
        Paint bgPaint = new Paint();
        Paint textPaint = new Paint();
        double oldAngle;

        public PitchMeter(Context context) {
            super(context);
        }

        public void onPitchChange(float pitch) {
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas) {
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(4);
            bgPaint.setAntiAlias(true);
            bgPaint.setColor(Color.WHITE);

            canvas.drawCircle(getWidth() / 2, getHeight() - 30, 350, bgPaint);
            drawMeter(canvas);
            drawMeterLayout(canvas);

            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(100);

            if(currentNote.getPitch() != -1){
                canvas.drawText("" + currentNote.getName() + "" + currentNote.getOctave(), getWidth() / 2, getHeight() - 80, textPaint);
            }

        }

        private void drawMeter(Canvas canvas) {
            if (currentNote.getPitch() == -1) return;
            paint.setStrokeWidth(10);
            paint.setColor(getResources().getColor(R.color.purple));
            double diff = desiredNote.getPitch() - currentNote.getPitch();
            if (diff > desiredNote.maxDifference()) diff = desiredNote.maxDifference();
            if (diff < -desiredNote.maxDifference()) diff = -desiredNote.maxDifference();

            oldAngle = lerp(oldAngle, Math.PI / 2 + Math.PI / 2 * diff / desiredNote.maxDifference(), 0.3f);
            double length = 300;

            int centerX = getWidth() / 2;
            int centerY = getHeight() - 30;

            canvas.drawLine(centerX, centerY,
                    (float) (centerX + Math.cos(oldAngle) * length),
                    (float) (centerY + Math.sin(-oldAngle) * length),
                    paint);

        }

        private double lerp(double a, double b, double f)
        {
            return a + f * (b - a);
        }

        private void drawMeterLayout(Canvas canvas) {
            paint.setStrokeWidth(10);
            int dot_number = 20;
            double length = 30;
            double handLength = 300;
            int centerX = getWidth() / 2;
            int centerY = getHeight() - 30;
            ColorTransition ct = new ColorTransition(0xff4ED1C1, 0xffd16a4e);
            for (int i = 0; i <= dot_number; i++) {
                float state = Math.abs((float) (i - dot_number / 2)) / (dot_number / 2);
                paint.setColor(ct.getColor(state));
                double angle = Math.PI * (double) i / dot_number;
                canvas.drawLine(
                        (float) (centerX + Math.cos(angle) * handLength),
                        (float) (centerY + Math.sin(-angle) * handLength),
                        (float) (centerX + Math.cos(angle) * (handLength + length)),
                        (float) (centerY + Math.sin(-angle) * (handLength + length)),
                        paint);
            }
        }
    }
}