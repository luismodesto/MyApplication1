package es.ieslosviveros.mapas;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import es.ieslosviveros.chat.ChatMessage;
import es.ieslosviveros.chat.mensajes;
import es.ieslosviveros.www.myapplication.R;

public class buscaUsuarios extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    public ProgressDialog pDialog;
    private mensajes mensas;
    public SharedPreferences prefs;
    private Map<Marker, Clientes> allMarkersMap = new HashMap<Marker, Clientes>();
private PintaRuta pintaRuta;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        TextView texto=(TextView) findViewById(R.id.texto_en_mapa);
        texto.setText("");
        mensas=new mensajes();

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Cargando Usuarios...");

        pDialog.show();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_maps);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        pintaRuta=new PintaRuta(mMap);
        prefs = getSharedPreferences(""+R.string.preferencias, Context.MODE_PRIVATE);
        pintaRuta.drawPath(prefs.getString("Ruta", ""));
        ///creo  hhtp
//http://www.ieslosviveros.es/androide/index.php?
// x2=37.349061891050276&x1=37.44561547273462&y2=-5.948801269531283&y1=-6.031198730468783&code=get_clients
        // String resp=excutePost("http://www.ieslosviveros.es/androide/index.php", "x2=37.384883958107075&x1=37.39695419258188&y2=-5.984850158691439&y1=-5.995149841308627&code=get_clients");
