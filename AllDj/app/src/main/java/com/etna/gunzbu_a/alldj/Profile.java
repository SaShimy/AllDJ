package com.etna.gunzbu_a.alldj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
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

public class Profile extends AppCompatActivity {
    public static final String KEY_FNAME = "firstname";
    public static final String KEY_LNAME = "lastname";
    public static final String KEY_EMAIL = "mail";
    public static final String KEY_BD = "birthday";
    public static final String KEY_GENDER = "gender";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String userToken = getIntent().getExtras().getString("userToken");
        loadUserInfos(userToken);
        final Button updateButton = (Button) findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfos(userToken);
            }
        });
    }
    private void loadUserInfos(final String userToken) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://apifreshdj.cloudapp.net/user/api/profile",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            TextView usernameText = (TextView) findViewById(R.id.userName);
                            EditText edFName = (EditText) findViewById(R.id.edFName);
                            EditText edLName = (EditText) findViewById(R.id.edLName);
                            EditText edBirthday = (EditText) findViewById(R.id.edBirthday);
                            EditText edEmail = (EditText) findViewById(R.id.edEmail);
                            RadioButton rMale = (RadioButton) findViewById(R.id.radioMale);
                            RadioButton rFemale = (RadioButton) findViewById(R.id.radioFemale);
                            JSONObject jResponse = new JSONObject(response);
                            usernameText.setText(jResponse.getString("username"));
                            edFName.setText(jResponse.getString("firstname"));
                            edLName.setText(jResponse.getString("lastname"));
                            edEmail.setText(jResponse.getString("email"));
                            edBirthday.setText(jResponse.getString("birthday"));
                            if (jResponse.get("gender") != null) {
                                if (jResponse.get("gender").equals("male")) {
                                    rMale.toggle();
                                }
                                else {
                                    rFemale.toggle();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Profile.this, error.toString(), Toast.LENGTH_LONG).show();
                        Log.v("ERR", error.toString());
                    }
                }) {
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", "Bearer "+ userToken);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Profile.this);
        requestQueue.add(stringRequest);
    }
    private void updateUserInfos(final String userToken){
        EditText edFName = (EditText) findViewById(R.id.edFName);
        EditText edLName = (EditText) findViewById(R.id.edLName);
        EditText edBirthday = (EditText) findViewById(R.id.edBirthday);
        EditText edEmail = (EditText) findViewById(R.id.edEmail);
        RadioButton rMale = (RadioButton) findViewById(R.id.radioMale);

        final String FName = edFName.getText().toString().trim();
        final String LName = edLName.getText().toString().trim();
        final String Birthday = edBirthday.getText().toString().trim();
        final String Email = edEmail.getText().toString().trim();
        String Gender = "";
        if (rMale.isChecked()){
            Gender = "male";
        }
        else {
            Gender = "female";
        }
        final String finalGender = Gender;
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, "http://apifreshdj.cloudapp.net/user/api/update",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jResponse = new JSONObject(response);
                            Toast.makeText(Profile.this, jResponse.getString("message"), Toast.LENGTH_LONG).show();
                            loadUserInfos(userToken);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Profile.this, error.toString(), Toast.LENGTH_LONG).show();
                        Log.v("ERR", error.toString());
                    }
                }) {
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Content-Type", "application/json");
                header.put("Authorization", "Bearer "+ userToken);
                return header;
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_FNAME, FName);
                params.put(KEY_EMAIL, Email);
                params.put(KEY_LNAME, LName);
                params.put(KEY_BD, Birthday);
                params.put(KEY_GENDER, finalGender);
                Log.v("PAR", params.toString());
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(Profile.this);
        requestQueue.add(stringRequest);
    }
}

