package com.etna.gunzbu_a.alldj;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayListVideosActivity extends AppCompatActivity {

    // Tire de la playlist dans laquelle on est
    TextView playlist_title;
    // ListView pour afficher les videos de la playlist
    SwipeMenuListView listView_videos;
    // Racine de l'url pour les requêtes
    final String urlcall_videos = "http://apifreshdj.cloudapp.net/playlist/api/";
    // Notre List avec l'information des videos de la playlist
    List<Video> list_videos;

    // La listview qui affichera les videos qu'on récupère avec la recherche YT
    ListView listView;
    Button searchBtn;
    EditText searchText;
    String search;
    // Notre List avec l'information des videos qu'on récupère avec avec la recherche YT
    List<Video> list_search;
    String urlcall_search;
    private static String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=20&";
    private static String API_KEY = "AIzaSyCqiRYh13_-Fjy6qCMO9zRP1reaG4S2K6w";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list_videos);

        // On récupère les Intent
        final String userToken = getIntent().getExtras().getString("userToken");
        final String playlistId = getIntent().getExtras().getString("playlistId");
        final String name = getIntent().getExtras().getString("name");

        // On récupère les éléments du layout
        playlist_title = (TextView) findViewById(R.id.tV_titlepl);
        listView_videos = (SwipeMenuListView) findViewById(R.id.listView_videos);

        listView = (ListView) findViewById(R.id.list);
        searchBtn = (Button) findViewById(R.id.SearchBtn);
        searchText = (EditText) findViewById(R.id.TextSearch);

        // On met le nom de la playlist
        playlist_title.setText(name);

        // Convertion de dip en pixel qui sera utilisé pour définir l'espace
        // pour le bouton qui apparaîtra après un swipe d'un élément d'une liste
        final int width = (int) dipToPixels(this,90);

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
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // On implémente le swipe à la listview
        listView_videos.setMenuCreator(creator);

        // Initialise la RequestQueue de volley, on ajoutera toutes nos requêtes à cette queue
        final RequestQueue queue = Volley.newRequestQueue(PlayListVideosActivity.this);

        // On fait la requête pour récupérer les videos de la playlist et on les affiche
        show_playlist_videos(queue, userToken, playlistId);

        // Fonction qui contient le onclickevent de searchBtn et qui fera la requête de recherche de videos sur YT
        set_searchbtn(searchBtn, queue, userToken, playlistId, name);
    }

    public void set_searchbtn(Button searchBtn, final RequestQueue queue, final String userToken, final String playlistId, final String name) {
        searchBtn.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        list_search = new ArrayList<Video>();

                        // récupère les mots clefs entrés pas l'utilisateur pour la recherche
                        search = searchText.getText().toString();
                        // On remplace les espaces par des plus car c'est géré comme ça dans les urls
                        search = search.replaceAll(" ", "+");

                        // On construit l'url avec les mots clefs de l'utilisateur et l'api key
                        urlcall_search = url +  "q=" + search + "&key=" + API_KEY;

                        // Requête de recherche de videos sur YT
                        JsonObjectRequest jsonRequest = new JsonObjectRequest(urlcall_search, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(final JSONObject response) {
                                        try {

                                            // Début du parsing on stocke dans arr l'élément items
                                            final JSONArray arr = response.getJSONArray("items");

                                            // on fait une boucle pour récupérer les informations de chaque vidéo
                                            for (int i = 0; i < arr.length(); i++) {
                                                JSONObject tmp = arr.getJSONObject(i);
                                                JSONObject objectId = tmp.getJSONObject("id");
                                                JSONObject objectSnippet = tmp.getJSONObject("snippet");
                                                String thumbnailurl = objectSnippet.getJSONObject("thumbnails").getJSONObject("default").getString("url");
                                                // On check si c'est bien une vidéo et non une chaîne YT
                                                if (objectId.has("videoId")) {
                                                    // On ajoute à notre liste new Video avec le titre de la video,
                                                    // l'id de la vidéo, la chaîne YT, et l'url de la thumbnail
                                                    list_search.add(new Video(objectSnippet.getString("title"), objectId.getString("videoId"), objectSnippet.getString("channelTitle"), thumbnailurl));
                                                }
                                            }

                                            // On utilise l'adapter avec notre liste, puis on l'implémente à notre listview
                                            videoAdapter adapter = new videoAdapter(PlayListVideosActivity.this, list_search);
                                            listView.setAdapter(adapter);
                                            listView.invalidateViews();

                                            // Evenement quand on clique sur une des videos YT dans la liste de recherche
                                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view,
                                                                        int position, long id) {
                                                    // Création d'un intent pour aller sur l'activité AddVideo, qui play la vidéo qu'on
                                                    // veut ajouter et qui nous permet de l'ajouter si on le souhaite
                                                    Intent Activity = new Intent(PlayListVideosActivity.this, AddVideo.class);
                                                    try {
                                                        // la variable position retourne la position de l'élément sur lequel on a cliqué
                                                        // et on peut le réutiliser pour recherche dans la réponse qu'on reçoit avec
                                                        // la requête mais vu qu'on a trié pour faire en sorte de virer les chaînes
                                                        // YT donc si on a position = 3 c'est pas forcément l'élément 3 de la réponse
                                                        // Donc j'ai fait une petite manip pour récupérer le bon élément avec position
                                                        Log.v("position", String.valueOf(position));
                                                        int i = 0,  y = 0;
                                                        for (; y <= position; i++) {
                                                            JSONObject tmp = arr.getJSONObject(i);
                                                            JSONObject objectId = tmp.getJSONObject("id");
                                                            if(objectId.has("videoId")){
                                                                y++;
                                                            }
                                                        }

                                                        JSONObject tmp = arr.getJSONObject(i - 1);
                                                        String thumbnailurl = tmp.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("default").getString("url");

                                                        // Ajout d'extra pour pouvoir faire la requête pour ajouter la vidéo à notre play list et
                                                        // des informations sur la vidéo pour pouvoir la lire
                                                        Activity.putExtra("userToken", userToken);
                                                        Activity.putExtra("playlistId", playlistId);
                                                        Activity.putExtra("playlistName", name);
                                                        Activity.putExtra("title", tmp.getJSONObject("snippet").getString("title"));
                                                        Activity.putExtra("videoId", tmp.getJSONObject("id").getString("videoId"));
                                                        Activity.putExtra("channelTitle", tmp.getJSONObject("snippet").getString("channelTitle"));
                                                        Activity.putExtra("thumbnailUrl", thumbnailurl);

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    startActivity(Activity);
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
                        });
                        queue.add(jsonRequest);
                    }
                }
        );
    }

    // Fonction avec la requête pour récupérer les videos de la play list et de les afficher
    public void show_playlist_videos(final RequestQueue queue, final String userToken, final String playlistId) {
        list_videos = new ArrayList<Video>();
        JsonObjectRequest jsonRequest = new JsonObjectRequest(urlcall_videos + playlistId + "/details", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        try {
                            final JSONArray arr;
                            arr = response.getJSONArray("musics");
                            if(arr.length() != 0) {
                                for (int i = 0; i < arr.length(); i++) {
                                    list_videos.add(new Video(arr.getJSONObject(i).getString("name"), arr.getJSONObject(i).getString("music_yt_id"), "", arr.getJSONObject(i).getString("img_url")));
                                }
                            }
                                videoAdapter adapter = new videoAdapter(PlayListVideosActivity.this, list_videos);
                                listView_videos.setAdapter(adapter);
                                listView_videos.invalidateViews();

                                // Implémente l'action quand on appuie sur le bouton qui apparaît après le swipe
                                set_listvideos_swipe(queue, userToken, playlistId, arr);


                                if(arr.length() == 1 || arr.length() == 0) {
                                    Toast.makeText(PlayListVideosActivity.this, "Il y a " + arr.length() + " musique.", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(PlayListVideosActivity.this, "Il y a " + arr.length() + " musiques.", Toast.LENGTH_LONG).show();
                                }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("error", String.valueOf(error));
            }
        }) {
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", "Bearer " + userToken);
                return header;
            }
        };
        queue.add(jsonRequest);
    }

    public void set_listvideos_swipe(final RequestQueue queue, final String userToken, final String playlistId, final JSONArray arr) {
        listView_videos.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        try {
                            // On supprime la vidéo de la playlist
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://apifreshdj.cloudapp.net/playlist/api/"+playlistId+"/music/"+arr.getJSONObject(position).getString("id")+"/remove",
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Toast.makeText(PlayListVideosActivity.this,"La musique a été supprimé.", Toast.LENGTH_LONG).show();
                                            // raffraichit la listview pour montrer les modifications de la playlist
                                            show_playlist_videos(queue,userToken,playlistId);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(PlayListVideosActivity.this,error.toString(), Toast.LENGTH_LONG).show();
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
    }

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}
