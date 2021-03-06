package com.etna.gunzbu_a.alldj.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.etna.gunzbu_a.alldj.Classes.PlayList;
import com.etna.gunzbu_a.alldj.Adapters.PlayListAdapter;
import com.etna.gunzbu_a.alldj.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayListsActivity extends AppCompatActivity {

    public static final String KEY_NAME = "name";
    public static final String KEY_ISPUBLIC = "isPublic";
    public String userToken = "";
    public String userName = "";

    // Une listView custom avec une possibilité de Swipe pour plus de fonctionnalités
    SwipeMenuListView listView;
    Button addBtn;

    EditText text_Playlist;


    List<PlayList> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_lists);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // création de la toolbar
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("PlayLists");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        userToken = getIntent().getExtras().getString("userToken");
        userName = getIntent().getExtras().getString("userName");


        // On récupère les différents éléments de notre layout
        listView = (SwipeMenuListView) findViewById(R.id.listView);
        addBtn = (Button) findViewById(R.id.addPlayList);
        text_Playlist = (EditText) findViewById(R.id.text_PlayList);

        // Convertion de dip en pixel qui sera utilisé pour définir l'espace pour le bouton qui apparaîtra après un swipe d'un élément d'une liste
        final int width = (int) dipToPixels(this,90);

        // Initialise la RequestQueue de volley, on ajoutera toutes nos requêtes à cette queue
        final RequestQueue queue = Volley.newRequestQueue(PlayListsActivity.this);

        // Fonction qui contient le onclickevent de addBtn et qui fera la requête pour ajouter une playlist
        RequestonAddbtn(addBtn, userToken, queue);

        // On crée le visuel de l'espace pour le bouton qui apparaît après le swipe
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(width);
                // set item title
                // On met une icône d'une "poubelle" pour représenter un delete
                deleteItem.setIcon(R.drawable.ic_delete);
                // On l'ajoute au menu
                menu.addMenuItem(deleteItem);
            }
        };

        // On implémente le swipe à la listview
        listView.setMenuCreator(creator);

        // Fonction qui fait la requête pour afficher les playlists de l'utilisateur
        show_playlist(userToken, queue);

    }
    @Override
    public void onBackPressed() {
        Intent Activity = new Intent(PlayListsActivity.this, Home.class);
        Activity.putExtra("userToken", userToken);
        Activity.putExtra("userName", userName);
        startActivity(Activity);
    }

    public void show_playlist(final String userToken, final RequestQueue queue) {
        list = new ArrayList<PlayList>();

        // Requête pour récupérer la liste des playlists (jsonarray)
        JsonArrayRequest jsonRequest = new JsonArrayRequest("http://apifreshdj.cloudapp.net/playlist/api/me", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {

                try {
                    for(int i = 0; i < response.length(); i++){
                        // On boucle les informations qu'on reçoit dans "response"
                        // on les utilise ensuite pour remplir notre ArrayList de classe <PlayList> qui contient un nom, si elle est public et son id
                        list.add(new PlayList(response.getJSONObject(i).getString("name"),response.getJSONObject(i).getString("isPublic"),response.getJSONObject(i).getString("id")));
                    }

                    // On fait appelle à notre adapter et on l'implémente à la listview
                    PlayListAdapter adapter = new PlayListAdapter(PlayListsActivity.this, list);
                    listView.setAdapter(adapter);
                    listView.invalidateViews();

                    // On crée un évènement quand on appuie sur un élément de la listview
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // On crée un intent pour aller sur une autre activité, la liste des videos de la playlist
                            // Et on passe des variables dont on aura besoin pour récupérer les videos
                            Intent Activity = new Intent(PlayListsActivity.this, PlayListVideosActivity.class);
                            try {
                                Activity.putExtra("playlistId", response.getJSONObject(position).getString("id"));
                                Activity.putExtra("name", response.getJSONObject(position).getString("name"));
                                Activity.putExtra("isPublic", response.getJSONObject(position).getString("isPublic"));
                                Activity.putExtra("userToken", userToken);
                                Activity.putExtra("userName", userName);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            startActivity(Activity);
                        }
                    });
                    // On crée un évènement quand on appuie sur l'élément qui apparaît sur la listview après un swipe
                    listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                            // s'il y a plusieurs éléments on peut dire ce qu'il y a à faire mais là il y en a qu'un seul donc ce sera 0
                            switch (index) {
                                case 0:
                                    try {
                                        // Requête pour la suppression de la playlist qu'on souhaite supprimer
                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://apifreshdj.cloudapp.net/playlist/api/"+response.getJSONObject(position).getString("id")+"/remove",
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        Toast.makeText(PlayListsActivity.this,"La playlist a été supprimé.", Toast.LENGTH_LONG).show();
                                                        // Après avoir supprimé la playlist on reload la listView en appelant show_playlist
                                                        show_playlist(userToken, queue);
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Toast.makeText(PlayListsActivity.this,"La playlist n'a pas pu être supprimée.", Toast.LENGTH_LONG).show();
                                                    }
                                                }){
                                            public Map<String, String> getHeaders() {
                                                Map<String, String> header = new HashMap<String, String>();
                                                header.put("Authorization", "Bearer " + userToken);
                                                return header;
                                            }
                                        };
                                        queue.add(stringRequest);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                            }
                            return false;
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", "Bearer " + userToken );
                return header;
            }
        };
        queue.add(jsonRequest);
    }
    public void RequestonAddbtn( final Button addBtn, final String userToken, final RequestQueue queue) {
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // On récupère l'EditText contenant le nom de la playlist qu'on souhaite créé
                text_Playlist = (EditText) findViewById(R.id.text_PlayList);

                // On récupère radiogroup qui contient les deux boutons radio
                RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radioGroup);

                // On récupère l'Id du bouton sélectionné actuellement
                int selectedId = radiogroup.getCheckedRadioButtonId();

                // On récupère le bouton sélectionné actuellement avec l'id qu'on vient de sélectionné
                RadioButton btn_ispublic = (RadioButton) findViewById(selectedId);

                // On récupère le tag du bouton selectionné qui sera true ou false dans ispublic
                final String ispublic = btn_ispublic.getTag().toString();

                // On récupère le texte qui sera le nom de la nouvelle playlist tapé par l'utilisateur
                final String name = text_Playlist.getText().toString().trim();

                // Si c'est équivalent à rien on envoie un toast pour lui dire qu'il faut entrer un nom
                if (name.matches("")) {
                    Toast.makeText(PlayListsActivity.this,"Veuillez entrer un nom pour votre playlist.", Toast.LENGTH_LONG).show();
                }
                // Sinon on fait la requête pour ajouter une nouvelle playlist
                else {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://apifreshdj.cloudapp.net/playlist/api/new",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // On réactualise la listView pour bien voir la modification de la liste des playlists
                                    show_playlist(userToken, queue);
                                    // On remet vide l'EditText pour le nom de la nouvelle playlist
                                    // comme ça s'il veut en recréer plusieurs il n'a pas besoin d'effacer le nom de la précédente playlist créé à chaque fois
                                    text_Playlist.setText("");
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(PlayListsActivity.this, "La playlist n'a pas pu être créée.", Toast.LENGTH_LONG).show();
                                    Log.v("ERR", error.toString());
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put(KEY_NAME, name);
                            params.put(KEY_ISPUBLIC, ispublic);
                            return params;
                        }

                        public Map<String, String> getHeaders() {
                            Map<String, String> header = new HashMap<String, String>();
                            header.put("Authorization", "Bearer " + userToken);
                            return header;
                        }
                    };
                    queue.add(stringRequest);
                }
                Toast.makeText(PlayListsActivity.this,"Votre nouvelle playlist a été créée.", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Conversion de dip en pixels
    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}
