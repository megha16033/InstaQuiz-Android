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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;


public class GetQuizActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    SharedPreferences sharedpreferences;
    String username;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_quiz);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
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

        boolean net = isNetworkAvailable();
        if(!net)
        {
            Toast.makeText(this,"Network not available!",Toast.LENGTH_SHORT).show();
            //Intent goHomeIntent = new Intent(this, StartActivity.class);
            //startActivity(goHomeIntent);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_get_quiz, menu);
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

    public void getQuiz(View v)
    {
        //new GetQuizTask().execute();
        Intent getQuizIntent = new Intent(GetQuizActivity.this, AnswerActivity.class);
        EditText codeText = (EditText) findViewById(R.id.quizcode);
        getQuizIntent.putExtra("code" ,codeText.getText().toString() );
        finish();
        startActivity(getQuizIntent);
    }

    public void goToHome(View v)
    {
        Intent goHomeIntent = new Intent(GetQuizActivity.this, StartActivity.class);
        finish();
        startActivity(goHomeIntent);
    }

//    private class GetQuizTask extends AsyncTask<Void, Void, Document> {
//
//
//        @Override
//        protected Document doInBackground(Void... params)
//
//        {
//            Document doc=null;
//            try {
//               //// String url = "http://web.insta-quiz.appspot.com/getQuiz?quizcode="+codeText.getText().toString();
//                //doc = Jsoup.connect(url).get();
//                //System.out.println(doc);
//            }
//            catch(IOException e)
//            {
//                e.printStackTrace();
//            }
//            return doc;
//        }
//
//        @Override
//        protected void onPostExecute(Document document) {
//            //if you had a ui element, you could display the title
//            //((TextView)findViewById (R.id.myTextView)).setText (result);
//            System.out.println("onpost");
//            if(document!=null) {
//                System.out.println(document.select("p").text());
//                String text[] = document.select("p").text().split(" ");
//                TextView livequiztitletext = (TextView)findViewById(R.id.livequiztitle);
//                livequiztitletext.setText(text[0]);
//                TextView livequizcodetext = (TextView)findViewById(R.id.livequizcode);
//                livequizcodetext.setText(text[2]);
//            }
//            else
//            {
//                Toast.makeText(getApplicationContext(), "Could not load document!", Toast.LENGTH_SHORT).show();
//            }
//            //EditText quiztitletext = (EditText)findViewById(R.id.quiztitle);
//            //quiztitletext.setText(prevquiztitle);
//
//        }
//
//    }

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
        Intent logoutIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(logoutIntent);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}

