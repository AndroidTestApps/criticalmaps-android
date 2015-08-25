package de.stephanlindauer.criticalmaps.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

import de.stephanlindauer.criticalmaps.handler.PullServerHandler;
import de.stephanlindauer.criticalmaps.notifications.trackinginfo.TrackingInfoNotificationSetter;

public class SyncService extends Service {


    private final int PULL_OTHER_LOCATIONS_TIME = 20 * 1000; //20 sec


    private Timer timerPullServer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        timerPullServer = new Timer();

        TimerTask timerTaskPullServer = new TimerTask() {
            @Override
            public void run() {
                new PullServerHandler().execute();
            }
        };
        timerPullServer.scheduleAtFixedRate(timerTaskPullServer, 0, PULL_OTHER_LOCATIONS_TIME);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        TrackingInfoNotificationSetter.getInstance().cancel();
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }
}
