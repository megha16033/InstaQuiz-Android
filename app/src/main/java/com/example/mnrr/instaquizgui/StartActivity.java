package com.example.mnrr.instaquizgui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);



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

            }
            else
            {
                Toast.makeText(getApplicationContext(),"Could not load document!",Toast.LENGTH_SHORT).show();
            }
            //EditText quiztitletext = (EditText)findViewById(R.id.quiztitle);
            //quiztitletext.setText(prevquiztitle);

        }

    }
}
