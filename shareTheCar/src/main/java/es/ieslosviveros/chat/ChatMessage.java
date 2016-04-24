package es.ieslosviveros.chat;

/**
 * Created by papa on 26/03/2016.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Random;

public class ChatMessage {

    public String mensaje,fecha;
    public int id_origen, id_destino;
    public String Date, Time;
    public String msgid;
    public int user_id;
    public int id;
    //public boolean
    // isMine;// Did I send the message.

    public ChatMessage(int id_origen, int id_destino,String fecha, String mensaje,int user_id) {
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.id_origen = id_origen;
        this.id_destino = id_destino;
        this.user_id=user_id;

//this.mensaje+="-"+id_origen+"-"+id_destino+"-"+user_id+"-"+this.fecha+"-"+id;
    }

    public void setMsgID() {

        msgid += "-" + String.format("%02d", new Random().nextInt(100));
        ;
    }
    public boolean isMine(){
        if (id_origen!=user_id)return true;
        else return false;
    }

}