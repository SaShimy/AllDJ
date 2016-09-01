package com.etna.gunzbu_a.alldj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {

    ListView listView;
    Button searchBtn;
    EditText searchText;
    String search;
    List<Video> list;
    String urlcall;
    private static String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=20&";
    private static String API_KEY = "AIzaSyCqiRYh13_-Fjy6qCMO9zRP1reaG4S2K6w";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final String PlayListId = getIntent().getExtras().getString("playlistId");
        final String userToken = getIntent().getExtras().getString("userToken");
        final String PlayListName = getIntent().getExtras().getString("playlistName");

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);
        searchBtn = (Button) findViewById(R.id.SearchBtn);
        searchText = (EditText) findViewById(R.id.TextSearch);

        searchBtn.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        list = new ArrayList<Video>();
                        search = searchText.getText().toString();
                        search = search.replaceAll(" ", "+");
                        urlcall = url +  "q=" + search + "&key=" + API_KEY;
                        Log.v("Test", urlcall);
                        RequestQueue queue = Volley.newRequestQueue(Search.this);
                        JsonObjectRequest jsonRequest = new JsonObjectRequest(urlcall, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(final JSONObject response) {
                                        try {
                                            final JSONArray arr = response.getJSONArray("items");
                                            for (int i = 0; i < arr.length(); i++) {
                                                JSONObject tmp = arr.getJSONObject(i);
                                                JSONObject objectId = tmp.getJSONObject("id");
                                                JSONObject objectSnippet = tmp.getJSONObject("snippet");
                                                String thumbnailurl = objectSnippet.getJSONObject("thumbnails").getJSONObject("default").getString("url");
                                                if (objectId.has("videoId")) {
                                                    Log.v("test" + i, "ok");
                                                    Log.v(String.valueOf(i), objectId.getString("videoId"));

                                                    list.add(new Video(objectSnippet.getString("title"), objectId.getString("videoId"), objectSnippet.getString("channelTitle"), thumbnailurl));
                                                }
                                            }
                                            videoAdapter adapter = new videoAdapter(Search.this, list);
                                            listView.setAdapter(adapter);
                                            listView.invalidateViews();
                                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view,
                                                                        int position, long id) {
                                                    Intent Activity = new Intent(Search.this, AddVideo.class);
                                                    try {
                                                        Log.v("position", String.valueOf(position));
                                                        int i = 0,  y = 0;
                                                        for (; y <= position; i++) {
                                                            JSONObject tmp = arr.getJSONObject(i);
                                                            JSONObject objectId = tmp.getJSONObject("id");
                                                            if(objectId.has("videoId")){
                                                                y++;
                                                            }
                                                        }
                                                        Log.v("i", String.valueOf(i));
                                                        JSONObject tmp = arr.getJSONObject(i - 1);
                                                        Log.v("debugSky1", String.valueOf(tmp));
                                                        JSONObject objectId = tmp.getJSONObject("id");
                                                        JSONObject objectSnippet = tmp.getJSONObject("snippet");
                                                        String thumbnailurl = objectSnippet.getJSONObject("thumbnails").getJSONObject("default").getString("url");

                                                        Activity.putExtra("userToken", userToken);
                                                        Activity.putExtra("playlistId", PlayListId);
                                                        Activity.putExtra("playlistName", PlayListName);
                                                        Activity.putExtra("title", objectSnippet.getString("title"));
                                                        Activity.putExtra("videoId", objectId.getString("videoId"));
                                                        Activity.putExtra("channelTitle", objectSnippet.getString("channelTitle"));
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
}
