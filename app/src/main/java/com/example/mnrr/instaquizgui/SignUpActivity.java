package com.example.mnrr.instaquizgui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class SignUpActivity extends ActionBarActivity {

    EditText usernametext = null;
    EditText passwordText = null;
    EditText emailText = null;
    EditText nameText = null;


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        boolean net = isNetworkAvailable();
        if (!net) {
            Toast.makeText(this, "Network not available!", Toast.LENGTH_SHORT).show();
            //Intent goHomeIntent = new Intent(this, StartActivity.class);
            //startActivity(goHomeIntent);
        }
        usernametext = (EditText) findViewById(R.id.username);
        passwordText = (EditText) findViewById(R.id.password);
        emailText = (EditText) findViewById(R.id.email);
        nameText = (EditText) findViewById(R.id.name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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


    public void addUser(View v) {
        String username = usernametext.getText().toString();
        String password = passwordText.getText().toString();
        String email = emailText.getText().toString();
        String name = nameText.getText().toString();

        if (username.equals("") || password.equals("") || email.equals("") || name.equals("")) {
            Toast.makeText(this, "Fill all fields!", Toast.LENGTH_SHORT).show();
        } else {
            new RegisterUserTask().execute();
           // Intent createQuizIntent = new Intent(SignUpActivity.this, LoginActivity.class);
            //EditText quiztitletext = (EditText) findViewById(R.id.quiztitle);
            //createQuizIntent.putExtra("quiztitle", quiztitletext.getText().toString());
        }
    }

    public void goToHome(View v) {
        Intent goHomeIntent = new Intent(SignUpActivity.this, StartActivity.class);
        startActivity(goHomeIntent);
    }

    private class RegisterUserTask extends AsyncTask<Void, Void, Document> {


        @Override
        protected Document doInBackground(Void... params)

        {
            Document doc = null;
            try {

                String username = usernametext.getText().toString();
                String password = passwordText.getText().toString();
                String email = emailText.getText().toString();
                String name = nameText.getText().toString();

                String url = "http://webm.insta-quiz.appspot.com/register?username="+username+"&password="+password+"&email="+email+"&name="+name;

                doc = Jsoup.connect(url).get();


                //System.out.println(doc);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document document) {
            //if you had a ui element, you could display the title
            //((TextView)findViewById (R.id.myTeoaxtView)).setText (result);
            if (document == null) {
                Toast.makeText(getApplicationContext(), "Cannot access internet !", Toast.LENGTH_SHORT).show();
            } else {
                System.out.println("onpost");
                Elements els  = document.select("p");
                for(Element ele:els)
                {
                    String msg = ele.text().toString();
                    if(msg.equalsIgnoreCase("exists"))
                    {
                        Toast.makeText(getApplicationContext(), "Username already exists!!!!", Toast.LENGTH_SHORT).show();
                    }
                    else if(msg.equalsIgnoreCase("registered"))
                    {
                        Toast.makeText(getApplicationContext(), "You are successfully registered", Toast.LENGTH_SHORT).show();
                        Intent createQuizIntent = new Intent(SignUpActivity.this, HomeActivity.class);
                        startActivity(createQuizIntent);
                    }
                }

            }
        }

    }

}
