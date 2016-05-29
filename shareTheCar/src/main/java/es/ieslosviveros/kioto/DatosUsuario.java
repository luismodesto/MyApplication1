package es.ieslosviveros.kioto;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import static java.security.AccessController.getContext;
import android.provider.Settings.Secure;
import android.widget.TextView;

import es.ieslosviveros.mapas.ActivityRuta;
import es.ieslosviveros.mapas.MapsUbicacion;
import es.ieslosviveros.www.myapplication.R;

public class DatosUsuario extends AppCompatActivity {

    private Button btnenviar;
    private Button btnruta;
    private Button btnubicacion;
    private Button btnCambiarClave;
    private Button btnRecuperarClave;
    public static EditText nombre, apellidos,email,clave,localidad,codigopostal,curso,ubicacion;
    private Spinner plazas;
    private RadioButton manana,tarde;
    private CheckBox busco,tengo;
    private RadioGroup turno;
    public ProgressDialog pDialog;
    SharedPreferences prefs;
    private Map<String,String> parametros;
    private JSONObject parametro;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datos_usuario);

        nombre=(EditText)findViewById(R.id.nombre);
        apellidos=(EditText)findViewById(R.id.apellidos);
        email=(EditText)findViewById(R.id.email);
        clave=(EditText)findViewById(R.id.clave);
        localidad=(EditText)findViewById(R.id.localidad);
        codigopostal =(EditText)findViewById(R.id.cp);
        curso=(EditText)findViewById(R.id.curso);
        ubicacion=(EditText)findViewById(R.id.ubicacion);
        busco=(CheckBox)findViewById(R.id.busco);
        tengo=(CheckBox)findViewById(R.id.tengo);
        turno=(RadioGroup)findViewById(R.id.turno);
        plazas=(Spinner)findViewById(R.id.comboPlazas);
        parametros = new HashMap<String, String>();
        parametro = new JSONObject();
        manana=(RadioButton)findViewById(R.id.manana);
        tarde=(RadioButton)findViewById(R.id.tarde);
        manana.setChecked(true);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("1");
        categories.add("2");
        categories.add("3");
        categories.add("4");
        categories.add("5");
        categories.add("6");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        plazas.setAdapter(dataAdapter);
        plazas.setSelection(3);
        ubicacion.setEnabled(false);
        prefs = getSharedPreferences(""+R.string.preferencias, Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);


        final CheckBox checkBox = (CheckBox) findViewById(R.id.tengo);
        final Button b = (Button) findViewById(R.id.obtenerRuta);
        final View l = (View) findViewById(R.id.coche1);
        l.setVisibility(View.GONE);
        final View mListView = (View) findViewById(R.id.linear);
        btnenviar = (Button) findViewById(R.id.enviar);
        btnruta = (Button) findViewById(R.id.obtenerRuta);
        btnubicacion = (Button) findViewById(R.id.obtenerUbicacion);
        btnCambiarClave=(Button) findViewById(R.id.btnCambiarClave);
        btnRecuperarClave=(Button) findViewById(R.id.btnRecuperaClave);
        ponDatos();
        if (tengo.isChecked())l.setVisibility(View.VISIBLE);
