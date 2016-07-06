package com.etna.gunzbu_a.freshdj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by SuChi on 06/07/2016.
 */
public class videoAdapter extends ArrayAdapter<Video> {

    public videoAdapter(Context context, List<Video> videos) {
        super(context, 0, videos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.show_videos,parent, false);
        }

        TweetViewHolder viewHolder = (TweetViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new TweetViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.channelName = (TextView) convertView.findViewById(R.id.channelName);
            viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        Video videos = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.title.setText(videos.getTitle());
        viewHolder.channelName.setText(videos.getChannelTitle());
        new ImageLoadTask(videos.getThumbnailUrl(), viewHolder.thumbnail).execute();
        return convertView;
    }

    private class TweetViewHolder{
        public TextView channelName;
        public TextView title;
        public ImageView thumbnail;
    }
}
