package com.eldisprojectcodelabs.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;

import com.eldisprojectcodelabs.notification.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity{
    Button buttonNotify, buttonCancel, buttonUpdate;
    ActivityMainBinding activityMainBinding;
    private static final String CHANNEL_ID = "primary_notification_channel";
    private static final int NOTIFICATION_ID = 0;
    private NotificationManager notificationManager;
    public static final String ACTION_UPDATE_NOTIFICATION = "com.eldisprojectcodelabs.norificationcodelabs.MainActivity.ACTION_UPDATE_NOTIFICATION";
    private NotificationReceiver notificationReceiver = new NotificationReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        provideButtonId();
        activityMainBinding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
        activityMainBinding.setMainActivity(this);
        createNotificationChannel();
        setEnableForButton(true, false, false);

        IntentFilter intentFilter = new IntentFilter(ACTION_UPDATE_NOTIFICATION);
        registerReceiver(notificationReceiver, intentFilter);
    }

    private void provideButtonId(){
        buttonNotify = findViewById(R.id.button_notify_me);
        buttonCancel = findViewById(R.id.button_cancel_me);
        buttonUpdate = findViewById(R.id.button_update_me);
    }

    public void sendNotification(){
        //send broadcast
        Intent intent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationCompat = getNotificationBuilder();
        Notification notification = notificationCompat.build();
        notificationManager.notify(NOTIFICATION_ID, notification);
        notificationCompat.addAction(R.drawable.ic_update, "Update Notification", updatePendingIntent);
    }

    public void createNotificationChannel(){
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Codelabs Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("This is notification codelabs channel");

            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public NotificationCompat.Builder getNotificationBuilder(){
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                 .setContentTitle("Notification Codelabs")
                 .setContentText("Hello there, this is notification codelabs")
                 .setSmallIcon(R.drawable.ic_favorite)
                 .setContentIntent(pendingIntent)
                 .setAutoCancel(true)
                 .setPriority(NotificationCompat.PRIORITY_HIGH)
                 .setDefaults(NotificationCompat.DEFAULT_ALL);
    }

    public void setClickNotifyMe(View view){
        sendNotification();
        Toast.makeText(this, "Notify Me", Toast.LENGTH_SHORT).show();
        setEnableForButton(false, true, true);
    }

    public void setClickUpdateMe(View view){
        updateNotification();
    }

    public void setClickCancelMe(View view){
        cancelNotification();
    }

    public void updateNotification(){
        //convert drawable into bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mascot_1);
        NotificationCompat.Builder notificationCompat = getNotificationBuilder();
        notificationCompat.setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(bitmap)
                .setBigContentTitle("Notification Updated"));
        notificationManager.notify(NOTIFICATION_ID, notificationCompat.build());
        setEnableForButton(false, false, true);
    }

    private void cancelNotification(){
        notificationManager.cancel(NOTIFICATION_ID);
        setEnableForButton(true, false, false);
    }

    private void setEnableForButton(boolean isNotifyEnabled, boolean isUpdateEnabled, boolean isCancelEnabled){
        provideButtonId();
        buttonNotify.setEnabled(isNotifyEnabled);
        buttonCancel.setEnabled(isUpdateEnabled);
        buttonUpdate.setEnabled(isCancelEnabled);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(notificationReceiver);
    }

    public class NotificationReceiver extends BroadcastReceiver {
        public NotificationReceiver(){

        }
        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotification();
        }
    }
}
