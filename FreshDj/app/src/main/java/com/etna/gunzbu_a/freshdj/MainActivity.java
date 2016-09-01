package com.etna.gunzbu_a.freshdj;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

//import retrofit2.Call;

public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    public static final String API_KEY = "AIzaSyCqiRYh13_-Fjy6qCMO9zRP1reaG4S2K6w";

    //http://youtu.be/<VIDEO_ID>
    public static final String VIDEO_ID = "VtrFwflxjWA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnSend = (Button) findViewById(R.id.sendButton);
        Button btnSign = (Button) findViewById(R.id.toSignUp);
        Button btnSearch = (Button) findViewById(R.id.toSearch);
        Button btnPlayList = (Button) findViewById(R.id.toPlayList);
        final EditText sendText = (EditText) findViewById(R.id.editText);
        assert btnSend != null;
        btnPlayList.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent Activity = new Intent(MainActivity.this, PlaylistActivity.class);
                startActivity(Activity);
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent Activity = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(Activity);
            }
        });
        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Activity = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(Activity);
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://apifreshdj.cloudapp.net/texttoupper/" + sendText.getText().toString();
                url = url.replaceAll(" ", "%20");
                Log.v("Test", url);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                JsonObjectRequest jsonRequest = new JsonObjectRequest(url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(final JSONObject response) {
                                try {
                                    sendText.setText(response.getString("texttoupper"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        sendText.setText("That didn't work!");
                    }
                });
                queue.add(jsonRequest);
            }
        });

        /** Initializing YouTube player view **/
        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
        youTubePlayerView.initialize(API_KEY, this);

        System.out.println("lol");
    }

    /*class ListRoomTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            ApiCalls client = ServiceGenerator.createService(ApiCalls.class);

            Call<List<Room>> call = client.rooms();

            try {
                List<Room> rooms = call.execute().body();
                for (Room room : rooms) {
                    System.out.println(room.getName());
                }
            } catch (IOException e) {
                // handle errors
            }
            return null;
        }
    }*/

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        /** add listeners to YouTubePlayer instance **/
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);

        /** Start buffering **/
        if (!wasRestored) {
            player.cueVideo(VIDEO_ID);
        }
    }
    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Failured to Initialize!", Toast.LENGTH_LONG).show();
    }

    private PlaybackEventListener playbackEventListener = new PlaybackEventListener() {

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

    private PlayerStateChangeListener playerStateChangeListener = new PlayerStateChangeListener() {

        @Override
        public void onAdStarted() {
        }

        @Override
        public void onError(ErrorReason arg0) {
        }

        @Override
        public void onLoaded(String arg0) {
        }

        @Override
        public void onLoading() {
        }

        @Override
        public void onVideoEnded() {
        }

        @Override
        public void onVideoStarted() {
        }
    };
}
