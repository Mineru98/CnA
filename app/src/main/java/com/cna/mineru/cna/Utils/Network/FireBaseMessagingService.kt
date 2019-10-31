package com.cna.mineru.cna.Utils.Network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import android.os.Build
import android.util.Log

import com.cna.mineru.cna.MainActivity
import com.cna.mineru.cna.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FireBaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(s: String?) {
        super.onNewToken(s)
        Log.e("Firebase", "Mineru Firebase ID " + s!!)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.data.size > 0) {

        }

        if (remoteMessage.notification != null) {
            val title = remoteMessage.notification!!.title
            val body = remoteMessage.notification!!.body
            val data = remoteMessage.data["image"]
            sendNotification(title, body, data)
        }
    }

    private fun sendNotification(title: String?, messageBody: String?, data: String?) {

        val main = Intent(this, MainActivity::class.java)
        main.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK
                    or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    or Intent.FLAG_ACTIVITY_SINGLE_TOP
        )

        val pendingIntent = PendingIntent.getActivity(
            this, 0, main,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = "CnA"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("CnA")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri).setLights(173, 500, 2000)
            .setContentIntent(pendingIntent)


        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "CnA"
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
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
    //                url = new URL("http://api.dbstore.or.kr:8880/foodinfo/list.do?uid="+ID+"&n=10&p=1&c=F3J01&s=food_name&o=u");
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