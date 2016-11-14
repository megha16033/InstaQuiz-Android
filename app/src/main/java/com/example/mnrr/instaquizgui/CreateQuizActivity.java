package com.example.mnrr.instaquizgui;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class CreateQuizActivity extends ActionBarActivity {

    EditText quiztitletext =null;
    EditText questionText = null;
    EditText option1Text = null;
    EditText option2Text = null;
    EditText option3Text = null;
    EditText option4Text = null;
    EditText answerText = null;
    SharedPreferences sharedpreferences;
    String username;

    String quiztitle="";

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

        sharedpreferences = getSharedPreferences("MyPref",Context.MODE_PRIVATE);
        //this.sharedpreferences = getSharedPreferences("username", -1);
        username = sharedpreferences.getString("username","");

        boolean net = isNetworkAvailable();
        if(!net)
        {
            Toast.makeText(this, "Network not available!", Toast.LENGTH_SHORT).show();
            //Intent goHomeIntent = new Intent(this, StartActivity.class);
            //startActivity(goHomeIntent);
        }

        quiztitletext = (EditText)findViewById(R.id.quiztitle);
        quiztitle=quiztitletext.getText().toString();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_quiz, menu);
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

    public void addQuestion(View v)
    {
        new MyTask().execute();
        Intent createQuizIntent = new Intent(CreateQuizActivity.this, CreateQuizActivity.class);
        EditText quiztitletext = (EditText)findViewById(R.id.quiztitle);
        createQuizIntent.putExtra("quiztitle", quiztitletext.getText().toString());
        startActivity(createQuizIntent);
    }

    public void goToHome(View v)
    {
        Intent goHomeIntent = new Intent(CreateQuizActivity.this, StartActivity.class);
        startActivity(goHomeIntent);
    }

    public void done(View v)
    {
        new MyTask().execute();
        Intent createQuizIntent = new Intent(CreateQuizActivity.this, PublishActivity.class);
        //createQuizIntent.putExtra("quiztitle", quiztitle);
        startActivity(createQuizIntent);
    }



    private class MyTask extends AsyncTask<Void, Void, Document> {


        @Override
        protected Document doInBackground(Void... params)

        {
            Document doc=null;
            try {
                quiztitle = quiztitletext.getText().toString();
                String question = questionText.getText().toString();
                String op1 = option1Text.getText().toString();
                String op2 = option2Text.getText().toString();
                String op3 = option3Text.getText().toString();
                String op4 = option4Text.getText().toString();
                String answer = answerText.getText().toString();

                String url = "http://webm.insta-quiz.appspot.com/saveQuestion?quiztitle="+quiztitle+"&question="+question+"&op1="+op1+"&op2="+op2+"&op3="+op3+"&op4="+op4+"&answer="+answer+"&username="+username;
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
}
