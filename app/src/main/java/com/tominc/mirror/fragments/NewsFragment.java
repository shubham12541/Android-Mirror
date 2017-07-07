package com.tominc.mirror.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.tominc.mirror.Config;
import com.tominc.mirror.MainActivity;
import com.tominc.mirror.R;
import com.tominc.mirror.Utility;
import com.tominc.mirror.models.News;
import com.tominc.mirror.models.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.mateware.snacky.Snacky;

/**
 * Created by shubham on 07/07/17.
 */

public class NewsFragment extends Fragment {
    TextView news_list;
    Utility utility;

    public NewsFragment(){
        utility = Utility.getInstance(getActivity().getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.news_layout, container, false);

        news_list = (TextView) root.findViewById(R.id.news_list);

        fetchNews();

        return root;
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

                    showNewsOnUI(news_text);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Snacky.builder().setActivty(getActivity())
                            .setText("Invalid Response Recieved")
                            .setDuration(Snacky.LENGTH_SHORT)
                            .error();
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
}
