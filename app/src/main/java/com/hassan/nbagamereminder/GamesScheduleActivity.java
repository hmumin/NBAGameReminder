package com.hassan.nbagamereminder;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GamesScheduleActivity extends AppCompatActivity {

    public Spinner filtergamesListSpinner;
    public EditText searchListViewEditText;
    public Spinner teamsFilterSpinner;
    public Button filterByTeamBtn;
    public Button deleteFilterBtn;

    public ListView gamesScheduleListView;
    ArrayList<Game> gamesScheduleItems = new ArrayList<>();
    ArrayList<Game> games = new ArrayList<>();

    public ArrayList<String> listOfTeams = new ArrayList<>();
    ArrayAdapter<String> filterByteamBtnAdapter;


    //adapter for listView
    GameAdapter adapter;

    public final String TAG = "DebugSchedule";

    //for FireBase
    private static final String ALL_GAMES_KEY = "games";
    private DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games_schedule);






        //set listview to xml
        gamesScheduleListView = (ListView) findViewById(R.id.upcomingGames_ListView);

        //create adapter and pass array of questions to it
        adapter = new GameAdapter(this,gamesScheduleItems);

        //set the listView adapter
        gamesScheduleListView.setAdapter(adapter);

        //Set up Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbReference = database.getReference();


        //fetch game schedules from firebase
        fetchGames();

        //filter lisview game schedules by team
        filterByTeamBtn = (Button) findViewById(R.id.filterByTeamBtn);
        filterByteamBtnAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_dropdown_item, listOfTeams);

        //remove filter that was set
        deleteFilterBtn = (Button) findViewById(R.id.deleteFilterBtn);
        deleteFilterBtn.setEnabled(false);
        deleteFilterBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                deleteFilterBtn.setEnabled(false);
                adapter.clear();
                games.clear();
                listOfTeams.clear();
                gamesScheduleItems.clear();
                fetchGames();
            }
        });


        //when listView item is clicked
        gamesScheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long viewId)
            {
                //
                TextView visitorTeamTv = (TextView) view.findViewById(R.id.vistorTeamTV);
                TextView homeTeamTv = (TextView) view.findViewById(R.id.homeTeamTV);
                TextView gamedateTv = (TextView) view.findViewById(R.id.dateGame);
                TextView gameTimeTv = (TextView) view.findViewById(R.id.timeGame);


                //get information from the listView textview item that was clicked
                String visitorTeam = visitorTeamTv.getText().toString();
                String homeTeam = homeTeamTv.getText().toString();
                String gameDate = gamedateTv.getText().toString();
                String gameTime = gameTimeTv.getText().toString();

                //add clicked game item to user calendar
                Calendar cal = Calendar.getInstance();
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("beginTime", cal.getTimeInMillis());
                intent.putExtra("allDay", false);
                intent.putExtra("rrule", "FREQ=DAILY");
                intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
                intent.putExtra("title", visitorTeam + " vs " + homeTeam);
                startActivity(intent);






            }
        });





    }


    //when filter by team button is clicked
    public void onClick(View v) {
        new AlertDialog.Builder(this)
                .setTitle("Select a team")
                .setAdapter(filterByteamBtnAdapter, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(getApplicationContext(), "All upcoming games for: " + listOfTeams.get(which),
                                Toast.LENGTH_SHORT).show();

                        String chosenTeam = listOfTeams.get(which).toString();
                        fetchScheduleByTeam(chosenTeam);
                        deleteFilterBtn.setEnabled(true);

                        dialog.dismiss();
                    }
                }).create().show();
    }


    //Fetch all nba upcoming games data from Firebase
    public void fetchGames()
    {
        //Fetch data from FireBase to add to listView
        Query getAllQuestions = dbReference.child(ALL_GAMES_KEY);
        getAllQuestions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //arrayList of all games
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    //Log.d(TAG, "ds: " + ds);
                    Game game = ds.getValue(Game.class);
                    //get the questions key from firebase and set it to the question object
                    //question.setKey(ds.getKey());
                    //Log.d(TAG, "question and key: " + question.getQuestion() + " " + question.getKey());
                    games.add(game);
                }

                //iterate over games objects in our arraylist that we received from firebase
                // and add them to listview
                for(int i = 0; i < games.size(); i++)
                {
                    //add to listView only upcoming games after todays date
                    Date date = new Date();
                    String todaysDate = new SimpleDateFormat("EEE MMM d yyyy").format(date);
                    String dateOfGame  = games.get(i).dateOfGame;

                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d yyyy");
                    try {
                        Date today = sdf.parse(todaysDate);
                        Date gameDate = sdf.parse(dateOfGame);

                        if(gameDate.after(today))
                        {
                            adapter.add(games.get(i));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    //add all teams to teams arraylist
                    if(!listOfTeams.contains(games.get(i).getHomeTeam()))
                    {
                        listOfTeams.add(games.get(i).getHomeTeam());
                    }
                    if(!listOfTeams.contains(games.get(i).getVisitorTeam()))
                    {
                        listOfTeams.add(games.get(i).getVisitorTeam());
                    }
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    //fetch Schedules by team
    public void fetchScheduleByTeam(String chosenTeam)
    {
        adapter.clear();
        ArrayList<Game> tempArrayList = new ArrayList<>();
        for(int i = 0; i < games.size(); i++)
        {
            if(games.get(i).getHomeTeam().equalsIgnoreCase(chosenTeam) ||
                    games.get(i).getVisitorTeam().equalsIgnoreCase(chosenTeam))
            {

                //update listview with upcoming games of chosen team after todays date
                Date date = new Date();
                String todaysDate = new SimpleDateFormat("EEE MMM d yyyy").format(date);
                String dateOfGame  = games.get(i).dateOfGame;

                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d yyyy");
                try {
                    Date today = sdf.parse(todaysDate);
                    Date gameDate = sdf.parse(dateOfGame);

                    if(gameDate.after(today))
                    {
                        adapter.add(games.get(i));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }

        adapter.notifyDataSetChanged();
    }
}
