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


public class AddPollsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener{

    String mtopic="";
    String musername="";
    SharedPreferences sharedpreferences;
    ProgressBar mpbar;
    private GoogleApiClient mGoogleApiClient;

    //checking netwrok availability
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedpreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        musername = sharedpreferences.getString("username", "");
        if(musername.equals(""))
        {
            Intent goStartIntent = new Intent(this, StartActivity.class);
            finish();
            startActivity(goStartIntent);
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_polls);
        TextView usernamet = (TextView) findViewById(R.id.username);
        usernamet.setText(musername);

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

        mpbar = (ProgressBar)findViewById(R.id.progressBar1);
        boolean net = isNetworkAvailable();
        if(!net)
        {
            Toast.makeText(this, "Network not available!", Toast.LENGTH_SHORT).show();

        }

        Bundle basket = getIntent().getExtras();

        if(basket != null) {
            mtopic = basket.getString("topic");
            System.out.println("topic:"+mtopic);
        }
        TextView topicname = (TextView) findViewById(R.id.topictitle);
        topicname.setText(mtopic);
    }



    public void onSubmit(View view)
    {
        TextView questxt = (TextView)findViewById(R.id.question);
        String question = questxt.getText().toString();
        if(question.equals(""))
        {
            Toast.makeText(this,"Field cannot be empty !",Toast.LENGTH_SHORT).show();
        }
        else
        {
            new AddPollsTask().execute(mtopic,question,musername);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //Adding the poll on a topic
    private class AddPollsTask extends AsyncTask<String, Void, Document> {


        @Override
        protected Document doInBackground(String... params)

        {
            Document doc=null;
            try {


                String url = "http://webm.insta-quiz.appspot.com/addpoll?topic="+params[0]+"&question="+params[1]+"&starter="+params[2];
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


            for(Element ele:links)
            {
                System.out.println(ele.text());
                if(ele.text().equals("Poll added"))
                {
                    Toast.makeText(getApplicationContext(),"Poll Added !!",Toast.LENGTH_SHORT).show();
                    Intent addintent = new Intent(getApplicationContext(), PollsActivity.class);
                    addintent.putExtra("topic",mtopic);
                    finish();
                    startActivity(addintent);

                }
                //buttonsContent = buttonsContent + ele.text() + "; " ;
                //System.out.println(buttonsContent);
                //polls.add(ele.text());
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
            //publishIntent.putExtra("buttons", buttonsContent);
            finish();
            startActivity(publishIntent);

        } else if (id == R.id.answer_quiz) {

            Intent answerIntent = new Intent(this, GetQuizActivity.class);
            //publishIntent.putExtra("buttons", buttonsContent);
            finish();
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

        try{Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
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
        Intent logoutIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(logoutIntent);


    }

}


