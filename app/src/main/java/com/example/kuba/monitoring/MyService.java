package com.example.kuba.monitoring;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Kuba on 03.04.2016.
 */
public class MyService extends IntentService {

    SitesDictController controller;
    Cursor cursor;

    public MyService() {
        super("MyService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        controller = new SitesDictController(this);
        controller.open();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        cursor = controller.readAll();
        String currentState, dbstate;
        while(cursor.moveToNext()){
            dbstate = cursor.getString(2);
            try {
                currentState = downloadUrl(cursor.getString(1));
            }catch (Exception e){
                currentState = dbstate;
                System.out.println("fail " + cursor.getString(1));
            }
            if(!currentState.equals(dbstate)) {
                System.out.println(cursor.getString(1) + " has changed"+ currentState +"   "+cursor.getString(2));
                controller.update(cursor.getInt(0), cursor.getString(1), currentState);
                sendNotification(cursor.getInt(0),cursor.getString(1));
            }else{
                System.out.println(cursor.getString(1) + " not changed" + cursor.getString(2));
            }
        }
    }

    //borrowed from stack/and dev
    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    private String downloadUrl(String myurl) throws IOException, NoSuchAlgorithmException {
        try {
            URL url = new URL(myurl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.6) Gecko/20100625 Firefox/3.6.6");
            String line;
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
            String html = builder.toString();
            //System.out.println(md5(html));
            return md5(html);

        } finally {
        }
    }
    void sendNotification(int id,String siteName){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle("URL Monitor")
                        .setContentText(siteName+" has changed!")
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(Intent.ACTION_VIEW);
        resultIntent.setData(Uri.parse(siteName));
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack( MainActivity.class );
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, mBuilder.build());
    }
}


