package es.ieslosviveros.chat;

import android.content.Context;

import java.util.Random;

import es.ieslosviveros.sql.SqlContactos;

/**
 * Created by papa on 29/03/2016.
 */
public class ChatContacto {
    public SqlContactos sqlContactos;
    public String id,nombre,user_id,email;
    public int sin_leer;

    Context contexto;
    //public boolean
    // isMine;// Did I send the message.

    public ChatContacto(String nombre,String user_id, String email,String id,int sin_leer) {
        this.nombre = nombre;
        this.user_id = user_id;
        this.email = email;
        this.id = id;
        this.sin_leer=sin_leer;
    }




}



