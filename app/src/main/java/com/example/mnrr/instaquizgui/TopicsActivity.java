package com.example.mnrr.instaquizgui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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


public class TopicsActivity extends ActionBarActivity {


    ArrayList<String> topics = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("");


        new GetTotalTopicsTask().execute();

        ListView listView1 = (ListView) findViewById(R.id.topics_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, topics);

        listView1.setAdapter(adapter);

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                String item = ((TextView) view).getText().toString();

                Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
                Intent pollsintent = new Intent(getApplicationContext(), PollsActivity.class);
                pollsintent.putExtra("topic",item);
                startActivity(pollsintent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_polls, menu);
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

    private class GetTotalTopicsTask extends AsyncTask<Void, Void, Document> {


        @Override
        protected Document doInBackground(Void... params)

        {
            Document doc=null;
            try {


                String url = "http://webm.insta-quiz.appspot.com/gettotaltopics";
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
                //System.out.println(ele.text());
                //buttonsContent = buttonsContent + ele.text() + "; " ;
                //System.out.println(buttonsContent);
                topics.add(ele.text());
            }
            //buttonsContent = buttonsContent.substring(0,buttonsContent.length()-2);


        }

    }
}
