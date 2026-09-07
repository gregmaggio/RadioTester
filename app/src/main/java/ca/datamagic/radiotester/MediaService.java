package ca.datamagic.radiotester;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.logging.Logger;

public class MediaService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private static final Logger logger = Logger.getLogger(MediaService.class.getName());
    private static final String CHANNEL_ID = "media_playback_channel";
    private static final int NOTIFICATION_ID = 101;
    private MediaPlayer mediaPlayer;
    private final IBinder binder = new LocalBinder();
    public static final String ACTION_STOP = "ca.datamagic.radiotester.ACTION_STOP";

    public class LocalBinder extends Binder {
        public MediaService getService() {
            return MediaService.this;
        }
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    private Notification createNotification(String title) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText("Tap to return to app")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Media Playback",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public void playStream(String url) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync(); // Always prepare asynchronously for network streams
        } catch (Throwable t) {
            logger.warning("Throwable: " + t.getMessage());
        }
    }

    private void stopPlaybackAndService() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        initMediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopPlaybackAndService();
            return START_NOT_STICKY;
        }

        // Start as Foreground Service with persistent notification
        Notification notification = createNotification("Playing Audio");
        startForeground(NOTIFICATION_ID, notification);

        String streamUrl = intent.getStringExtra("STREAM_URL");
        if (streamUrl != null) {
            playStream(streamUrl);
        }

        return START_STICKY; // Restart service if system kills it
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mediaPlayer.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
