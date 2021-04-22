package com.example.huellasveterinaria;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.UserHandle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static android.content.Intent.getIntent;
import static android.content.Intent.getIntentOld;
import static android.widget.Toast.*;


public class Bienvenido extends Fragment implements  Response.Listener<JSONObject>, Response.ErrorListener  {
    RequestQueue rq;
    JsonRequest jrq;
    public static final String nombres="names";
    TextView Nm, NMAS, NUSU, FEC;
    String NomUs, NomMas, Fecha;

    JsonObjectRequest jsonObjectRequest;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View vista = inflater.inflate(R.layout.fragment_sesion, container, false);
        Nm = (TextView) vista.findViewById(R.id.txtbienve);
        NMAS = (TextView) vista.findViewById(R.id.txtvNM);
        NUSU = (TextView) vista.findViewById(R.id.txtvUS);
        FEC = (TextView) vista.findViewById(R.id.txtvFV);
        ConsultarEvento();
        NMAS.setText(NomMas);
        NUSU.setText(NomUs);
        FEC.setText(Fecha);
        String usu;
        rq = Volley.newRequestQueue(getContext());
        handleSSLHandshake();
        Nm.setText("¡Bienvenido  ! ");
        return vista;
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(),"No Se ha conectado a la BD" + error.toString(), LENGTH_SHORT).show();
        Log.d("my app" ,"On Error Responde " + error.toString());
    }

    @Override
    public void onResponse(JSONObject response) {
        JSONArray jsonArray = response.optJSONArray("eventos");
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonArray.getJSONObject(0);
            NomMas =  (jsonObject.optString("Nombre_mascota")).toString();
            Fecha = (jsonObject.optString("Fecha_evento")).toString();
            NomUs = (jsonObject.optString("user")).toString();
        } catch (JSONException e){
            e.printStackTrace();
            Log.d("my app" ,"onResponde " + e.toString());
            Toast.makeText(getContext(),"No Se ha conectado a la BD" + e.toString(), LENGTH_SHORT).show();
        }

    }

    private  void ConsultarEvento(){
        Usuario u = new Usuario();

        String url="https://192.168.0.25/login/Events.php?user=CAROLINDA";
        Toast.makeText(getContext(),"No Se ha conectado a la BD" + url, LENGTH_SHORT).show();
        Log.d("my app" ,"onResponde " + url);
        jrq = new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        Log.d("my app" ,"onResponde " + jrq.toString());
        rq.add(jrq);
        Log.d("my app" ,"onRe " + rq.toString());
    }

    /**
     * Enables https connections
     */
    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }

}