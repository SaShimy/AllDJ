package com.etna.gunzbu_a.alldj;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_GRANTYPE = "grant_type";
    public static final String KEY_CLIENTID = "client_id";
    public static final String KEY_CLIENTSECRET = "client_secret";
    private static final String REGISTER_URL = "http://apifreshdj.cloudapp.net/oauth/v2/token";
    EditText edUsername;
    EditText edPassword;
    private String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button logIn = (Button) findViewById(R.id.loginButton);
        assert logIn != null;
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        }
        );
        Button signUp = (Button) findViewById(R.id.signUp);
        assert signUp != null;
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Activity = new Intent(Login.this, SignUp.class);
                startActivity(Activity);
            }
        });
    }
    private void logIn() {
        edUsername = (EditText) findViewById(R.id.loginUsername);
        edPassword = (EditText) findViewById(R.id.loginPassword);
        final String username = edUsername.getText().toString().trim();
        final String password = edPassword.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jResponse = new JSONObject(response);
                            Intent Activity = new Intent(Login.this, Home.class);
                            Activity.putExtra("userToken", jResponse.getString("access_token"));
                            startActivity(Activity);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this,error.toString(), Toast.LENGTH_LONG).show();
                        Log.v("ERR", error.toString());
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(KEY_GRANTYPE,"password");
                params.put(KEY_CLIENTID,"1_3bcbxd9e24g0gk4swg0kwgcwg4o8k8g4g888kwc44gcc0gwwk4");
                params.put(KEY_CLIENTSECRET,"4ok2x70rlfokc8g0wws8c8kwcokw80k44sg48goc0ok4w0so0k");
                params.put(KEY_USERNAME,username);
                params.put(KEY_PASSWORD,password);
                Log.v("PAR", params.toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
        requestQueue.add(stringRequest);
    }
}
