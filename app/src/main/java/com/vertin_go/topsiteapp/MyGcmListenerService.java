package com.vertin_go.topsiteapp;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyGcmListenerService extends FirebaseMessagingService
{
    private static final String TAG = "MyGcmListenerService";
    private static final String NOTIFICATION_TAG = "NewMessage";

    /**
     * Group id constants, required for initialization and to place channels into them.
     */
    private static final String GROUP_ID_SMS = "group_id_sms";
    private static final String GROUP_ID_PROMOTION = "promotion_id_sms";

    /**
     * Channels id constants, required on initialization and possible later usage.
     */
    private static final String SMS_CHANNEL_ID = "sms_channel_id";
    private static final String PICTURE_CHANNEL_ID = "picture_channel_id";
    private static final String PROMOTION_CHANNEL_ID = "promotion_channel_id";

    private static int NOTIFICATION_ID;
    private static final int PRIORITY_DEFAULT = NotificationManager.IMPORTANCE_DEFAULT;
    private NotificationManager mNotificationManager;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);

        sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message, String title)
    {
        /* Create a notification manager, and create two groups that will categorize the notification channels */
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannelGroup(new NotificationChannelGroup(GROUP_ID_SMS, getString(R.string.SMS)));
        mNotificationManager.createNotificationChannelGroup(new NotificationChannelGroup(GROUP_ID_PROMOTION, getString(R.string.Promotions)));

        /* Create two notification channels for the group SMS, one for receiving texts, and one for pictures */
        NotificationChannel smsNotificationChannel = new NotificationChannel(SMS_CHANNEL_ID, getString(R.string.sms_channel_name), PRIORITY_DEFAULT);
        NotificationChannel pictureNotificationChannel = new NotificationChannel(PICTURE_CHANNEL_ID, getString(R.string.picture_channel_name), PRIORITY_DEFAULT);
        pictureNotificationChannel.setGroup(GROUP_ID_SMS);
        smsNotificationChannel.setGroup(GROUP_ID_SMS);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent notificationIntent = new Intent(this, Notifications.class);
        notificationIntent.putExtra("ms", message);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        /* Create one notification channel for ads, that will go into a promotional group */
        NotificationChannel promotionNotificationChannel = new NotificationChannel(PROMOTION_CHANNEL_ID, getString(R.string.ads_channel_name), PRIORITY_DEFAULT);
        promotionNotificationChannel.setGroup(GROUP_ID_PROMOTION);

        /* Use the notification manager to finalize the initializations of the channels */
        mNotificationManager.createNotificationChannel(smsNotificationChannel);
        mNotificationManager.createNotificationChannel(pictureNotificationChannel);
        mNotificationManager.createNotificationChannel(promotionNotificationChannel);

        android.support.v4.app.NotificationCompat.Builder mBuilder = new android.support.v4.app.NotificationCompat.Builder(MyGcmListenerService.this)
                .setSmallIcon(R.mipmap.ic_launcher);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setChannelId(SMS_CHANNEL_ID);
        NOTIFICATION_ID = 1234;

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


    public static void notify(final Context context, final String exampleString, final int number)
    {
        final Resources res = context.getResources();

        // This image is used as the notification's large icon (thumbnail).
        // TODO: Remove this if your notification has no relevant thumbnail.
        final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.cloud_download);


        final String ticker = exampleString;
        final String title = res.getString(
                R.string.new_message_notification_title_template, exampleString);
        final String text = res.getString(
                R.string.new_message_notification_placeholder_text_template, exampleString);

        final android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(context)

                // Set appropriate defaults for the notification light, sound,
                // and vibration.
                .setDefaults(Notification.DEFAULT_ALL)

                // Set required fields, including the small icon, the
                // notification title, and text.
                .setSmallIcon(R.drawable.ic_stat_new_message)
                .setContentTitle(title)
                .setContentText(text)

                // All fields below this line are optional.

                // Use a default priority (recognized on devices running Android
                // 4.1 or later)
                .setPriority(android.support.v4.app.NotificationCompat.PRIORITY_DEFAULT)

                // Provide a large icon, shown with the notification in the
                // notification drawer on devices running Android 3.0 or later.
                .setLargeIcon(picture)

                // Set ticker text (preview) information for this notification.
                .setTicker(ticker)

                // Show a number. This is useful when stacking notifications of
                // a single type.
                .setNumber(number)

                // If this notification relates to a past or upcoming event, you
                // should set the relevant time information using the setWhen
                // method below. If this call is omitted, the notification's
                // timestamp will by set to the time at which it was shown.
                // TODO: Call setWhen if this notification relates to a past or
                // upcoming event. The sole argument to this method should be
                // the notification timestamp in milliseconds.
                //.setWhen(...)

                // Set the pending intent to be initiated when the user touches
                // the notification.
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com")),
                                PendingIntent.FLAG_UPDATE_CURRENT))

                // Show expanded text content on devices running Android 4.1 or
                // later.
                .setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle()
                        .bigText(text)
                        .setBigContentTitle(title)
                        .setSummaryText("Dummy summary text"))

                // Example additional actions for this notification. These will
                // only show on devices running Android 4.1 or later, so you
                // should ensure that the activity in this notification's
                // content intent provides access to the same actions in
                // another way.
                .addAction(
                        R.drawable.ic_action_stat_share,
                        res.getString(R.string.action_share),
                        PendingIntent.getActivity(
                                context,
                                0,
                                Intent.createChooser(new Intent(Intent.ACTION_SEND)
                                        .setType("text/plain")
                                        .putExtra(Intent.EXTRA_TEXT, "Dummy text"), "Dummy title"),
                                PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(
                        R.drawable.ic_action_stat_reply,
                        res.getString(R.string.action_reply),
                        null)

                // Automatically dismiss the notification when it is touched.
                .setAutoCancel(true);

        notify(context, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification)
    {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR)
        {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        }
        else
            {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }

    /**
     * Cancels any notifications of this type previously shown using
     * {@link #notify(Context, String, int)}.
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context)
    {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR)
        {
            nm.cancel(NOTIFICATION_TAG, 0);
        }
        else
            {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }
}
