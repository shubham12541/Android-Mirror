package com.tominc.mirror;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tominc.mirror.models.IpLocation;
import com.tominc.mirror.models.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shubham on 01/07/17.
 */

public class Utility {
    public static Utility mInstance;
    Context mContext;
    private RequestQueue rq;
    private static final String TAG = "Utility";

    private Utility(Context c){
        this.mContext = c;
        rq = getRequestQueue();
    }

    public static synchronized Utility getInstance(Context c){
        if(mInstance == null){
            mInstance = new Utility(c);
        }
        return mInstance;
    }

    public void jsonObjectRequest(String url, final VolleyCallback callback){
        final IpLocation[] ipLocation = new IpLocation[1];

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure(error);
            }
        });
        request.setTag(TAG);

        rq.add(request);
    };

    public <T> void addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }

    public void cancelAllRequest(){
        if(rq!=null){
            rq.cancelAll(TAG);
        }
    }

    public RequestQueue getRequestQueue(){
        if(rq==null){
            rq = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return rq;
    }


}
