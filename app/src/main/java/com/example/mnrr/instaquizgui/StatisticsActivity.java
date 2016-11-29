package com.example.mnrr.instaquizgui;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//import com.itextpdf.text.Document;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.Font;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.pdf.PdfWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class StatisticsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.OnConnectionFailedListener {

    ProgressBar pbar;
    String livequiztitle="";
    SharedPreferences sharedpreferences;
    String username;


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static final String TAG = "Statistics Activity";
    private int[] mMarks;
    private int[] mQuestions ={ 3,5,1,0,0};
    private int[] mIndex = new int[mQuestions.length];
    private float mAverage = 0.0f ;
    private int mMedian = 0 ;
    private int mMax = 0 ;
    private int mAboveAverage = 0;
    private int mBelowAverage = 0;
    private String mWrongAnswered = "";
    private int mSum = 0 ;
    private int mMinimum = 0;
    private int mNumberOfStudents ;
    private String text="" ;

    private TextView mStudentsTextview ;
    private TextView mMaximumTextview;
    private TextView mMinimumTextview ;
    private TextView mAverageTextView ;
    private TextView mMedianTextview ;
    private TextView mAboveAverageTextView ;
    private TextView mBelowAverageTextView ;
    private TextView mMaximumWrongTextView ;
    private GoogleApiClient mGoogleApiClient;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        sharedpreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        username = sharedpreferences.getString("username", "");
        if(username.equals(""))
        {
            Intent goStartIntent = new Intent(this, StartActivity.class);
            finish();
            startActivity(goStartIntent);
        }
        TextView usernamet = (TextView) findViewById(R.id.username);
        usernamet.setText(username);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.server_client_id))
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }catch(Exception e)
        {

        }


        // Slide menu -------------------
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //-----------------------


        boolean net = isNetworkAvailable();
        if(!net)
        {
            Toast.makeText(this,"Network not available!",Toast.LENGTH_SHORT).show();
            //Intent goHomeIntent = new Intent(this, StartActivity.class);
            //startActivity(goHomeIntent);
        }

        Bundle basket = getIntent().getExtras();
        pbar = (ProgressBar)findViewById(R.id.progressBar2);

        if(basket != null) {
            livequiztitle = basket.getString("title");
        }
        if(livequiztitle.equals("")||livequiztitle==null)
        {
            //return null;
        }

        new GetMarksTask().execute();




    }

    private int findMin ( ) {
        Arrays.sort ( mMarks) ;
        return mMarks[0] ;
    }

    private int findMax () {
       // Log.d(TAG , new Integer( mMarks[mNumberOfStudents -1 ] ).toString()) ;
        return mMarks[mNumberOfStudents -1  ];

    }

    private int getSum (){
        int sum = 0;
        for ( int i = 0 ; i < mMarks.length ; i++)
            sum += mMarks[i] ;
        return sum;
    }

    private int findMedian (){
        if ( mNumberOfStudents %2 == 0)
            return mMarks[mNumberOfStudents/2] ;
        else
            return mMarks[mNumberOfStudents/2 ] ;
    }

    private void findAboveBelowAverage() {
        int i = mNumberOfStudents - 1 ;

        while ( mMarks [ i ] > mAverage){
            i = i/2;
        }
        mBelowAverage = i +1 ;
        mAboveAverage = mNumberOfStudents - i -1;
    }

    public String findWrongAnswered(){

        int c ,d, swap;
        int n = mQuestions.length ;
        for (c = 0; c < ( n - 1 ); c++) {
            for (d = 0; d < n - c - 1; d++) {
                if (mQuestions[d] > mQuestions[d+1]) /* For descending order use < */
                {
                    swap       = mQuestions[d];
                    mQuestions[d]   = mQuestions[d+1];
                    mQuestions[d+1] = swap;
                    swap = mIndex[d];
                    mIndex[d] = mIndex[d+1] ;
                    mIndex[d+1] = swap ;
                }
            }
        }
        int threshold =(int) (0.3*mQuestions.length ) ;
        String wrong ="";
        for ( int i=0 ; i < threshold ; i++ )
            wrong+= mIndex[mQuestions.length-1-i]+"," ; // returns the index of question in the array
        return wrong ;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


//    public void createandDisplayPdf(String text) {
//
//        Document doc = new Document();
//
//        try {
//            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                    + File.separator + "results";
//            Log.d(TAG , Environment.getExternalStorageDirectory().getAbsolutePath()) ;
//            File dir = new File(path);
//            //if(!dir.exists())
//            dir.mkdirs();
//
//            File file = new File(dir, "newFile.pdf");
//            FileOutputStream fOut = new FileOutputStream(file);
//
//            PdfWriter.getInstance(doc, fOut);
//
//            //open the document
//            doc.open();
//
//            Paragraph p1 = new Paragraph(text);
//            // Font paraFont= new Font(Font.COURIER);
//            p1.setAlignment(Paragraph.ALIGN_CENTER);
//            //  p1.setFont(paraFont);
//
//            //add paragraph to document
//            doc.add(p1);
//
//        } catch (DocumentException de) {
//            Log.e("PDFCreator", "DocumentException:" + de);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }finally {
//            doc.close();
//        }
//
//        viewPdf("newFile.pdf", "Dir");
//    }
//
//    // Method for opening a pdf file
//    private void viewPdf(String file, String directory) {
//
//        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
//        Uri path = Uri.fromFile(pdfFile);
//
//        // Setting the intent for pdf reader
//        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
//        pdfIntent.setDataAndType(path, "application/pdf");
//        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        try {
//            startActivity(pdfIntent);
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText( this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    public void saveAsPdf(View view) {
//        createandDisplayPdf(text);
//    }

    private class GetMarksTask extends AsyncTask<Void, Void, Document> {


        @Override
        protected Document doInBackground(Void... params)

        {
            Document doc=null;
            try {


                String url = "http://webm.insta-quiz.appspot.com/getStats?quiztitle="+livequiztitle;
                doc = Jsoup.connect(url).get();
                System.out.println("Doc:"+doc);
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
            List<Integer> marks = new ArrayList<Integer>();
            for(Element ele:links)
            {
                System.out.println(ele.text());
                String marksarr = ele.text();
                String markssplit[] = marksarr.split(",");
                System.out.println("markssplit:" + markssplit.length);
                for(int i=1;i<markssplit.length;i++)
                {
                    if(!markssplit[i].equals("")) {
                        int m = Integer.parseInt(markssplit[i]);
                        marks.add(m);
                    }
                    // create marks array
                }
            }

            if(marks.size()==0)
            {
                Toast.makeText(getApplicationContext(),"No Student has submitted!",Toast.LENGTH_SHORT).show();
            }
            else {
                mMarks = convertIntegers(marks);
                mNumberOfStudents = marks.size();
                System.out.println("no of stud = "+mNumberOfStudents);
                mSum = getSum();
                mMax = findMax();
                mMinimum = findMin();
                mAverage = mSum / mNumberOfStudents;
                findAboveBelowAverage();
                mMedian = findMedian();
                for (int i = 0; i < mQuestions.length; i++) {
                    mIndex[i] = i;
                }

                //mWrongAnswered = findWrongAnswered();

                mStudentsTextview = (TextView) findViewById(R.id.numberOfStudentsTextview);
                mStudentsTextview.setText("Number of Students = " + mNumberOfStudents);
                text += "\nNumber of Students = " + mNumberOfStudents;

                mMinimumTextview = (TextView) findViewById(R.id.minimumTextview);
                mMinimumTextview.setText("Minimum marks = " + mMinimum);
                text += "\nMinimum marks = " + mMinimum;

                mMaximumTextview = (TextView) findViewById(R.id.maximumTextview);
                mMaximumTextview.setText("Maximum marks = " + mMax);
                text += "\nMaximum marks = " + mMax;

                mMedianTextview = (TextView) findViewById(R.id.medianTextview);
                mMedianTextview.setText("Median = " + mMedian);
                text += "Median = " + mMedian;

                mAverageTextView = (TextView) findViewById(R.id.averageTextview);
                mAverageTextView.setText("Average Marks = " + mAverage);
                text += "Average Marks = " + mAverage;

                mAboveAverageTextView = (TextView) findViewById(R.id.aboveAverageTextview);
                mAboveAverageTextView.setText("Above Average Marks = " + mAboveAverage);
                text += "Above Average Marks = " + mAboveAverage;

                mBelowAverageTextView = (TextView) findViewById(R.id.belowAverageTextview);
                mBelowAverageTextView.setText("Below Average Students = " + mBelowAverage);
                text += "Below Average Students = " + mBelowAverage;

//        mMaximumWrongTextView = (TextView) findViewById(R.id.maximumWrongTextview ) ;
//        mMaximumWrongTextView.setText("Maximum wrong answered questons = " + mWrongAnswered);
//        text+= "Maximum Wrong answered questions = " + mWrongAnswered ;



                System.out.println("mmarks:" + mMarks[0]);
                System.out.println("mmarks:" + mMarks.length);
                //buttonsContent = buttonsContent.substring(0,buttonsContent.length()-2);

            }
        }

    }
    // ---------  Slide menu overriden methods code ----------------------


    public void goToHome(View v)
    {
        Intent goHomeIntent = new Intent(StatisticsActivity.this, StartActivity.class);
        finish();
        startActivity(goHomeIntent);
    }

    public void refresh(View v)
    {
        Intent goRefreshIntent = new Intent(StatisticsActivity.this, StatisticsActivity.class);
        finish();
        goRefreshIntent.putExtra("title" , livequiztitle);
        startActivity(goRefreshIntent);

    }

    public static int[] convertIntegers(List<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {

            Intent startIntent = new Intent(this, StartActivity.class);
            finish();
            startActivity(startIntent);

        } else if (id == R.id.publish_quiz) {
            Intent publishIntent = new Intent(this, PublishActivity.class);
            finish();
            //publishIntent.putExtra("buttons", buttonsContent);
            startActivity(publishIntent);

        } else if (id == R.id.answer_quiz) {

            Intent answerIntent = new Intent(this, GetQuizActivity.class);
            finish();
            //publishIntent.putExtra("buttons", buttonsContent);
            startActivity(answerIntent);

        } else if (id == R.id.polls) {

            Intent answerIntent = new Intent(this, TopicsActivity.class);
            finish();
            startActivity(answerIntent);
            //Intent intent = new Intent(this, StatisticsActivity.class);
            //startActivity(intent);

        } else if (id == R.id.logout) {

            logout();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    // --------------------------------------------------------------------------------

    public void logout() {
        // sharedpreferences = getSharedPreferences("MyPref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("username", "");
        editor.commit();

        try {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            // [START_EXCLUDE]
                            // updateUI(false);
                            // [END_EXCLUDE]
                            Intent logoutIntent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(logoutIntent);
                            finish();
                        }
                    });
        }catch(Exception e)
        {

        }
        Intent logoutIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(logoutIntent);

    }

}




