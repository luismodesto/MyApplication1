package es.ieslosviveros.kioto;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import es.ieslosviveros.sql.SqlContactos;
import es.ieslosviveros.www.myapplication.R;
//import java.util.function.IntConsumer;

/**
 * Created by papa on 15/03/2016.
 */
public class httpRequest {

    public ProgressDialog pDialog;
    //AppCompatActivity padre;
    Context padre;
    SqlContactos padres;
    int sw1=0;
    Method recibir;
    private Map<String,String> parametros;

    public httpRequest(AppCompatActivity parent,Method funcion){
        padre=parent;
        recibir=funcion;
        pDialog = new ProgressDialog(parent);
        pDialog.setMessage("Enviando datos...");
    }


    public httpRequest(Context parent,Method funcion,int sw){
        padre=parent;
        recibir=funcion;
        if (sw==1) {
            pDialog = new ProgressDialog(parent);
            pDialog.setMessage("Enviando datos...");
        }
    }

    public httpRequest(SqlContactos parent,Method funcion,int sw){
        padres=parent;
        padre=MainActivity.appContext;
        recibir=funcion;
        sw1=1;
        if (sw==1) {
            //pDialog = new ProgressDialog(parent);
            //pDialog.setMessage("Enviando datos...");
        }
    }

    public void enviaDatos(){

        // Showing progress dialog before making http request

        pDialog.show();
        //guardaDatos();

    }
    public JSONObject getRespuesta(String url,Map<String, String> para) {
        final Map<String, String> params=new HashMap<String, String>(para);
        //final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(padre);
        JSONObject resp=new JSONObject();
        System.out.println("------------------------********** 25 datosSon.length() " + url+params.toString());
        //ConnectionRequest req = new ConnectionRequest;
        /////////////////////////////////////////////////////////////////////////////////////

        StringRequest jsonRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hidePDialog();
                System.out.println("------------------------********** longitud " + response.length() + response.toString());

                try {
                    JSONObject jsonObj = new JSONObject(response);
                    if (sw1==1){
                        recibir.invoke(padres,jsonObj);}
                    else {
                        recibir.invoke(padre,jsonObj);
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mPostCommentResponse.requestEndedWithError(error);
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        //////////////////////////////////////////////////////////////////////////
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(15000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
        System.out.println("----------1");
        Volley.newRequestQueue(padre).add(jsonRequest);
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
    public void alerta(String mensaje,String icono){
        AlertDialog alertDialog = new AlertDialog.Builder(padre).create();
        alertDialog.setTitle("Datos enviados");
        alertDialog.setMessage("" + mensaje);
        alertDialog.setButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
// aquí puedes añadir Funciones
            }
        });
        if (icono.equals("1"))alertDialog.setIcon(R.drawable.ic_warning_24dp);
        else alertDialog.setIcon(R.drawable.ic_thumb_up_24dp);
        alertDialog.show();


    }


}
