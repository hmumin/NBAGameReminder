package com.hassan.nbagamereminder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by hmumin on 12/9/16.
 */

public class GameAdapter extends ArrayAdapter<Game> {


    public GameAdapter(Context context, ArrayList<Game> games)
    {
        super(context, 0 , games); //Llst starting at Zero or the first one
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //get data item for this positon
        Game game = getItem(position);
        //check if an existing view is being reused, otherwise inflate the view
        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.gameitem, parent
                    , false);
        }

        //Lookup view from data population
        TextView dateTv = (TextView) convertView.findViewById(R.id.dateGame);
        TextView timeTV = (TextView) convertView.findViewById(R.id.timeGame);
        TextView vistorTeamTV = (TextView) convertView.findViewById(R.id.vistorTeamTV);
        TextView homeTeamTv = (TextView) convertView.findViewById(R.id.homeTeamTV);
        //TextView emptyFieldTV = (TextView) convertView.findViewById(R.id.emptyFieldTV);




        //Change time zone of game which is ET to users time zone
        String gameTimeET = game.getTimeOfGame();
        SimpleDateFormat df = new SimpleDateFormat("h:mm a");
        df.setTimeZone(TimeZone.getTimeZone("EST"));
        Date timestamp = null;
        String timeSend = null;

        try {
            timestamp = df.parse(gameTimeET);
            df.setTimeZone(TimeZone.getDefault());
            //System.out.println(df.format(timestamp));
            timeSend = df.format(timestamp).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        //populate data into template view using data object
        dateTv.setText(game.getDateOfGame());
        timeTV.setText(timeSend);
        vistorTeamTV.setText(game.getVisitorTeam());
        homeTeamTv.setText(game.getHomeTeam());


        //return completed view to screen
        return convertView;
    }
}





