package com.etna.gunzbu_a.alldj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by SuChi on 01/09/2016.
 */
public class PlayListAdapter extends ArrayAdapter<PlayList> {

    public PlayListAdapter(Context context, List<PlayList> playlists) {
        super(context, 0, playlists);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.show_playlists,parent, false);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.isPublic = (TextView) convertView.findViewById(R.id.txt_isPublic);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        PlayList playlists = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.title.setText(playlists.getName());
        if(playlists.getIspublic() == "true")
            viewHolder.isPublic.setText("Publique");
        else if (playlists.getIspublic() == "false")
            viewHolder.isPublic.setText("Privée");
        else
            viewHolder.isPublic.setText("Error");
        return convertView;
    }

    private class ViewHolder{
        public TextView isPublic;
        public TextView title;
    }

}
