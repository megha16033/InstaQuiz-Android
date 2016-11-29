package com.example.mnrr.instaquizgui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.support.design.widget.NavigationView;

import java.io.IOException;

public class StartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    final int QUIZTIME = 1;

    ProgressBar pbar;
    SharedPreferences sharedpreferences;
    String username;
    String livequiztitle;
    String livequizcode;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

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

        sharedpreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        username = sharedpreferences.getString("username","");
        if(username.equals(""))
        {
            Toast.makeText(this,"Please Login!",Toast.LENGTH_SHORT).show();
            Intent answerIntent = new Intent(StartActivity.this, LoginActivity.class);
            //finish();
            //publishIntent.putExtra("buttons", buttonsContent);
            startActivity(answerIntent);

        }


        TextView usernamet = (TextView) findViewById(R.id.username);
        usernamet.setText(username);

        livequiztitle = sharedpreferences.getString("livequiztitle","");
        livequizcode = sharedpreferences.getString("livequizcode","");

        if(!livequiztitle.equals("")&&!livequizcode.equals(""))
        {
            TextView livequiztexttext = (TextView)findViewById(R.id.livequiztext);
            livequiztexttext.setVisibility(View.VISIBLE);

            TextView livequiztitletext = (TextView)findViewById(R.id.livequiztitle);
            livequiztitletext.setText(livequiztitle);
            livequiztitletext.setVisibility(View.VISIBLE);

            TextView livequizcodetext = (TextView)findViewById(R.id.livequizcode);
            livequizcodetext.setText(livequizcode);
            livequizcodetext.setVisibility(View.VISIBLE);

            Button getstatbtn = (Button)findViewById(R.id.getstatbtn);
            getstatbtn.setVisibility(View.VISIBLE);

            Button answerbtn = (Button)findViewById(R.id.answerbtn);
            answerbtn.setVisibility(View.GONE);
        }


        pbar = (ProgressBar)findViewById(R.id.progressBar2);
        boolean net = isNetworkAvailable();
        if(!net)
        {
            Toast.makeText(this,"Network not available!",Toast.LENGTH_SHORT).show();
            //Intent goHomeIntent = new Intent(this, StartActivity.class);
            //startActivity(goHomeIntent);
        }

        try{ GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
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


        new GetLiveQuizTask().execute();
    }

    public void goToPublish(View v) throws IOException
    {
        //Document doc = Jsoup.connect("https://web.insta-quiz.appspot.com").get();
        //System.out.println(doc);
        Intent publishIntent = new Intent(StartActivity.this, PublishActivity.class);
        //finish();
        //publishIntent.putExtra("buttons", buttonsContent);
        startActivity(publishIntent);
    }

    public void goToAnswer(View v) throws IOException
    {
        //Document doc = Jsoup.connect("https://web.insta-quiz.appspot.com").get();
        //System.out.println(doc);
        Intent answerIntent = new Intent(StartActivity.this, GetQuizActivity.class);
        //finish();
        //publishIntent.putExtra("buttons", buttonsContent);
        startActivity(answerIntent);
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

            Intent startIntent = new Intent(StartActivity.this, StartActivity.class);
            //finish();
            startActivity(startIntent);

        } else if (id == R.id.publish_quiz) {
            Intent publishIntent = new Intent(StartActivity.this, PublishActivity.class);
            //finish();
            //publishIntent.putExtra("buttons", buttonsContent);
            startActivity(publishIntent);

        } else if (id == R.id.answer_quiz) {

            Intent answerIntent = new Intent(StartActivity.this, GetQuizActivity.class);
            //finish();
            //publishIntent.putExtra("buttons", buttonsContent);
            startActivity(answerIntent);

        } else if (id == R.id.polls) {

            Intent answerIntent = new Intent(StartActivity.this, TopicsActivity.class);
            //finish();
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




    public void goToPolls(View v) throws IOException
    {

        Intent answerIntent = new Intent(StartActivity.this, TopicsActivity.class);
        startActivity(answerIntent);
    }

    public void getStats(View v)
    {
        Intent statsIntent = new Intent(StartActivity.this, StatisticsActivity.class);
        //finish();
        TextView title = (TextView) findViewById(R.id.livequiztitle);
        statsIntent.putExtra("title" , title.getText().toString());
        startActivity(statsIntent);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,"Network Error!!",Toast.LENGTH_SHORT);
    }

    private class GetLiveQuizTask extends AsyncTask<Void, Void, Document>{


        @Override
        protected Document doInBackground(Void... params)

        {

            Bundle basket = getIntent().getExtras();
            String livequiztitle="";
            if(basket != null) {

                livequiztitle = basket.getString("livequiztitle");
            }
            if(livequiztitle==null || livequiztitle.equals(""))
            {
                return null;
            }
            //pbar.setVisibility(View.VISIBLE);
            Document doc=null;
            try {
                String url = "http://webm.insta-quiz.appspot.com/publishQuiz?quiztitle="+livequiztitle+"&username="+username;
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
            if(document!=null) {
                System.out.println(document.select("p").text());
                String text[] = document.select("p").text().split("-");

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("livequiztitle",text[0]);
                editor.putString("livequizcode",text[1]);
                editor.commit();

                TextView livequiztexttext = (TextView)findViewById(R.id.livequiztext);
                livequiztexttext.setVisibility(View.VISIBLE);

                TextView livequiztitletext = (TextView)findViewById(R.id.livequiztitle);
                livequiztitletext.setText(text[0]);
                livequiztitletext.setVisibility(View.VISIBLE);

                TextView livequizcodetext = (TextView)findViewById(R.id.livequizcode);
                livequizcodetext.setText(text[1]);
                livequizcodetext.setVisibility(View.VISIBLE);

                Button getstatbtn = (Button)findViewById(R.id.getstatbtn);
                getstatbtn.setVisibility(View.VISIBLE);

                Button answerbtn = (Button)findViewById(R.id.answerbtn);
                answerbtn.setVisibility(View.GONE);

                pbar.setVisibility(View.GONE);

                //new EndQuizTask().execute();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new EndQuizTask().execute();
                        Toast.makeText(getApplicationContext(),"Quiz ended !",Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("livequiztitle","");
                        editor.putString("livequizcode", "");
                        editor.commit();
                        Intent start = new Intent(getApplicationContext(),StatisticsActivity.class);
                        TextView title = (TextView) findViewById(R.id.livequiztitle);
                        start.putExtra("title" , title.getText().toString());
                        startActivity(start);

                    }
                }, QUIZTIME*60*1000);
            }
            else
            {
                //Toast.makeText(getApplicationContext(),"Could not load document!",Toast.LENGTH_SHORT).show();
            }
            //EditText quiztitletext = (EditText)findViewById(R.id.quiztitle);
            //quiztitletext.setText(prevquiztitle);

        }

    }

    private class EndQuizTask extends AsyncTask<Void, Void, Document>{


        @Override
        protected Document doInBackground(Void... params)

        {

            Document doc=null;
            try {
                String url = "http://webm.insta-quiz.appspot.com/endQuiz?username="+username;
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
            System.out.println("onpost-end quiz task");
            if(document!=null) {
                //System.out.println(document.select("p").text());

            }
            else
            {
                //Toast.makeText(getApplicationContext(),"Could not load document!",Toast.LENGTH_SHORT).show();
            }
            //EditText quiztitletext = (EditText)findViewById(R.id.quiztitle);
            //quiztitletext.setText(prevquiztitle);

        }

    }

    public void logout(View view)
    {
       // sharedpreferences = getSharedPreferences("MyPref",Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedpreferences.edit();
//        editor.putString("username", "");
//        editor.commit();
//        Intent logoutIntent = new Intent(StartActivity.this, HomeActivity.class);
//        startActivity(logoutIntent);
        logout();

    }

    public void logout()
    {

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
            Intent logoutIntent = new Intent(StartActivity.this, HomeActivity.class);
            startActivity(logoutIntent);
    }

}
