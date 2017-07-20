package com.tominc.mirror.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.tominc.mirror.Config;
import com.tominc.mirror.MainActivity;
import com.tominc.mirror.R;
import com.tominc.mirror.Utility;
import com.tominc.mirror.models.IpLocation;
import com.tominc.mirror.models.VolleyCallback;
import com.tominc.mirror.models.Weather;

import org.json.JSONException;
import org.json.JSONObject;

import de.mateware.snacky.Snacky;

/**
 * Created by shubham on 07/07/17.
 */

public class WeatherFragment extends Fragment {
    Context mContext;
    Utility utility;
    private static final String TAG = "WeatherFragment";

    public WeatherFragment(){
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        utility = Utility.getInstance(getActivity());
    }

    ImageView weather_image;
    TextView weather_temp, weather_des, weather_wind, weather_elevation, weather_humidity, weather_visibility;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.weather_layout, container, false);

        weather_image = (ImageView) root.findViewById(R.id.weather_image);
        weather_temp  = (TextView) root.findViewById(R.id.weather_temp);
        weather_des  = (TextView) root.findViewById(R.id.weather_description);
        weather_wind  = (TextView) root.findViewById(R.id.weather_wind);
        weather_elevation  = (TextView) root.findViewById(R.id.weather_elevation);
        weather_humidity  = (TextView) root.findViewById(R.id.weather_humidity);
        weather_visibility  = (TextView) root.findViewById(R.id.weather_visibility);

        getLocation();

        return root;
    }

    private void getLocation(){
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
                    Snacky.builder().setActivty(getActivity())
                            .setText("In Valid Response")
                            .setDuration(Snacky.LENGTH_SHORT)
                            .warning();
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                Snacky.builder().setActivty(getActivity())
                        .setText("No Internet Connection")
                        .setDuration(Snacky.LENGTH_SHORT)
                        .error();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Glide.with(getActivity()).pauseRequests();
    }

    private void getWeather(IpLocation location){
        utility.jsonObjectRequest(Config.GET_WEATHER_URL_BASE + location.getLoc() + ".json", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject res) {
                Weather weather = new Weather();
                try {
                    JSONObject response = res.getJSONObject("current_observation");
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
                    Snacky.builder().setActivty(getActivity())
                            .setText("In Valid Response")
                            .setDuration(Snacky.LENGTH_SHORT)
                            .warning();
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                Snacky.builder().setActivty(getActivity())
                        .setText("No Internet Connection")
                        .setDuration(Snacky.LENGTH_SHORT)
                        .error();
            }
        });
    }

    private void showWeatheronUI(Weather weather){
        Glide.with(getActivity())
                .load(weather.getIcon_url())
                .into(weather_image);

        weather_des.setText(weather.getWeather());
        weather_temp.setText(weather.getTemp_c() + "");
        weather_wind.setText(weather.getWind_string());
        weather_humidity.setText("Humidity: " + weather.getRelative_humidity());
        weather_visibility.setText(weather.getVisibility_km());
        weather_elevation.setText(weather.getOb_elevation());
    }
}