///con volley
        LocationManager loc = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location loca = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        final String gpsLocationProvider = LocationManager.GPS_PROVIDER;
        final String networkLocationProvider = LocationManager.NETWORK_PROVIDER;
        LocationManager locat = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastKnownLocation_byGps = locat.getLastKnownLocation(gpsLocationProvider);
        Location lastKnownLocation_byNetwork = locat.getLastKnownLocation(networkLocationProvider);
        LatLng aqui= new LatLng(Double.parseDouble(getString(R.string.ies_lat)), Double.parseDouble(getString(R.string.ies_long)));
        if (lastKnownLocation_byGps!=null) aqui = new LatLng(lastKnownLocation_byGps.getLatitude(), lastKnownLocation_byGps.getLongitude());
        if (lastKnownLocation_byNetwork!=null) aqui = new LatLng(lastKnownLocation_byNetwork.getLatitude(), lastKnownLocation_byNetwork.getLongitude());

        mMap.addMarker(new MarkerOptions().position(aqui).title("Posicion actual"));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            //@override
            public void onCameraChange(CameraPosition cameraPosition) {
                // Make a web call for the locations
                //myTask = new MyTask();
                //myTask.execute();


                Point p1 = new Point();
                p1.set(0, 0);
                //String url1 = "http://www.ieslosviveros.es/androide/index.php?code=get_clients&x2="+x2+"&x1="+x1+"&y2="+y2+"&y1="+y1;
                String z1 = "" + mMap.getProjection().fromScreenLocation(p1).toString();
                //z1=""+mMap.getProjection().fromScreenLocation(p1).toString();
                //System.out.println("url1 --------------------------------------------------------------"+z1);
                double x1, x2, y1, y2, z2, z3, z4;
                x1 = mMap.getProjection().getVisibleRegion().nearLeft.latitude;
                y1 = mMap.getProjection().getVisibleRegion().nearLeft.longitude;
                x2 = mMap.getProjection().getVisibleRegion().farRight.latitude;
                y2 = mMap.getProjection().getVisibleRegion().farRight.longitude;
                //String url1 = "http://www.ieslosviveros.es/androide/index.php?code=get_clients&x2=" + x1 + "&x1=" + x2 + "&y2=" + y2 + "&y1=" + y1+"&turno="+turno;
                // Point p1=new Point();p1.set(0,0);
                Point p2 = new Point();
                p2.set(20, 10);
                //z1 = "" + mMap.getProjection().fromScreenLocation(p1).toString();
                //System.out.println(url1);
                //pDialog.show();
                String turno=prefs.getString("turno", "");
                pideMarcadores(x1, y1, x2, y2,turno);
               // hidePDialog();

            }
        });
        //String url = "http://www.ieslosviveros.es/androide/index.php?code=get_clients&x2=37.349061891050276&x1=37.44561547273462&y2=-5.948801269531283&y1=-6.031198730468783";
        String x1, x2, y1, y2, z1, z2, z3, z4;


        x1 = "" + mMap.getProjection().getVisibleRegion().nearLeft.latitude;
        y1 = "" + mMap.getProjection().getVisibleRegion().nearLeft.longitude;
        x2 = "" + mMap.getProjection().getVisibleRegion().farRight.latitude;
        y2 = "" + mMap.getProjection().getVisibleRegion().farRight.longitude;
        String turno=prefs.getString("turno", "");
        String url1 = "http://www.ieslosviveros.es/androide/index.php?code=get_clients&x2=" + x2 + "&x1=" + x1 + "&y2=" + y2 + "&y1=" + y1+"&turno="+turno;
        Point p1 = new Point();
        p1.set(0, 0);
        Point p2 = new Point();
        p2.set(20, 10);
        z1 = "" + mMap.getProjection().fromScreenLocation(p1).toString();
        System.out.println(url1 + z1);


        //////////////////////////////////////////////////////////////////////


        ////////////////fin hhtp

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        LatLng dos = new LatLng(37.383785094965, -6.0067499967032);
        //Obtenemos una referencia al LocationManager


        if (lastKnownLocation_byGps == null) {
            //textView_GpsLocation.setText("GPS Last Location not available");
        }


        //Obtenemos la última posición conocida
        //Location loc1 = loc.getLastKnownLocation() getLastKnownLocation(LocationManager.GPS_PROVIDER);
      //  aqui = new LatLng(lastKnownLocation_byNetwork.getLatitude(), lastKnownLocation_byNetwork.getLongitude());
        //mMap.addMarker(new MarkerOptions().position(aqui).title("Marker aqui " + x1 + " - " + y1 + " - " + x2 + " - " + y2));
        LatLng cole = new LatLng(Double.parseDouble(getString(R.string.ies_lat)), Double.parseDouble(getString(R.string.ies_long)));
        mMap.addMarker(new MarkerOptions().position(cole).title("IES los Viveros"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cole));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

    }

    private void showMyAddress(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Geocoder myLocation = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            System.out.println(myLocation.getFromLocation(latitude, longitude, 1).toString());

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    public static String excutePost(String targetURL, String urlParameters) {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();

        } catch (Exception e) {

            e.printStackTrace();
            return e.toString();

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public void alert(String mensaje,String curso,final String user_id) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Ofrecer Asiento...");
        alertDialog.setMessage("Enviar mensaje ofreciendo asiento al compañero del curso " + curso);
        alertDialog.setButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences prefs;
                prefs = getSharedPreferences(""+R.string.preferencias, Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
                String id_origen=prefs.getString("user_id", "");
                String id_destino=user_id;
                String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
                String mensa="Hola, dispongo de asientos en mi coche y paso cerca de tu ubicación y me gustaría saber si estas intereado en venir conmigo. Gracias";
                ChatMessage mensaje=new ChatMessage(Integer.parseInt(id_origen),Integer.parseInt(id_destino),date,mensa,Integer.parseInt(id_origen));
                mensas.enviaMensaje(mensaje);

// aquí puedes añadir Funciones
            }
        });
        alertDialog.setIcon(R.drawable.ic_menu_send);
        alertDialog.show();
    }

    public void addMarcador(final ArrayList datos, GoogleMap mc) {
        String lon = datos.get(4).toString();
        String lat = datos.get(3).toString();

        LatLng marca = new LatLng(Double.valueOf(lat), Double.valueOf(lon));

        Marker marker=mc.addMarker(new MarkerOptions().position(marca).title("" + datos.get(0).toString() + " " + datos.get(1).toString()).snippet("This is my test app"));
        //addMarker(new MarkerOptions().position(dos).title("Marker 2 aqui"));
        Clientes cliente=new Clientes(datos.get(0).toString(),  datos.get(1).toString(),  datos.get(2).toString(),  datos.get(3).toString(),  datos.get(4).toString());
        allMarkersMap.put(marker,cliente);
        System.out.println("marca *********** - " + marca.toString());
        mc.setOnMarkerClickListener(this);

    }

    public void ponMarcadores(ArrayList datos, GoogleMap mc) {
        //borro todo
        //mc.clear();
        //añado todo
        // campos id, email, curso, lon,lat
        for (Object dato : datos) {
            ArrayList al1 = new ArrayList();
            al1 = (ArrayList) dato;
            addMarcador(al1, mc);

            System.out.println("punto-------------------------------------------------" + al1.get(0).toString());
        }
//for( String s : datos ){
        //   System.out.println( s );
        //  }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Clientes cliente = allMarkersMap.get(marker);
        alert("", cliente.curso, cliente.user_id);
        //Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
        System.out.println("mostrnado  " + marker.getTitle());
        return true;
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    public void pintaMarcadores() {


    }

    public void pideMarcadores(double x1, double y1, double x2, double y2,String turno) {
        final String url1 = "http://www.ieslosviveros.es/androide/index.php?code=get_clients&x2=" + x1 + "&x1=" + x2 + "&y2=" + y2 + "&y1=" + y1+"&turno="+turno;
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url1, null, new Response.Listener<JSONObject>() {
                    //pDialog.show();

                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        hidePDialog();
                        //datos = (ArrayList)h.get("datos");
                        System.out.println("------------------------********** 25 datosSon.length() " + url1);
                        try {
                            ArrayList datos;
                            //String code,message;
                            String code = response.getString("code");
                            String message = response.getString("message");
                            JSONArray datosSon = response.getJSONArray("datos");

                            ArrayList arrayList = new ArrayList(datosSon.length());
                            for (int i = 0; i < datosSon.length(); i++) {
                                ArrayList fila = new ArrayList(datosSon.getJSONArray(i).length());
                                for (int j = 0; j < datosSon.getJSONArray(i).length(); j++) {
                                    fila.add(datosSon.getJSONArray(i).get(j).toString());
                                }
                                arrayList.add(fila);
                                System.out.println("-" + datosSon.length());
                            }
                            ponMarcadores(arrayList, mMap);
                            System.out.println("response-----" + arrayList.size());
                            //System.out.println("Site: "+site+"\nNetwork: "+network);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(15000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
        Volley.newRequestQueue(this).add(jsonRequest);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://es.ieslosviveros.www.myapplication/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://es.ieslosviveros.www.myapplication/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
