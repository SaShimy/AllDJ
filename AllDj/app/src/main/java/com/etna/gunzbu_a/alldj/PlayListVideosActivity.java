package com.etna.gunzbu_a.alldj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayListVideosActivity extends AppCompatActivity {

    TextView playlist_title;
    ListView listView_videos;
    final String urlcall = "http://apifreshdj.cloudapp.net/playlist/api/";
    List<Video> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list_videos);

        list = new ArrayList<Video>();
        final String userToken = getIntent().getExtras().getString("userToken");
        final String playlistId = getIntent().getExtras().getString("playlistId");
        final String name = getIntent().getExtras().getString("name");
        final String isPublic = getIntent().getExtras().getString("isPublic");

        playlist_title = (TextView) findViewById(R.id.tV_titlepl);
        listView_videos = (ListView) findViewById(R.id.listView_videos);

        playlist_title.setText(name);

        Button addBtn = (Button) findViewById(R.id.addtoPlayList);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Activity = new Intent(PlayListVideosActivity.this, Search.class);
                Activity.putExtra("userToken", userToken);
                Activity.putExtra("playlistId", playlistId);
                Activity.putExtra("playlistName", name);
                startActivity(Activity);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(PlayListVideosActivity.this);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(urlcall + playlistId + "/details", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        Log.v("test", String.valueOf(response));
                        try {
                            final JSONArray arr;
                            arr = response.getJSONArray("musics");
                            if(arr.length() != 0) {
                                for (int i = 0; i < arr.length(); i++) {
                                    list.add(new Video(arr.getJSONObject(i).getString("name"), arr.getJSONObject(i).getString("music_yt_id"), "", arr.getJSONObject(i).getString("img_url")));
                                }
                                videoAdapter adapter = new videoAdapter(PlayListVideosActivity.this, list);
                                listView_videos.setAdapter(adapter);
                                listView_videos.invalidateViews();
                            /*listView_videos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    Intent Activity = new Intent(SearchActivity.this, AddVideoActivity.class);
                                    try {
                                        JSONObject tmp = arr.getJSONObject(position);
                                        JSONObject objectId = tmp.getJSONObject("id");
                                        JSONObject objectSnippet = tmp.getJSONObject("snippet");
                                        String thumbnailurl = objectSnippet.getJSONObject("thumbnails").getJSONObject("default").getString("url");

                                        Activity.putExtra("playlistId", playlistId);
                                        Activity.putExtra("title", objectSnippet.getString("title"));
                                        Activity.putExtra("videoId", objectId.getString("videoId"));
                                        Activity.putExtra("channelTitle", objectSnippet.getString("channelTitle"));
                                        Activity.putExtra("thumbnailUrl", thumbnailurl);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(Activity);


                                });*/

                           /* if(arr.length() == 1 || arr.length() == 0) {
                                Toast.makeText(PlayListVideosActivity.this, "Il y a " + arr.length() + " musique.", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(PlayListVideosActivity.this, "Il y a " + arr.length() + " musiques.", Toast.LENGTH_LONG).show();
                            }*/
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
}
