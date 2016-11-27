package com.example.mnrr.instaquizgui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class StartActivity extends Activity {

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
        sharedpreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        username = sharedpreferences.getString("username","");
        if(username.equals(""))
        {
            Toast.makeText(this,"Please Login!",Toast.LENGTH_SHORT).show();
            Intent answerIntent = new Intent(StartActivity.this, LoginActivity.class);
            //publishIntent.putExtra("buttons", buttonsContent);
            startActivity(answerIntent);

        }
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
        }


        pbar = (ProgressBar)findViewById(R.id.progressBar2);
        boolean net = isNetworkAvailable();
        if(!net)
        {
            Toast.makeText(this,"Network not available!",Toast.LENGTH_SHORT).show();
            //Intent goHomeIntent = new Intent(this, StartActivity.class);
            //startActivity(goHomeIntent);
        }

        new GetLiveQuizTask().execute();
    }

    public void goToPublish(View v) throws IOException
    {
        //Document doc = Jsoup.connect("https://web.insta-quiz.appspot.com").get();
        //System.out.println(doc);
        Intent publishIntent = new Intent(StartActivity.this, PublishActivity.class);
        //publishIntent.putExtra("buttons", buttonsContent);
        startActivity(publishIntent);
    }

    public void goToAnswer(View v) throws IOException
    {
        //Document doc = Jsoup.connect("https://web.insta-quiz.appspot.com").get();
        //System.out.println(doc);
        Intent answerIntent = new Intent(StartActivity.this, GetQuizActivity.class);
        //publishIntent.putExtra("buttons", buttonsContent);
        startActivity(answerIntent);
    }

    public void goToPolls(View v) throws IOException
    {

        Intent answerIntent = new Intent(StartActivity.this, TopicsActivity.class);
        startActivity(answerIntent);
    }

    public void getStats(View v)
    {
        Intent statsIntent = new Intent(StartActivity.this, GetStatsActivity.class);
        TextView title = (TextView) findViewById(R.id.livequiztitle);
        statsIntent.putExtra("title" , title.getText().toString());
        startActivity(statsIntent);

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
            if(livequiztitle.equals("")||livequiztitle==null)
            {
                return null;
            }
            pbar.setVisibility(View.VISIBLE);
            Document doc=null;
            try {
                String url = "http://webm.insta-quiz.appspot.com/publishQuiz?quiztitle="+livequiztitle;
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
                        editor.putString("livequizcode","");
                        editor.commit();
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
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("username", "");
        editor.commit();
        Intent logoutIntent = new Intent(StartActivity.this, HomeActivity.class);
        startActivity(logoutIntent);

    }

}
