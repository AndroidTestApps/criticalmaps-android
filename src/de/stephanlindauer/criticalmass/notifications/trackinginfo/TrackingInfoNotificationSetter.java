package de.stephanlindauer.criticalmass.notifications.trackinginfo;

import android.annotation.TargetApi;
import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import de.stephanlindauer.criticalmass.R;

public class TrackingInfoNotificationSetter {

    private static TrackingInfoNotificationSetter instance;

    public static final int NOTIFICATION_ID = 123456;

    private Context context;
    private Activity activity;

    private NotificationManager mNotificationManager;

    public static TrackingInfoNotificationSetter getInstance() {
        if (TrackingInfoNotificationSetter.instance == null) {
            TrackingInfoNotificationSetter.instance = new TrackingInfoNotificationSetter();
        }
        return TrackingInfoNotificationSetter.instance;
    }

    public void initialize(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void show() {

        Intent dismissIntent = new Intent(context, activity.getClass());
        dismissIntent.setAction("bla");
        dismissIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Intent resultIntent = new Intent(context, activity.getClass());
        PendingIntent resultPendingIntent = PendingIntent.getActivity(activity, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_action_location_found)
                .setContentTitle(activity.getString(R.string.notification_tracking_title))
                .setContentText(activity.getString(R.string.notification_tracking_text))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(activity.getString(R.string.notification_tracking_text)))
                .setContentIntent(resultPendingIntent)
                .setPriority(Notification.PRIORITY_MAX);

        Notification notification = mBuilder.build();

        mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void cancel() {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(NOTIFICATION_ID);
        }
    }
}
