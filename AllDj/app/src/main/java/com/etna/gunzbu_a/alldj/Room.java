package com.etna.gunzbu_a.alldj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Room extends AppCompatActivity {
    private String TAG = "Room";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        TextView nameRoom = (TextView) findViewById(R.id.nameRoom);
        Intent i = getIntent();
        int id = i.getExtras().getInt("id");
        String name = i.getExtras().getString("name");
        nameRoom.setText(name);
        Log.v(TAG, String.valueOf(id));
        Log.v(TAG, name);
    }
}
