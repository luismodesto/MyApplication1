package es.ieslosviveros.chat;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Random;

import es.ieslosviveros.kioto.MainActivity;
import es.ieslosviveros.kioto.httpRequest;
import es.ieslosviveros.noticias.Noticia;
import es.ieslosviveros.sql.SqlContactos;
import es.ieslosviveros.sql.SqlMensajes;
import es.ieslosviveros.www.myapplication.R;

/**
 * Created by papa on 29/03/2016.
 */
public class Contactos extends AppCompatActivity implements IObservable {
    private static final int MENU_CONTEXT_DELETE_ID =5 ;

    private int useryo,userotro;
    private Random random;
    public static ArrayList<ChatContacto> ctslist;
    public static ContactosAdapter contactosAdapter;
    ListView ctsListView;
    public int cant=1200;//numero de mensajes antes de recargar
    public int pos=0;//incio de los mensajes a recargar
    public  int fin=0;
    public SqlContactos miContacto;;
    public httpRequest request;
    Context ctx;
    MainActivity contex;

    public void update(){
        pintacontactos();
        System.out.println("----update contactos--");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactos);
        //Chats_obsoleto chat=new Chats_obsoleto();
        //((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(  "Chats_obsoleto");

        ctsListView = (ListView) findViewById(R.id.msgListViewContactos);
        miContacto = new SqlContactos(this);

        // ----Set autoscroll of listview when a new message arrives----//
        ctsListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        ctsListView.setStackFromBottom(true);

        contex=MainActivity.activity;
        contex.addObserver(this);

        ctslist = new ArrayList<ChatContacto>();
        contactosAdapter = new ContactosAdapter(this, ctslist);
        ctsListView.setAdapter(contactosAdapter);

        registerForContextMenu(ctsListView);
        ctsListView.setLongClickable(true);
        System.out.println("-------------------n usuariocion  ");
        pintacontactos();
        //////////////////////////
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relative_layout);
        relativeLayout.setClickable(true);
        relativeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println("-------------cerrar  ");
                finish();
            }
        });
//////////////////////////
     //   ActionBar actionBar = getActionBar();
       // actionBar.setDisplayHomeAsUpEnabled(true);


        //////////////////////
        ctsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("----click on usuariocion  ");
                SharedPreferences prefs = getSharedPreferences("" + R.string.preferencias, Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
                //prefs.getString("user_id", "").length()>0)
                String id_origen = prefs.getString("user_id", "");
                //(ChatContacto)contactosAdapter.ge .getItem(position);
                // ChatContacto contact=(ChatContacto)parent.getSelectedItem();
                ChatContacto contact = (ChatContacto) contactosAdapter.getItem(position);

                Intent i;
                i = new Intent(MainActivity.appContext, mensajes.class);
                String id_destino = contact.user_id;
                System.out.println("-------------datos enviados  " + id_destino + " " + id_origen);
                i.putExtra("id_origen", Integer.parseInt(id_origen)); //id_usuario el mio
                i.putExtra("id_destino", Integer.parseInt(id_destino)); //id_ del otro

                miContacto.quita_mensaje(id_destino);
                pintacontactos();
                startActivity(i);


    }
});

    }
    public void pintacontactos(){
        pos=0;
        contactosAdapter.clear();
        ArrayList<ChatContacto> tabla=miContacto.consulta();
        for(int x=0;x<tabla.size();x++) {
            contactosAdapter.add(tabla.get(x));
        }
        contactosAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, 0, 0, "Borrar contacto");
        //menu.add(0, 1, 1, "Vaciar chat");

    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        System.out.println("----opcion  "+menuItemIndex+"-"+item.getOrder());
        //ChatMessage c=array.get(menuInfo.position);
        //Toast.makeText(List.this, "Selected String is :-" + c.toString(), Toast.LENGTH_SHORT).show();
        // check for selected option

        if (menuItemIndex == 0) {
            ChatContacto mcha= contactosAdapter.chatContactoList.get(info.position);
            SqlContactos mensa=new SqlContactos(this);
            System.out.println("----borrarando "+mcha.id+" - " +mcha.toString()+" - ");
            mensa.baja(mcha.id);
            //chatAdapter=new NoticiaAdapter(this, chatlist);
            // msgListView.invalidateViews();
            pintacontactos();

            System.out.println("----borrar--cccc "+info.id+" - " +info.toString()+" - ");

        }
        if (menuItemIndex == 1) {
            int id_borra=0;
           /* ChatMessage mcha=    chatAdapter.chatMessageList.get(info.position);
            int id1=mcha.id_destino;
            int id2=mcha.id_origen;
            if (mcha.id==id1) id_borra=id2;
            else id_borra=id1;
            SqlMensajes mensa=new SqlMensajes(this);
            mensa.borraChat(id_borra);
            // msgListView.setAdapter(null);
            // msgListView.invalidateViews();
            pintamensajes();*/
            System.out.println("----borrar todo--cccc "+info.id+" - " +info.toString()+" - "+id_borra);

        }

        return true;
    }
    public void buscaUsuario(String id_origen){
        SqlContactos conta=new SqlContactos(MainActivity.appContext);
        /////////si no existe lo pido

        conta.bbuscaUsuario(id_origen);
        System.out.println("----------Paso por-peticion--borrar todo--cc");

    }

    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        contex.removeObserver(this);
        super.onDestroy();

        Log.v("MyApp", "onDestroy");
    }

    }
