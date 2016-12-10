package com.hassan.nbagamereminder;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
    private Switch confrenceSwitch;
    public TextView conferenceTextView;
    public boolean westConferenceSwitchChecked;


    public String TAG = "DebugMain";
    public String url = "https://www.erikberg.com/nba/standings.json";


    //an array list of hashmaps of nba team standings
    ArrayList<HashMap<String, String>> nbaStandingsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //refrenace view components
        helloTV = (TextView) findViewById(R.id.helloTV);
        standingListView = (ListView) findViewById(R.id.standings_ListView);
        conferenceTextView = (TextView) findViewById(R.id.conference_nameTV);

        //switch
        confrenceSwitch = (Switch) findViewById(R.id.east_west_toggle);
        confrenceSwitch.setChecked(true);
        westConferenceSwitchChecked = true;
        conferenceTextView.setText("WESTERN CONFERENCE STANDINGS");

        //initialize arraylist of hash maps
        nbaStandingsList = new ArrayList<>();

        RequestStandings staningTask = new RequestStandings();
        staningTask.execute();
        Log.d(TAG, "WE START");
        //when switch toggle is clicked
        confrenceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    westConferenceSwitchChecked = true;
                    nbaStandingsList.clear();
                    RequestStandings staningTask = new RequestStandings();
                    staningTask.execute();
                    conferenceTextView.setText("WESTERN CONFERENCE STANDINGS");





                }
                else
                {
                    westConferenceSwitchChecked = false;
                    nbaStandingsList.clear();
                    RequestStandings staningTask = new RequestStandings();
                    staningTask.execute();
                    Log.d(TAG, "JUST CLEARED FOR TRUE EAST TEST");
                    conferenceTextView.setText("EASTERN CONFERENCE STANDINGS");



                }
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

                        Log.d(TAG, "INNER CHECK: " + westConferenceSwitchChecked);
                        if(westConferenceSwitchChecked == true)
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
                        else if(westConferenceSwitchChecked == false)
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
