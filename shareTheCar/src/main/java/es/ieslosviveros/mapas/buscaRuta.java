package es.ieslosviveros.mapas;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import es.ieslosviveros.chat.ChatMessage;
import es.ieslosviveros.chat.mensajes;
import es.ieslosviveros.kioto.MainActivity;
import es.ieslosviveros.www.myapplication.R;

public class buscaRuta extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    public ProgressDialog pDialog;
    private mensajes mensas;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    public ArrayList<Coches> rutas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading Articles...");
        pDialog.show();
        rutas=new ArrayList<Coches>();
        mensas=new mensajes();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
        LatLng aqui = new LatLng(lastKnownLocation_byNetwork.getLatitude(), lastKnownLocation_byNetwork.getLongitude());
        mMap.addMarker(new MarkerOptions().position(aqui).title("Marker aqui"));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        //String url = "http://www.ieslosviveros.es/androide/index.php?code=get_clients&x2=37.349061891050276&x1=37.44561547273462&y2=-5.948801269531283&y1=-6.031198730468783";
        String x1, x2, y1, y2, z1, z2, z3, z4;


        x1 = "" + mMap.getProjection().getVisibleRegion().nearLeft.latitude;
        y1 = "" + mMap.getProjection().getVisibleRegion().nearLeft.longitude;
        x2 = "" + mMap.getProjection().getVisibleRegion().farRight.latitude;
        y2 = "" + mMap.getProjection().getVisibleRegion().farRight.longitude;
       // String url1 = "http://www.ieslosviveros.es/androide/index.php?code=get_clients&x2=" + x2 + "&x1=" + x1 + "&y2=" + y2 + "&y1=" + y1;
        Point p1 = new Point();
        p1.set(0, 0);
        Point p2 = new Point();
        p2.set(20, 10);
        z1 = "" + mMap.getProjection().fromScreenLocation(p1).toString();
        System.out.println("`pido rutas-----");
        SharedPreferences prefs;
        prefs = getSharedPreferences(""+R.string.preferencias, Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
        String id_origen=prefs.getString("user_id", "");


        pideRutas(0,0,0,0,id_origen);

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
        aqui = new LatLng(lastKnownLocation_byNetwork.getLatitude(), lastKnownLocation_byNetwork.getLongitude());
        mMap.addMarker(new MarkerOptions().position(aqui).title("Marker aqui " + x1 + " - " + y1 + " - " + x2 + " - " + y2));
        //mMap.addMarker(new MarkerOptions().position(dos).title("Marker 2 aqui"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(dos));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                //do something with polyline
                int pos=Integer.parseInt(polyline.getId().substring(2));
                for (int x=0;x<rutas.size();x++){
                    int pos1=Integer.parseInt(rutas.get(x).poly.substring(2));
                    if (pos <pos1 ){
                        System.out.println("`Poly clickado el -----"+x+" id "+rutas.get(x).id);
                        alert("",rutas.get(x).curso,rutas.get(x).id);
                        x=rutas.size()+1;
                    }
                }


                System.out.println("`click on poly-----"+polyline.getId());
            }
        });


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

    public void alert(String mensaje, String curso,final String id1) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Solicitar contacto...");
        alertDialog.setMessage("Enviar mensaje solicitando plaza al compañero del curso " + curso);
        alertDialog.setButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences prefs;
                prefs = getSharedPreferences(""+R.string.preferencias, Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
                String id_origen=prefs.getString("user_id", "");
                String id_destino=id1;
                String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
                String mensa="Hola, he visto que vivo cerca de tu ruta y me gustaría saber si hay sitio para mí en tu coche. Gracias";
                ChatMessage mensaje=new ChatMessage(Integer.parseInt(id_origen),Integer.parseInt(id_destino),date,mensa,Integer.parseInt(id_origen));
                mensas.enviaMensaje(mensaje);
// aquí puedes añadir funciones
            }
        });
        alertDialog.setIcon(R.drawable.ic_menu_send);
        alertDialog.show();
    }

    public void addMarcador(final ArrayList datos, GoogleMap mc) {
        String lon = datos.get(4).toString();
        String lat = datos.get(3).toString();

        LatLng marca = new LatLng(Double.valueOf(lat), Double.valueOf(lon));

        mc.addMarker(new MarkerOptions().position(marca).title("" + datos.get(0).toString() + " " + datos.get(1).toString()).snippet("This is my test app"));
        //addMarker(new MarkerOptions().position(dos).title("Marker 2 aqui"));
        System.out.println("marca *********** - " + marca.toString());
        mc.setOnMarkerClickListener(this);


    }

    public void ponRutas(ArrayList datos, GoogleMap mc) {
        //borro todo
        //mc.clear();
        //añado todo
        // campos id, email, curso, lon,lat
        int colores[]={Color.GREEN,Color.BLUE,Color.RED,Color.CYAN,Color.YELLOW,Color.MAGENTA};
        int color;
        Random rnd = new Random();
        for(int x=0;x<datos.size();x++) {
            String cadena=datos.get(x).toString();
            String [] items = cadena.split(",");

            String id_1=items[0].substring(1);
            Coches coche=new Coches(id_1,items[1],items[2]);

            rutas.add(x,coche);
            List<String> container = Arrays.asList(items);
            String sub=items[3].substring(1);
            sub=sub.substring(0, sub.length()-1);
            System.out.println("cadenao-----------"+sub+"-"+items[0]+"-"+items[1]+"-"+items[2]);
            color = Color.rgb(rnd.nextInt(128), rnd.nextInt(128), rnd.nextInt(128));//Color.argb(255, rnd.nextInt(128), rnd.nextInt(128), rnd.nextInt(128));
            //r=Color.GREEN;
            color=colores[x%6];

            drawPath(sub, color,x);

        }

        for (Object dato : datos) {
            ArrayList al1 = new ArrayList();
            al1 = (ArrayList) dato;

            System.out.println("punto-------------------------------------------------" + al1.get(0).toString());
        }
//for( String s : datos ){
        //   System.out.println( s );
        //  }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
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
////////////////////////////////////////////////////////////




    /////////////////////////////////////////////////////////////////////////
    public void pideRutas(double x1, double y1, double x2, double y2,String user_id) {
        final String url1 = "http://www.ieslosviveros.es/androide/index.php?code=get_cars&id="+user_id+"&x2=" + x1 + "&x1=" + x2 + "&y2=" + y2 + "&y1=" + y1;
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url1, null, new Response.Listener<JSONObject>() {
                    //pDialog.show();

                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        hidePDialog();
                        //datos = (ArrayList)h.get("datos");
                        System.out.println("------------------------********** pido rutas " + url1);
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
                            ponRutas(arrayList, mMap);
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

    public void drawPath(String result,int color,int y) {
        try {

            String encodedString = result;

            System.out.println("---------------" + result);
            Log.d("test: ", encodedString);
            List<LatLng> list = decodePoly(encodedString);

            LatLng last = null;
            System.out.println("--------------------------- mlista " + list.size());


            for (int i = 0; i < list.size() - 1; i++) {
                LatLng src = list.get(i);
                LatLng dest = list.get(i + 1);
                last = dest;
                //Double d=last.longitude;

                // Log.d("Last latLng: pinto- ", last.latitude + ", " + last.longitude );
                Polyline line = mMap.addPolyline(new PolylineOptions().add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude)).width(6).color(color));
                line.setClickable(true);
                rutas.get(y).poly=line.getId();
                //linea.add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude));

                //mMap.addPolyline(linea);
            }


            Log.d("Last latLng:", last.latitude + ", " + last.longitude+" --------------------------------color ---------------------- "+color);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //mMap.s
        System.out.println("--salgo-------------");
        hidePDialog();
    }

    private List<LatLng> decodePoly(String encoded) {
        System.out.println("polylinea----------------------------  " + encoded);
        ArrayList poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            // System.out.println("punto " + lat + " * " + lng);
            LatLng p = new LatLng(lat / 1E5, lng / 1E5);/////////////////////////////////
            poly.add(p);
        }
        return poly;
    }




}
