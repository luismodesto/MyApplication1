package es.ieslosviveros.kioto;

import android.Manifest;
import android.app.ActionBar;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;


import es.ieslosviveros.chat.Contactos;
import es.ieslosviveros.chat.IObservable;
import es.ieslosviveros.chat.IObserver;
import es.ieslosviveros.chat.mensajes;
import es.ieslosviveros.mapas.ActivityRuta;
import es.ieslosviveros.mapas.buscaRuta;
import es.ieslosviveros.mapas.buscaUsuarios;
import es.ieslosviveros.noticias.Noticia;
import es.ieslosviveros.noticias.NoticiaAdapter;
import es.ieslosviveros.www.myapplication.About;
import es.ieslosviveros.www.myapplication.R;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public SharedPreferences prefs;
    public static MainActivity activity;
    DrawerLayout  drawerLayout;
    ListView drawerList;
    String [] tagTitles;
   // Menu menu_lateral;
    httpRequest request;
    NavigationView navigationView;
    ListView listado;
    ArrayAdapter<String> adaptador;
    NoticiaAdapter adaptadorNoticia;
    ListView lista;
    int cont=0;
    int fin=0;
    ArrayList<String> arregloCadenas;
    ArrayList<Noticia> arregloNoticias;
    String url;
    Noticia noticia;
    public static Context appContext;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public WebView webView;
    float version;
    String caducada;
    public ArrayList<IObservable> observerlist=new ArrayList<IObservable>();

    private String[] dayOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday","Thursday", "Friday", "Saturday"};

    public static int NOTIFICATION_ID = 1010;
    public static LatLng aqui;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity=this;

        super.onCreate(savedInstanceState);
        appContext=getApplicationContext();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String noti_url ="",notification_id="";

        Intent intent = getIntent();
        if (intent.hasExtra(notification_id)){
            notification_id = getIntent().getExtras().getString("notification_id");
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(Integer.parseInt(notification_id));
        }
        if (intent.hasExtra(noti_url)){
            noti_url = getIntent().getExtras().getString("url");
            webView.getSettings().setBuiltInZoomControls(true);
            webView.setWebViewClient(new WebViewClient());
            webView.getSettings().setJavaScriptEnabled(true);
            if (noticia.enlace.indexOf("http://")==-1 && noti_url.indexOf("https://")==-1)
                webView.loadUrl("http://" + noti_url);
            else
                webView.loadUrl( noti_url);

        }
//////////////////////////////////////permisos para gps/////////
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

///////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////////// menu pestañas
        /*TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("TELÉFONOS"));
        tabs.addTab(tabs.newTab().setText("TABLETS"));
        tabs.addTab(tabs.newTab().setText("PORTÁTILES"));*/
        //////////////////////

        prefs = getSharedPreferences(""+R.string.preferencias, Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
        version=prefs.getFloat("version", 0);
        caducada=prefs.getString("caducada","0");
        if (caducada.equals("1")){
            TextView texto=(TextView) this.findViewById(R.id.encabezado);
            texto.setText("Nueva version disponible");
        }
        if (caducada.equals("2")){
            TextView texto=(TextView) this.findViewById(R.id.encabezado);
            texto.setText("Version caducada. Debe instalar la nueva");
            return;
        }
        Class[] parameterTypes = new Class[1];

        webView=(WebView) this.findViewById(R.id.webView1);;
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setLoadWithOverviewMode(false);
        webView.getSettings().setUseWideViewPort(false);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);

        //adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arregloCadenas)

        try {
            parameterTypes[0] = JSONObject.class;
            Method method1 = MainActivity.class.getMethod("recibir", parameterTypes);///el metodo recibir procesa las respuestas de request cuando llegan
            request = new httpRequest(this, method1);
        }catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
