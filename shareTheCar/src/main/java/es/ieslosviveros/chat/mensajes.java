package es.ieslosviveros.chat;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObservable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import es.ieslosviveros.kioto.MainActivity;
import es.ieslosviveros.kioto.httpRequest;
import es.ieslosviveros.sql.SqlMensajes;
import es.ieslosviveros.www.myapplication.R;

public class mensajes extends AppCompatActivity implements IObservable{
    private static final int MENU_CONTEXT_DELETE_ID =5 ;
    private EditText msg_edittext;
    private int useryo,userotro;
    private Random random;
    public static ArrayList<ChatMessage> chatlist;
    public static ChatAdapter chatAdapter;
    ListView msgListView;
    public int cant=1200;//numero de mensajes antes de recargar
    public int pos=0;//incio de los mensajes a recargar
    public  int fin=0;
    public SqlMensajes miMensaje;;
    public httpRequest request;
    Context ctx;
    MainActivity contex;


    public void update(){
        pintamensajes();
        System.out.println("----update mensajeo--");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mensajes);
        //Chats_obsoleto chat=new Chats_obsoleto();
        useryo = getIntent().getExtras().getInt("id_origen");
        userotro = getIntent().getExtras().getInt("id_destino");
        String notification_id="";

        Intent intent = getIntent();
        if (intent.hasExtra(notification_id)) {
            notification_id = getIntent().getExtras().getString("notification_id");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
             notificationManager.cancel(Integer.parseInt(notification_id));

        }

        System.out.println("------------------------------mensajes " + useryo + " - " +userotro + " --- " + notification_id);

        random = new Random();
        //((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(  "Chats_obsoleto");
        msg_edittext = (EditText) findViewById(R.id.messageEditText);
        msgListView = (ListView) findViewById(R.id.msgListView);
        ImageButton sendButton = (ImageButton) findViewById(R.id.sendMessageButton);
        miMensaje=new SqlMensajes(this);

