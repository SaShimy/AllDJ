package com.etna.gunzbu_a.freshdj;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kevin_Tan on 08/06/2016.
 */
public class PlaylistActivity extends MainActivity {

    public static final String KEY_NAME = "name";
    public static final String KEY_ISPUBLIC = "isPublic";
    ListView listView;
    Button addBtn;

    EditText text_Playlist;
    List<PlayList> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);




        listView = (ListView) findViewById(R.id.listView);
        addBtn = (Button) findViewById(R.id.addPlayList);

        text_Playlist = (EditText) findViewById(R.id.text_PlayList);

        list = new ArrayList<PlayList>();

        final RequestQueue queue = Volley.newRequestQueue(PlaylistActivity.this);
        JsonArrayRequest jsonRequest = new JsonArrayRequest("http://apifreshdj.cloudapp.net/playlist/api/me", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {
                Log.v("arr", String.valueOf(response));
                try {
                    for(int i = 0; i < response.length(); i++){
                        list.add(new PlayList(response.getJSONObject(i).getString("name"),response.getJSONObject(i).getString("isPublic"),response.getJSONObject(i).getString("id")));
                    }
                    playListAdapter adapter = new playListAdapter(PlaylistActivity.this, list);
                    listView.setAdapter(adapter);
                    listView.invalidateViews();
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent Activity = new Intent(PlaylistActivity.this, PlayListVideosActivity.class);
                            try {
                                Activity.putExtra("id", response.getJSONObject(position).getString("id"));
                                Activity.putExtra("name", response.getJSONObject(position).getString("name"));
                                Activity.putExtra("isPublic", response.getJSONObject(position).getString("isPublic"));
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
        }) {
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", "Bearer " + "NzZiZDljNWM0YjA4MzU5YmE3YzBkNmQ1MzlhMDIwNmNhMTMxOWFlODQ3YzYzN2I3MDRjZTgzMGNmNTM2Nzg1Yg" );
                return header;
            }
        };
        queue.add(jsonRequest);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ispublic;
                text_Playlist = (EditText) findViewById(R.id.text_PlayList);
                RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radioGroup);
                int selectedId = radiogroup.getCheckedRadioButtonId();
                RadioButton btn_ispublic = (RadioButton) findViewById(selectedId);
                if(btn_ispublic.getText() == "public") { ispublic = "true";}
                else { ispublic = "false"; }
                final String name = text_Playlist.getText().toString().trim();
                if (name.matches("")) {
                    Toast.makeText(PlaylistActivity.this,"Veuillez entrer un nom pour votre playlist.", Toast.LENGTH_LONG).show();
                }
                else {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://apifreshdj.cloudapp.net/playlist/api/new",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(PlaylistActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                                    Log.v("ERR", error.toString());
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put(KEY_NAME, name);
                            params.put(KEY_ISPUBLIC, ispublic);
                            Log.v("PAR", params.toString());
                            return params;
                        }

                        public Map<String, String> getHeaders() {
                            Map<String, String> header = new HashMap<String, String>();
                            header.put("Authorization", "Bearer " + "NzZiZDljNWM0YjA4MzU5YmE3YzBkNmQ1MzlhMDIwNmNhMTMxOWFlODQ3YzYzN2I3MDRjZTgzMGNmNTM2Nzg1Yg");
                            return header;

                        }
                    };
                    queue.add(stringRequest);
                }
                Toast.makeText(PlaylistActivity.this,"Votre nouvelle playlist " + name + " a été créée.", Toast.LENGTH_LONG).show();
            }
        });
    }
}