//////////////////////////////obtengo el regid y lo mando al servidor
        String PROJECT_NUMBER="323188939745";//498610966391";


        GCMClientManager pushClientManager = new GCMClientManager(this, PROJECT_NUMBER);
        try {
            LocationManager locat = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location lastKnownLocation_byGps = locat.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location lastKnownLocation_byNetwork = locat.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            aqui = new LatLng(lastKnownLocation_byNetwork.getLatitude(), lastKnownLocation_byNetwork.getLongitude());
        } catch (SecurityException e) {
        //dialogGPS(this.getContext()); // lets the user know there is a problem with the gps
            e.printStackTrace();
    }

        pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
//////////////////////obtengo la loc

            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {
                System.out.println("---------------reg------------ " + registrationId + " - " + "id");
                if (isNewRegistration )//&& prefs.getString("user_id", "").length()>0)
                 {
                    url = "http://www.ieslosviveros.es/androide/index.php";
                    Map<String, String> params = new HashMap<String, String>();
                     params.put("code", "reg_id_gcm");
                     params.put("android_id", "" + prefs.getString("android_id", ""));
                    params.put("user_id", "" + prefs.getString("user_id", ""));
                    params.put("reg_id", "" + registrationId);
                    params.put("last_loc", "" + aqui);
                     System.out.println("---------------enviado----------------- " + registrationId + " - " + "id");

                     SharedPreferences.Editor editor = prefs.edit();
                     editor.putString("reg_id", "" + registrationId);
                     editor.commit();
                    request.getRespuesta(url, params);
                }
                Log.d("Registration id", registrationId);
                //send this registrationId to your server
            }

            @Override
            public void onFailure(String ex) {
                super.onFailure(ex);
            }
        });
        /////////////////////////////////////////////////////////////////////////////////////////////
        //Obtener arreglo de strings desde los recursos
        tagTitles = getResources().getStringArray(R.array.Tags);
        //Obtener drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Obtener listview
        //  drawerList = (ListView) findViewById(R.id.left_drawer);

        ///////////////////////////////////////////////////////////////////////////////
        url="http://www.ieslosviveros.es/androide/index.php";
        Map<String,String> params=new HashMap<String,String>();
            params.put("code", "indice_noticias");
            params.put("inicio",""+0);
            params.put("salto" ,""+12);

        request.getRespuesta(url, params);
        ///url de descarga http://www.ieslosviveros.es/v2/ultimasentradas.php?inicio=0&salto=10
        // Relacionar el adaptador y la escucha de la lista del drawer
       // drawerList.setAdapter(new DrawerListAdapter(this, items));
        arregloCadenas = new ArrayList<String>();
        arregloCadenas.add("cadena "+cont++);


        arregloNoticias = new ArrayList<Noticia>();
        Noticia noticia=new Noticia("hola","cara","cola");
        //arregloNoticias.add(noticia);
        //adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arregloCadenas);
        lista=(ListView)findViewById(R.id.list_view_inside_nav);
        adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arregloCadenas);
       ////
        adaptadorNoticia=new NoticiaAdapter(this,arregloNoticias);
        lista.setAdapter(adaptadorNoticia);
        //listado=(ListView)findViewById(R.id.list_view_inside_nav);
        //arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dayOfWeek);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                drawerLayout.closeDrawer(GravityCompat.START);
                Noticia noticia=(Noticia)adaptadorNoticia.getItem(position);
                webView.getSettings().setBuiltInZoomControls(true);
                webView.setWebViewClient(new WebViewClient());
                webView.getSettings().setJavaScriptEnabled(true);
                if (noticia.enlace.indexOf("http://")==-1 && noticia.enlace.indexOf("https://")==-1)
                webView.loadUrl("http://" + noticia.enlace);
                else
                    webView.loadUrl(noticia.enlace);
                System.out.println("--------------------------------click " + position + " - " + id + noticia.enlace);
            }
        });


        lista.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
        int a= view.getScrollX();
               int b= view.getListPaddingBottom();
                System.out.println("--------------------------------Sroll "+fin+" - "+a+" cambio "+scrollState);
                if (fin==0){
                    int tot=arregloNoticias.size();
                    url="http://www.ieslosviveros.es/androide/index.php";
                    Map<String,String> params=new HashMap<String,String>();
                    params.put("code", "indice_noticias");
                    params.put("inicio",""+tot);
                    params.put("salto" ,""+12);
                    request.getRespuesta(url, params);
                    //arregloCadenas.add("cadena "+cont++);
                    //adaptador.notifyDataSetChanged();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                fin=(int)totalItemCount-visibleItemCount-firstVisibleItem;
                System.out.println("--------------------------------Sroll posicion primero "+firstVisibleItem+" visibles items "+visibleItemCount+" total items "+totalItemCount);
            }
        });



        ///////////////////////////////////////////////////

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ///////////////////


        ///////////////////////////////////////////////////////
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        try {
             //lista.seton
            listado=drawerList;
            System.out.println("colocado  ----");

            }catch (Exception e){
        e.printStackTrace();
            System.out.println("dfgdfgd----------------------------------------------------------------------------------------------------***********************************************d");
        }
     }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (prefs.getString("user_id", "").length()>0) {
            /////si no hay datos en preferencias saco menu 1
            //si hay datos guardados saco menu 2
            menu.add(0, 0, 0, "Datos de usuario").setShortcut('3', 'c');
            menu.add(0, 1, 0, "Buscar vehiculos").setShortcut('3', 'c');
            menu.add(0, 2, 0, "Buscar ocupantes").setShortcut('4', 's');
            menu.add(0, 3, 0, "Mensajes").setShortcut('4', 's');
            menu.add(0, 6, 0, "Acerca de").setShortcut('4', 's');
            //menu.add(0, 4, 0, "Opciones").setShortcut('4', 's');
        }else {
            menu.add(0, 0, 0, "Datos de usuario").setShortcut('3', 'c');
            menu.add(0, 5, 0, "Login con email y clave").setShortcut('3', 'c');
            menu.add(0, 6, 0, "Acerca de").setShortcut('4', 's');
        }

        //menu.add(0, 2, 0, "Otra Información").setShortcut('4', 's');
        //}
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    menu.clear();
        if (prefs.getString("user_id", "").length()>0) {
            /////si no hay datos en preferencias saco menu 1
            //si hay datos guardados saco menu 2
            menu.add(0, 0, 0, "Datos de usuario").setShortcut('3', 'c');
            menu.add(0, 1, 0, "Buscar vehiculos").setShortcut('3', 'c');
            menu.add(0, 2, 0, "Buscar ocupantes").setShortcut('4', 's');
            menu.add(0, 3, 0, "Mensajes").setShortcut('4', 's');
            menu.add(0, 4, 0, "Opciones").setShortcut('4', 's');
        }else {
            menu.add(0, 0, 0, "Datos de usuario").setShortcut('3', 'c');
            menu.add(0, 5, 0, "Login con email y clave").setShortcut('3', 'c');
            menu.add(0, 4, 0, "Opciones").setShortcut('4', 's');
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent i;
    switch (id) {
        case 0:
            i = new Intent(this, DatosUsuario.class);
            startActivity(i);
            break;
        case 1:
            i = new Intent(this, buscaRuta.class);
            startActivity(i);
            break;
        case 2:
            i = new Intent(this, buscaUsuarios.class);
            startActivity(i);
            break;
        case 3:
            i = new Intent(this, Contactos.class);
            i.putExtra("id_origen",1234); //id_usuario el mio
            i.putExtra("id_destino",5678); //id_ del otro
            //i.getClass() //addObserver(i);
            startActivity(i);
            break;
        case 4:
            i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            break;
        case 5:
            i = new Intent(this, LoginActivity.class);
            startActivity(i);
            break;
        case 6:
            i = new Intent(this, About.class);
            startActivity(i);
            break;
    }

        System.out.println("----pulsado--ccccccccccccc" + id);
        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //SharedPreferences.Editor editor = prefs.edit();
        LatLng cole = new LatLng(37.373034, -6.0131858);
        LatLng casa = new LatLng(37.3264213, -5.9030752);
        //HashSet origen=new HashSet("casa");

        //HashSet destino=new HashSet(<String>(ArrayList("37.373034", "-6.0131858")));
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("origenLatitud", "" + casa.latitude);
        editor.putString("origenLongitud", "" + casa.longitude);
        editor.putString("destinoLatitud", "" + cole.latitude);
        editor.putString("destinoLongitud", "" + cole.longitude);
        editor.commit();
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            // Handle the camera action
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_datos_usuario) { //datos de usuario
            Intent i = new Intent(this,DatosUsuario.class);
            startActivity(i);
        } else if (id == R.id.nav_busca_coches) { //
            Intent i = new Intent(this,ActivityRuta.class);
            startActivity(i);
        } else if (id == R.id.nav_busca_ocupantes) {
            Intent i = new Intent(this,buscaUsuarios.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void getPrefs(){

    }
    public void setPrefs(){

    }

public void recibir(JSONObject jsonObj ){
    try {

    if ( jsonObj.has("user_id")) {

    }
        if ( jsonObj.has("url")) {
            webView.getSettings().setBuiltInZoomControls(true);
            webView.setWebViewClient(new WebViewClient());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(jsonObj.getString("url").toString());
        }
        if ( jsonObj.has("version")) {
            float ver=Float.parseFloat(jsonObj.getString("version").toString());
            if (version==0){
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("version",""+ver);
                editor.commit();
            }
            if (version<ver){
                SharedPreferences.Editor editor = prefs.edit();
                if ( jsonObj.has("caducar")){
                    if (jsonObj.getString("caducar").toString().equals("2"))  editor.putString("caducada","2");
                  }else  editor.putString("caducada", "1");
                editor.commit();
            }

        }
    System.out.println("----recibido--cccccccccccccccccccccccccccc--");
    //navigationView.getMenu().aadd("dd").setIcon(1).
    if ( jsonObj.has("message")) {
        if (jsonObj.getString("message").toString().equals("indice noticias")){
            int cont=1,sw=0;
            while (sw==0){
                sw=1;
                if (jsonObj.has("cadena" + cont)) {
                    noticia=new Noticia(jsonObj.getString("cadena"+cont).toString(),jsonObj.getString("fecha"+cont).toString(),jsonObj.getString("enlace"+cont).toString());
                    arregloNoticias.add(noticia);
                    adaptadorNoticia.notifyDataSetChanged();
                    cont++;
                    sw=0;
                }
            }
        }
    }
    }catch (Exception e){
        e.printStackTrace();
        System.out.println("dfgdfgd----------------------------------------------------------------------------------------------------***********************************************d");
    }
}


    public void triggerNotification(String msg,int tipo,String destino){

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.ic_menu_manage, "¡Nuevo mensaje!", System.currentTimeMillis());
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
        //contentView.setImageViewResource(R.id.imagen_notifi, R.drawable.ic_menu_manage);

        contentView.setTextViewText(R.id.txt_notifi, " "+msg+" - "+tipo+" - "+destino);

        notification.contentView = contentView;
        Intent notificationIntent = new Intent(this,MainActivity.class );
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.contentIntent = contentIntent;
        notificationManager.notify(MainActivity.NOTIFICATION_ID, notification);


       /* NotificationManager mNotificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setContentTitle("Notificación GCM")
                        .setContentText(msg);

        Intent notIntent =  new Intent(this, MainActivity.class);
        PendingIntent contIntent = PendingIntent.getActivity(
                this, 0, notIntent, 0);

        mBuilder.setContentIntent(contIntent);

        mNotificationManager.notify(MainActivity.NOTIFICATION_ID, mBuilder.build());
*/
    }

    public void update(){
        for(int x=0;x<observerlist.size();x++){
            observerlist.get(x).update();
        }
    }
    public void addObserver (IObservable observer){
        observerlist.add(observer);
    }
    public void removeObserver(IObservable observer){
        observerlist.remove(observer);
    }

}
