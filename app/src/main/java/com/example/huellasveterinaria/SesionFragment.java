package com.example.huellasveterinaria;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

import androidx.fragment.app.FragmentManager;

public class SesionFragment extends Fragment implements  Response.Listener<JSONObject>, Response.ErrorListener {
    RequestQueue rq;
    JsonRequest jrq;
    EditText cajauser, cajapwd;
    Button btnconsultar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
       // return inflater.inflate(R.layout.fragment_sesion,container,false);
        View vista = inflater.inflate(R.layout.fragment_sesion, container, false);
        cajauser = (EditText) vista.findViewById(R.id.txtUser);
        cajapwd = (EditText) vista.findViewById(R.id.txtPwd);
        btnconsultar = (Button) vista.findViewById(R.id.btnSesion);
        rq = Volley.newRequestQueue(getContext());
        handleSSLHandshake();
        btnconsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( "Myapp","onClick: Voy pal ck"+view.toString());
                iniciarsesion();
           }
        });
        return vista;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(),"No Se ha conectado a la BD" + error.toString(), LENGTH_SHORT).show();
        Log.d("my app" ,"onErrorResponse:" + error.toString());
    }

    @Override
    public void onResponse(JSONObject response) {
        Usuario u = new Usuario();

        JSONArray jsonArray = response.optJSONArray("datos");
        JSONObject jsonObject = null;

        try {
            jsonObject = jsonArray.getJSONObject(0);
            u.setUser(jsonObject.optString("user"));
            u.setPwd(jsonObject.optString("pwd"));
            u.setNombre(jsonObject.optString("names"));
            Log.d("my app" ,"onResponse" + u.getNombre());
        } catch (JSONException e){
            e.printStackTrace();
            Toast.makeText(getContext(),"Hay errores en la conexión" + e.toString(), LENGTH_LONG).show();

        }
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.Ingreso, new Bienvenido()).commit();
    }

    private  void iniciarsesion(){
        String url="https://192.168.0.25/login/sesion.php?user="+cajauser.getText().toString()+
                "&pwd="+cajapwd.getText().toString();

        jrq = new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        rq.add(jrq);

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