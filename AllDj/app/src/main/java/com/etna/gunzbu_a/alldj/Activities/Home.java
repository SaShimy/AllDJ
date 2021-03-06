package com.etna.gunzbu_a.alldj.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.etna.gunzbu_a.alldj.R;
import com.etna.gunzbu_a.alldj.Classes.Video;
import com.etna.gunzbu_a.alldj.Adapters.videoAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {
    private String TAG = "Home";

    public static final String KEY_NAME = "name";
    public static final String KEY_TYPES = "types";

    //Empêche l'utilisateur d'utiliser le bouton Back de son téléphone
    @Override
    public void onBackPressed() {
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        prepareToolbar();
        prepareMenu();
        final String userToken = getIntent().getExtras().getString("userToken");
        final String userName = getIntent().getExtras().getString("userName");
        final ProgressBar spinner = (ProgressBar)findViewById(R.id.progressBar);
        final Button disconnectButton = (Button) findViewById(R.id.logout);
        //Préparation de tous les évènements OnClick des bouttons
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnectUser();
            }
        });
        assert spinner != null;
        spinner.setVisibility(View.VISIBLE);
        final ListView Rooms = (ListView) findViewById(R.id.roomList);
        createList(spinner, Rooms, userName, userToken);

        FloatingActionButton createRoom = (FloatingActionButton) findViewById(R.id.createRoom);
        assert createRoom != null;
        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newRoom(userToken, spinner, Rooms, userName);
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
                Activity.putExtra("userName", userName);
                startActivity(Activity);
            }
        });
        final SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                createList(spinner, Rooms, userName, userToken);
                swipeContainer.setRefreshing(false);
            }
        });
    }
    private void prepareToolbar() {
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_action_name);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
    }
    private void prepareMenu() {
        ListView list = (ListView) findViewById(R.id.navList);
        ArrayList<String> menu = new ArrayList<>();
        menu.add("Profil");
        menu.add("Playlist");
        menu.add("Types");
        menu.add("Favoris");
        ArrayAdapter<String> stringAdapter = new ArrayAdapter<String>(Home.this , android.R.layout.simple_list_item_1, menu);
        list.setAdapter(stringAdapter);
    }
    //Fonction de création de l'alerte pour rajouter un nouveau salon
    private void createAlert(final String userToken, final ProgressBar spinner, final ListView Rooms, final String userName, final ArrayList<String> types) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        builder.setTitle("Ajout d'un salon");

        // Set up the input
        Context context = Home.this;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText edName = new EditText(context);
        edName.setHint("Nom du salon");
        layout.addView(edName);

        final Spinner sTypes = new Spinner(this);
        ArrayAdapter<String> Adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, types);
        sTypes.setAdapter(Adapter);
        layout.addView(sTypes);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Créer le salon", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String name = edName.getText().toString().trim();
                final String type = String.valueOf(sTypes.getSelectedItemPosition() + 1);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://apifreshdj.cloudapp.net/room/api/new",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jResponse = new JSONObject(response);
                                    Toast.makeText(Home.this, jResponse.getString("message"), Toast.LENGTH_LONG).show();
                                    createList(spinner, Rooms, userName, userToken);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Home.this, "Incident lors de la création du salon.", Toast.LENGTH_LONG).show();
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
                        params.put(KEY_TYPES, type);
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
    //Fonction de création de la liste des salons
    private void createList(final ProgressBar spinner, final ListView Rooms, final String userName, final String userToken){
        RequestQueue queue = Volley.newRequestQueue(Home.this);

        JsonArrayRequest jsonRequest = new JsonArrayRequest("http://apifreshdj.cloudapp.net/room/all",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(final JSONArray response) {
                        spinner.setVisibility(View.GONE);
                        final ArrayList<Video> rooms = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++){
                            try {
                                String thumbnailurl = new String();
                                if (response.getJSONObject(i).getString("musicYtId") == "null") {
                                    thumbnailurl = "http://www.2ememain.be/css/3148/images/pas-de-photo.png";
                                }
                                else {
                                    thumbnailurl = "http://img.youtube.com/vi/" + response.getJSONObject(i).getString("musicYtId") + "/default.jpg";
                                }
                                rooms.add(new Video(response.getJSONObject(i).getString("name"), "", response.getJSONObject(i).getString("type"), thumbnailurl));
                                videoAdapter adapter = new videoAdapter(Home.this, rooms);
                                Rooms.setAdapter(adapter);
                                Rooms.invalidateViews();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Rooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent Activity = new Intent(Home.this, Room.class);
                                try {
                                    Activity.putExtra("id", response.getJSONObject((int) id).getString("id"));
                                    Activity.putExtra("name", response.getJSONObject((int) id).getString("name"));
                                    Activity.putExtra("username", userName);
                                    Activity.putExtra("userToken", userToken);
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
    //Création d'un nouveau salon
    private void newRoom(final String userToken, final ProgressBar spinner, final ListView Rooms, final String userName) {
        RequestQueue queue = Volley.newRequestQueue(Home.this);

        JsonArrayRequest jsonRequest = new JsonArrayRequest("http://apifreshdj.cloudapp.net/music_type/types",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(final JSONArray response) {
                        ArrayList<String> types = new ArrayList<String>();
                        try {
                            for (int i = 0; response.length() > i; i++)
                            {
                                types.add(i, response.getJSONObject(i).getString("name"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        createAlert(userToken, spinner, Rooms, userName, types);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(jsonRequest);
    }
    private void disconnectUser() {
        Intent Login = new Intent(Home.this, Login.class);
        startActivity(Login);
    }
}