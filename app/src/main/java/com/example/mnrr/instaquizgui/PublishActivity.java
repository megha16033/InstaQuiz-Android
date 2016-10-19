package com.example.mnrr.instaquizgui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class PublishActivity extends ActionBarActivity {


    String buttonsContent="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        new CreateButtonsTask().execute();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_publish, menu);
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

    public void goToCreateQuiz(View v)
    {
        Intent createQuizIntent = new Intent(this, CreateQuizActivity.class);
        startActivity(createQuizIntent);
    }

    public void goToHome(View v)
    {
        Intent goHomeIntent = new Intent(PublishActivity.this, StartActivity.class);
        startActivity(goHomeIntent);
    }

    private class CreateButtonsTask extends AsyncTask<Void, Void, Document> {


        @Override
        protected Document doInBackground(Void... params)

        {
            Document doc=null;
            try {
                doc = Jsoup.connect("http://www.web.insta-quiz.appspot.com/callPublish").get();
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
            buttonsContent = "";

            Elements links = document.select("div.form-group");
            for(Element ele:links)
            {
                //System.out.println(ele.text());
                buttonsContent = buttonsContent + ele.text() + "; " ;
                System.out.println(buttonsContent);
            }
            buttonsContent = buttonsContent.substring(0,buttonsContent.length()-2);
            //Intent publishIntent = new Intent(StartActivity.this, PublishActivity.class);
            ///publishIntent.putExtra("buttons", buttonsContent);
            //startActivity(publishIntent);
            System.out.println("buttonscontent:" + buttonsContent);
            String buttons[] = buttonsContent.split("; ");
            LinearLayout pbll = (LinearLayout)findViewById(R.id.publishButtons);
            View.OnClickListener clicks=new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Button b = (Button)v;
                    String quiztitlefrombutton = b.getText().toString().substring(8);
                    Intent startIntent = new Intent(PublishActivity.this, StartActivity.class);
                    startIntent.putExtra("livequiztitle", quiztitlefrombutton);
                    startActivity(startIntent);


                }
            };
            for(int i=0;i<buttons.length;i++)
            {
                Button button1=new Button(getApplicationContext());
                button1.setText(buttons[i]);
                button1.setId(i);
                button1.setOnClickListener(clicks);
                pbll.addView(button1);

            }
        }

    }
}
