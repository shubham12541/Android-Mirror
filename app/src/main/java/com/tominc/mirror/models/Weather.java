package com.tominc.mirror.models;

/**
 * Created by shubham on 02/07/17.
 */

public class Weather {
    private String ob_full, ob_city, ob_state, ob_country, ob_lat, ob_long, ob_elevation, ob_time;
    private String weather, temp_string, relative_humidity, wind_string, wind_dir, wind_degrees, pressure_mb, pressure_in, pressure_trend,
                        windchill_string, feelslike_c, visibility_km, solar_radiation, UV, precip_today_in, icon, icon_url;

    private int temp_c, wind_mph, wind_kph, dewpoint_c, heat_index_c, windchill_c;


    public String getOb_full() {
        return ob_full;
    }

    public void setOb_full(String ob_full) {
        this.ob_full = ob_full;
    }

    public String getOb_city() {
        return ob_city;
    }

    public void setOb_city(String ob_city) {
        this.ob_city = ob_city;
    }

    public String getOb_state() {
        return ob_state;
    }

    public void setOb_state(String ob_state) {
        this.ob_state = ob_state;
    }

    public String getOb_country() {
        return ob_country;
    }

    public void setOb_country(String ob_country) {
        this.ob_country = ob_country;
    }

    public String getOb_lat() {
        return ob_lat;
    }

    public void setOb_lat(String ob_lat) {
        this.ob_lat = ob_lat;
    }

    public String getOb_long() {
        return ob_long;
    }

    public void setOb_long(String ob_long) {
        this.ob_long = ob_long;
    }

    public String getOb_elevation() {
        return ob_elevation;
    }

    public void setOb_elevation(String ob_elevation) {
        this.ob_elevation = ob_elevation;
    }

    public String getOb_time() {
        return ob_time;
    }

    public void setOb_time(String ob_time) {
        this.ob_time = ob_time;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTemp_string() {
        return temp_string;
    }

    public void setTemp_string(String temp_string) {
        this.temp_string = temp_string;
    }

    public String getRelative_humidity() {
        return relative_humidity;
    }

    public void setRelative_humidity(String relative_humidity) {
        this.relative_humidity = relative_humidity;
    }

    public String getWind_string() {
        return wind_string;
    }

    public void setWind_string(String wind_string) {
        this.wind_string = wind_string;
    }

    public String getWind_dir() {
        return wind_dir;
    }

    public void setWind_dir(String wind_dir) {
        this.wind_dir = wind_dir;
    }

    public String getWind_degrees() {
        return wind_degrees;
    }

    public void setWind_degrees(String wind_degrees) {
        this.wind_degrees = wind_degrees;
    }

    public String getPressure_mb() {
        return pressure_mb;
    }

    public void setPressure_mb(String pressure_mb) {
        this.pressure_mb = pressure_mb;
    }

    public String getPressure_in() {
        return pressure_in;
    }

    public void setPressure_in(String pressure_in) {
        this.pressure_in = pressure_in;
    }

    public String getPressure_trend() {
        return pressure_trend;
    }

    public void setPressure_trend(String pressure_trend) {
        this.pressure_trend = pressure_trend;
    }

    public String getWindchill_string() {
        return windchill_string;
    }

    public void setWindchill_string(String windchill_string) {
        this.windchill_string = windchill_string;
    }

    public String getFeelslike_c() {
        return feelslike_c;
    }

    public void setFeelslike_c(String feelslike_c) {
        this.feelslike_c = feelslike_c;
    }

    public String getVisibility_km() {
        return visibility_km;
    }

    public void setVisibility_km(String visibility_km) {
        this.visibility_km = visibility_km;
    }

    public String getSolar_radiation() {
        return solar_radiation;
    }

    public void setSolar_radiation(String solar_radiation) {
        this.solar_radiation = solar_radiation;
    }

    public String getUV() {
        return UV;
    }

    public void setUV(String UV) {
        this.UV = UV;
    }

    public String getPrecip_today_in() {
        return precip_today_in;
    }

    public void setPrecip_today_in(String precip_today_in) {
        this.precip_today_in = precip_today_in;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public int getTemp_c() {
        return temp_c;
    }

    public void setTemp_c(String temp_c) {
        this.temp_c = Integer.valueOf(temp_c);
    }

    public int getWind_mph() {
        return wind_mph;
    }

    public void setWind_mph(String wind_mph) {
        this.wind_mph = Integer.valueOf(wind_mph);
    }

    public int getWind_kph() {
        return wind_kph;
    }

    public void setWind_kph(String wind_kph) {
        this.wind_kph = Integer.valueOf(wind_kph);
    }

    public int getDewpoint_c() {
        return dewpoint_c;
    }

    public void setDewpoint_c(String dewpoint_c) {
        this.dewpoint_c = Integer.valueOf(dewpoint_c);
    }

    public int getHeat_index_c() {
        return heat_index_c;
    }

    public void setHeat_index_c(String heat_index_c) {
        this.heat_index_c = Integer.valueOf(heat_index_c);
    }

    public int getWindchill_c() {
        return windchill_c;
    }

    public void setWindchill_c(String windchill_c) {
        this.windchill_c = Integer.valueOf(windchill_c);
    }
}
