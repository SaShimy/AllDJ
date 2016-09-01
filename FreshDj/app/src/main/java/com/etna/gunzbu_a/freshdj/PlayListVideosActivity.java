package com.etna.gunzbu_a.freshdj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by SuChi on 31/08/2016.
 */
public class PlayListVideosActivity extends MainActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_videos);

        final String id = getIntent().getExtras().getString("id");
        final String name = getIntent().getExtras().getString("name");
        final String isPublic = getIntent().getExtras().getString("isPublic");

        System.out.println("id : " + id + " name : " + name + " public : " + isPublic);

        Button addBtn = (Button) findViewById(R.id.addtoPlayList);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Activity = new Intent(PlayListVideosActivity.this, SearchActivity.class);
                Activity.putExtra("id",id);
                startActivity(Activity);
            }
        });
    }
}
