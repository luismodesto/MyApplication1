package es.ieslosviveros.mapas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import es.ieslosviveros.kioto.MainActivity;
import es.ieslosviveros.www.myapplication.R;

public class ActivityRuta extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    public ProgressDialog pDialog;
    String respuesta;
    //FragmentActivity base;
    JSONObject resp;
    String url;
    ArrayList<LatLng> waypoints =new ArrayList();;
    LatLng cole;
    LatLng casa;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ativity_ruta);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //waypoints = new ArrayList();
        //LatLng cole = new LatLng(37.373034, -6.0131858);
        //LatLng casa = new LatLng(37.3264213, -5.9030752);
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading Articles...");
        pDialog.show();
        prefs = getSharedPreferences(""+R.string.preferencias, Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);

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
        //waypoints = new ArrayList();
        //new LatLng()
        cole = new LatLng(Double.parseDouble(getString(R.string.ies_lat)), Double.parseDouble(getString(R.string.ies_long)));
        String ubLat=prefs.getString("ubicacionLatitud", "");
        String ubLng=prefs.getString("ubicacionLongitud", "");
        if (ubLat.equals(""))
        {

            LocationManager locat = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location lastKnownLocation_byGps = locat.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location lastKnownLocation_byNetwork = locat.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            LatLng aqui = new LatLng(lastKnownLocation_byNetwork.getLatitude(), lastKnownLocation_byNetwork.getLongitude());
     //       LatLng aquiGps = new LatLng(lastKnownLocation_byGps.getLatitude(), lastKnownLocation_byNetwork.getLongitude());

            ubLat=""+ aqui.latitude;
            ubLng= ""+aqui.longitude;
        }


        casa = new LatLng(Double.parseDouble(ubLat), Double.parseDouble(ubLng));

///////leo los waypoints
        //Gson gson = new Gson();
        //List<Product> productFromShared = new ArrayList<>();
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    /// busco si hay coordenas de origen guardas para
       // SharedPreferences sharedPrefs = getSharedPreferences("" + R.string.preferencias, Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
        //

        Gson gson = new Gson();
        String json = prefs.getString("waypoints", null);
        Type type = new TypeToken<ArrayList<LatLng>>() {}.getType();
        ArrayList<LatLng> waypoints1;
        waypoints1 = gson.fromJson(json, type);
        if (waypoints1!=null)waypoints=waypoints1;
