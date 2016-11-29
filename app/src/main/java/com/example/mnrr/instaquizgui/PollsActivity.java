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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


public class PollsActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    ArrayList<String> polls = new ArrayList<String>();
    ArrayList<Integer> upvotes = new ArrayList<Integer>();
    SharedPreferences sharedpreferences;
    String polltitle="";
    String title="";
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
        setContentView(R.layout.activity_polls);

        pbar = (ProgressBar)findViewById(R.id.progressBar1);
        boolean net = isNetworkAvailable();
        if(!net)
        {
            Toast.makeText(this, "Network not available!", Toast.LENGTH_SHORT).show();

        }

        sharedpreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String username = sharedpreferences.getString("username", "");
//        TextView usernamet = (TextView) findViewById(R.id.username);
//        usernamet.setText(username);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("");

        Bundle basket = getIntent().getExtras();

        if(basket != null) {
            polltitle = basket.getString("topic");
            System.out.println("topic:"+polltitle);
        }

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


        new GetPollsTask().execute(polltitle);
        System.out.println("Polls Size : "+polls.size());

//        ListView listView1 = (ListView) findViewById(R.id.polls_list);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, polls);
//
//        listView1.setAdapter(adapter);
//
//        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position,
//                                    long id) {
//
//                String item = ((TextView) view).getText().toString();
//                int lastindexdash = item.lastIndexOf("-");
//                String question = item.substring(0,lastindexdash-1);
//
//                Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
//                Intent pollsintent = new Intent(getApplicationContext(), UpvotePollActivity.class);
//                pollsintent.putExtra("topic", polltitle);
//
//                pollsintent.putExtra("question",question);
//                startActivity(pollsintent);
//            }
//        });

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_polls, menu);
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class GetPollsTask extends AsyncTask<String, Void, Document> {


        @Override
        protected Document doInBackground(String... params)

        {
            Document doc=null;
            try {


                String url = "http://webm.insta-quiz.appspot.com/getpolls?topic="+params[0];
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
            for(int i=0;i<links.size();i++)
            {
                Element ele1 = links.get(i);
                Element ele2 = links.get(++i);
                title = ele1.text();
                String  poll = ele1.text() + " - "+ele2.text() + " upvotes";
                Log.d("PollsActivity","poll:"+poll);
                polls.add(poll);
            }

            ListView listView1 = (ListView) findViewById(R.id.polls_list);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                    R.layout.simple_list_item_1, polls);

            listView1.setAdapter(adapter);

            listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {

                    String item = ((TextView) view).getText().toString();
                    int lastindexdash = item.lastIndexOf("-");
                    String question = item.substring(0,lastindexdash-1);

                    Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
                    Intent pollsintent = new Intent(getApplicationContext(), UpvotePollActivity.class);
                    pollsintent.putExtra("topic", polltitle);

                    pollsintent.putExtra("question",question);
                    finish();
                    startActivity(pollsintent);
                }
            });

//            for(Element ele:links)
//            {
//                //System.out.println(ele.text());
//                //buttonsContent = buttonsContent + ele.text() + "; " ;
//                //System.out.println(buttonsContent);
//                polls.add(ele.text());
//            }
            //buttonsContent = buttonsContent.substring(0,buttonsContent.length()-2);


        }

    }

    public void onAdd(View view)
    {
        Intent addintent = new Intent(getApplicationContext(), AddPollsActivity.class);
        System.out.println("value of topic:" + polltitle);
        addintent.putExtra("topic", polltitle);
        finish();
        startActivity(addintent);
    }

    // ---------  Slide menu overriden methods code ----------------------
//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.home) {
//
//            Intent startIntent = new Intent(this, StartActivity.class);
//            finish();
//            startActivity(startIntent);
//
//        } else if (id == R.id.publish_quiz) {
//            Intent publishIntent = new Intent(this, PublishActivity.class);
//            //publishIntent.putExtra("buttons", buttonsContent);
//            finish();
//            startActivity(publishIntent);
//
//        } else if (id == R.id.answer_quiz) {
//
//            Intent answerIntent = new Intent(this, GetQuizActivity.class);
//            //publishIntent.putExtra("buttons", buttonsContent);
//            finish();
//            startActivity(answerIntent);
//
//        } else if (id == R.id.polls) {
//
//            Intent answerIntent = new Intent(this, TopicsActivity.class);
//            finish();
//            startActivity(answerIntent);
//            //Intent intent = new Intent(this, StatisticsActivity.class);
//            //startActivity(intent);
//
//        } else if (id == R.id.logout) {
//
//            logout();
//
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
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



