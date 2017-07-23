package com.tominc.mirror;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.tominc.mirror.adapters.ViewPagerAdapter;
import com.tominc.mirror.adapters.ZoomOutPageTransformer;
import com.tominc.mirror.models.IpLocation;
import com.tominc.mirror.models.News;
import com.tominc.mirror.models.VolleyCallback;
import com.tominc.mirror.models.Weather;
import com.tominc.mirror.utils.SpeechRecognizerManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.mateware.snacky.Snacky;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;
import it.macisamuele.calendarprovider.CalendarInfo;
import it.macisamuele.calendarprovider.EventInfo;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity implements RecognitionListener, TextToSpeech.OnInitListener {
    Utility utility;

    RelativeLayout mContentView;

    private static final String TAG = "MainActivity";

    private TextToSpeech tts;

    private static final int NUM_PAGES = 4;
    ViewPager viewPager;
    ViewPagerAdapter mPageAdapter;

    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH = "wakeup";
    private static final String MENU_SEARCH = "menu";

    private static final int REQUEST_AUDIO = 32;

    private static final String KEYPHRASE = Config.KEYPHRASE;

    private edu.cmu.pocketsphinx.SpeechRecognizer recognizer;

    private SpeechRecognizerManager mSpeechManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tts = new TextToSpeech(this, this);


        setContentView(R.layout.activity_main);

        mContentView = (RelativeLayout) findViewById(R.id.main_content);
        viewPager = (ViewPager) findViewById(R.id.view_pager);


        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }


        mPageAdapter = new ViewPagerAdapter(getSupportFragmentManager(), NUM_PAGES);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setAdapter(mPageAdapter);

        // Check if user has given permission to record audio
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO);
        } else{
            runRecognizerSetup();
        }
    }



    private void runRecognizerSetup() {
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(MainActivity.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    Snacky.builder()
                            .setActivty(MainActivity.this)
                            .setText("Failed to init recognizer" + result)
                            .warning().show();
                } else {
                    switchSearch(KWS_SEARCH);
                }
            }
        }.execute();
    }


    @Override
    protected void onPause() {
        if(mSpeechManager!=null){
            mSpeechManager.destroy();
            mSpeechManager=null;
        }
        super.onPause();
    }

    private void switchSearch(String searchName) {
        recognizer.stop();

        Log.d(TAG, "switchSearch: " + searchName);

        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(KWS_SEARCH))
            recognizer.startListening(searchName);
        else{
            recognizer.startListening(searchName, 10000);
            speakOut("Hello");
        }

        Snacky.builder()
                .setText(searchName)
                .setActivty(MainActivity.this)
                .success()
                .show();
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)

                .getRecognizer();
        recognizer.addListener(this);

        /** In your application you might not need to add all those searches.
         * They are added here for demonstration. You can leave just one.
         */

        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);

        // Create grammar-based search for selection between demos
        File menuGrammar = new File(assetsDir, "menu.gram");
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);
//
//        // Create grammar-based search for digit recognition
//        File digitsGrammar = new File(assetsDir, "digits.gram");
//        recognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);
//
//        // Create language model search
//        File languageModel = new File(assetsDir, "weather.dmp");
//        recognizer.addNgramSearch(FORECAST_SEARCH, languageModel);
//
//        // Phonetic search
//        File phoneticModel = new File(assetsDir, "en-phone.dmp");
//        recognizer.addAllphoneSearch(PHONE_SEARCH, phoneticModel);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                runRecognizerSetup();
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {

        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }

        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }

        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem()==0){
            super.onBackPressed();
        } else{
            viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
        }
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {
        if(!recognizer.getSearchName().equals(KWS_SEARCH)){
            switchSearch(KWS_SEARCH);
        }
    }

    private void listen(){
        if(mSpeechManager == null){
            SetSpeechListener();
        } else if(!mSpeechManager.ismIsListening()){
            mSpeechManager.destroy();
            SetSpeechListener();
        }
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if(hypothesis == null) return;


        String text = hypothesis.getHypstr();
        Log.d(TAG, "onPartialResult: " + text);
//        Snacky.builder().setActivty(MainActivity.this).setText(text).success().show();

        if(text.equals(KEYPHRASE)) listen();
        else if(text.contains(KEYPHRASE)) listen();
        else{
            Snacky.builder()
                    .setActivty(MainActivity.this)
                    .setText(text)
                    .success().show();
        }

//        if(text.equals(KEYPHRASE)) switchSearch(MENU_SEARCH);
//        else if(text.equals(DIGITS_SEARCH)) switchSearch(DIGITS_SEARCH);
//        else if(text.equals(PHONE_SEARCH)) switchSearch((PHONE_SEARCH));
//        else if(text.equals(FORECAST_SEARCH)) switchSearch(FORECAST_SEARCH);
//        else {
//            Snacky.builder().setActivty(MainActivity.this).setText(text).success().show();
//        }
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if(hypothesis!=null){
            String text = hypothesis.getHypstr();
            Log.d(TAG, "onResult: " + text);

            Snacky.builder()
                    .setActivty(MainActivity.this)
                    .setText(text)
                    .success().show();
        }
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTimeout() {

    }

    private void speakOut(String text){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            int result = tts.setLanguage(Locale.ENGLISH);

            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                result = tts.setLanguage(Locale.ENGLISH);
            } else{
                return;
            }

            if(result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA){
                Snacky.builder()
                        .setActivty(MainActivity.this)
                        .setText("Engligh language not supported")
                        .error().show();
                return;
            }
        } else{
            Snacky.builder()
                    .setActivty(MainActivity.this)
                    .setText("Speech not availalbe")
                    .error().show();
        }
    }

    private void processSpeech(String text){
        text = text.toLowerCase();
        if(text.contains("music")){
            Snacky.builder()
                    .setActivty(MainActivity.this)
                    .setText("Now Playing Song")
                    .success().show();
            viewPager.setCurrentItem(NUM_PAGES-1);
        } else if(text.contains("news")){
            Snacky.builder()
                    .setActivty(MainActivity.this)
                    .setText("Reading out news")
                    .success().show();
            speakOut("Current News are as follows");
            viewPager.setCurrentItem(1);
        } else if(text.contains("weather") || text.contains("rain")){
            Snacky.builder()
                    .setActivty(MainActivity.this)
                    .setText("Reading current weather")
                    .success().show();
            speakOut("I think its going to rain today");
            viewPager.setCurrentItem(0);
        } else if(text.contains("calender") || text.contains("agenda")){
            Snacky.builder()
                    .setActivty(MainActivity.this)
                    .setText("Showing today's agenda")
                    .success().show();
            speakOut("Today's agenda is");
            viewPager.setCurrentItem(2);
        } else {
            Snacky.builder()
                    .setActivty(MainActivity.this)
                    .setText("Sorry, not supported right now")
                    .warning().show();
        }
    }

    private void SetSpeechListener()
    {
        mSpeechManager=new SpeechRecognizerManager(this, new SpeechRecognizerManager.onResultsReady() {
            @Override
            public void onResults(ArrayList<String> results) {



                if(results!=null && results.size()>0)
                {

                    if(results.size()==1)
                    {
                        mSpeechManager.destroy();
                        mSpeechManager = null;
//                        result_tv.setText(results.get(0));
                    }
                    else {
                        StringBuilder sb = new StringBuilder();
                        if (results.size() > 5) {
                            results = (ArrayList<String>) results.subList(0, 5);
                        }
                        for (String result : results) {
                            sb.append(result).append("\n");
                        }
//                        result_tv.setText(sb.toString());
                    }
                }
//                else
//                    result_tv.setText(getString(R.string.no_results_found));
            }
        });
    }
}