//        System.out.println("-----------strat --arrastro--"+waypoints.toString());
        ///////////////////////


        Marker marker = mMap.addMarker(new MarkerOptions().position(cole).title("IES los Viveros"));
        Marker marker1 = mMap.addMarker(new MarkerOptions().position(casa).title("Mi ubicación"));

        // ponMarcadores();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cole));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        //hidePDialog();
        /////////////////////////////
        url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + casa.latitude + "," + casa.longitude + "&destination=" + cole.latitude + "," + cole.longitude + "&mode=driving&sensor=false";
        //ConectaHttp con=new ConectaHttp(this);
        pDialog.show();
        ponMarcadores();
        //JSONObject datosSon =this.getRespuesta(url);

        /////////////////////
        /*
        tengo que:
        Añadir puntos ///on click
        borrar puntos  ////click largo en marker
        mover puntos   ////////click en marker




         */

        ///////////////////////////////////////// add listener when touch
        // añado marcador y recalculo ruta

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                waypoints.add(latLng);
                ponMarcadores();
            }
        });
        ////////////////////////////////////////////////
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
                String[] campos = arg0.getTitle().split(" ");
                int id = Integer.parseInt(campos[campos.length - 1]);
                waypoints.remove(id - 1);
                System.out.println("-----------strat --arrastro--" + arg0.getPosition() + " - " + arg0.getTitle() + " - " + arg0.getId() + "  - " + id);
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                // TODO Auto-generated method stub
                LatLng dragPosition = marker.getPosition();
                double dragLat = dragPosition.latitude;
                double dragLong = dragPosition.longitude;

                waypoints.add(marker.getPosition());
                //Toast.makeText(FragmentActivity.this, "After onMarkerDragEnd position: " + dragLat + "  " + dragLong, Toast.LENGTH_LONG).show();
                System.out.println("------------fin -arrastro--");
                ponMarcadores();
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
                waypoints.remove(arg0.getPosition());
                System.out.println("-------------arrastro--");
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                //if(arg0.getTitle().equals("MyHome")) // if marker source is clicked
                //  Toast.makeText(MainActivity.this, arg0.getTitle(), Toast.LENGTH_SHORT).show();// display toast
                waypoints.remove(arg0.getPosition());
                ponMarcadores();
                System.out.println("-------------click--");
                return true;
            }

        });

        ////////////////
        Log.d("fin", "-----------------------------");

    }

    public void drawPath(JSONObject result) {
        try {
            final JSONObject jsonObject = result;

            JSONArray routeArray = jsonObject.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);

            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");

            String statusString = jsonObject.getString("status");
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("ruta", "" + encodedString);
            editor.commit();

            System.out.println("---------------" + encodedString);
            Log.d("test: ", encodedString);
            List<LatLng> list = decodePoly(encodedString);

            LatLng last = null;
            System.out.println("--------------------------- mlista " + list.size());
            for (int i = 0; i < list.size() - 1; i++) {
                LatLng src = list.get(i);
                LatLng dest = list.get(i + 1);
                last = dest;
                Log.d("Last latLng: pinto", last.latitude + ", " + last.longitude );
               // Random rnd = new Random();
             //   int color = Color.argb(255, rnd.nextInt(128), rnd.nextInt(128), rnd.nextInt(128));

                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
                        .width(6)
                        .color(Color.GREEN));
            }


            Log.d("Last latLng:", last.latitude + ", " + last.longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Caught ArrayIndexOutOfBoundsException: " + e.getMessage());
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
            LatLng p = new LatLng(lat / 1E5, lng / 1E5);
            poly.add(p);
        }
        return poly;
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public JSONObject getRespuesta(String url) {
        System.out.println("------------------------********** 25 datosSon.length() " + url);
        //ConnectionRequest req = new ConnectionRequest;
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    //pDialog.show();

                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        System.out.println("------------------------********  hide");
                        hidePDialog();
                        //datos = (ArrayList)h.get("datos");
                        resp = response;
                        respuesta = response.toString();
                        System.out.println("---------pruebo ---------------********** longitud " + response.length() + response.toString());
                        drawPath(response);

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
        System.out.println("----------1");
        Volley.newRequestQueue(this).add(jsonRequest);
        System.out.println("----------2");
        //pDialog.show();

        /////////////////////////////////////////////////////////////
        System.out.println("getDirections");

        return resp;
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    public void ponMarcadores() {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(cole).title("IES los Viveros"));
        mMap.addMarker(new MarkerOptions().position(casa).title("Salida"));
        MarkerOptions markerOptions = new MarkerOptions();
        System.out.println("--------1---");
        int ways=0;
        if (waypoints!=null) ways=waypoints.size();
        System.out.println("--------2---" + ways);
/////pinto marcadores//////////////////////////////////////////////
        for (int x = 0; x < ways; x++) {
            System.out.println("-----------------punto   " + waypoints.get(x));
            markerOptions.position(waypoints.get(x));
            markerOptions.title("Punto " + x + 1);
            markerOptions.draggable(true);
            System.out.println("-----");
            mMap.addMarker(markerOptions);
        }
        /////////////////////preparo la cadena para buscar al ruta con los marcadores
            String url1 = url + "&waypoints=";
            for ( int x = 0; x < ways; x++) {
                if (x > 0) url1 = url1 + "|";
                url1 = url1 + waypoints.get(x).latitude + "," + waypoints.get(x).longitude;
                System.out.println(waypoints.get(x));
            }
                System.out.println("--------------waipoints --------------- " + url1);
                //System.out.println("----"+latLng.longitude+"  "+latLng.latitude);
                //mMap.clear();
                //ponMarcadores();
//    pDialog.show();
                JSONObject datosSon = getRespuesta(url1);

                /////////////////////////////////guarod los waypoints/////////////////
                Gson gson = new Gson();
                String jsonCurProduct = gson.toJson(waypoints);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("waypoints", jsonCurProduct);
                editor.commit();


               /* SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor mEdit1 = prefs.edit();
                mEdit1.putInt("waypoints_size", sKey.size()); // sKey is an array

                for(int i=0;i<sKey.size();i++)
                {
                    mEdit1.remove("Status_" + i);
                    mEdit1.putString("Status_" + i, sKey.get(i));
                }

                return mEdit1.commit();
*/
                ///////////////////////////
    }

}




/////////////////////////////////////