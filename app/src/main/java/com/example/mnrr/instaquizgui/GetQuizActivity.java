package com.example.mnrr.instaquizgui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;


public class GetQuizActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_quiz);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_get_quiz, menu);
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

    public void getQuiz(View v)
    {
        //new GetQuizTask().execute();
        Intent getQuizIntent = new Intent(GetQuizActivity.this, AnswerActivity.class);
        EditText codeText = (EditText) findViewById(R.id.quizcode);
        getQuizIntent.putExtra("code" ,codeText.getText().toString() );
        startActivity(getQuizIntent);
    }

    public void goToHome(View v)
    {
        Intent goHomeIntent = new Intent(GetQuizActivity.this, StartActivity.class);
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
}
