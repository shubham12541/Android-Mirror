package com.tominc.mirror;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.tominc.mirror.models.IpLocation;
import com.tominc.mirror.models.News;
import com.tominc.mirror.models.VolleyCallback;
import com.tominc.mirror.models.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.mateware.snacky.Snacky;
import it.macisamuele.calendarprovider.CalendarInfo;
import it.macisamuele.calendarprovider.EventInfo;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {
    Utility utility;
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };


    ImageView weather_image;
    TextView weather_temp, weather_des, weather_wind, weather_elevation, weather_humidity, weather_visibility,
                news_list, agenda_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        weather_image = (ImageView) findViewById(R.id.weather_image);
        weather_temp  = (TextView) findViewById(R.id.weather_temp);
        weather_des  = (TextView) findViewById(R.id.weather_description);
        weather_wind  = (TextView) findViewById(R.id.weather_wind);
        weather_elevation  = (TextView) findViewById(R.id.weather_elevation);
        weather_humidity  = (TextView) findViewById(R.id.weather_humidity);
        weather_visibility  = (TextView) findViewById(R.id.weather_visibility);

        news_list = (TextView) findViewById(R.id.news_list);
        agenda_list = (TextView) findViewById(R.id.agenda_list);

        mVisible = true;
        mContentView = findViewById(R.id.mirror_content);

        fetchNews();

        utility = Utility.getInstance(MainActivity.this.getApplicationContext());

        utility.jsonObjectRequest(Config.GET_LOCATION_URL, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                IpLocation location = new IpLocation();
                try {
                    location.setCity(response.getString("city"));
                    location.setIp(response.getString("ip"));
                    location.setRegion(response.getString("region"));
                    location.setCountry(response.getString("country"));
                    location.setLoc(response.getString("loc"));
                    location.setOrg(response.getString("org"));

                    getWeather(location);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Snacky.builder().setActivty(MainActivity.this)
                            .setText("In Valid Response")
                            .setDuration(Snacky.LENGTH_SHORT)
                            .warning();
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                Snacky.builder().setActivty(MainActivity.this)
                        .setText("No Internet Connection")
                        .setDuration(Snacky.LENGTH_SHORT)
                        .error();
            }
        });

    }

    private void showAgendaOnUI(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            agenda_list.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
        } else{
            agenda_list.setText(Html.fromHtml(text));
        }
    }

    private void fetchCalender(){
        String agenda_text = "<b>Today: </b><br>";
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        Calendar endOfToday = Calendar.getInstance();
        endOfToday.set(Calendar.HOUR_OF_DAY, 23);
        List<EventInfo> todayEvents = EventInfo.getEvents(MainActivity.this, today.getTime(), endOfToday.getTime(), null, null);
        for(EventInfo event: todayEvents){
            agenda_text += event.getTitle() + "<br>";
        }

        agenda_text += "<b>Tommorrow</b><br>";
        today.add(Calendar.DATE, 1);
        endOfToday.add(Calendar.DATE, 1);
        List<EventInfo> tomEvents = EventInfo.getEvents(MainActivity.this, today.getTime(), endOfToday.getTime(), null, null);
        for(EventInfo event: tomEvents){
            agenda_text += event.getTitle() + "<br>";
        }

    }

    private void showNewsOnUI(String text){
        news_list.setText(text);
    }

    private void fetchNews(){
        utility.jsonObjectRequest(Config.GET_NEWS_URL, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray articles = response.getJSONArray("articles");

                    String news_text ="";

                    for(int i=0;i<articles.length();i++){
                        JSONObject article = (JSONObject) articles.get(i);
                        News newt = new News();
                        newt.setTitle(article.getString("title"));
                        newt.setAuthor(article.getString("author"));
                        newt.setDescription(article.getString("description"));
                        newt.setUrl(article.getString("url"));
                        newt.setUrlToImage(article.getString("urlToImage"));
                        newt.setPublishedOn(article.getString("publishedAt"));

                        news_text += newt.getTitle() + "\n";
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Snacky.builder().setActivty(MainActivity.this)
                            .setText("Invalid Response Recieved")
                            .setDuration(Snacky.LENGTH_SHORT)
                            .error();
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                Snacky.builder().setActivty(MainActivity.this)
                        .setText("No Internet Connection")
                        .setDuration(Snacky.LENGTH_SHORT)
                        .error();
            }
        });
    }

    private void getWeather(IpLocation location){
        utility.jsonObjectRequest(Config.GET_WEATHER_URL_BASE + location.getLoc() + ".json", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Weather weather = new Weather();
                try {
                    JSONObject observationData = response.getJSONObject("observation_location");
                    weather.setOb_full(observationData.getString("full"));
                    weather.setOb_city(observationData.getString("city"));
                    weather.setOb_country(observationData.getString("country"));
                    weather.setOb_state(observationData.getString("state"));
                    weather.setOb_lat(observationData.getString("latitude"));
                    weather.setOb_long(observationData.getString("longitude"));
                    weather.setOb_elevation(observationData.getString("elevation"));

                    weather.setOb_time(response.getString("observation_time"));
                    weather.setWeather(response.getString("weather"));
                    weather.setTemp_c(response.getString("temp_c"));
                    weather.setRelative_humidity(response.getString("relative_humidity"));
                    weather.setWind_string(response.getString("wind_string"));
                    weather.setWind_dir(response.getString("wind_dir"));
                    weather.setWind_degrees(response.getString("wind_degrees"));
                    weather.setWind_mph(response.getString("wind_mph"));
                    weather.setWind_kph(response.getString("wind_kph"));
                    weather.setPressure_in(response.getString("pressure_in"));
                    weather.setPressure_mb(response.getString("pressure_mb"));
                    weather.setPressure_trend(response.getString("pressure_trend"));
                    weather.setDewpoint_c(response.getString("dewpoint_c"));
                    weather.setHeat_index_c(response.getString("heat_index_c"));
                    weather.setWindchill_c(response.getString("windchill_c"));
                    weather.setWindchill_string(response.getString("windchill_string"));
                    weather.setFeelslike_c(response.getString("feelslike_c"));
                    weather.setVisibility_km(response.getString("visibility_km"));
                    weather.setSolar_radiation(response.getString("solarradiation"));
                    weather.setPrecip_today_in(response.getString("precip_today_in"));
                    weather.setIcon(response.getString("icon"));
                    weather.setIcon_url(response.getString("icon_url"));

                    showWeatheronUI(weather);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Snacky.builder().setActivty(MainActivity.this)
                            .setText("In Valid Response")
                            .setDuration(Snacky.LENGTH_SHORT)
                            .warning();
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                Snacky.builder().setActivty(MainActivity.this)
                        .setText("No Internet Connection")
                        .setDuration(Snacky.LENGTH_SHORT)
                        .error();
            }
        });
    }

    private void showWeatheronUI(Weather weather){
        Glide.with(MainActivity.this)
                .load(weather.getIcon_url())
                .into(weather_image);

        weather_des.setText(weather.getWeather());
        weather_temp.setText(weather.getTemp_c() + "");
        weather_wind.setText(weather.getWind_string());
        weather_humidity.setText("Humidity: " + weather.getRelative_humidity());
        weather_visibility.setText(weather.getVisibility_km());
        weather_elevation.setText(weather.getOb_elevation());
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
