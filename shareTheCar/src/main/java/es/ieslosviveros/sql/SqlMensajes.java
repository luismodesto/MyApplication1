package es.ieslosviveros.sql;

/**
 * Created by papa on 26/03/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.ArrayList;

import es.ieslosviveros.chat.ChatMessage;
import es.ieslosviveros.chat.mensajes;
import es.ieslosviveros.kioto.MainActivity;

import static com.google.android.gms.internal.zzir.runOnUiThread;

public class SqlMensajes {
    Context contexto;
    String database;
    String tabla;
    SQLiteDatabase bd;
    int user_id;
    BackgroundTask backgroundtask;
    AdminSQLiteOpenHelper admin;
    //CREATE TABLE SqlMensajes (id INTEGER PRIMARY KEY   AUTOINCREMENT, id_origen integer, id_destino integer, fecha datet


    public SqlMensajes(Context context){
        contexto=context;
        database="viveros";
        tabla="mensajes";
        admin = new AdminSQLiteOpenHelper(MainActivity.appContext,database, null, 3);//contexto
        bd = admin.getWritableDatabase();
        backgroundtask=new BackgroundTask(contexto);


    }
    public void alta(int id_origen, int id_destino, String mensaje) {

        ContentValues registro = new ContentValues();
        registro.put("id_origen", id_origen);
        registro.put("id_destino", id_destino);
        registro.put("mensaje", mensaje);
        bd.insert(tabla, null, registro);
        //bd.close();
        //Toast.makeText(contexto, "Se cargaron los datos de la persona", Toast.LENGTH_SHORT).show();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity contex= MainActivity.activity;
                contex.update();

            }
        });


    }

    public ArrayList<ChatMessage> consulta(int id_usuario,int start, int cant) {

        ArrayList<ChatMessage> tabla_res=new ArrayList<ChatMessage>();
        String query="select id_origen ,id_destino,fecha,mensaje,id  from " + tabla + " where id_origen=" + id_usuario+" or id_destino =" +id_usuario+" LIMIT "+cant+" offset "+start;
        Cursor fila = bd.rawQuery(query, null);

        //Nos aseguramos de que existe al menos un registro
        if (fila.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {

                ChatMessage chatMessage=new ChatMessage(fila.getInt(0),fila.getInt(1), fila.getString(2), fila.getString(3),id_usuario);
                chatMessage.id=fila.getInt(4);
                tabla_res.add(chatMessage);
            } while(fila.moveToNext());
        }
        System.out.println("----sqlc " + query);
        return tabla_res;
    }

    public void baja(int id) {
           int cant = bd.delete(tabla, "id=" + id, null);
       // bd.close();

        if (cant == 1)
            Toast.makeText(contexto, "Se borró el mensaje selecionado",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(contexto, "No existe el mensaje selecionado",  Toast.LENGTH_SHORT).show();
    }


    public void modificacion(int id,String nombre, int id_usuario) {

        ContentValues registro = new ContentValues();
        registro.put("nombre", nombre);
        registro.put("id_usuario", id_usuario);

        int cant = bd.update(tabla, registro, "id=" + id, null);
        //bd.close();
        if (cant == 1)
            Toast.makeText(contexto, "se modificaron los datos", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(contexto, "no existe una persona con dicho documento", Toast.LENGTH_SHORT).show();
    }


    public void borraChat(int id) {

        int cant=0;
        cant = bd.delete(tabla, "id_origen=" + id, null);
        cant += bd.delete(tabla, "id_destino=" + id, null);
        //bd.close();

        if (cant == 1)
            Toast.makeText(contexto, "Se borró todo el chat",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(contexto, "No existe el chat selecionado",  Toast.LENGTH_SHORT).show();
    }
    public int cuenta_usuario(int id_usuario){

        String query="select count(*)  from " + tabla + " where id_origen=" + id_usuario+" or id_destino =" +id_usuario;
        Cursor fila = bd.rawQuery(query, null);
        fila.moveToFirst();
        return fila.getInt(0);

    }
    public void altaBackground(int id_origen, int id_destino, String mensaje) {

        backgroundtask.execute("add_info",""+id_origen,""+id_destino,mensaje);
    }

    public class BackgroundTask extends AsyncTask<String, Void, Void> {
        Context ctx;
        String database;
        BackgroundTask(Context ctx){
            this.ctx=ctx;

        }
        //@Override
        protected void onPreExeccute(){
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... params){

            String method=params[0];
            if (method.equals("add_info")) {
                int id_origen=Integer.parseInt(params[1]);
                int id_destino = Integer.parseInt(params[2]);
                String mensaje = params[3];
                //SQLiteDatabase db=dbOperations.getWritableDatabase();
               // dbOperations.add
                alta(id_origen,id_destino,mensaje);
                //actualizo
                //ChatMessage chatMessage=new ChatMessage(id_origen,id_destino,"",mensaje,id_destino);
                MainActivity contex= MainActivity.activity;
                contex.update();;
                //mensa.pintamensajes();
                //mensa.chatAdapter.add(chatMessage);
                //mensa.chatAdapter.notifyDataSetChanged();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate (Void... values){
            super.onProgressUpdate(values);
        }
        @Override
        protected void onPostExecute (Void aVoid){
            super.onPostExecute(aVoid);
        }


    }

}

