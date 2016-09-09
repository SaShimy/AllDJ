package com.etna.gunzbu_a.alldj.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.etna.gunzbu_a.alldj.Classes.PlayList;
import com.etna.gunzbu_a.alldj.R;

import java.util.List;

/**
 * Created by SuChi on 06/09/2016.
 */
public class PlayListSpinnerAdapter extends ArrayAdapter<PlayList>{

        public PlayListSpinnerAdapter(Context context,int textViewResourceId, List<PlayList> playlists) {
            super(context, textViewResourceId, playlists);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.show_playlistspinner,parent, false);
            }

            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            if(viewHolder == null){
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) convertView.findViewById(R.id.name);
                convertView.setTag(viewHolder);
            }

            //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
            PlayList playlists = getItem(position);

            //il ne reste plus qu'à remplir notre vue
            viewHolder.name.setText(playlists.getName());

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.show_playlistspinner,parent, false);
            }

            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            if(viewHolder == null){
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) convertView.findViewById(R.id.name);
                convertView.setTag(viewHolder);
            }

            //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
            PlayList playlists = getItem(position);

            //il ne reste plus qu'à remplir notre vue
            viewHolder.name.setText(playlists.getName());

            return convertView;
        }

        private class ViewHolder{
            public TextView name;
        }
}
