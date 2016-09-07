package com.etna.gunzbu_a.alldj;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
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

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class PlayListsActivity extends AppCompatActivity {

    public static final String KEY_NAME = "name";
    public static final String KEY_ISPUBLIC = "isPublic";

    //ListView listView;
    SwipeMenuListView listView;
    Button addBtn;

    EditText text_Playlist;
    List<PlayList> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_lists);

        final Toolbar mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final String userToken = getIntent().getExtras().getString("userToken");

        listView = (SwipeMenuListView) findViewById(R.id.listView);
        addBtn = (Button) findViewById(R.id.addPlayList);

        text_Playlist = (EditText) findViewById(R.id.text_PlayList);

        final int width = (int) dipToPixels(this,90);
        final RequestQueue queue = Volley.newRequestQueue(PlayListsActivity.this);

        RequestonAddbtn(addBtn, userToken, queue);

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

        // set creator
        listView.setMenuCreator(creator);

        show_playlist(userToken, queue);


        /*JsonArrayRequest jsonRequest = new JsonArrayRequest("http://apifreshdj.cloudapp.net/playlist/api/me", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {
                Log.v("arr", String.valueOf(response));
                try {
                    for(int i = 0; i < response.length(); i++){
                        list.add(new PlayList(response.getJSONObject(i).getString("name"),response.getJSONObject(i).getString("isPublic"),response.getJSONObject(i).getString("id")));
                    }
                    PlayListAdapter adapter = new P layListAdapter(PlayListsActivity.this, list);
                    listView.setAdapter(adapter);
                    listView.invalidateViews();
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent Activity = new Intent(PlayListsActivity.this, PlayListVideosActivity.class);
                            try {
                                Activity.putExtra("playlistId", response.getJSONObject(position).getString("id"));
                                Activity.putExtra("name", response.getJSONObject(position).getString("name"));
                                Activity.putExtra("isPublic", response.getJSONObject(position).getString("isPublic"));
                                Activity.putExtra("userToken", userToken);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            startActivity(Activity);
                        }
                    });
                    listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                            switch (index) {
                                case 0:
                                    try {
                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://apifreshdj.cloudapp.net/playlist/api/"+response.getJSONObject(position).getString("id")+"/remove",
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        Toast.makeText(PlayListsActivity.this,"La playlist a été supprimé.", Toast.LENGTH_LONG).show();
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Toast.makeText(PlayListsActivity.this,error.toString(), Toast.LENGTH_LONG).show();
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
        queue.add(jsonRequest);*/
    }

    public void show_playlist(final String userToken, final RequestQueue queue) {
        list = new ArrayList<PlayList>();
        JsonArrayRequest jsonRequest = new JsonArrayRequest("http://apifreshdj.cloudapp.net/playlist/api/me", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {
                Log.v("arr", String.valueOf(response));
                try {
                    for(int i = 0; i < response.length(); i++){
                        list.add(new PlayList(response.getJSONObject(i).getString("name"),response.getJSONObject(i).getString("isPublic"),response.getJSONObject(i).getString("id")));
                    }
                    PlayListAdapter adapter = new PlayListAdapter(PlayListsActivity.this, list);
                    listView.setAdapter(adapter);
                    listView.invalidateViews();
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent Activity = new Intent(PlayListsActivity.this, PlayListVideosActivity.class);
                            try {
                                Activity.putExtra("playlistId", response.getJSONObject(position).getString("id"));
                                Activity.putExtra("name", response.getJSONObject(position).getString("name"));
                                Activity.putExtra("isPublic", response.getJSONObject(position).getString("isPublic"));
                                Activity.putExtra("userToken", userToken);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            startActivity(Activity);
                        }
                    });
                    listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                            switch (index) {
                                case 0:
                                    try {
                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://apifreshdj.cloudapp.net/playlist/api/"+response.getJSONObject(position).getString("id")+"/remove",
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        Toast.makeText(PlayListsActivity.this,"La playlist a été supprimé.", Toast.LENGTH_LONG).show();
                                                        show_playlist(userToken, queue);
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Toast.makeText(PlayListsActivity.this,error.toString(), Toast.LENGTH_LONG).show();
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
    public void RequestonAddbtn( final Button test, final String userToken, final RequestQueue queue) {
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ispublic;
                text_Playlist = (EditText) findViewById(R.id.text_PlayList);
                RadioGroup radiogroup = (RadioGroup) findViewById(R.id.radioGroup);
                int selectedId = radiogroup.getCheckedRadioButtonId();
                RadioButton btn_ispublic = (RadioButton) findViewById(selectedId);
                ispublic = btn_ispublic.getTag().toString();

                final String name = text_Playlist.getText().toString().trim();
                if (name.matches("")) {
                    Toast.makeText(PlayListsActivity.this,"Veuillez entrer un nom pour votre playlist.", Toast.LENGTH_LONG).show();
                }
                else {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://apifreshdj.cloudapp.net/playlist/api/new",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    show_playlist(userToken, queue);
                                    text_Playlist.setText("");
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(PlayListsActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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
                            header.put("Authorization", "Bearer " + userToken);
                            return header;
                        }
                    };
                    queue.add(stringRequest);
                }
                Toast.makeText(PlayListsActivity.this,"Votre nouvelle playlist " + " a été créée.", Toast.LENGTH_LONG).show();
            }
        });
    }
    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}