        // ----Set autoscroll of listview when a new message arrives----//
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);

        contex=MainActivity.activity;
        contex.addObserver(this);

        chatlist = new ArrayList<ChatMessage>();
        chatAdapter = new ChatAdapter(this, chatlist);
        msgListView.setAdapter(chatAdapter);

        registerForContextMenu(msgListView);
        msgListView.setLongClickable(true);
        pintamensajes();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sendMessageButton:
                        System.out.println("----mensajeo--ccccccccccccc" + useryo + " - " + userotro);
                        sendTextMessage(v);

                }
            }
        });
        msgListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int a = view.getScrollX();
                int b = view.getListPaddingBottom();
                //System.out.println("--------------------------------Sroll " + fin + " - " + a + " cambio " + scrollState);
                if (fin == 80) {
                    //arregloCadenas.add("cadena " + cont++);
                    //adaptador.notifyDataSetChanged();
                    int total = miMensaje.cuenta_usuario(userotro);

                    int dif = 0;
                    if (pos + cant > total) dif = pos - cant;
                    else pos += cant;
                    addmensajes(pos, cant - dif);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                fin = (int) totalItemCount - visibleItemCount - firstVisibleItem;
                //System.out.println("--------------------------------Sroll posicion primero " + firstVisibleItem + " visibles items " + visibleItemCount + " total items " + totalItemCount);

                if (mLastFirstVisibleItem < firstVisibleItem) {
                    Log.i("SCROLLING DOWN", "TRUE");
                }
                if (mLastFirstVisibleItem > firstVisibleItem) {
                    Log.i("SCROLLING UP", "TRUE");
                    int total = miMensaje.cuenta_usuario(userotro);
/*
                    int dif=0;
                    if (pos+cant>total) dif=pos-cant;
                    else  pos+=cant;
                    addmensajes(pos,cant-dif);*/
                }
                mLastFirstVisibleItem = firstVisibleItem;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_mensajes);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

    }

    public void sendTextMessage(View v) {
        String message = msg_edittext.getEditableText().toString();
        if (!message.equalsIgnoreCase("")) {
            ChatMessage chatMessage = new ChatMessage( useryo, userotro, "", message,userotro);
            chatMessage.setMsgID();
            msg_edittext.setText("");
            chatAdapter.add(chatMessage);
            chatAdapter.notifyDataSetChanged();
            enviaMensaje(chatMessage);
        }
    }

    public void pintamensajes(){
    SqlMensajes mensa= new SqlMensajes(this);
    pos=0;
    chatAdapter.clear();
    ArrayList<ChatMessage> tabla=mensa.consulta(userotro,pos,cant);
    for(int x=0;x<tabla.size();x++) {
        chatAdapter.add(tabla.get(x));
    }
    chatAdapter.notifyDataSetChanged();
}

    public void addmensajes(int inicio,int cant){
        SqlMensajes mensa= new SqlMensajes(this);
        //chatAdapter.clear();
        ArrayList<ChatMessage> tabla=mensa.consulta(userotro,inicio,cant);
        for(int x=0;x<tabla.size();x++) {
            chatAdapter.add(tabla.get(x));
        }
        chatAdapter.notifyDataSetChanged();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0,0,0, "Borrar mensaje");
        menu.add(0, 1, 1, "Vaciar chat");

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
            ChatMessage mcha=    chatAdapter.chatMessageList.get(info.position);
            SqlMensajes mensa=new SqlMensajes(this);
            mensa.baja(mcha.id);
            //chatAdapter=new NoticiaAdapter(this, chatlist);
           // msgListView.invalidateViews();
            pintamensajes();

            System.out.println("----borrar--cccc "+info.id+" - " +info.toString()+" - "+mcha.id);

        }
        if (menuItemIndex == 1) {
            int id_borra=0;
            ChatMessage mcha= chatAdapter.chatMessageList.get(info.position);
            int id1=mcha.id_destino;
            int id2=mcha.id_origen;
            if (mcha.id==id1) id_borra=id2;
            else id_borra=id1;
            SqlMensajes mensa=new SqlMensajes(this);
            mensa.borraChat(id_borra);
           // msgListView.setAdapter(null);
            // msgListView.invalidateViews();
            pintamensajes();
            System.out.println("----borrar todo--cccc "+info.id+" - " +info.toString()+" - "+id_borra);

        }

        return true;
    }

    public void enviaMensaje(ChatMessage chatMessage){
        //lo guardo en sql lite
        SqlMensajes mensa= new SqlMensajes(this);
        System.out.println("------ack " + chatMessage.id_origen + "-" + chatMessage.id_destino + "-" + chatMessage.mensaje);
        //mensa.altaBackground(chatMessage.id_origen, chatMessage.id_destino, chatMessage.mensaje);
        mensa.alta(chatMessage.id_origen, chatMessage.id_destino, chatMessage.mensaje);
        ///lo mando al servidor
        String url="http://www.ieslosviveros.es/androide/index.php";
        Map<String,String> params=new HashMap<String,String>();
        params.put("code", "mensajes");
        params.put("id_destino", "" + chatMessage.id_destino);
        params.put("id_origen", ""+chatMessage.id_origen);
        params.put("mensaje", chatMessage.mensaje);
        Class[] parameterTypes = new Class[1];
        try {
            parameterTypes[0] = JSONObject.class;
            Method method1 = mensajes.class.getMethod("recibir", parameterTypes);///el metodo recibir procesa las respuestas de request cuando llegan
            request = new httpRequest(MainActivity.appContext, method1,0);
        }catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        request.getRespuesta(url, params);
    }

    public void recibirMensaje(ChatMessage chatMessage ){

        //SqlMensajes mensa= new SqlMensajes(this);
        System.out.println("------ack-recepcion " + chatMessage.id_origen + "-" + chatMessage.id_destino + "-" + chatMessage.mensaje);

        SqlMensajes Mensaje=new SqlMensajes(MainActivity.appContext);
        //Mensaje.altaBackground(chatMessage.id_origen, chatMessage.id_destino, chatMessage.mensaje);
        Mensaje.alta(chatMessage.id_origen, chatMessage.id_destino, chatMessage.mensaje);
        ///lo mando al servidor
      }
    public void recibir(JSONObject jsonObj ){

        if ( jsonObj.has("user_id")) {

        }


        System.out.println("----recibido--cccccccccccccccccccccccccccc--");

        //navigationView.getMenu().aadd("dd").setIcon(1).
    }

    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        contex.removeObserver(this);
        super.onDestroy();

        Log.v("MyApp", "onDestroy");
    }


}
