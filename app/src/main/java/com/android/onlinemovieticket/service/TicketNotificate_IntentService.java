package com.android.onlinemovieticket.service;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.onlinemovieticket.My_User;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.Cinema;
import com.android.onlinemovieticket.db.Comment;
import com.android.onlinemovieticket.db.Movie;
import com.android.onlinemovieticket.db.Session;
import com.android.onlinemovieticket.db.Ticket;
import com.android.onlinemovieticket.repository.CinemaRepository;
import com.android.onlinemovieticket.repository.HallRepository;
import com.android.onlinemovieticket.repository.MovieRepository;
import com.android.onlinemovieticket.repository.SessionRepository;
import com.android.onlinemovieticket.repository.TicketRepository;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TicketNotificate_IntentService extends IntentService {

    public TicketNotificate_IntentService() {
        super("TicketNotificate_IntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String account = intent.getStringExtra("account");
        String type = intent.getStringExtra("type");
        TicketRepository ticketRepository = new TicketRepository();
        List<Ticket> ticketList = ticketRepository.getTicketByAccount(account);
        List<Session> sessionList = new ArrayList<>();
        List<String> cinemaNameList = new ArrayList<>();
        List<String> hallNameList = new ArrayList<>();
        List<Movie> movieList = new ArrayList<>();

        for (int i = 0; i < ticketList.size(); i++) {
            SessionRepository sessionRepository = new SessionRepository();
            Session session = sessionRepository.getSessionBySid(ticketList.get(i).getSid());
            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            ParsePosition pos = new ParsePosition(0);
            String endTimeStr = sdf2.format(session.getShowDate()) + " "
                    + sdf1.format(session.getEndTime());

            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ParsePosition pos2 = new ParsePosition(0);

            Date endTime = sdf3.parse(endTimeStr, pos2);
            if (ticketList.get(i).getIsGrade() == 0 && getTime(endTime, getNowDate()) <= 3600) {
                sessionList.add(session);
                MovieRepository movieRepository = new MovieRepository();
                Movie movie = movieRepository.getMovieByMid(session.getMid());
                movieList.add(movie);

                CinemaRepository cinemaRepository = new CinemaRepository();
                String cinemaName = cinemaRepository.getCnameByCid(session.getCid());
                cinemaNameList.add(cinemaName);

                HallRepository hallRepository = new HallRepository();
                String hallName = hallRepository.getHnameByHid(session.getHid());
                hallNameList.add(hallName);
            } else {
                ticketList.remove(i);
                i--;
            }
        }

        for (int i = 0; i < ticketList.size(); i++) {
            Ticket ticket = ticketList.get(i);
            Session session = sessionList.get(i);
            String cinemaName = cinemaNameList.get(i);
            String hallName = hallNameList.get(i);
            Movie movie = movieList.get(i);
            new Notification_Thread(i, account, type, ticket, session,
                    cinemaName, hallName, movie).start();
        }
    }

    class Notification_Thread extends Thread {
        private int num;
        private String account;
        private String type;
        private Ticket ticket;
        private Session session;
        private String cinemaName;
        private String hallName;
        private Movie movie;

        public Notification_Thread(int num, String account, String type,
                                   Ticket ticket, Session session, String cinemaName,
                                   String hallName, Movie movie) {
            this.num = num;
            this.account = account;
            this.type = type;
            this.ticket = ticket;
            this.session = session;
            this.cinemaName = cinemaName;
            this.hallName = hallName;
            this.movie = movie;
        }

        public void ViewNotification(long sleep1) {
            if (sleep1 > 0) {
                try {
                    Thread.sleep(sleep1 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            NotificationManager messageNotificatioManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("5996775",
                        "安卓10a", NotificationManager.IMPORTANCE_DEFAULT);
                channel.enableLights(true);//是否在桌面icon右上角展示小红点
                channel.setLightColor(Color.GREEN);//小红点颜色
                channel.setShowBadge(false); //是否在久按桌面图标时显示此渠道的通知
                channel.setDescription("描述");
                messageNotificatioManager.createNotificationChannel(channel);
            }

//
            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    getApplicationContext());
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            builder.setContentTitle(movie.getMname());
            builder.setWhen(System.currentTimeMillis());
            builder.setChannelId("5996775");
            builder.setContentText(cinemaName + " " + hallName + " "
                    + ((sleep1 + 3600) / 60) + "分钟后开始");

            Intent notificationIntent = new Intent(getApplicationContext(), My_User.class);

            notificationIntent.putExtra("account", account);
            notificationIntent.putExtra("type", type);

            PendingIntent pt = PendingIntent.getActivity(getApplicationContext(), 0,
                    notificationIntent, 0);
            builder.setContentIntent(pt);
            Notification notification = builder.build();
            messageNotificatioManager.notify(1, notification);
        }

        public void CommentNotification(long sleep2) {
            if (sleep2 > 0) {
                try {
                    Thread.sleep(sleep2 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            NotificationManager messageNotificatioManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("5996777",
                        "安卓10a", NotificationManager.IMPORTANCE_DEFAULT);
                channel.enableLights(true);//是否在桌面icon右上角展示小红点
                channel.setLightColor(Color.GREEN);//小红点颜色
                channel.setShowBadge(false); //是否在久按桌面图标时显示此渠道的通知
                channel.setDescription("描述");
                messageNotificatioManager.createNotificationChannel(channel);
            }

//
            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    getApplicationContext());
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            builder.setContentTitle(movie.getMname());
            builder.setWhen(System.currentTimeMillis());
            builder.setChannelId("5996777");
            builder.setContentText("打分评价一下吧");

            Intent notificationIntent = new Intent(getApplicationContext(), My_User.class);

            boolean isbuy = true;
            notificationIntent.putExtra("account", account);
            notificationIntent.putExtra("type", type);
            notificationIntent.putExtra("isbuy", isbuy);

            Bundle bundle = new Bundle();
            bundle.putSerializable("movie", movie);
            notificationIntent.putExtras(bundle);

            TicketRepository ticketRepository = new TicketRepository();
            ticketRepository.updateIsGrade(ticket.getTid(),1);

            PendingIntent pt = PendingIntent.getActivity(getApplicationContext(), 0,
                    notificationIntent, 0);
            builder.setContentIntent(pt);
            Notification notification = builder.build();
            messageNotificatioManager.notify(1, notification);
        }

        @Override
        public void run() {
            Date nowTime = getNowDate();
            Date showDate = session.getShowDate();
            Date showTime = session.getShowTime();
            Date endTime = session.getEndTime();
            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            String showDateStr = sdf2.format(showDate);
            String showTimeStr = sdf1.format(showTime);
            String endTimeStr = sdf1.format(endTime);

            ParsePosition pos = new ParsePosition(0);
            ParsePosition pos2 = new ParsePosition(0);
            Date startTime = sdf1.parse(showDateStr + " " + showTimeStr, pos);
            Date endTime2 = sdf1.parse(showDateStr + " " + endTimeStr, pos2);

            long sleep1 = getTime(nowTime, startTime) - 3600;
            ViewNotification(sleep1);

            try {
                Thread.sleep(getTime(startTime,endTime2) * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long sleep2 = 3600;
            CommentNotification(sleep2);
        }
    }

    public Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(0);
        Date currentTime_2 = formatter.parse(dateString, pos);

        return currentTime_2;
    }

    //计算时间差
    public long getTime(Date startTime, Date endTime) {
        long time = (endTime.getTime() - startTime.getTime()) / 1000;
        return time;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}