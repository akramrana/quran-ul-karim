package com.akramhossain.quranulkarim;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akramhossain.quranulkarim.helper.AudioPlay;
import com.akramhossain.quranulkarim.helper.ExoAudioPlay;
import com.akramhossain.quranulkarim.model.Sura;
import com.akramhossain.quranulkarim.task.BackgroundTask;
import com.akramhossain.quranulkarim.util.ConnectionDetector;

import java.util.concurrent.TimeUnit;

public class PopUpClass {

    int oTime = 0;
    int sTime =0;
    int eTime =0;
    int fTime = 5000;
    int bTime = 5000;
    Handler hdlr;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    ImageButton playbtn,pausebtn,backwardbtn,forwardbtn;
    TextView startTime,songTime, txtSuraName, cancelTxt;
    SeekBar songPrgs;
    private boolean isSeeking = false;

    public PopUpClass(){

    }

    public void showPopupWindow(final View view, Sura sura, Context c, Activity activity) {

        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_window, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        cd = new ConnectionDetector(c);
        isInternetPresent = cd.isConnectingToInternet();

        playbtn = popupView.findViewById(R.id.btnPlay);
        pausebtn = popupView.findViewById(R.id.btnPause);
        backwardbtn = popupView.findViewById(R.id.btnBackward);
        forwardbtn = popupView.findViewById(R.id.btnForward);

        startTime = popupView.findViewById(R.id.txtStartTime);
        songTime = popupView.findViewById(R.id.txtSongTime);
        txtSuraName = popupView.findViewById(R.id.txtSuraName);
        cancelTxt = popupView.findViewById(R.id.cancelTxt);

        txtSuraName.setText(sura.getName_simple());

        hdlr = new Handler(Looper.getMainLooper());

        songPrgs = popupView.findViewById(R.id.sBar);
        songPrgs.setClickable(false);
        pausebtn.setEnabled(false);

        AudioPlay.stopAudio();
        ExoAudioPlay.release();

        oTime = 0;
        sTime = 0;
        eTime = 0;

        songTime.setText(String.format("%d min, %d sec", 0, 0));
        startTime.setText(String.format("%d min, %d sec", 0, 0));
        songPrgs.setProgress(0);

        hdlr.removeCallbacksAndMessages(null);

        if (isInternetPresent) {

            playbtn.setOnClickListener(v -> {

                String formatted = String.format(
                        "%03d",
                        Integer.parseInt(sura.getSurah_id())
                );

                String audioUri =
                        "https://download.quranicaudio.com/quran/sa3d_al-ghaamidi/complete/"
                                + formatted + ".mp3";

                if (ExoAudioPlay.isLoaded()
                        && audioUri.equals(ExoAudioPlay.getAudioUri())) {

                    ExoAudioPlay.resume();

                    sTime = (int) ExoAudioPlay.getCurrentPosition();
                    eTime = (int) ExoAudioPlay.getDuration();

                    pausebtn.setEnabled(true);
                    playbtn.setEnabled(false);

                    hdlr.removeCallbacks(UpdateSongTime);
                    hdlr.postDelayed(UpdateSongTime, 1000);

                    return;
                }

                playbtn.setEnabled(false);
                pausebtn.setEnabled(false);

                ExoAudioPlay.play(c, audioUri, duration -> {

                    eTime = (int) duration;
                    sTime = (int) ExoAudioPlay.getCurrentPosition();
                    oTime = 0;

                    updatePlayerUI(c);
                });
            });

            pausebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExoAudioPlay.pause();
                    pausebtn.setEnabled(false);
                    playbtn.setEnabled(true);
                    Toast.makeText(c, "Pausing Audio", Toast.LENGTH_SHORT).show();
                }
            });

            forwardbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSeeking) {
                        return;
                    }

                    if ((sTime + fTime) <= eTime) {

                        isSeeking = true;

                        sTime = sTime + fTime;
                        ExoAudioPlay.seekTo(sTime);

                        hdlr.postDelayed(() -> {
                            isSeeking = false;
                        }, 700);

                    } else {

                        Toast.makeText(
                                c,
                                "Cannot jump forward 5 seconds",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
            });

            backwardbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSeeking) {
                        return;
                    }

                    if ((sTime - bTime) > 0) {

                        isSeeking = true;

                        sTime = sTime - bTime;
                        ExoAudioPlay.seekTo(sTime);

                        hdlr.postDelayed(() -> {
                            isSeeking = false;
                        }, 700);

                    } else {

                        Toast.makeText(
                                c,
                                "Cannot jump backward 5 seconds",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
            });
        }

        cancelTxt.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               popupWindow.dismiss();
           }
        });

        popupWindow.setOnDismissListener(() -> {
            hdlr.removeCallbacks(UpdateSongTime);
            ExoAudioPlay.release();
        });
    }


    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {

            Log.d(
                    "EXO_DEBUG",
                    "POSITION=" + ExoAudioPlay.getCurrentPosition()
                            + " playing=" + ExoAudioPlay.isPlaying()
            );

            if (ExoAudioPlay.isStopped()) {

                hdlr.removeCallbacks(this);

                sTime = 0;
                eTime = 0;
                oTime = 0;

                songPrgs.setProgress(0);

                pausebtn.setEnabled(false);
                playbtn.setEnabled(true);

                return;
            }

            sTime = (int) ExoAudioPlay.getCurrentPosition();

            startTime.setText(String.format(
                    "%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(sTime),
                    TimeUnit.MILLISECONDS.toSeconds(sTime)
                            - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(sTime))
            ));

            songPrgs.setProgress(sTime);

            hdlr.postDelayed(this, 1000);
        }
    };

    private void updatePlayerUI(Context c) {

        if (oTime == 0) {
            songPrgs.setMax(eTime);
            oTime = 1;
        }

        songTime.setText(String.format(
                "%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(eTime),
                TimeUnit.MILLISECONDS.toSeconds(eTime)
                        - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(eTime))
        ));

        startTime.setText(String.format(
                "%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(sTime),
                TimeUnit.MILLISECONDS.toSeconds(sTime)
                        - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(sTime))
        ));

        hdlr.removeCallbacks(UpdateSongTime);
        hdlr.postDelayed(UpdateSongTime, 1000);

        pausebtn.setEnabled(true);
        playbtn.setEnabled(false);

        Toast.makeText(c, "Playing Audio", Toast.LENGTH_SHORT).show();
    }
}
