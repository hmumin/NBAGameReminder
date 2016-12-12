package com.hassan.nbagamereminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

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

        //populate data into template view using data object
        dateTv.setText(game.getDateOfGame());
        timeTV.setText(game.getTimeOfGame());
        vistorTeamTV.setText(game.getVisitorTeam());
        homeTeamTv.setText(game.getHomeTeam());
        //emptyFieldTV.setText(game.getEmptyField());
        //upVoteTv.setText(String.valueOf(question.getUpvote()));

        //return completed view to screen
        return convertView;
    }
}





