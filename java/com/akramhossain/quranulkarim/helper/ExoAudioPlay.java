package com.akramhossain.quranulkarim.helper;

import android.content.Context;

import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import android.util.Log;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;

public class ExoAudioPlay {

    private static ExoPlayer player;
    private static String audioUri;

    public interface OnReadyListener {
        void onReady(long duration);
    }

    public static void play(Context context, String uri) {
        play(context, uri, null);
    }

    public static void play(
            Context context,
            String uri,
            OnReadyListener listener
    ) {
        release();

        player = new ExoPlayer.Builder(
                context.getApplicationContext()
        ).build();

        AudioAttributes audioAttributes =
                new AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
                        .build();

        player.setAudioAttributes(audioAttributes, true);

        audioUri = uri;

        player.addListener(new Player.Listener() {
            private boolean readyCalled = false;
            @Override
            public void onPlaybackStateChanged(int playbackState) {

                Log.d("EXO_DEBUG",
                        "state=" + playbackState +
                                " playing=" + (player != null && player.isPlaying()) +
                                " position=" + (player != null ? player.getCurrentPosition() : -1)
                );

                if (playbackState == Player.STATE_READY && !readyCalled) {

                    readyCalled = true;

                    if (player == null) {
                        return;
                    }

                    long duration = player.getDuration();

                    if (listener != null) {
                        listener.onReady(
                                duration > 0 ? duration : 0
                        );
                    }
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Log.d("EXO_DEBUG",
                        "isPlaying=" + isPlaying +
                                " state=" + (player != null ? player.getPlaybackState() : -1)
                );
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                Log.e("EXO_DEBUG", "PLAYER ERROR", error);
            }
        });

        player.setMediaItem(MediaItem.fromUri(uri));
        player.prepare();
        player.play();
    }

    public static void pause() {
        if (player != null) {
            player.pause();
        }
    }

    public static void resume() {
        if (player == null) {
            return;
        }

        if (player.getPlaybackState() == Player.STATE_ENDED) {
            player.seekTo(0);
            player.play();
            return;
        }

        player.play();
    }

    public static void stop() {
        if (player != null) {
            player.stop();
        }
    }

    public static void release() {
        if (player != null) {
            player.release();
            player = null;
        }

        audioUri = null;
    }

    public static void seekTo(long position) {
        if (player != null) {
            player.seekTo(position);
        }
    }

    public static long getCurrentPosition() {
        return player != null ? player.getCurrentPosition() : 0;
    }

    public static long getDuration() {
        if (player == null) {
            return 0;
        }

        long duration = player.getDuration();

        return duration > 0 ? duration : 0;
    }

    public static boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    public static boolean isLoaded() {
        return player != null;
    }

    public static String getAudioUri() {
        return audioUri;
    }

    public static boolean isStopped() {
        return player == null ||
                player.getPlaybackState() == Player.STATE_ENDED ||
                player.getPlaybackState() == Player.STATE_IDLE;
    }
}
