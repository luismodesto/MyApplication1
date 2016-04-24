package es.ieslosviveros.sql;

/**
 * Created by papa on 26/03/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.ieslosviveros.chat.ChatContacto;
import es.ieslosviveros.chat.ChatMessage;
import es.ieslosviveros.chat.Contactos;
import es.ieslosviveros.chat.mensajes;
import es.ieslosviveros.kioto.MainActivity;
import es.ieslosviveros.kioto.httpRequest;

public class SqlContactos {
    Context contexto;
    String database="viveros";
    String tabla="Contactos";
    AdminSQLiteOpenHelper admin;
    SQLiteDatabase bd;
    httpRequest request;


    public SqlContactos(Context context){

        contexto=context;
        admin = new AdminSQLiteOpenHelper(MainActivity.appContext,database, null, 3);//contexto
        bd = admin.getWritableDatabase();
    }
    public void alta(String nombre, String id_usuario,String email) {
        Cursor fila = bd.rawQuery("select nombre,id,sin_leer  from " + tabla + " where id_usuario=" + id_usuario, null);
        if (fila.moveToFirst()) {
            System.out.println("-----busco y encuentro " + id_usuario);
            add_mensaje(id_usuario,fila.getInt(2),fila.getInt(1));
        }else {
            ContentValues registro = new ContentValues();
            registro.put("id_usuario", Integer.parseInt(id_usuario));
            registro.put("nombre", nombre);
            registro.put("email", email);
            registro.put("sin_leer", 1);
            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();
            registro.put("fecha",ts);
            bd.insert(tabla, null, registro);
            Toast.makeText(contexto, "Se cargaron los datos del usuario", Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<ChatContacto> consulta() {

        ArrayList<ChatContacto> tabla_res=new ArrayList<ChatContacto>();
        String query="select nombre,id_usuario ,email,id,sin_leer  from " + tabla+" order by fecha " ;
        Cursor fila = bd.rawQuery(query, null);

        //Nos aseguramos de que existe al menos un registro
        if (fila.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {

                ChatContacto chatContacto=new ChatContacto(fila.getString(0),fila.getString(1), fila.getString(2),fila.getString(3),fila.getInt(4));
                //chatContacto.id=fila.getInt(4);
                tabla_res.add(chatContacto);
            } while(fila.moveToNext());
        }
        System.out.println("----sqlc " + query);
        return tabla_res;


    }
    public void baja(String id) {

        int cant = bd.delete(tabla, "id=" + id, null);
        if (cant == 1)
            Toast.makeText(contexto, "Se borró la persona con dicho documento",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(contexto, "No existe una persona con dicho documento",  Toast.LENGTH_SHORT).show();
    }
    public void baja_id(String id) {

        int cant = bd.delete(tabla, "user_id=" + id, null);
        if (cant == 1)
            Toast.makeText(contexto, "Se borró la persona con dicho documento",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(contexto, "No existe una persona con dicho documento",  Toast.LENGTH_SHORT).show();
    }

    public void modificacion(int id,String nombre, int id_usuario) {

        ContentValues registro = new ContentValues();
        registro.put("nombre", nombre);
        registro.put("id_usuario", id_usuario);

        int cant = bd.update(tabla, registro, "id=" + id, null);

        if (cant == 1)
            Toast.makeText(contexto, "se modificaron los datos", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(contexto, "no existe una persona con dicho documento", Toast.LENGTH_SHORT).show();
    }

    public void bbuscaUsuario (String id_usuario) {
        System.out.println("-----busco " + id_usuario);
          Cursor fila = bd.rawQuery("select nombre,id,sin_leer  from " + tabla + " where id_usuario=" + id_usuario, null);
        if (fila.moveToFirst()) {
            System.out.println("-----busco y encuentro " + id_usuario);
            add_mensaje(id_usuario,fila.getInt(2),fila.getInt(1));
            } else{
            System.out.println("-----busco y no encuentro" + id_usuario);
            pideUsuario(id_usuario);


        }
    }

    public void pideUsuario(String id_usuario){
        //lo guardo en sql lite

        //SqlMensajes mensa= new SqlMensajes(contexto);
        System.out.println("-----pide usuario " + id_usuario);

        ///lo mando al servidor
        String url="http://www.ieslosviveros.es/androide/index.php";
        Map<String,String> params=new HashMap<String,String>();
        params.put("code", "pide_usuario");
        params.put("id_usuario", "" + id_usuario);
        Class[] parameterTypes = new Class[1];
        try {
            parameterTypes[0] = JSONObject.class;
            Method method1 = SqlContactos.class.getMethod("recibir", parameterTypes);///el metodo recibir procesa las respuestas de request cuando llegan
            request = new httpRequest(this, method1,0);//MainActivity.appContext
        }catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        request.getRespuesta(url, params);
    }

    public void recibir(JSONObject jsonObj ){
        String code="",message="",nombre="",email="",id_usuario="";
        try {
            if ( jsonObj.has("code"))       {code       = jsonObj.getString("code").toString();}
            if ( jsonObj.has("message"))    {message    = jsonObj.getString("message").toString();}
            if ( jsonObj.has("nombre"))     {nombre     = jsonObj.getString("nombre").toString();}
            if ( jsonObj.has("email"))      {email      = jsonObj.getString("email").toString();}
            if ( jsonObj.has("id_usuario")) {id_usuario = jsonObj.getString("id_usuario").toString();}
        if (code.equals("0")){
            alta(nombre,id_usuario,email)   ;
            ChatContacto  contacto=new ChatContacto(nombre,id_usuario,email,"",1);
            Contactos conta=new Contactos();//(Contactos)contexto;
            conta.contactosAdapter.add(contacto);
            conta.contactosAdapter.notifyDataSetChanged();
            System.out.println("----altao datos usuario--cccccccccccccccccccccccccccc--");
        }

    }catch (JSONException e) {
        e.printStackTrace();
    }
        System.out.println("----recibido datos usuario--cccccccccccccccccccccccccccc--");

        //navigationView.getMenu().aadd("dd").setIcon(1).
    }

    public void add_mensaje(String id_usuario,int valor,int id) {

        ContentValues registro = new ContentValues();
        int idd=valor+1;
        registro.put("sin_leer", idd);
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        registro.put("fecha",ts);
        int cant = bd.update(tabla, registro, "id=" + id, null);

        if (cant == 1)
            System.out.println("----incremento de sin leer-ccccccccccc");
        else
            System.out.println("----error en incremento de sin leer-ccccccccccc");
    }
    public void quita_mensaje(String id_usuario) {

        ContentValues registro = new ContentValues();
        registro.put("sin_leer", 0);
        int cant = bd.update(tabla, registro, "id_usuario=" + id_usuario, null);

        if (cant == 1)
            System.out.println("----reset de sin leer-ccccccccccc");
        else
            System.out.println("----error en reset de sin leer-ccccccccccc");
    }

}
