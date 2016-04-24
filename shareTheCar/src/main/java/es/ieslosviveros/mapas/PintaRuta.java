package es.ieslosviveros.mapas;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by papa on 10/04/2016.
 */
public class PintaRuta {
    private GoogleMap mMap;
    public PintaRuta(GoogleMap googleMap){

        mMap = googleMap;
    }

    public void drawPath(String encodedString) {
        try {

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

                Polyline line = mMap.addPolyline(new PolylineOptions().add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude)).width(6).color(Color.GREEN));
            }


            Log.d("Last latLng:", last.latitude + ", " + last.longitude);
        } catch (Exception e) {
            e.printStackTrace();
               }
        //mMap.s
        System.out.println("--salgo-------------");
        //hidePDialog();
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
}
