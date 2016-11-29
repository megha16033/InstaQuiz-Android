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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class CreateQuizActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.OnConnectionFailedListener {

    private static final CharSequence REQUIRED = "Required";
    private GoogleApiClient mGoogleApiClient;
    private static final java.lang.CharSequence DIGIT = "Only Digit";
    private EditText quiztitletext =null;
    private  EditText questionText = null;
    private EditText option1Text = null;
    private EditText option2Text = null;
    private EditText option3Text = null;
    private EditText option4Text = null;
    private EditText answerText = null;
    SharedPreferences sharedpreferences;
    String musername;

    String mquiztitle="";

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_create_quiz);
        // Slide menu -------------------
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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
        musername = sharedpreferences.getString("username","");
        TextView usernamet = (TextView) findViewById(R.id.username);
        usernamet.setText(musername);

        boolean net = isNetworkAvailable();
        if(!net)
        {
            Toast.makeText(this, "Network not available!", Toast.LENGTH_SHORT).show();
            //Intent goHomeIntent = new Intent(this, StartActivity.class);
            //startActivity(goHomeIntent);
        }

        quiztitletext = (EditText)findViewById(R.id.quiztitle);
        mquiztitle=quiztitletext.getText().toString();
         questionText = (EditText) findViewById(R.id.question);
         option1Text = (EditText) findViewById(R.id.option1);
         option2Text = (EditText) findViewById(R.id.option2);
         option3Text = (EditText) findViewById(R.id.option3);
         option4Text = (EditText) findViewById(R.id.option4);
         answerText = (EditText) findViewById(R.id.answer);

        Bundle basket = getIntent().getExtras();
        String prevquiztitle="";
        if(basket != null) {
            prevquiztitle = basket.getString("quiztitle");
        }
        EditText quiztitletext = (EditText)findViewById(R.id.quiztitle);
        quiztitletext.setText(prevquiztitle);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_create_quiz, menu);
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

    public void addQuestion(View v)
    {
        mquiztitle = quiztitletext.getText().toString();
        String question = questionText.getText().toString();
        String op1 = option1Text.getText().toString();
        String op2 = option2Text.getText().toString();
        String op3 = option3Text.getText().toString();
        String op4 = option4Text.getText().toString();
        String answer = answerText.getText().toString();

        // Title is required
        if (TextUtils.isEmpty(mquiztitle)) {
            quiztitletext.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(answer)) {
            answerText.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(question)) {
            questionText.setError(REQUIRED);
            return;
        }

        // Answer is required
        if (TextUtils.isEmpty(answer)) {
            answerText.setError(REQUIRED);
            return;
        }

        if (!TextUtils.isDigitsOnly(answer)) {
            answerText.setError(DIGIT);
            return;
        }

        // Options  are required
        if (TextUtils.isEmpty(op1)) {
            option1Text.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(op2)) {
            option2Text.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(op3)) {
            option3Text.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(op4)) {
            option4Text.setError(REQUIRED);
            return;
        }
        // validation
        if(mquiztitle.equals("")||question.equals("")||op1.equals("")||op2.equals("")||op3.equals("")||op4.equals("")||answer.equals(""))
        {

           Toast.makeText(getApplicationContext(),"Fields cannot be empty!",Toast.LENGTH_SHORT).show();
            Intent self = new Intent(getApplicationContext(),CreateQuizActivity.class);
            finish();
            startActivity(self);
        }
        else if(!(answer.equals("1")||answer.equals("2")||answer.equals("3")||answer.equals("4")))
        {
            answerText.setError("Answer can only be 1,2,3 or 4 !");
            return;

//            Toast.makeText(getApplicationContext(),"Answer can be 1,2,3 or 4 ! ",Toast.LENGTH_SHORT).show();
//            Intent self = new Intent(getApplicationContext(),CreateQuizActivity.class);
//            finish();
//            startActivity(self);
        }
        else {
            new MyTask().execute();
        }
        Intent createQuizIntent = new Intent(CreateQuizActivity.this, CreateQuizActivity.class);
        EditText quiztitletext = (EditText)findViewById(R.id.quiztitle);
        createQuizIntent.putExtra("quiztitle", quiztitletext.getText().toString());
        finish();
        startActivity(createQuizIntent);
    }

    public void goToHome(View v)
    {
        Intent goHomeIntent = new Intent(CreateQuizActivity.this, StartActivity.class);
        finish();
        startActivity(goHomeIntent);
    }

    public void done(View v)
    {
        mquiztitle = quiztitletext.getText().toString();
        String question = questionText.getText().toString();
        String op1 = option1Text.getText().toString();
        String op2 = option2Text.getText().toString();
        String op3 = option3Text.getText().toString();
        String op4 = option4Text.getText().toString();
        String answer = answerText.getText().toString();

        // Title is required
        if (TextUtils.isEmpty(mquiztitle)) {
            quiztitletext.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(answer)) {
            answerText.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(question)) {
            questionText.setError(REQUIRED);
            return;
        }

        // Answer is required
        if (TextUtils.isEmpty(answer)) {
            answerText.setError(REQUIRED);
            return;
        }

        if (!TextUtils.isDigitsOnly(answer)) {
            answerText.setError(DIGIT);
            return;
        }

        // Options  are required
        if (TextUtils.isEmpty(op1)) {
            option1Text.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(op2)) {
            option2Text.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(op3)) {
            option3Text.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(op4)) {
            option4Text.setError(REQUIRED);
            return;
        }
        // validation
        if( mquiztitle.equals("")||question.equals("")||op1.equals("")||op2.equals("")||op3.equals("")||op4.equals("")||answer.equals(""))
        {
            Toast.makeText(getApplicationContext(),"Fields cannot be empty!",Toast.LENGTH_SHORT).show();
            Intent self = new Intent(getApplicationContext(),CreateQuizActivity.class);
            ///finish();
            startActivity(self);
        }
        else if(!(answer.equals("1")||answer.equals("2")||answer.equals("3")||answer.equals("4")))
        {
            answerText.setError("Answer can only be 1,2,3 or 4 !");
            return;

          /*  Toast.makeText(getApplicationContext(),"Answer can be 1,2,3 or 4 ! ",Toast.LENGTH_SHORT).show();
            Intent self = new Intent(getApplicationContext(),CreateQuizActivity.class);
            //finish();
            startActivity(self);*/
        }
        else {
            new MyTask().execute();
            Intent createQuizIntent = new Intent(CreateQuizActivity.this, PublishActivity.class);
            //createQuizIntent.putExtra("quiztitle", quiztitle);
            finish();
            startActivity(createQuizIntent);
        }


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    private class MyTask extends AsyncTask<Void, Void, Document> {


        @Override
        protected Document doInBackground(Void... params)

        {
            Document doc=null;
            try {
                mquiztitle = quiztitletext.getText().toString();
                String question = questionText.getText().toString();
                String op1 = option1Text.getText().toString();
                String op2 = option2Text.getText().toString();
                String op3 = option3Text.getText().toString();
                String op4 = option4Text.getText().toString();
                String answer = answerText.getText().toString();

                //==================================

                String url = "http://webm.insta-quiz.appspot.com/saveQuestion?quiztitle="+mquiztitle+"&question="+question+"&op1="+op1+"&op2="+op2+"&op3="+op3+"&op4="+op4+"&answer="+answer+"&username="+musername;
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
            Toast.makeText(getApplicationContext(),"Question has been added!!",Toast.LENGTH_SHORT).show();
//            String buttonsContent = "";

//            Elements links = document.select("div.form-group");
//            for(Element ele:links)
//            {
//                //System.out.println(ele.text());
//                buttonsContent = buttonsContent + ele.text() + "; " ;
//                System.out.println(buttonsContent);
//            }
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

            startActivity(startIntent);

        } else if (id == R.id.publish_quiz) {
            Intent publishIntent = new Intent(this, PublishActivity.class);
            //publishIntent.putExtra("buttons", buttonsContent);
            startActivity(publishIntent);

        } else if (id == R.id.answer_quiz) {

            Intent answerIntent = new Intent(this, GetQuizActivity.class);
            //publishIntent.putExtra("buttons", buttonsContent);
            startActivity(answerIntent);

        } else if (id == R.id.polls) {

            Intent answerIntent = new Intent(this, TopicsActivity.class);
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
        Intent logoutIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(logoutIntent);

    }

}

