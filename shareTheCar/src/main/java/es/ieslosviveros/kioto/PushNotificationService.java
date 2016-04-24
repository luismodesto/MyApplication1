package es.ieslosviveros.kioto;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ieslosviveros.chat.ChatMessage;
import es.ieslosviveros.chat.Contactos;
import es.ieslosviveros.chat.mensajes;
import es.ieslosviveros.noticias.Noticia;
import es.ieslosviveros.sql.SqlContactos;
import es.ieslosviveros.www.myapplication.R;

/**
 * Created by papa on 23/03/2016.
 */
public class PushNotificationService extends GcmListenerService {
    int con=101010;
    ChatMessage chatMensaje;
    public SharedPreferences prefs;
    mensajes mensa1=new mensajes();
    Contactos conta1=new Contactos();
    httpRequest request;


    @Override
    public void onMessageReceived (String from, Bundle data) { //onMessageRecived

        String message = data.getString("message");
        String origen = data.getString("origen");
        String tipo = data.getString("tipo");
        System.out.println("recibido metodo 1 " + from + message + origen);
        //triggerNotification(message, Integer.parseInt(tipo), "String destino");
        Class[] parameterTypes = new Class[1];
        try {
            parameterTypes[0] = JSONObject.class;
            Method method1 = MainActivity.class.getMethod("recibir", parameterTypes);///el metodo recibir procesa las respuestas de request cuando llegan
            request = new httpRequest(MainActivity.appContext, method1,0);
        }catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }


        switch (tipo) {

            case "0":///////notificaciones novedades web

                triggerNotification(message, 0, message);
                break;

            case "1"://///////////////mensajes de chat
                prefs = getSharedPreferences(""+R.string.preferencias, Context.MODE_PRIVATE);
                int id_origen=Integer.parseInt(origen);
                int id_destino=Integer.parseInt(prefs.getString("user_id", ""));
                Date date = new Date();
                String fecha=date.toString();
                chatMensaje=new ChatMessage ( id_origen, id_destino,fecha,  message, id_destino);
                //mensa1=new mensajes();
                mensa1.recibirMensaje(chatMensaje);
                System.out.println("antes de buscar ---------------");
                conta1.buscaUsuario("" + chatMensaje.id_origen);
                triggerNotification(message, 1, "" + id_origen + "#" + id_destino);
                break;

            case "2":///pesca ubicacion
                LocationManager locat = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location lastKnownLocation_byGps = locat.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location lastKnownLocation_byNetwork = locat.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                LatLng aqui = new LatLng(lastKnownLocation_byNetwork.getLatitude(), lastKnownLocation_byNetwork.getLongitude());
                String url = "http://www.ieslosviveros.es/androide/index.php";
                Map<String, String> params = new HashMap<String, String>();
                params.put("code", "reg_pos");
                params.put("user_id", "" + prefs.getString("user_id", ""));
                params.put("last_loc", "" + aqui);
                System.out.println("---------------enviado----------------- " + aqui + " - " + "id");
                request.getRespuesta(url, params);


                break;


            //pesca foto
            case "3":break;
            case "4":break;


        }
       // triggerNotification(message, Integer.parseInt(tipo), "String destino");
     //   triggerNotification(message, 0, "String destino");
        //createNotification(mTitle, push_msg);
    }
    public void triggerNotification(String msg,int tipo,String destino) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.cast_ic_notification_2, "¡Nuevo mensaje!", System.currentTimeMillis());
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
        //contentView.setImageViewResource(R.id.imagen_notifi, R.drawable.ic_menu_manage);

        contentView.setTextViewText(R.id.txt_notifi,  msg);
    //tono         y vibracion

        // Sonido por defecto de notificaciones, podemos usar otro
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

// Uso en API 10 o menor
        notification.sound = defaultSound;

// Uso en API 11 o mayor
        //builder.setSound(defaultSound);
// Patrón de vibración: 1 segundo vibra, 0.5 segundos para, 1 segundo vibra
        long[] pattern = new long[]{1000,500,1000};

// Uso en API 10 o menor
        notification.vibrate = pattern;

// Uso en API 11 o mayor
        //builder.setVibrate(pattern);

        // API 10 o menor
        notification.ledARGB = Color.RED;
        notification.ledOnMS = 1;
        notification.ledOffMS = 0;
        notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;

// API 11 o mayor
        //builder.setLights(Color.RED, 1, 0);
///que es lo que hay que abrir
        notification.contentView = contentView;
       // if ( checkApp()){tipo=17000000;}
        if (tipo==1) {
            String trozos[]=destino.split("#");
            Intent notificationIntent = new Intent(this, mensajes.class);
            con=MainActivity.NOTIFICATION_ID+1;

            //i.putExtra("id_origen", Integer.parseInt(id_origen)); //id_usuario el mio
            //i.putExtra("id_destino", Integer.parseInt(id_destino)); //id_ del otro
            System.out.println("envia al cliente " + Integer.parseInt(trozos[0])+" - "+Integer.parseInt(trozos[1])+" - "+destino+"-" +con );

            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notificationIntent.putExtra("id_origen", Integer.parseInt(trozos[1])); //id_usuario el mio
            notificationIntent.putExtra("id_destino", Integer.parseInt(trozos[0])); //id_ del otro
            notificationIntent.putExtra("notification_id", con);

            PendingIntent contentIntent = PendingIntent.getActivity(this,con, notificationIntent, 0);
            notification.contentIntent = contentIntent;
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(con, notification);//MainActivity.NOTIFICATION_ID, notification);
        }
        /////////////////////////
        if (tipo==0) {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.putExtra("url", destino);
            con=MainActivity.NOTIFICATION_ID;
            notificationIntent.putExtra("notification_id", con);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            notification.contentIntent = contentIntent;
            notificationManager.notify(con,notification);//MainActivity.NOTIFICATION_ID, notification);
        }


        /*notification.contentView = contentView;
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.contentIntent = contentIntent;
        con=MainActivity.NOTIFICATION_ID++;
        notificationManager.notify(con,notification);//MainActivity.NOTIFICATION_ID, notification);
        */
    }
//@Override
    public void onMessage(String from, Bundle data) { //onMessageRecived

        System.out.println("recibido metodo 2 " + from );
    }

    public void recibir(JSONObject jsonObj ){
        try {

            if ( jsonObj.has("user_id")) {

            }
            System.out.println("----recibido--cccccccccccccccccccccccccccc--");

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("dfgdfgd----------------------------------------------------------------------------------------------------***********************************************d");
        }
    }
    public boolean checkApp(){

        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager
                .getRunningTasks(Integer.MAX_VALUE);
        boolean isActivityFound = false;

        if (services.get(0).topActivity.getPackageName().toString()
                .equalsIgnoreCase(this.getPackageName().toString())) {
            isActivityFound = true;
        }
        if (isActivityFound) {
            return false;//true;
        } else {
            return  false;
            // write your code to build a notification.
            // return the notification you built here
        }
        /*

        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

        // get the info from the currently running task
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

        ComponentName componentInfo = taskInfo.get(0).topActivity;
        if (componentInfo.getPackageName().equalsIgnoreCase("YourPackage")) {
            return true;
        } else {
            return false;
        }*/
    }

}
