package com.android.onlinemovieticket.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.onlinemovieticket.My_User;
import com.android.onlinemovieticket.R;

public class MyService extends Service {

    private TicketNotificateBinder mBinder = new TicketNotificateBinder();

    public class TicketNotificateBinder extends Binder {
        public void startNotification() {
            Log.d("MyService", "startNotification");
        }

        public int getProgress() {
            Log.d("MyService", "getProgress");
            return 0;
        }
    }

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyService", "onCreate");
        NotificationManager messageNotificatioManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("5996773",
                    "安卓10a", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);//是否在桌面icon右上角展示小红点
            channel.setLightColor(Color.GREEN);//小红点颜色
            channel.setShowBadge(false); //是否在久按桌面图标时显示此渠道的通知
            messageNotificatioManager.createNotificationChannel(channel);
        }

//
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle("服务标题");
        builder.setContentText("哈哈哈哈 测试服务");
        builder.setWhen(System.currentTimeMillis());
        builder.setChannelId("5996773");

        Intent notificationIntent = new Intent(this, My_User.class);
        PendingIntent pt = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        builder.setContentIntent(pt);
        Notification notification = builder.build();

        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MyService", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MyService", "onDestroy");
    }
}