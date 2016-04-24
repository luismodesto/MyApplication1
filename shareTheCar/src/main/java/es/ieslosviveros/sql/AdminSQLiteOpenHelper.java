package es.ieslosviveros.sql;

/**
 * Created by papa on 26/03/2016.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(Context context, String nombre, CursorFactory factory, int version) {
        super(context, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("create table SqlMensajes(id integer primary key, nombre text, colegio text, nromesa integer)");
        System.out.println("----rcreatecc--");
        String crear1="CREATE TABLE contactos (id INTEGER PRIMARY KEY   AUTOINCREMENT,id_usuario integer,nombre text,email text ,sin_leer integer,fecha datetime DEFAULT CURRENT_TIMESTAMP)";
        String crear2="CREATE TABLE mensajes (id INTEGER PRIMARY KEY   AUTOINCREMENT,mensaje text, id_origen integer, id_destino integer, fecha datetime DEFAULT CURRENT_TIMESTAMP)";

        db.execSQL(crear1);
        db.execSQL(crear2);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnte, int versionNue) {
        System.out.println("----upgrade--cccccccccccccccccccccccccccc--");
        db.execSQL("drop table if exists contactos");
        db.execSQL("drop table if exists mensajes");
        String crear1="CREATE TABLE contactos (id INTEGER PRIMARY KEY   AUTOINCREMENT,id_usuario integer,nombre text,email text ,sin_leer integer,fecha datetime DEFAULT CURRENT_TIMESTAMP)";
        String crear2="CREATE TABLE mensajes (id INTEGER PRIMARY KEY   AUTOINCREMENT,mensaje text, id_origen integer, id_destino integer, fecha datetime DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(crear1);
        db.execSQL(crear2);
    }
}
