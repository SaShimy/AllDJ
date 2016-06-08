package com.etna.gunzbu_a.freshdj;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;

public class SignUpActivity extends AppCompatActivity {
    JSONArray array = new JSONArray();
    EditText username;
    EditText password;
    EditText email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Button signUp = (Button) findViewById(R.id.signUp);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);

        assert signUp != null;
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObject json = new JsonObject();
                json.addProperty("foo", "bar");

                Ion.with(SignUpActivity.this)
                        .load("http://apifreshdj.cloudapp.net/signup/")
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                // do stuff with the result or error
                            }
                        });

            }
        });

    }
}
