package com.tominc.mirror.models;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by shubham on 02/07/17.
 */

public interface VolleyCallback {
    public void onSuccess(JSONObject response);
    public void onFailure(VolleyError error);
}
