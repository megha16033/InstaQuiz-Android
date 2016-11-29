package com.example.mnrr.instaquizgui;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class UpvotePollActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.OnConnectionFailedListener {

    String topic="";
    String username="";
    String question="";
    SharedPreferences sharedpreferences;
    private GoogleApiClient mGoogleApiClient;


    ProgressBar pbar;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upvote_poll);

        // Slide menu -------------------
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //-----------------------

        pbar = (ProgressBar)findViewById(R.id.progressBar1);
        boolean net = isNetworkAvailable();
        if(!net)
        {
            Toast.makeText(this, "Network not available!", Toast.LENGTH_SHORT).show();

        }

        sharedpreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        username = sharedpreferences.getString("username", "");

        if(username.equals(""))
        {
            Intent goStartIntent = new Intent(this, StartActivity.class);
            finish();
            startActivity(goStartIntent);
        }
        TextView usernamet = (TextView) findViewById(R.id.username);
        usernamet.setText(username);

        Bundle basket = getIntent().getExtras();

        if(basket != null) {
            topic = basket.getString("topic");
            question = basket.getString("question");
            System.out.println("topic:"+topic + " question: " + question);
        }
        TextView topicname = (TextView) findViewById(R.id.topic);
        topicname.setText(topic);

        TextView quesname = (TextView) findViewById(R.id.question);
        quesname.setText(question);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.server_client_id))
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }catch(Exception e)
        {

        }


    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_upvote_poll, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public void upvote(View v)
    {
        new UpdatePollTask().execute(question,username);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class UpdatePollTask extends AsyncTask<String, Void, Document> {


        @Override
        protected Document doInBackground(String... params)

        {
            Document doc=null;
            try {

                System.out.println("questionparams[0]"+params[0]);
                String url = "http://webm.insta-quiz.appspot.com/upvotepoll?question="+params[0]+"&upvoter="+params[1];
                doc = Jsoup.connect(url).get();
                //System.out.println(doc);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document document) {
            //if you had a ui element, you could display the title
            //((TextView)findViewById (R.id.myTextView)).setText (result);
            System.out.println("onpost");
//            String buttonsContent = "";

            Elements links = document.select("p");

//            ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar2);
//            pb.setVisibility(View.GONE);

            for(Element ele:links)
            {
                System.out.println(ele.text());
                if(ele.text().equals("Poll Upvoted"))
                {
                    Toast.makeText(getApplicationContext(),"Poll upvoted !!",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Poll already upvoted !!",Toast.LENGTH_SHORT).show();
                }
                Intent pollsintent = new Intent(getApplicationContext(), PollsActivity.class);
                finish();
                pollsintent.putExtra("topic",topic);
                System.out.println("in upvote:" + topic);
                startActivity(pollsintent);
                //buttonsContent = buttonsContent + ele.text() + "; " ;
                //System.out.println(buttonsContent);
                //topics.add(ele.text());
            }
            //buttonsContent = buttonsContent.substring(0,buttonsContent.length()-2);


        }

    }

    // ---------  Slide menu overriden methods code ----------------------
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {

            Intent startIntent = new Intent(this, StartActivity.class);
            finish();

            startActivity(startIntent);

        } else if (id == R.id.publish_quiz) {
            Intent publishIntent = new Intent(this, PublishActivity.class);
            finish();
            //publishIntent.putExtra("buttons", buttonsContent);
            startActivity(publishIntent);

        } else if (id == R.id.answer_quiz) {

            Intent answerIntent = new Intent(this, GetQuizActivity.class);
            finish();
            //publishIntent.putExtra("buttons", buttonsContent);
            startActivity(answerIntent);

        } else if (id == R.id.polls) {

            Intent answerIntent = new Intent(this, TopicsActivity.class);
            finish();
            startActivity(answerIntent);
            //Intent intent = new Intent(this, StatisticsActivity.class);
            //startActivity(intent);

        } else if (id == R.id.logout) {

            logout();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    // --------------------------------------------------------------------------------

    public void logout() {
        // sharedpreferences = getSharedPreferences("MyPref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("username", "");
        editor.commit();

        try {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            // [START_EXCLUDE]
                            // updateUI(false);
                            // [END_EXCLUDE]
                            Intent logoutIntent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(logoutIntent);
                            finish();
                        }
                    });
        }catch(Exception e)
        {

        }
        Intent logoutIntent = new Intent(UpvotePollActivity.this, HomeActivity.class);
        startActivity(logoutIntent);

    }

}



