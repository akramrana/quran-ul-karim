package com.akramhossain.quranulkarim;

import android.content.Context;
import android.os.Handler;
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
import com.akramhossain.quranulkarim.model.Sura;
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
    TextView startTime,songTime, txtSuraName;
    SeekBar songPrgs;

    public PopUpClass(){

    }

    public void showPopupWindow(final View view, Sura sura, Context c) {

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

        txtSuraName.setText(sura.getName_simple());

        hdlr = new Handler();

        songPrgs = popupView.findViewById(R.id.sBar);
        songPrgs.setClickable(false);
        pausebtn.setEnabled(false);

        AudioPlay.stopAudio();
        songTime.setText(String.format("%d min, %d sec", 0, 0));
        startTime.setText(String.format("%d min, %d sec", 0, 0));
        songPrgs.setProgress(0);
        hdlr.removeCallbacksAndMessages(null);

        if (isInternetPresent) {

            playbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isLoaded = AudioPlay.isLoadedAudio();
                    String mp3Uri = AudioPlay.getAudioUri();
                    String audioUri = "https://download.quranicaudio.com/qdc/mishari_al_afasy/murattal/" + sura.getSurah_id() + ".mp3";

                    if(isLoaded) {
                        if(mp3Uri.equals(audioUri)) {
                            AudioPlay.resumeAudio();
                            eTime = AudioPlay.getDuration();
                            sTime = AudioPlay.getCurrentPosition();
                        }else{
                            AudioPlay.stopAudio();
                            AudioPlay.playAudio(c, audioUri);
                            eTime = AudioPlay.getDuration();
                            sTime = AudioPlay.getCurrentPosition();
                            oTime = 0;
                        }
                    }else{
                        AudioPlay.stopAudio();
                        AudioPlay.playAudio(c, audioUri);
                        eTime = AudioPlay.getDuration();
                        sTime = AudioPlay.getCurrentPosition();
                        oTime = 0;
                    }
                    Log.d("audioUri",audioUri);
                    Log.d("eTime",String.valueOf(eTime));

                    if (oTime == 0) {
                        songPrgs.setMax(eTime);
                        oTime = 1;
                    }
                    songTime.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(eTime), TimeUnit.MILLISECONDS.toSeconds(eTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(eTime))));
                    startTime.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(sTime), TimeUnit.MILLISECONDS.toSeconds(sTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sTime))));
                    //songPrgs.setProgress(sTime);
                    hdlr.postDelayed(UpdateSongTime, 1000);
                    pausebtn.setEnabled(true);
                    playbtn.setEnabled(false);
                    Toast.makeText(c, "Playing Audio", Toast.LENGTH_SHORT).show();
                }
            });

            pausebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AudioPlay.pauseAudio();
                    pausebtn.setEnabled(false);
                    playbtn.setEnabled(true);
                    Toast.makeText(c, "Pausing Audio", Toast.LENGTH_SHORT).show();
                }
            });

            forwardbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if((sTime + fTime) <= eTime)
                    {
                        sTime = sTime + fTime;
                        AudioPlay.seekTo(sTime);
                    }
                    else
                    {
                        Toast.makeText(c, "Cannot jump forward 5 seconds", Toast.LENGTH_SHORT).show();
                    }
                    if(!playbtn.isEnabled()){
                        playbtn.setEnabled(true);
                    }
                }
            });

            backwardbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if((sTime - bTime) > 0)
                    {
                        sTime = sTime - bTime;
                        AudioPlay.seekTo(sTime);
                    }
                    else
                    {
                        Toast.makeText(c, "Cannot jump backward 5 seconds", Toast.LENGTH_SHORT).show();
                    }
                    if(!playbtn.isEnabled()){
                        playbtn.setEnabled(true);
                    }
                }
            });
        }


//        TextView test2 = popupView.findViewById(R.id.titleText);
//        test2.setText("Popup");
//        Button buttonEdit = popupView.findViewById(R.id.messageButton);
//        buttonEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //As an example, display the message
//                Toast.makeText(view.getContext(), "Wow, popup action button", Toast.LENGTH_SHORT).show();
//            }
//        });

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Close the window when clicked
                AudioPlay.stopAudio();
                popupWindow.dismiss();
                return true;
            }
        });
    }

    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            boolean isAudioStopped = AudioPlay.isStopped();
            if(isAudioStopped){
                hdlr.removeCallbacks(this);
                pausebtn.setEnabled(false);
                playbtn.setEnabled(true);

            }else {
                sTime = AudioPlay.getCurrentPosition();
                Log.d("stopped", String.valueOf(isAudioStopped));
                startTime.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(sTime), TimeUnit.MILLISECONDS.toSeconds(sTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sTime))));
                songPrgs.setProgress(sTime);
                hdlr.postDelayed(this, 1000);
            }
        }
    };
}