//////////////////////////////////////////////////////////
        btnruta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos el Intent
                Intent intent = new Intent(DatosUsuario.this, ActivityRuta.class);
                //Intent i = new Intent(this,LoginActivity.class);
                startActivity(intent);
                //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences();
                ubicacion.setText(prefs.getString("latitud", "") + " - " + prefs.getString("longitud", ""));
            }
        });

        btnubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos el Intent
                Intent intent = new Intent(DatosUsuario.this, MapsUbicacion.class);
                startActivity(intent);
            }
        });

        btnenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos el Intent
                ///verificamos email, clave
                String error="";
                if (email.getText().length()<5)error+="Debe poner un email válido. ";
                if (email.getText().length()<5)error+="Debe poner una clave válida(al menos 5 carcateres).";
                if (error.length()<2)enviaDatos();
                else alerta(error,"1");
                //Intent intent = new Intent(DatosUsuario.this, SaludoActivity.class);
                //startActivity(intent);
            }
        });
        btnRecuperarClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //////////hay que enviar un email
                //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                Map<String,String> params=new  HashMap<String,String>();
                try{

                    params.put("code", "remember_pass");
                    params.put("email",""+prefs.getString("email",""));
                    params.put("clave",""+prefs.getString("clave",""));
                    params.put("android_id",""+prefs.getString("android_id", ""));
                    params.put("user_id",""+prefs.getString("user_id", ""));
                    String url1 = "http://www.ieslosviveros.es/androide/index.php";//?code=register"+cola+ubi;
                    getRespuesta(url1,params,1);
                }catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("------cccccccccccccccccccccccccccc--");
                }
                //alerta("Se ha enviado un email con la clave","0");
                //startActivity(intent);
            }
        });
        btnCambiarClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //se pide la clave nueva y sem enviar al servidor
                //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                showCambiarClave();

            }
        });

        //////////////////////////////////////////////////////////////////////ocultar botones
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (buttonView.isChecked()) {
                    //System.out.println("----" + mListView.getVerticalScrollbarPosition()+mListView.getVerticalScrollbarWidth());
                    l.setVisibility(View.VISIBLE);
                    //mListView.smoothScrollToPosition(100);
                } else {
                    //System.out.println("----"+mListView.getVerticalScrollbarPosition());
                    l.setVisibility(View.GONE);

                    //int h2 = v.getHeight();
                    //mListView.smoothScrollToPosition(100);

                }

            }

        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_usuario);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

    }



    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DatosUsuario Page", // TODO: Define a title for the content shown.
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
                "DatosUsuario Page", // TODO: Define a title for the content shown.
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
    /////////////////copiar los datos desde las preferencias
    public void ponDatos(){
        String turn="1";
       // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            nombre.setText(prefs.getString("nombre", ""));
            apellidos.setText(prefs.getString("apellidos", ""));
            email.setText(prefs.getString("email", ""));
            clave.setText(prefs.getString("clave", ""));
            localidad.setText(prefs.getString("localidad", ""));
            codigopostal.setText(prefs.getString("codigopostal", ""));
            curso.setText(prefs.getString("curso", ""));
            ubicacion.setText(prefs.getString("latitud", "")+" / "+prefs.getString("longitud", ""));
            //if (prefs.getString("busco", "").equals("2"))busco.setChecked(true);
            //if (prefs.getString("tengo", "").equals("2"))tengo.setChecked(true);
            busco.setChecked(Boolean.valueOf(prefs.getString("B_coche", "")));
            tengo.setChecked(Boolean.valueOf(prefs.getString("T_coche", "")));

            System.out.println("------abusco--"+prefs.getString("B_coche", ""));
            System.out.println("------atengo--"+prefs.getString("T_coche", ""));

            plazas.setSelection(Integer.parseInt(prefs.getString("plazas", "")) - 1);

            System.out.println("------aturno--"+prefs.getString("turno", ""));
            if (prefs.getString("turno", "").equals("1"))manana.setChecked(true);
            else tarde.setChecked(true);

        }catch (Exception e) {
        e.printStackTrace();
    }


    }

    public void guardaDatos(int resp){
        String turn="1";
        if (tarde.isChecked())turn="2";
        //String android_id = Settings.Secure.getString(getContext().getContentResolver(),Settings.Secure.ANDROID_ID);
        String android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        try{
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
           // import android.provider.Settings.Secure;

        if (prefs.getString("android_id", "").length()<2)editor.putString("android_id", "" + android_id);

        editor.putString("nombre", "" + nombre.getText());
        editor.putString("apellidos", "" + apellidos.getText());
        editor.putString("android_id", android_id  );
        editor.putString("email",""+email.getText());
        editor.putString("clave",""+clave.getText());
        editor.putString("localidad",""+localidad.getText());
        editor.putString("codigopostal",""+codigopostal.getText());
        editor.putString("curso", "" + curso.getText());
        editor.putString("ubicacion", "" + ubicacion.getText());
        editor.putString("busco", "" + busco.isChecked());
        editor.putString("tengo", "" + tengo.isChecked());
        editor.putString("turno", turn );
        editor.putString("plazas", "" + plazas.getSelectedItem().toString());
        editor.commit();
        String ubi="&longitud="+prefs.getString("longitud", "")+"&latitud="+prefs.getString("latitud", "");//+"&Ruta="+URLEncoder.encode(prefs.getString("Ruta", ""), "UTF-8");
            System.out.println("------a--");

           parametros.clear();

        parametros.put("code", "register");
        parametros.put("nombre", "" + nombre.getText());
        parametros.put("apellidos",""+apellidos.getText());
        parametros.put("email",""+email.getText());
        parametros.put("clave",""+clave.getText());
        parametros.put("localidad",""+localidad.getText());
        parametros.put("codigo_postal",""+codigopostal.getText());
        parametros.put("curso",""+curso.getText());
        parametros.put("ubicacion",""+ubicacion.getText());
        parametros.put("B_coche",""+busco.isChecked());
        parametros.put("T_coche",""+tengo.isChecked());
        parametros.put("turno",turn);
        parametros.put("plazas",""+plazas.getSelectedItem().toString());
        parametros.put("longitud",""+prefs.getString("longitud", ""));
        parametros.put("latitud",""+prefs.getString("latitud", ""));
        parametros.put("ruta",""+prefs.getString("ruta", ""));
        parametros.put("android_id",""+prefs.getString("android_id", ""));
        parametros.put("user_id",""+prefs.getString("user_id", ""));
        parametros.put("reg_gcm_id", "" + prefs.getString(GCMClientManager.PROPERTY_REG_ID, ""));
            System.out.println("------b--");

        //String cola="&nombre="+ URLEncoder.encode(nombre.getText().toString(), "UTF-8")+"&apellidos="+URLEncoder.encode(apellidos.getText().toString(), "UTF-8") + "&email=" + URLEncoder.encode(email.getText().toString(), "UTF-8");
        //cola=cola+"&localidad="+URLEncoder.encode(localidad.getText().toString(), "UTF-8") + "&codigo_postal=" + URLEncoder.encode(codigopostal.getText().toString(), "UTF-8") + "&curso=" + URLEncoder.encode(curso.getText().toString(), "UTF-8");
        //cola=cola+"&ubicacion="+URLEncoder.encode(ubicacion.getText().toString(), "UTF-8")+"&B_coche="+URLEncoder.encode(""+busco.isChecked(), "UTF-8")+"&T_coche="+URLEncoder.encode(""+tengo.isChecked(), "UTF-8");
        //cola=cola+"&turno="+URLEncoder.encode(""+turno.getCheckedRadioButtonId(), "UTF-8")+"&plazas="+URLEncoder.encode(plazas.getSelectedItem().toString(), "UTF-8")+"&clave="+URLEncoder.encode(clave.getText().toString(), "UTF-8");
        String url1 = "http://www.ieslosviveros.es/androide/index.php";//?code=register"+cola+ubi;
        getRespuesta(url1,parametros,resp);
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("------cccccccccccccccccccccccccccc--");
        }

    }

    public void enviaDatos(){
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Cargando datos...");
        pDialog.show();
        guardaDatos(1);

    }

    public JSONObject getRespuesta(String url,Map<String, String> para,final int respuesta) {
        final Map<String, String> params=new HashMap<String, String>(para);
        //final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        JSONObject resp=new JSONObject();
        System.out.println("------------------------********** 25 datosSon.length() " + url+params.toString());
        //ConnectionRequest req = new ConnectionRequest;
      /////////////////////////////////////////////////////////////////////////////////////

        StringRequest jsonRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //response.printStackTrace();;
                hidePDialog();
                //datos = (ArrayList)h.get("datos");
                //if (response.getJSONObject("code").equals("1")
                //respuesta = response.toString();
                System.out.println("------------------------********** longitud " + response.length() + response.toString());

                //String Data=response.getEntity().getText().toString(); // reading the string value

    try {
        JSONObject jsonObj = new JSONObject(response);
        SharedPreferences.Editor editor = prefs.edit();
        if (jsonObj.has("user_id")) {
            editor.putString("user_id", jsonObj.getString("user_id").toString());
            editor.commit();
        }
        if (jsonObj.has("clave")) {
            editor.putString("clave", jsonObj.getString("clave").toString());
            editor.commit();
        }
        if (respuesta==1) {
            alerta("" + jsonObj.getString("message").toString(), "" + jsonObj.getString("code").toString());
        }

    } catch (JSONException e) {
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
public void alerta(String mensaje,String icono){
    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
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

public void setUbicacion(String valor){
    ubicacion.setText(valor);


}
    private TextView resultText;
    protected void showCambiarClave() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(DatosUsuario.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DatosUsuario.this);
        alertDialogBuilder.setView(promptView);

        final EditText clave_antes = (EditText) promptView.findViewById(R.id.edittext);
        final EditText clave_nueva = (EditText) promptView.findViewById(R.id.edittext1);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Map<String,String> params=new  HashMap<String,String>();
                        try{
                            params.put("code", "change_pass");
                            params.put("email",""+prefs.getString("email",""));
                            params.put("clave_anterior",""+clave_antes.getText());
                            params.put("clave_nueva",""+clave_nueva.getText());
                            params.put("android_id",""+prefs.getString("android_id", ""));
                            params.put("user_id",""+prefs.getString("user_id",""));
                            String url1 = "http://www.ieslosviveros.es/androide/index.php";//?code=register"+cola+ubi;
                            getRespuesta(url1,params,1);

                        }catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("------cccccccccccccccccccccccccccc--");
                        }

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    public void onBackPressed() {
        guardaDatos(0);
        finish();
    }
}

