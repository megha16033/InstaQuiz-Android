package com.example.mnrr.instaquizgui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


public class PollsActivity extends ActionBarActivity {

    ArrayList<String> polls = new ArrayList<String>();
    ArrayList<Integer> upvotes = new ArrayList<Integer>();
    String polltitle="";
    String title="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polls);

        Bundle basket = getIntent().getExtras();

        if(basket != null) {
            polltitle = basket.getString("topic");
            System.out.println("topic:"+polltitle);
        }

        new GetPollsTask().execute(polltitle);

        ListView listView1 = (ListView) findViewById(R.id.polls_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, polls);

        listView1.setAdapter(adapter);

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                String item = ((TextView) view).getText().toString();

                Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
                Intent pollsintent = new Intent(getApplicationContext(), UpvotePollActivity.class);
                pollsintent.putExtra("topic", polltitle);
                pollsintent.putExtra("question",title);
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
                polls.add(poll);
            }

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
        startActivity(addintent);
    }
}
