package com.hassan.nbagamereminder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private TextView helloTV;
    private ListView standingListView;
    private ProgressDialog progressDialog;
    public TextView conferenceTextView;
    public Button setUpGameReminer;
    public RadioButton easternRadioBtn;
    public RadioButton westerRadioBtn;
    public RadioGroup conferenceRadioGroup;
    public boolean westConferenceRadioBtnChecked;


    public String TAG = "DebugMain";
    public String url = "https://www.erikberg.com/nba/standings.json";



    //an array list of hashmaps of nba team standings
    ArrayList<HashMap<String, String>> nbaStandingsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //initialize view components
        helloTV = (TextView) findViewById(R.id.helloTV);
        standingListView = (ListView) findViewById(R.id.standings_ListView);
        conferenceTextView = (TextView) findViewById(R.id.conference_nameTV);
        setUpGameReminer = (Button) findViewById(R.id.set_game_reminder);


        //initialize radio buttons
        easternRadioBtn = (RadioButton) findViewById(R.id.easternRadioBtn);
        westerRadioBtn = (RadioButton) findViewById(R.id.westernRadioBtn);
        conferenceRadioGroup = (RadioGroup) findViewById(R.id.conferenceRadioGroup);
        //easternRadioBtn.isChecked();
        westConferenceRadioBtnChecked = false;
        conferenceTextView.setText("EASTERN CONFERENCE STANDINGS");



        //initialize arraylist of hash maps
        nbaStandingsList = new ArrayList<>();

        RequestStandings standingTask = new RequestStandings();
        standingTask.execute();
        Log.d(TAG, "WE START");


        conferenceRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.westernRadioBtn:
                        // do operations specific to this selection
                        westConferenceRadioBtnChecked = true;
                        nbaStandingsList.clear();
                        RequestStandings standingTask = new RequestStandings();
                        standingTask.execute();
                        conferenceTextView.setText("WESTERN CONFERENCE STANDINGS");
                        break;
                    case R.id.easternRadioBtn:
                        // do operations specific to this selection
                        westConferenceRadioBtnChecked = false;
                        nbaStandingsList.clear();
                        RequestStandings standingTask2 = new RequestStandings();
                        standingTask2.execute();
                        Log.d(TAG, "JUST CLEARED FOR TRUE EAST TEST");
                        conferenceTextView.setText("EASTERN CONFERENCE STANDINGS");
                        break;

                }
            }
        });







        //when set game reminder button is clicked
        setUpGameReminer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent view_game_schedule = new Intent(getApplicationContext(), GamesScheduleActivity.class);
                startActivity(view_game_schedule);

            }
        });

    }



    //innerclass get json data
    private class RequestStandings extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            //Show progress dialog
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void...arg0)
        {

            //handler class
            HttpHandler httpHandler = new HttpHandler();
            //making request to url and getting responses
            String jsonStr = httpHandler.makeApiCall(url);
            Log.e(TAG, "Response from URL: " + jsonStr);

            //if response is not null
            if(jsonStr != null)
            {
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);

                    //getting json array node
                    JSONArray standings = jsonObject.getJSONArray("standing");

                    //looping through all objects in the standings json array
                    for(int i = 0; i < standings.length(); i++)
                    {
                        JSONObject s = standings.getJSONObject(i);

                        //String rank = s.getString("rank");
                        String firstName = s.getString("first_name");
                        String lastName = s.getString("last_name");
                        String wins = s.getString("won");
                        String losses = s.getString("lost");
                        String conference = s.getString("conference");

                        Log.d(TAG, "INNER CHECK: " + westConferenceRadioBtnChecked);
                        if(westConferenceRadioBtnChecked == true)
                        {
                            HashMap<String,String> choiceMap = new HashMap<>();

                            if(conference.equalsIgnoreCase("WEST"))
                            {
                                //choiceMap.put("rank", "Rank #"+rank);
                                choiceMap.put("team_name", firstName + " " + lastName);
                                choiceMap.put("wins", "Wins: " + wins);
                                choiceMap.put("losses", "Losses: " + losses);

                                nbaStandingsList.add(choiceMap);

                            }



                        }
                        else if(westConferenceRadioBtnChecked == false)
                        {
                            HashMap<String,String> choiceMap = new HashMap<>();

                            if(conference.equalsIgnoreCase("EAST"))
                            {
                                //choiceMap.put("rank", "Rank #"+rank);
                                choiceMap.put("team_name", firstName + " " + lastName);
                                choiceMap.put("wins", "Wins: " + wins);
                                choiceMap.put("losses", "Losses: " + losses);

                                nbaStandingsList.add(choiceMap);


                            }


                        }

                    }

                }catch (final JSONException e)
                {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            }else {
                Log.e(TAG, "Couldn't get JSon from server");
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            //End progress dialog
            if(progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }

            //Add json data to listView
             ListAdapter adapter = new SimpleAdapter(MainActivity.this, nbaStandingsList,
                                                    R.layout.standing_list_item,
                                                    new String[]{"team_name",
                                                    "wins", "losses"}, new int[]{
                                                    R.id.team_name_textView, R.id.wins_textView,
                                                    R.id.loses_textView});
            standingListView.setAdapter(adapter);


        }



    }




}
