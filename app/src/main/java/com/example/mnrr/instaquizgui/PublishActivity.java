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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class PublishActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.OnConnectionFailedListener{

    ProgressBar pbar;
    String buttonsContent = "";
    SharedPreferences sharedpreferences;
    String username;
    private GoogleApiClient mGoogleApiClient;
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {


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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        sharedpreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        username = sharedpreferences.getString("username", "");
        if(username.equals(""))
        {
            Intent goStartIntent = new Intent(this, StartActivity.class);
            finish();
            startActivity(goStartIntent);
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



        sharedpreferences = getSharedPreferences("MyPref",Context.MODE_PRIVATE);
        //this.sharedpreferences = getSharedPreferences("username", -1);
        username = sharedpreferences.getString("username","");
        TextView usernamet = (TextView) findViewById(R.id.username);
        usernamet.setText(username);


        pbar = (ProgressBar) findViewById(R.id.progressBar1);
        boolean net = isNetworkAvailable();
        System.out.println("Net:" + net);
        if (!net) {
            Toast.makeText(this, "Network not available!", Toast.LENGTH_SHORT).show();
            //Intent goHomeIntent = new Intent(this, StartActivity.class);
            //startActivity(goHomeIntent);
        }
        new CreateButtonsTask().execute();

    }


    public void goToCreateQuiz(View v) {
        Intent createQuizIntent = new Intent(this, CreateQuizActivity.class);
        finish();
        startActivity(createQuizIntent);
    }

    public void goToHome(View v) {
        Intent goHomeIntent = new Intent(PublishActivity.this, StartActivity.class);
        finish();
        startActivity(goHomeIntent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class CreateButtonsTask extends AsyncTask<Void, Void, Document> {


        @Override
        protected Document doInBackground(Void... params)

        {
            Document doc = null;
            try {
                System.out.println("user is:" + username);
                doc = Jsoup.connect("http://www.webm.insta-quiz.appspot.com/callPublish?username="+username).get();
                System.out.println(doc);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document document) {
            //if you had a ui element, you could display the title
            //((TextView)findViewById (R.id.myTeoaxtView)).setText (result);
            if (document == null) {
                Toast.makeText(getApplicationContext(), "Cannot access internet !", Toast.LENGTH_SHORT).show();
            } else {
                System.out.println("onpost");
                buttonsContent = "";
                Elements links = document.select("div.form-group");
                if (links.size() == 0) {
                    TextView codeText = (TextView) findViewById(R.id.nonequiz);
                    codeText.setVisibility(View.VISIBLE);
                    pbar.setVisibility(View.INVISIBLE);
                } else {
                    for (Element ele : links) {
                        //System.out.println(ele.text());
                        buttonsContent = buttonsContent + ele.text() + "; ";
                        System.out.println(buttonsContent);
                    }
                    pbar.setVisibility(View.INVISIBLE);
                    buttonsContent = buttonsContent.substring(0, buttonsContent.length() - 2);
                    //Intent publishIntent = new Intent(StartActivity.this, PublishActivity.class);
                    ///publishIntent.putExtra("buttons", buttonsContent);
                    //startActivity(publishIntent);
                    System.out.println("buttonscontent:" + buttonsContent);
                    String buttons[] = buttonsContent.split("; ");
                    LinearLayout pbll = (LinearLayout) findViewById(R.id.publishButtons);
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    View.OnClickListener clicks = new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Button b = (Button) v;
                            String quiztitlefrombutton = b.getText().toString().substring(8);
                            Intent startIntent = new Intent(PublishActivity.this, StartActivity.class);
                            startIntent.putExtra("livequiztitle", quiztitlefrombutton);
                            finish();
                            startActivity(startIntent);
                        }
                    };


                    for (int i = 0; i < buttons.length; i++) {
                        Button button1 = new Button(getApplicationContext());
                        button1.setText(buttons[i]);
                        //button1.setBackgroundColor(getResources().getColor(R.color.pink));

                        button1.setId(i);
                        button1.setOnClickListener(clicks);
                        pbll.addView(button1);

                    }
                }
            }
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
        }
        catch(Exception e)
        {

        }
        Intent logoutIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(logoutIntent);

    }

}


