package com.etna.gunzbu_a.alldj;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {
    private String TAG = "Home";

    public static final String KEY_NAME = "name";
    public static final String KEY_TYPES = "types";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final String userToken = getIntent().getExtras().getString("userToken");
        final ProgressBar spinner = (ProgressBar)findViewById(R.id.progressBar);
        assert spinner != null;
        spinner.setVisibility(View.VISIBLE);
        final ListView Rooms = (ListView) findViewById(R.id.roomList);
        createList(spinner, Rooms);
        FloatingActionButton createRoom = (FloatingActionButton) findViewById(R.id.createRoom);
        assert createRoom != null;
        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlert(userToken, spinner, Rooms);
            }
        });
        Button chatButton = (Button) findViewById(R.id.button2);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Activity = new Intent(Home.this, Room.class);
                startActivity(Activity);
            }
        });
        Button profileButton = (Button) findViewById(R.id.profileButton);
        assert profileButton != null;
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Activity = new Intent(Home.this, Profile.class);
                Activity.putExtra("userToken", userToken);
                startActivity(Activity);
            }
        });
        Button playlistsButton = (Button) findViewById(R.id.playlistsButton);
        assert playlistsButton != null;
        playlistsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Activity = new Intent(Home.this, PlayListsActivity.class);
                Activity.putExtra("userToken", userToken);
                startActivity(Activity);
            }
        });
        final SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                createList(spinner, Rooms);
                swipeContainer.setRefreshing(false);
            }
        });
        Log.v(TAG, userToken);
    }
    private void createAlert(final String userToken, final ProgressBar spinner, final ListView Rooms) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        builder.setTitle("Ajout d'un salon");

        // Set up the input
        Context context = Home.this;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText edName = new EditText(context);
        edName.setHint("Nom du salon");
        layout.addView(edName);

        final EditText edTypes = new EditText(context);
        edTypes.setHint("Type de musique");
        layout.addView(edTypes);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Créer le salon", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String name = edName.getText().toString().trim();
                final String types = edTypes.getText().toString().trim();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://apifreshdj.cloudapp.net/room/api/new",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jResponse = new JSONObject(response);
                                    Toast.makeText(Home.this, jResponse.getString("message"), Toast.LENGTH_LONG).show();
                                    createList(spinner, Rooms);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Home.this, error.toString(), Toast.LENGTH_LONG).show();
                                Log.v("ERR", error.toString());
                            }
                        }) {
                    public Map<String, String> getHeaders() {
                        Map<String, String> header = new HashMap<String, String>();
                        header.put("Authorization", "Bearer "+ userToken);
                        return header;
                    }
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(KEY_NAME, name);
                        params.put(KEY_TYPES, types);
                        Log.v("PAR", params.toString());
                        return params;
                    }


                };

                RequestQueue requestQueue = Volley.newRequestQueue(Home.this);
                requestQueue.add(stringRequest);
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void createList(final ProgressBar spinner, final ListView Rooms){
        RequestQueue queue = Volley.newRequestQueue(Home.this);

        JsonArrayRequest jsonRequest = new JsonArrayRequest("http://apifreshdj.cloudapp.net/room/all",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(final JSONArray response) {
                        spinner.setVisibility(View.GONE);
                        final String[] rooms = new String[response.length()];
                        for (int i = 0; i < response.length(); i++){
                            try {
                                rooms[i] = response.getJSONObject(i).getString("name");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(Home.this , android.R.layout.simple_list_item_1, rooms);
                        assert Rooms != null;
                        Rooms.setAdapter(myAdapter);
                        Rooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent Activity = new Intent(Home.this, Room.class);
                                try {
                                    Activity.putExtra("id", response.getJSONObject((int) id).getInt("id"));
                                    Activity.putExtra("name", response.getJSONObject((int) id).getString("name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                startActivity(Activity);
                            }
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(jsonRequest);
    }
}
