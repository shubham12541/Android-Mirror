package com.tominc.mirror;

/**
 * Created by shubham on 01/07/17.
 */

public class Config {

    public static final String GET_LOCATION_URL = "http://ipinfo.io/json";
    public static final String GET_WEATHER_URL_BASE =
            "http://api.wunderground.com/api/7f1df5eb2105d68f/conditions/q/"; // + coordinates + .json
    public static final String GET_NEWS_URL =
            "https://newsapi.org/v1/articles?source=the-verge&sortBy=top&apiKey=61d2bac5da354fbe88f176ff16a32fc4";
    // change the source to the-hindu for indian news


}
