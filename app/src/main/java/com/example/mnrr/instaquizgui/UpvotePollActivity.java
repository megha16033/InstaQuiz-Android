package com.example.mnrr.instaquizgui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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


public class UpvotePollActivity extends ActionBarActivity {

    String topic="";
    String username="";
    String question="";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upvote_poll);

        sharedpreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        username = sharedpreferences.getString("username", "");

        if(username.equals(""))
        {
            Intent goStartIntent = new Intent(this, StartActivity.class);
            startActivity(goStartIntent);
        }

        Bundle basket = getIntent().getExtras();

        if(basket != null) {
            topic = basket.getString("topic");
            question = basket.getString("question");
            System.out.println("topic:"+topic);
        }
        TextView topicname = (TextView) findViewById(R.id.topic);
        topicname.setText(topic);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upvote_poll, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void upvote(View v)
    {
        new UpdatePollTask().execute(question,username);
    }

    private class UpdatePollTask extends AsyncTask<String, Void, Document> {


        @Override
        protected Document doInBackground(String... params)

        {
            Document doc=null;
            try {


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

            ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar2);
            pb.setVisibility(View.GONE);

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
                //buttonsContent = buttonsContent + ele.text() + "; " ;
                //System.out.println(buttonsContent);
                //topics.add(ele.text());
            }
            //buttonsContent = buttonsContent.substring(0,buttonsContent.length()-2);


        }

    }

}
