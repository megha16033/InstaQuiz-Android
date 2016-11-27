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
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class AddPollsActivity extends ActionBarActivity {

    String topic="";
    String username="";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedpreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        username = sharedpreferences.getString("username", "");
        if(username.equals(""))
        {
            Intent goStartIntent = new Intent(this, StartActivity.class);
            startActivity(goStartIntent);
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_polls);
        Bundle basket = getIntent().getExtras();

        if(basket != null) {
            topic = basket.getString("topic");
            System.out.println("topic:"+topic);
        }
        TextView topicname = (TextView) findViewById(R.id.topictitle);
        topicname.setText(topic);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_polls, menu);
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
            new AddPollsTask().execute(topic,question,username);
        }

    }

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
                    addintent.putExtra("topic",topic);
                    startActivity(addintent);

                }
                //buttonsContent = buttonsContent + ele.text() + "; " ;
                //System.out.println(buttonsContent);
                //polls.add(ele.text());
            }
            //buttonsContent = buttonsContent.substring(0,buttonsContent.length()-2);


        }

    }
}
