package com.etna.gunzbu_a.alldj;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.google.android.gms.appdatasearch.GetRecentContextCall;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.pubnub.api.Callback;
import com.pubnub.api.PnGcmMessage;
import com.pubnub.api.PnMessage;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kevin_Tan on 31/08/16.
 */
public class Room extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static YouTubePlayer player;
    private static Boolean is_initialized;

    private Pubnub mPubNub;
    private Button mChannelView;
    private EditText mMessageET;
    private MenuItem mHereNow;
    private ListView mListView;
    private ChatAdapter mChatAdapter;
    private SharedPreferences mSharedPrefs;
    private String username;
    private GoogleCloudMessaging gcm;
    private String gcmRegId;
    private String channel  = "";

    public static String VIDEOID;
    public static Boolean is_master = false;
    public static Integer TIME_VID = 0;
    public static String USERTOKEN = "";
    public static String ROOMID = "";
    public static Boolean is_inqueue;


    public Button JoinQueue;
    public static final String API_KEY = "AIzaSyCqiRYh13_-Fjy6qCMO9zRP1reaG4S2K6w";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        is_initialized = false;
        is_inqueue = false;
        VIDEOID = "";
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mSharedPrefs = getSharedPreferences(ChatConstants.CHAT_PREFS, MODE_PRIVATE);

        Intent i = getIntent();

        String name = i.getExtras().getString("name");
        Log.v("name", name);
        channel = name;

        username = i.getExtras().getString("username");
        SharedPreferences sp = getSharedPreferences(ChatConstants.CHAT_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(ChatConstants.CHAT_USERNAME, username);
        edit.apply();

        if (!mSharedPrefs.contains(ChatConstants.CHAT_USERNAME)){
            Intent toLogin = new Intent(this, Room.class);
            startActivity(toLogin);
            return;
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            Log.d("Main-bundle",extras.toString() + " Has Chat: " + extras.getString(ChatConstants.CHAT_ROOM));
            if (extras.containsKey(ChatConstants.CHAT_ROOM)) this.channel = extras.getString(ChatConstants.CHAT_ROOM);
        }

        this.username = mSharedPrefs.getString(ChatConstants.CHAT_USERNAME,"Anonymou0s");
        this.mListView = (ListView) findViewById(R.id.listnulle);
        this.mChatAdapter = new ChatAdapter(this, new ArrayList<ChatMessage>());
        this.mChatAdapter.userPresence(this.username, "join"); // Set user to online. Status changes handled in presence
        setupAutoScroll();
        this.mListView.setAdapter(mChatAdapter);
        setupListView();
        
        this.mMessageET = (EditText) findViewById(R.id.message_et);
        this.mChannelView = (Button) findViewById(R.id.channel_bar);
        this.mChannelView.setText(this.channel);

        this.JoinQueue = (Button) findViewById(R.id.JoinQueuebtn);
        final String userToken = getIntent().getExtras().getString("userToken");
        USERTOKEN = userToken;
        final String id = getIntent().getExtras().getString("id");
        ROOMID = id;
        Log.v("roomid", id);
        final RequestQueue queue = Volley.newRequestQueue(Room.this);

        this.JoinQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_master == false) {
                    joinqueue(userToken, queue);
                }
            }
        });

        initPubNub();
        playvideo(userToken, queue, id);
    }



    public void leavequeue() {
        final RequestQueue queue = Volley.newRequestQueue(Room.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://apifreshdj.cloudapp.net/room/api/"+ ROOMID +"/waiting_list/leave",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("ok", "queue left");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Room.this, error.toString(), Toast.LENGTH_LONG).show();
                        Log.v("ERR", error.toString());
                    }
                }) {
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", "Bearer " + USERTOKEN);
                return header;
            }
        };
        queue.add(stringRequest);
    }
    @Override
    public void onBackPressed() {
        Log.v("back", "ok");
        if (is_inqueue) {
            leavequeue();
        }
        super.onBackPressed();
    }

    public void playvideo(final String userToken, final RequestQueue queue, final String RoomId) {

        final JsonObjectRequest jsonRequest = new JsonObjectRequest("http://apifreshdj.cloudapp.net/room/api/" + RoomId + "/music", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        if(!response.has("message")){
                            try {
                                is_master = response.getBoolean("is_master");
                                if(is_master == true) {
                                    is_inqueue = false;
                                    JoinQueue.setText("Rejoindre \n la file d'attente");
                                }
                                if(response.getInt("time") < 1) {
                                    TIME_VID = 0;
                                }
                                else {
                                    TIME_VID = response.getInt("time") * 1000;
                                }

                                if(is_initialized == false && !VIDEOID.equals(response.getString("music_id"))) {
                                    VIDEOID =response.getString("music_id");
                                    Log.v("initialize", "yes");
                                    YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
                                    youTubePlayerView.initialize(API_KEY, Room.this);
                                    is_initialized  = true;
                                }
                                else if (is_initialized == true && !VIDEOID.equals(response.getString("music_id"))){
                                    VIDEOID =response.getString("music_id");
                                    player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
                                    player.loadVideo(VIDEOID, TIME_VID);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }})
            {
                public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", "Bearer " + userToken);
                return header;
            }
        };
        queue.add(jsonRequest);
    }

    public void joinqueue(final String userToken, final RequestQueue queue) {
        createAlert(userToken, queue);
    }

    public void createAlert(final String userToken, final RequestQueue queue) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Room.this);
        builder.setTitle("Choix de la musique");

        // Set up the input
        Context context = Room.this;
        final LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText edName = new EditText(context);
        edName.setHint("Mots clefs");
        final Button button = new Button(context);
        final ListView listV = new ListView(context);

        layout.addView(edName);
        layout.addView(button);
        layout.addView(listV);
        builder.setView(layout);
        final AlertDialog ad = builder.show();
        set_searchbtn(button, queue, userToken, edName, listV, ad);

    }

    public void set_searchbtn(Button searchBtn, final RequestQueue queue, final String userToken, final EditText searchText, final ListView listView, final AlertDialog ad) {
        searchBtn.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        String search;
                        final String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=20&";
                        final String API_KEY = "AIzaSyCqiRYh13_-Fjy6qCMO9zRP1reaG4S2K6w";
                        final ArrayList<Video> list_search = new ArrayList<Video>();
                        search = searchText.getText().toString();
                        search = search.replaceAll(" ", "+");
                        String urlcall_search = url +  "q=" + search + "&key=" + API_KEY;

                        searchRequest(urlcall_search,listView, userToken, queue, ad);

                    }
                }
        );
    }

    public void searchRequest(final String urlcall_search, final ListView listView,final String userToken, final RequestQueue queue, final AlertDialog ad) {
        final JsonObjectRequest jsonRequest = new JsonObjectRequest(urlcall_search, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        final String API_KEY = "AIzaSyCqiRYh13_-Fjy6qCMO9zRP1reaG4S2K6w";
                        final ArrayList<Video> list_search = new ArrayList<Video>();
                        try {
                            final JSONArray arr = response.getJSONArray("items");
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject tmp = arr.getJSONObject(i);
                                JSONObject objectId = tmp.getJSONObject("id");
                                JSONObject objectSnippet = tmp.getJSONObject("snippet");
                                String thumbnailurl = objectSnippet.getJSONObject("thumbnails").getJSONObject("default").getString("url");
                                if (objectId.has("videoId")) {
                                    list_search.add(new Video(objectSnippet.getString("title"), objectId.getString("videoId"), objectSnippet.getString("channelTitle"), thumbnailurl));
                                }
                            }
                            videoAdapter adapter = new videoAdapter(Room.this, list_search);
                            listView.setAdapter(adapter);
                            listView.invalidateViews();
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {

                                    try {
                                        Log.v("position", String.valueOf(position));
                                        int i = 0, y = 0;
                                        for (; y <= position; i++) {
                                            JSONObject tmp = arr.getJSONObject(i);
                                            JSONObject objectId = tmp.getJSONObject("id");
                                            if (objectId.has("videoId")) {
                                                y++;
                                            }
                                        }
                                        final JSONObject tmp = arr.getJSONObject(i - 1);

                                        String urlgetTD = "https://www.googleapis.com/youtube/v3/videos?part=contentDetails&id="+tmp.getJSONObject("id").getString("videoId")+"&key="+API_KEY;
                                        Log.v("urlget", urlgetTD);
                                        final JsonObjectRequest jsonRequest = new JsonObjectRequest(urlgetTD, null,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(final JSONObject response) {
                                                        try {
                                                            if (is_inqueue == false)
                                                                joinMusicQueue(userToken, queue, tmp, response.getJSONArray("items").getJSONObject(0).getJSONObject("contentDetails").getString("duration"));
                                                            else if(is_inqueue == true)
                                                                changeMusic(userToken, queue, tmp, response.getJSONArray("items").getJSONObject(0).getJSONObject("contentDetails").getString("duration"));
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.v("requestError", String.valueOf(error));
                                            }
                                        });
                                        queue.add(jsonRequest);
                                    }catch(JSONException e){
                                        e.printStackTrace();
                                    }

                                    ad.dismiss();
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

    public void joinMusicQueue(final String userToken, final RequestQueue queue, final JSONObject tmp, final String duration) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://apifreshdj.cloudapp.net/room/api/" + ROOMID + "/waiting_list/join",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("ok", "queue joined");
                        JoinQueue.setText("Changer\n la musique");
                        playvideo(userToken, queue, ROOMID);
                        is_inqueue = true;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Room.this, error.toString(), Toast.LENGTH_LONG).show();
                        Log.v("ERR", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                try {
                    params.put("musicId", tmp.getJSONObject("id").getString("videoId"));
                    params.put("duration", duration);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    public void changeMusic(final String userToken, final RequestQueue queue, final JSONObject tmp, final String duration){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://apifreshdj.cloudapp.net/room/api/" + ROOMID + "/waiting_list/music/update",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        playvideo(userToken, queue, ROOMID);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Room.this, error.toString(), Toast.LENGTH_LONG).show();
                        Log.v("ERR", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                try {
                    params.put("musicId", tmp.getJSONObject("id").getString("videoId"));
                    params.put("duration", duration);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // TODO: Update to store messages in the array.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.mHereNow = menu.findItem(R.id.action_here_now);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.action_here_now:
                hereNow(true);
                return true;
            case R.id.action_sign_out:
                signOut();
                return true;
            case R.id.action_gcm_register:
                gcmRegister();
                return true;
            case R.id.action_gcm_unregister:
                gcmUnregister();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Might want to unsubscribe from PubNub here and create background service to listen while
     *   app is not in foreground.
     * PubNub will stop subscribing when screen is turned off for this demo, messages will be loaded
     *   when app is opened through a call to history.
     * The best practice would be creating a background service in onStop to handle messages.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (this.mPubNub != null)
            this.mPubNub.unsubscribeAll();
    }

    /**
     * Instantiate PubNub object if it is null. Subscribe to channel and pull old messages via
     *   history.
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        if (this.mPubNub==null){
            initPubNub();
        } else {
            subscribeWithPresence();
            history();
        }
    }

    /**
     * I remove the PubNub object in onDestroy since turning the screen off triggers onStop and
     *   I wanted PubNub to receive messages while the screen is off.
     */
    @Override
    protected void onDestroy() {
            if (player != null) {
                player.release();
            }
        super.onDestroy();
    }

    /**
     * Instantiate PubNub object with username as UUID
     *   Then subscribe to the current channel with presence.
     *   Finally, populate the listview with past messages from history
     */
    private void initPubNub(){
        this.mPubNub = new Pubnub(ChatConstants.PUBLISH_KEY, ChatConstants.SUBSCRIBE_KEY);
        this.mPubNub.setUUID(this.username);
        subscribeWithPresence();
        history();
        gcmRegister();
    }

    /**
     * Use PubNub to send any sort of data
     * @param type The type of the data, used to differentiate groupMessage from directMessage
     * @param data The payload of the publish
     */
    public void publish(String type, JSONObject data){
        JSONObject json = new JSONObject();
        try {
            json.put("type", type);
            json.put("data", data);
        } catch (JSONException e) { e.printStackTrace(); }

        this.mPubNub.publish(this.channel, json, new BasicCallback());
    }

    /**
     * Update here now number, uses a call to the pubnub hereNow function.
     * @param displayUsers If true, display a modal of users in room.
     */
    public void hereNow(final boolean displayUsers) {
        this.mPubNub.hereNow(this.channel, new Callback() {
            @Override
            public void successCallback(String channel, Object response) {
                try {
                    JSONObject json = (JSONObject) response;
                    final int occ = json.getInt("occupancy");
                    final JSONArray hereNowJSON = json.getJSONArray("uuids");
                    Log.d("JSON_RESP", "Here Now: " + json.toString());
                    final Set<String> usersOnline = new HashSet<String>();
                    usersOnline.add(username);
                    for (int i = 0; i < hereNowJSON.length(); i++) {
                        usersOnline.add(hereNowJSON.getString(i));
                    }
                    Room.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mHereNow != null)
                                mHereNow.setTitle(String.valueOf(occ));
                            mChatAdapter.setOnlineNow(usersOnline);
                            if (displayUsers)
                                alertHereNow(usersOnline);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Called at login time, sets meta-data of users' log-in times using the PubNub State API.
     *   Information is retrieved in getStateLogin
     */
    public void setStateLogin(){
        Callback callback = new Callback() {
            @Override
            public void successCallback(String channel, Object response) {
                Log.d("PUBNUB", "State: " + response.toString());
            }
        };
        try {
            JSONObject state = new JSONObject();
            state.put(ChatConstants.STATE_LOGIN, System.currentTimeMillis());
            this.mPubNub.setState(this.channel, this.mPubNub.getUUID(), state, callback);
        }
        catch (JSONException e) { e.printStackTrace(); }
    }

    /**
     * Get state information. Information is deleted when user unsubscribes from channel
     *   so display a user not online message if there is no UUID data attached to the
     *   channel's state
     * @param user
     */
    public void getStateLogin(final String user){
        Callback callback = new Callback() {
            @Override
            public void successCallback(String channel, Object response) {
                if (!(response instanceof JSONObject)) return; // Ignore if not JSON
                try {
                    JSONObject state = (JSONObject) response;
                    final boolean online = state.has(ChatConstants.STATE_LOGIN);
                    final long loginTime = online ? state.getLong(ChatConstants.STATE_LOGIN) : 0;

                    Room.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!online)
                                Toast.makeText(Room.this, user + " is not online.", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(Room.this, user + " logged in since " + ChatAdapter.formatTimeStamp(loginTime), Toast.LENGTH_SHORT).show();

                        }
                    });

                    Log.d("PUBNUB", "State: " + response.toString());
                } catch (JSONException e){ e.printStackTrace(); }
            }
        };
        this.mPubNub.getState(this.channel, user, callback);
    }

    /**
     * Subscribe to channel, when subscribe connection is established, in connectCallback, subscribe
     *   to presence, set login time with setStateLogin and update hereNow information.
     * When a message is received, in successCallback, get the ChatMessage information from the
     *   received JSONObject and finally put it into the listview's ChatAdapter.
     * Chat adapter calls notifyDatasetChanged() which updates UI, meaning must run on UI thread.
     */
    public void subscribeWithPresence(){
        Callback subscribeCallback = new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                if (message instanceof JSONObject){
                    try {
                        JSONObject jsonObj = (JSONObject) message;
                        JSONObject json = jsonObj.getJSONObject("data");
                        String name = json.getString(ChatConstants.JSON_USER);
                        String msg  = json.getString(ChatConstants.JSON_MSG);
                        long time   = json.getLong(ChatConstants.JSON_TIME);
                        if (name.equals(mPubNub.getUUID())) return; // Ignore own messages
                        final ChatMessage chatMsg = new ChatMessage(name, msg, time);
                        Room.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mChatAdapter.addMessage(chatMsg);
                            }
                        });
                    } catch (JSONException e){ e.printStackTrace(); }
                }
                Log.d("PUBNUB", "Channel: " + channel + " Msg: " + message.toString());
            }

            @Override
            public void connectCallback(String channel, Object message) {
                Log.d("Subscribe","Connected! " + message.toString());
                hereNow(false);
                setStateLogin();
            }
        };
        try {
            mPubNub.subscribe(this.channel, subscribeCallback);
            presenceSubscribe();
        } catch (PubnubException e){ e.printStackTrace(); }
    }

    /**
     * Subscribe to presence. When user join or leave are detected, update the hereNow number
     *   as well as add/remove current user from the chat adapter's userPresence array.
     *   This array is used to see what users are currently online and display a green dot next
     *   to users who are online.
     */
    public void presenceSubscribe()  {
        Callback callback = new Callback() {
            @Override
            public void successCallback(String channel, Object response) {
                Log.i("PN-pres","Pres: " + response.toString() + " class: " + response.getClass().toString());
                if (response instanceof JSONObject){
                    JSONObject json = (JSONObject) response;
                    Log.d("PN-main","Presence: " + json.toString());
                    try {
                        final int occ = json.getInt("occupancy");
                        final String user = json.getString("uuid");
                        final String action = json.getString("action");
                        Room.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mChatAdapter.userPresence(user, action);
                                //mHereNow.setTitle(String.valueOf(occ));
                            }
                        });
                    } catch (JSONException e){ e.printStackTrace(); }
                }
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                Log.d("Presence", "Error: " + error.toString());
            }
        };
        try {
            this.mPubNub.presence(this.channel, callback);
        } catch (PubnubException e) { e.printStackTrace(); }
    }

    /**
     * Get last 100 messages sent on current channel from history.
     */
    public void history(){
        this.mPubNub.history(this.channel,100,false,new Callback() {
            @Override
            public void successCallback(String channel, final Object message) {
                try {
                    JSONArray json = (JSONArray) message;
                    Log.d("History", json.toString());
                    final JSONArray messages = json.getJSONArray(0);
                    final List<ChatMessage> chatMsgs = new ArrayList<ChatMessage>();
                    for (int i = 0; i < messages.length(); i++) {
                        try {
                            if (!messages.getJSONObject(i).has("data")) continue;
                            JSONObject jsonMsg = messages.getJSONObject(i).getJSONObject("data");
                            String name = jsonMsg.getString(ChatConstants.JSON_USER);
                            String msg = jsonMsg.getString(ChatConstants.JSON_MSG);
                            long time = jsonMsg.getLong(ChatConstants.JSON_TIME);
                            ChatMessage chatMsg = new ChatMessage(name, msg, time);
                            chatMsgs.add(chatMsg);
                        } catch (JSONException e) { // Handle errors silently
                            e.printStackTrace();
                        }
                    }

                    Room.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(Room.this,"RUNNIN",Toast.LENGTH_SHORT).show();
                            mChatAdapter.setMessages(chatMsgs);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                Log.d("History", error.toString());
            }
        });
    }

    /**
     * Log out, remove username from SharedPreferences, unsubscribe from PubNub, and send user back
     *   to the LoginActivity
     */
    public void signOut(){
        this.mPubNub.unsubscribeAll();
        SharedPreferences.Editor edit = mSharedPrefs.edit();
        edit.remove(ChatConstants.CHAT_USERNAME);
        edit.apply();
        Intent intent = new Intent(this, Home.class);
        intent.putExtra("oldUsername", this.username);
        startActivity(intent);
    }

    /**
     * Setup the listview to scroll to bottom anytime it receives a message.
     */
    private void setupAutoScroll(){
        this.mChatAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mListView.setSelection(mChatAdapter.getCount() - 1);
                // mListView.smoothScrollToPosition(mChatAdapter.getCount()-1);
            }
        });
    }

    /**
     * On message click, display the last time the user logged in.
     */
    private void setupListView(){
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatMessage chatMsg = mChatAdapter.getItem(position);
                sendNotification(chatMsg.getUsername());
            }
        });
    }

    /**
     * Publish message to current channel.
     * @param view The 'SEND' Button which is clicked to trigger a sendMessage call.
     */
    public void sendMessage(View view){
        String message = mMessageET.getText().toString();
        if (message.equals("")) return;
        mMessageET.setText("");
        ChatMessage chatMsg = new ChatMessage(username, message, System.currentTimeMillis());
        try {
            JSONObject json = new JSONObject();
            json.put(ChatConstants.JSON_USER, chatMsg.getUsername());
            json.put(ChatConstants.JSON_MSG,  chatMsg.getMessage());
            json.put(ChatConstants.JSON_TIME, chatMsg.getTimeStamp());
            publish(ChatConstants.JSON_GROUP, json);
        } catch (JSONException e){ e.printStackTrace(); }
        mChatAdapter.addMessage(chatMsg);
    }

    /**
     * Create an alert dialog with a list of users who are here now.
     * When a user's name is clicked, get their state information and display it with Toast.
     * @param userSet
     */
    private void alertHereNow(Set<String> userSet){
        List<String> users = new ArrayList<String>(userSet);
        LayoutInflater li = LayoutInflater.from(this);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Here Now");
        alertDialog.setNegativeButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final ArrayAdapter<String> hnAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,users);
        alertDialog.setAdapter(hnAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String user = hnAdapter.getItem(which);
                getStateLogin(user);
            }
        });
        alertDialog.show();
    }

    /**
     * Create an alert dialog with a text view to enter a new channel to join. If the channel is
     *   not empty, unsubscribe from the current channel and join the new one.
     *   Then, get messages from history and update the channelView which displays current channel.
     * @param view
     */
    public void changeChannel(View view){
    }

    /**
     * GCM Functionality.
     * In order to use GCM Push notifications you need an API key and a Sender ID.
     * Get your key and ID at - https://developers.google.com/cloud-messaging/
     */

    private void gcmRegister() {
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            try {
                gcmRegId = getRegistrationId();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (gcmRegId.isEmpty()) {
                registerInBackground();
            }
//            else {
//                Toast.makeText(this,"Registration ID already exists: " + gcmRegId,Toast.LENGTH_SHORT).show();
//            }
        } else {
            Log.e("GCM-register", "No valid Google Play Services APK found.");
        }
    }

    private void gcmUnregister() {
        new UnregisterTask().execute();
    }

    private void removeRegistrationId() {
        SharedPreferences prefs = getSharedPreferences(ChatConstants.CHAT_PREFS, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(ChatConstants.GCM_REG_ID);
        editor.apply();
    }

    public void sendNotification(String toUser) {
        PnGcmMessage gcmMessage = new PnGcmMessage();
        JSONObject json = new JSONObject();
        try {
            json.put(ChatConstants.GCM_POKE_FROM, this.username);
            json.put(ChatConstants.GCM_CHAT_ROOM, this.channel);
            gcmMessage.setData(json);

            PnMessage message = new PnMessage(
                    this.mPubNub,
                    toUser,
                    new BasicCallback(),
                    gcmMessage);
            message.put("pn_debug",true); // Subscribe to yourchannel-pndebug on console for reports
            message.publish();
        }
        catch (JSONException e) { e.printStackTrace(); }
        catch (PubnubException e) { e.printStackTrace(); }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, ChatConstants.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e("GCM-check", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void registerInBackground() {
        new RegisterTask().execute();
    }

    private void storeRegistrationId(String regId) {
        SharedPreferences prefs = getSharedPreferences(ChatConstants.CHAT_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ChatConstants.GCM_REG_ID, regId);
        editor.apply();
    }


    private String getRegistrationId() {
        SharedPreferences prefs = getSharedPreferences(ChatConstants.CHAT_PREFS, Context.MODE_PRIVATE);
        return prefs.getString(ChatConstants.GCM_REG_ID, "");
    }

    private void sendRegistrationId(String regId) {
        this.mPubNub.enablePushNotificationsOnChannel(this.username, regId, new BasicCallback());
    }

    private class RegisterTask extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... params) {
            String msg="";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(Room.this);
                }
                gcmRegId = gcm.register(ChatConstants.GCM_SENDER_ID);
                msg = "Device registered, registration ID: " + gcmRegId;

                sendRegistrationId(gcmRegId);

                storeRegistrationId(gcmRegId);
                Log.i("GCM-register", msg);
            } catch (IOException e){
                e.printStackTrace();
            }
            return msg;
        }
    }

    private class UnregisterTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(Room.this);
                }

                // Unregister from GCM
                gcm.unregister();

                // Remove Registration ID from memory
                removeRegistrationId();

                // Disable Push Notification
                mPubNub.disablePushNotificationsOnChannel(username, gcmRegId);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        /** add listeners to YouTubePlayer instance **/
        this.player = player;
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);

        /** Start buffering **/
        if (!wasRestored) {
            //set video to a playlist but dont play it automatically
            /*this.player.cueVideo(VIDEOID);
            this.player.play();*/
            // play automatically the video
            //player.loadVideo(VIDEOID);

            // play automatically the video at a settime in millisecond 2000ms = 2s 20000ms = 20s
            player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
            player.loadVideo(VIDEOID, TIME_VID);

        }
    }
    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Log.v("failure", String.valueOf(youTubeInitializationResult));
        Toast.makeText(this, "Failured to Initialize!", Toast.LENGTH_LONG).show();
    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {

        @Override
        public void onBuffering(boolean arg0) {
        }

        @Override
        public void onPaused() {
        }

        @Override
        public void onPlaying() {

        }

        @Override
        public void onSeekTo(int arg0) {
        }

        @Override
        public void onStopped() {
        }

    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {

        @Override
        public void onAdStarted() {
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
        }

        @Override
        public void onLoaded(String arg0) {
        }

        @Override
        public void onLoading() {
        }

        @Override
        public void onVideoEnded() {
            RequestQueue queue = Volley.newRequestQueue(Room.this);
            TIME_VID = 0;
            is_master = false;
            //YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
            playvideo(USERTOKEN, queue, ROOMID/*, youTubePlayerView*/);
        }

        @Override
        public void onVideoStarted() {
        }
    };
}
