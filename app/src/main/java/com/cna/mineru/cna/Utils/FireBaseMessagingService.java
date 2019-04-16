package com.cna.mineru.cna.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cna.mineru.cna.MainActivity;
import com.cna.mineru.cna.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FireBaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {

        }

        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            String data = remoteMessage.getData().get("image");
            sendNotification(title,body,data);
        }
    }

    private void sendNotification(String title, String messageBody, String data) {

        Intent main = new Intent(this, MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, main,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "CnA";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("CnA")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri).setLights(000000255,500,2000)
                        .setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "CnA";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0, notificationBuilder.build());
    }
//    public class Task extends AsyncTask<String, void, String> {
//
//        String clientKey = "#########################";;
//        private String str, receiveMsg;
//        private final String ID = "########";
//
//        @Override
//        protected String doInBackground(String... params) {
//            URL url = null;
//            try {
//                url = new URL("http://api.dbstore.or.kr:8880/foodinfo/list.do?uid="+ID+"&n=10&p=btn_1mid&c=F3J01&s=food_name&o=u");
//
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
//                conn.setRequestProperty("x-waple-authorization", clientKey);
//
//                if (conn.getResponseCode() == conn.HTTP_OK) {
//                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
//                    BufferedReader reader = new BufferedReader(tmp);
//                    StringBuffer buffer = new StringBuffer();
//                    while ((str = reader.readLine()) != null) {
//                        buffer.append(str);
//                    }
//                    receiveMsg = buffer.toString();
//                    Log.i("receiveMsg : ", receiveMsg);
//
//                    reader.close();
//                } else {
//                    Log.i("통신 결과", conn.getResponseCode() + "에러");
//                }
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return receiveMsg;
//        }
//    }
}