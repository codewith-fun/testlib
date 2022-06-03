package investwell.utils.notifications;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.iw.acceleratordemo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import investwell.client.activity.AppApplication;
import investwell.client.activity.MainActivity;
import investwell.utils.AppConstants;
import investwell.utils.AppSession;


/**
 * Created by Binesh on 5/27/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String INTENT_FILTER = "INTENT_FILTER";
    private AppSession mSession;


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        AppSession appSession = AppSession.getInstance(getApplication());
        appSession.setFcmToken(s);
        FirebaseMessaging.getInstance().subscribeToTopic(getApplicationContext().getString(R.string.TokenKey)+ AppConstants.APP_BID);

        //  Toast.makeText(getBaseContext(), ""+mSession.getFcmToken(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            mSession = AppSession.getInstance(getApplication());
            Map<String, String> params = remoteMessage.getData();
            JSONObject object = new JSONObject(params);
            notificationNewPattern(object);

        } catch (Exception e) {
            System.out.println();
        }
    }

    private void saveNotification(JSONObject object) {

        try {

            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int mint = Calendar.getInstance().get(Calendar.MINUTE);
            int date = Calendar.getInstance().get(Calendar.DATE);
            String pattern = "dd-MM-yyyy";
            String dateInString =new SimpleDateFormat(pattern).format(new Date());
            String time = dateInString+" "+convertTime(hour)+":"+convertTime(mint);
            object.put("time",time);
            if (!mSession.getNotification().isEmpty()) {
                JSONArray jsonArray = new JSONArray(mSession.getNotification());
                jsonArray.put(object);
                mSession.setNotification(jsonArray.toString());
            } else {
                JSONArray firstTimeArray = new JSONArray();
                firstTimeArray.put(object);
                mSession.setNotification(firstTimeArray.toString());
            }

            AppApplication.notification = "1";
            Intent myIntent = new Intent("FBR-IMAGE");
            myIntent.putExtra("action",AppApplication.notification);
            this.sendBroadcast(myIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public String convertTime(int input) {
        if (input >= 10) {
            return String.valueOf(input);
        } else {
            return "0" + input;
        }
    }
    private void notificationNewPattern(JSONObject object) {
        saveNotification(object);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            String channelId = getString(R.string.default_notification_channel_id);
            String name = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, name, importance);
            mChannel.setDescription("hello");
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300});
            notificationManager.createNotificationChannel(mChannel);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
// Set the notification parameters to the notification builder object

            Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("coming_from", "notification");
            intent1.putExtras(bundle);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 123, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            @SuppressLint("WrongConstant") NotificationCompat.Builder notificationBuilder
                    = new NotificationCompat.Builder(getApplicationContext(), channelId)
                    .setSmallIcon(R.mipmap.notification_icon) //your app icon
                    .setBadgeIconType(R.mipmap.notification_icon) //your app icon
                    .setChannelId(channelId)
                    .setContentTitle(object.optString("title"))
                    .setSound(defaultSoundUri)
                    .setContentText(object.optString("text"))
                    .setAutoCancel(false).setContentIntent(pendingIntent);
// Set the image for the notification
            if (!TextUtils.isEmpty(object.optString("imgpath"))&&!object.optString("imgpath").equalsIgnoreCase("null")) {
                Bitmap bitmap = getBitmapFromUrl(object.optString("imgpath"));
                notificationBuilder.setStyle(
                        new NotificationCompat.BigPictureStyle()
                                .bigPicture(bitmap)
                                .bigLargeIcon(null)
                ).setLargeIcon(bitmap);
            }
            notificationBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(1, notificationBuilder.build());


        } else {
            sendNotification(object);
        }
    }

    public Bitmap getBitmapFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            Log.e("awesome", "Error in getting notification image: " + e.getLocalizedMessage());
            return null;
        }
    }
    private void sendNotification(JSONObject object) {
        Intent mIntent = new Intent(this, MainActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putString("coming_from", "notification");
        bundle.putString("data", object.toString());
        mIntent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(pendingIntent)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(false)
                .setStyle(new Notification.BigTextStyle().bigText(object.optString("text")))
                .setContentTitle(object.optString("title"))
                .setContentText(object.optString("text"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext()
                    .getResources(), R.mipmap.notification_icon));
            builder.setSmallIcon(R.mipmap.notification_icon);
            builder.setColor(getResources().getColor(R.color.colorAccent));

        } else {
            builder.setSmallIcon(R.mipmap.notification_icon);
        }

    /*    Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        }
        notification.flags |= Notification.FLAG_AUTO_CANCEL;*/
        notificationManager.notify(1, builder.build());
    }
}
