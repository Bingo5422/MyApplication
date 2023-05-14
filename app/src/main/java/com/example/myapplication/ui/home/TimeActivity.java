package com.example.myapplication.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.content.Context;

import com.example.myapplication.R;

public class TimeActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        //铃声
        mediaPlayer = MediaPlayer.create(this, R.raw.ring);
        mediaPlayer.start();

        new AlertDialog.Builder(TimeActivity.this)
                .setTitle("Alarm")
                .setMessage("Time is up!")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialogInterface, int which) {
                        TimeActivity.this.finish();
                        mediaPlayer.stop();
                    }
                }).create().show();

        // Show a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.clock)
                .setContentTitle("Alarm")
                .setContentText("Time is up!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, builder.build());


    }



    public void close(View view){
        mediaPlayer.stop();
        finish();
    }
}